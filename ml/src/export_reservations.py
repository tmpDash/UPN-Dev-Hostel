"""Exporta reservas de MySQL mediante consultas de solo lectura."""

from __future__ import annotations

import argparse
import json
import os
from datetime import date, timedelta
from pathlib import Path
from urllib.parse import parse_qs, urlparse

import mysql.connector
import pandas as pd


RESERVATION_QUERY = """
SELECT id, check_in_date, check_out_date, room_type, status, total_price
FROM reservations
WHERE check_in_date <= %s
ORDER BY check_in_date, id
"""

CAPACITY_QUERY = "SELECT COALESCE(SUM(habitaciones), 0) AS capacity FROM hoteles"


def connection_config() -> dict[str, object]:
    """Lee ML_DB_* o, como compatibilidad, DB_URL/DB_USER/DB_PASSWORD."""
    if os.getenv("ML_DB_HOST"):
        return {
            "host": os.environ["ML_DB_HOST"],
            "port": int(os.getenv("ML_DB_PORT", "3306")),
            "database": os.environ["ML_DB_NAME"],
            "user": os.environ["ML_DB_USER"],
            "password": os.environ["ML_DB_PASSWORD"],
        }

    raw_url = os.getenv("DB_URL", "")
    if raw_url.startswith("jdbc:"):
        raw_url = raw_url[5:]
    parsed = urlparse(raw_url)
    if parsed.scheme != "mysql" or not parsed.hostname or not parsed.path:
        raise RuntimeError(
            "Configure ML_DB_HOST, ML_DB_NAME, ML_DB_USER y ML_DB_PASSWORD "
            "o una DB_URL con formato jdbc:mysql://host:3306/base."
        )
    query = parse_qs(parsed.query)
    config: dict[str, object] = {
        "host": parsed.hostname,
        "port": parsed.port or 3306,
        "database": parsed.path.lstrip("/"),
        "user": os.environ["DB_USER"],
        "password": os.environ["DB_PASSWORD"],
    }
    if query.get("ssl-mode", [""])[0].upper() in {"REQUIRED", "VERIFY_CA", "VERIFY_IDENTITY"}:
        config["ssl_disabled"] = False
    return config


def export_reservations(output: Path, metadata_output: Path, as_of: date) -> tuple[int, int]:
    connection = mysql.connector.connect(**connection_config())
    try:
        connection.start_transaction(readonly=True)
        cursor = connection.cursor(dictionary=True)
        cursor.execute(RESERVATION_QUERY, (as_of,))
        reservations = pd.DataFrame(cursor.fetchall(), columns=[
            "id", "check_in_date", "check_out_date", "room_type", "status", "total_price"
        ])
        cursor.execute(CAPACITY_QUERY)
        capacity_row = cursor.fetchone()
        cursor.close()
        connection.rollback()
    finally:
        connection.close()

    capacity = int(capacity_row["capacity"])
    if capacity <= 0:
        raise ValueError("La suma de hoteles.habitaciones debe ser mayor que cero.")

    output.parent.mkdir(parents=True, exist_ok=True)
    reservations.to_csv(output, index=False)
    metadata_output.write_text(
        json.dumps(
            {
                "capacity": capacity,
                "source": "mysql_readonly",
                "as_of_date": as_of.isoformat(),
                "reservations": len(reservations),
            },
            indent=2,
        ),
        encoding="utf-8",
    )
    return len(reservations), capacity


def parse_args() -> argparse.Namespace:
    ml_dir = Path(__file__).resolve().parents[1]
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, default=ml_dir / "data/raw/reservations.csv")
    parser.add_argument("--metadata", type=Path, default=ml_dir / "data/raw/metadata.json")
    parser.add_argument("--as-of", type=date.fromisoformat, default=date.today() - timedelta(days=1))
    return parser.parse_args()


if __name__ == "__main__":
    args = parse_args()
    count, room_capacity = export_reservations(args.output, args.metadata, args.as_of)
    print(f"Extracción finalizada: {count:,} reservas; capacidad global={room_capacity}.")

