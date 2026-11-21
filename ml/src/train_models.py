"""Entrena y compara M0, M1 y M2 para ocupación a 7 y 30 días."""

from __future__ import annotations

import argparse
import json
import math
from dataclasses import dataclass
from datetime import date, timedelta
from pathlib import Path
from typing import Any

import joblib
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_absolute_error, mean_squared_error
from sklearn.model_selection import TimeSeriesSplit
from sklearn.tree import DecisionTreeRegressor


ACTIVE_STATUSES = {"CONFIRMADA", "COMPLETADA"}
REQUIRED_COLUMNS = {
    "id",
    "check_in_date",
    "check_out_date",
    "room_type",
    "status",
    "total_price",
}
FEATURE_COLUMNS = [
    "occupied_now",
    "occupied_lag_1",
    "occupied_lag_7",
    "occupied_lag_14",
    "occupied_lag_28",
    "occupied_mean_7",
    "occupied_mean_28",
    "occupied_std_28",
    "arrivals_now",
    "arrivals_mean_7",
    "cancellations_now",
    "cancellations_mean_7",
    "target_day_of_week",
    "target_month",
    "target_is_weekend",
    "target_day_sin",
    "target_day_cos",
]


@dataclass
class ExperimentResult:
    daily: pd.DataFrame
    comparison: pd.DataFrame
    fold_metrics: pd.DataFrame
    predictions: pd.DataFrame
    summary: dict[str, Any]


def load_and_validate(path: Path) -> pd.DataFrame:
    frame = pd.read_csv(path)
    missing = REQUIRED_COLUMNS.difference(frame.columns)
    if missing:
        raise ValueError(f"Faltan columnas obligatorias: {sorted(missing)}")

    frame = frame.copy()
    frame["check_in_date"] = pd.to_datetime(frame["check_in_date"], errors="coerce")
    frame["check_out_date"] = pd.to_datetime(frame["check_out_date"], errors="coerce")
    frame["status"] = frame["status"].astype(str).str.upper().str.strip()
    frame["total_price"] = pd.to_numeric(frame["total_price"], errors="coerce").fillna(0.0)

    invalid = (
        frame["check_in_date"].isna()
        | frame["check_out_date"].isna()
        | (frame["check_out_date"] <= frame["check_in_date"])
        | frame["id"].duplicated()
    )
    if invalid.any():
        raise ValueError(f"Se detectaron {int(invalid.sum())} reservas inválidas o duplicadas.")
    return frame.sort_values(["check_in_date", "id"]).reset_index(drop=True)


def aggregate_daily(
    reservations: pd.DataFrame, capacity: int, as_of_date: date | None = None
) -> pd.DataFrame:
    if capacity <= 0:
        raise ValueError("capacity debe ser mayor que cero.")
    start = reservations["check_in_date"].min().normalize()
    end = (reservations["check_out_date"].max() - timedelta(days=1)).normalize()
    if as_of_date is not None:
        end = min(end, pd.Timestamp(as_of_date))
    if end < start:
        raise ValueError("La fecha de corte es anterior al primer check-in.")
    dates = pd.date_range(start, end, freq="D")
    day_number = pd.Series(np.arange(len(dates)), index=dates)
    difference = np.zeros(len(dates) + 1, dtype=int)

    active = reservations[reservations["status"].isin(ACTIVE_STATUSES)]
    for row in active.itertuples(index=False):
        first = int(day_number[row.check_in_date.normalize()])
        checkout = row.check_out_date.normalize()
        last_exclusive = int((checkout - start).days)
        difference[first] += 1
        difference[min(last_exclusive, len(dates))] -= 1

    daily = pd.DataFrame(index=dates)
    daily.index.name = "date"
    daily["occupied_rooms"] = np.cumsum(difference[:-1])
    if (daily["occupied_rooms"] > capacity).any():
        peak = int(daily["occupied_rooms"].max())
        raise ValueError(f"La ocupación máxima ({peak}) supera la capacidad ({capacity}).")

    arrival_counts = active.groupby(active["check_in_date"].dt.normalize()).size()
    cancelled = reservations[reservations["status"] == "CANCELADA"]
    cancellation_counts = cancelled.groupby(cancelled["check_in_date"].dt.normalize()).size()
    daily["arrivals"] = arrival_counts.reindex(dates, fill_value=0).astype(int)
    daily["cancellations"] = cancellation_counts.reindex(dates, fill_value=0).astype(int)
    daily["occupancy_rate"] = daily["occupied_rooms"] / capacity
    return daily


def build_supervised(daily: pd.DataFrame, horizon: int) -> pd.DataFrame:
    occupied = daily["occupied_rooms"].astype(float)
    target_dates = daily.index + timedelta(days=horizon)
    day_of_year = target_dates.dayofyear.to_numpy()
    data = pd.DataFrame(index=daily.index)
    data["occupied_now"] = occupied
    for lag in (1, 7, 14, 28):
        data[f"occupied_lag_{lag}"] = occupied.shift(lag)
    data["occupied_mean_7"] = occupied.rolling(7).mean()
    data["occupied_mean_28"] = occupied.rolling(28).mean()
    data["occupied_std_28"] = occupied.rolling(28).std()
    data["arrivals_now"] = daily["arrivals"]
    data["arrivals_mean_7"] = daily["arrivals"].rolling(7).mean()
    data["cancellations_now"] = daily["cancellations"]
    data["cancellations_mean_7"] = daily["cancellations"].rolling(7).mean()
    data["target_day_of_week"] = target_dates.dayofweek
    data["target_month"] = target_dates.month
    data["target_is_weekend"] = (target_dates.dayofweek >= 5).astype(int)
    data["target_day_sin"] = np.sin(2 * np.pi * day_of_year / 365.25)
    data["target_day_cos"] = np.cos(2 * np.pi * day_of_year / 365.25)
    data["target"] = occupied.shift(-horizon)

    # M0 usa el último valor del mismo día de semana disponible en el origen.
    seasonal_origin_lag = 7 * math.ceil(horizon / 7) - horizon
    data["M0: Baseline estacional"] = occupied.shift(seasonal_origin_lag)
    return data.dropna().copy()


def model_factories() -> dict[str, Any]:
    return {
        "M1: Árbol de decisión": lambda: DecisionTreeRegressor(
            max_depth=5, min_samples_leaf=5, random_state=42
        ),
        "M2: Random Forest": lambda: RandomForestRegressor(
            n_estimators=100,
            max_depth=10,
            min_samples_leaf=5,
            random_state=42,
            n_jobs=-1,
        ),
    }


def metric_values(actual: np.ndarray, predicted: np.ndarray) -> dict[str, float]:
    return {
        "mae": float(mean_absolute_error(actual, predicted)),
        "rmse": float(mean_squared_error(actual, predicted) ** 0.5),
        "bias": float(np.mean(predicted - actual)),
    }


def evaluate_horizon(
    supervised: pd.DataFrame, horizon: int, capacity: int
) -> tuple[pd.DataFrame, pd.DataFrame, dict[str, Any]]:
    splitter = TimeSeriesSplit(n_splits=5, gap=horizon)
    fold_rows: list[dict[str, Any]] = []
    prediction_rows: list[pd.DataFrame] = []

    for fold, (train_index, test_index) in enumerate(splitter.split(supervised), start=1):
        train = supervised.iloc[train_index]
        test = supervised.iloc[test_index]
        actual = test["target"].to_numpy()

        baseline = test["M0: Baseline estacional"].to_numpy()
        baseline_metrics = metric_values(actual, baseline)
        fold_rows.append({"horizon": horizon, "fold": fold, "model": "M0: Baseline estacional", **baseline_metrics})
        prediction_rows.append(
            pd.DataFrame(
                {
                    "origin_date": test.index,
                    "target_date": test.index + timedelta(days=horizon),
                    "horizon": horizon,
                    "fold": fold,
                    "model": "M0: Baseline estacional",
                    "actual": actual,
                    "predicted": baseline,
                }
            )
        )

        for model_name, factory in model_factories().items():
            model = factory()
            model.fit(train[FEATURE_COLUMNS], train["target"])
            predicted = np.clip(model.predict(test[FEATURE_COLUMNS]), 0, capacity)
            metrics = metric_values(actual, predicted)
            fold_rows.append({"horizon": horizon, "fold": fold, "model": model_name, **metrics})
            prediction_rows.append(
                pd.DataFrame(
                    {
                        "origin_date": test.index,
                        "target_date": test.index + timedelta(days=horizon),
                        "horizon": horizon,
                        "fold": fold,
                        "model": model_name,
                        "actual": actual,
                        "predicted": predicted,
                    }
                )
            )

    folds = pd.DataFrame(fold_rows)
    predictions = pd.concat(prediction_rows, ignore_index=True)
    comparison = (
        folds.groupby(["horizon", "model"], as_index=False)
        .agg(mae=("mae", "mean"), rmse=("rmse", "mean"), bias=("bias", "mean"))
        .sort_values(["horizon", "mae"])
        .reset_index(drop=True)
    )
    best_name = str(comparison.iloc[0]["model"])
    selected_predictions = predictions[predictions["model"] == best_name].copy()
    residuals = selected_predictions["actual"] - selected_predictions["predicted"]
    lower_residual = float(residuals.quantile(0.10))
    upper_residual = float(residuals.quantile(0.90))
    selected_predictions["lower"] = np.clip(selected_predictions["predicted"] + lower_residual, 0, capacity)
    selected_predictions["upper"] = np.clip(selected_predictions["predicted"] + upper_residual, 0, capacity)
    coverage = float(
        (
            (selected_predictions["actual"] >= selected_predictions["lower"])
            & (selected_predictions["actual"] <= selected_predictions["upper"])
        ).mean()
    )
    interval_width = float((selected_predictions["upper"] - selected_predictions["lower"]).mean())
    confidence = "alta" if coverage >= 0.75 and interval_width <= capacity * 0.15 else "media" if coverage >= 0.65 else "baja"

    if best_name == "M0: Baseline estacional":
        fitted_model = None
    else:
        fitted_model = model_factories()[best_name]()
        fitted_model.fit(supervised[FEATURE_COLUMNS], supervised["target"])

    artifact = {
        "model_name": best_name,
        "model": fitted_model,
        "horizon": horizon,
        "features": FEATURE_COLUMNS,
        "capacity": capacity,
        "residual_quantiles": {"p10": lower_residual, "p90": upper_residual},
        "validation_coverage": coverage,
        "confidence": confidence,
        "training_origin_end": supervised.index.max().date().isoformat(),
    }
    return comparison, folds, selected_predictions, artifact


def save_plots(comparison: pd.DataFrame, predictions: pd.DataFrame, reports_dir: Path) -> None:
    labels = [f"H{row.horizon} {row.model.split(':')[0]}" for row in comparison.itertuples()]
    fig, axis = plt.subplots(figsize=(10, 5))
    axis.bar(labels, comparison["mae"], color="#aa895f")
    axis.set_title("Comparación de modelos por MAE")
    axis.set_ylabel("MAE (habitaciones)")
    axis.tick_params(axis="x", rotation=35)
    fig.tight_layout()
    fig.savefig(reports_dir / "model_comparison.png", dpi=160)
    plt.close(fig)

    fig, axes = plt.subplots(2, 1, figsize=(11, 7), sharex=False)
    for axis, horizon in zip(axes, (7, 30)):
        subset = predictions[predictions["horizon"] == horizon].tail(180)
        axis.plot(subset["target_date"], subset["actual"], label="Real", color="#1f2937")
        axis.plot(subset["target_date"], subset["predicted"], label="Pronóstico", color="#aa895f")
        axis.fill_between(subset["target_date"], subset["lower"], subset["upper"], color="#d8c3a5", alpha=0.35, label="P10-P90")
        axis.set_title(f"Validación temporal - horizonte {horizon} días")
        axis.set_ylabel("Habitaciones ocupadas")
        axis.legend(loc="upper left")
    fig.tight_layout()
    fig.savefig(reports_dir / "temporal_validation.png", dpi=160)
    plt.close(fig)


def run_experiment(
    input_path: Path,
    capacity: int,
    output_dir: Path,
    as_of_date: date | None = None,
) -> ExperimentResult:
    reservations = load_and_validate(input_path)
    daily = aggregate_daily(reservations, capacity, as_of_date)
    if len(daily) < 365:
        raise ValueError("Se requieren al menos 365 días para validar estacionalidad y cortes temporales.")

    reports_dir = output_dir / "reports"
    models_dir = output_dir / "models"
    reports_dir.mkdir(parents=True, exist_ok=True)
    models_dir.mkdir(parents=True, exist_ok=True)

    comparisons: list[pd.DataFrame] = []
    fold_frames: list[pd.DataFrame] = []
    prediction_frames: list[pd.DataFrame] = []
    summaries: dict[str, Any] = {
        "capacity": capacity,
        "reservations": len(reservations),
        "daily_start": daily.index.min().date().isoformat(),
        "daily_end": daily.index.max().date().isoformat(),
        "horizons": {},
    }

    for horizon in (7, 30):
        supervised = build_supervised(daily, horizon)
        comparison, folds, predictions, artifact = evaluate_horizon(supervised, horizon, capacity)
        comparisons.append(comparison)
        fold_frames.append(folds)
        prediction_frames.append(predictions)
        joblib.dump(artifact, models_dir / f"occupancy_h{horizon}.joblib")
        summaries["horizons"][str(horizon)] = {
            key: value for key, value in artifact.items() if key not in {"model", "features"}
        }

    all_comparisons = pd.concat(comparisons, ignore_index=True)
    all_folds = pd.concat(fold_frames, ignore_index=True)
    all_predictions = pd.concat(prediction_frames, ignore_index=True)
    all_comparisons.to_csv(reports_dir / "model_comparison.csv", index=False)
    all_folds.to_csv(reports_dir / "fold_metrics.csv", index=False)
    (reports_dir / "experiment_summary.json").write_text(
        json.dumps(summaries, ensure_ascii=False, indent=2), encoding="utf-8"
    )
    save_plots(all_comparisons, all_predictions, reports_dir)
    return ExperimentResult(daily, all_comparisons, all_folds, all_predictions, summaries)


def read_metadata(metadata_path: Path | None, fallback_capacity: int) -> tuple[int, date | None]:
    if metadata_path and metadata_path.exists():
        metadata = json.loads(metadata_path.read_text(encoding="utf-8"))
        raw_cutoff = metadata.get("as_of_date") or metadata.get("end_date")
        cutoff = date.fromisoformat(raw_cutoff) if raw_cutoff else None
        return int(metadata["capacity"]), cutoff
    return fallback_capacity, None


def parse_args() -> argparse.Namespace:
    ml_dir = Path(__file__).resolve().parents[1]
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input", type=Path, default=ml_dir / "data/raw/reservations.csv")
    parser.add_argument("--metadata", type=Path, default=ml_dir / "data/raw/metadata.json")
    parser.add_argument("--capacity", type=int, default=180)
    parser.add_argument("--output-dir", type=Path, default=ml_dir)
    return parser.parse_args()


if __name__ == "__main__":
    args = parse_args()
    selected_capacity, cutoff = read_metadata(args.metadata, args.capacity)
    result = run_experiment(args.input, selected_capacity, args.output_dir, cutoff)
    print("\nComparación de modelos (menor MAE es mejor):")
    print(result.comparison.round(3).to_string(index=False))
    print("\nModelos seleccionados:")
    for horizon, details in result.summary["horizons"].items():
        print(
            f"  H{horizon}: {details['model_name']} | "
            f"cobertura={details['validation_coverage']:.1%} | "
            f"confiabilidad={details['confidence']}"
        )

