from pathlib import Path
import sys
import unittest
from datetime import timedelta

import pandas as pd


ML_DIR = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ML_DIR / "src"))

from train_models import aggregate_daily, build_supervised  # noqa: E402


class TrainModelsTest(unittest.TestCase):
    def test_cancelled_reservation_does_not_occupy_room(self):
        reservations = pd.DataFrame(
            [
                {
                    "id": 1,
                    "check_in_date": pd.Timestamp("2026-01-01"),
                    "check_out_date": pd.Timestamp("2026-01-04"),
                    "room_type": "SIMPLE",
                    "status": "COMPLETADA",
                    "total_price": 360,
                },
                {
                    "id": 2,
                    "check_in_date": pd.Timestamp("2026-01-02"),
                    "check_out_date": pd.Timestamp("2026-01-05"),
                    "room_type": "DOBLE",
                    "status": "CANCELADA",
                    "total_price": 0,
                },
            ]
        )
        daily = aggregate_daily(reservations, capacity=10)
        self.assertEqual(daily.loc["2026-01-02", "occupied_rooms"], 1)
        self.assertEqual(daily.loc["2026-01-02", "cancellations"], 1)

    def test_horizon_30_baseline_uses_last_available_same_weekday(self):
        dates = pd.date_range("2025-01-01", periods=100, freq="D")
        daily = pd.DataFrame(
            {
                "occupied_rooms": range(100),
                "arrivals": 1,
                "cancellations": 0,
                "occupancy_rate": 0.5,
            },
            index=dates,
        )
        supervised = build_supervised(daily, horizon=30)
        origin = supervised.index[0]
        expected = daily.loc[origin - timedelta(days=5), "occupied_rooms"]
        self.assertEqual(supervised.loc[origin, "M0: Baseline estacional"], expected)

    def test_as_of_date_excludes_future_labels(self):
        reservations = pd.DataFrame(
            [
                {
                    "id": 1,
                    "check_in_date": pd.Timestamp("2026-01-01"),
                    "check_out_date": pd.Timestamp("2026-01-10"),
                    "room_type": "SIMPLE",
                    "status": "COMPLETADA",
                    "total_price": 1080,
                }
            ]
        )
        daily = aggregate_daily(reservations, capacity=10, as_of_date=pd.Timestamp("2026-01-05").date())
        self.assertEqual(daily.index.max(), pd.Timestamp("2026-01-05"))


if __name__ == "__main__":
    unittest.main()

