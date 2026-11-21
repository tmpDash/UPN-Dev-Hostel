"""Genera reservas sintéticas reproducibles con el esquema de Hotel Luxury."""

from __future__ import annotations

import argparse
import csv
import json
import math
import random
from datetime import date, timedelta
from pathlib import Path


ROOMS = {
    "SIMPLE": 120.0,
    "DOBLE": 190.0,
    "SUITE": 350.0,
}


def generate_demo_dataset(
    output: Path,
    metadata_output: Path,
    start_date: date = date(2024, 1, 1),
    end_date: date = date(2026, 10, 31),
    capacity: int = 180,
    seed: int = 42,
) -> int:
    """Crea un CSV sin datos personales y devuelve la cantidad de reservas."""
    rng = random.Random(seed)
    output.parent.mkdir(parents=True, exist_ok=True)
    metadata_output.parent.mkdir(parents=True, exist_ok=True)

    rows: list[dict[str, object]] = []
    current = start_date
    reservation_id = 1

    while current <= end_date:
        annual = 2.5 * math.sin(2 * math.pi * current.timetuple().tm_yday / 365.25)
        weekend = 3 if current.weekday() >= 5 else 0
        high_season = 4 if current.month in (7, 8, 12) else 0
        arrivals = max(5, round(10 + annual + weekend + high_season + rng.gauss(0, 1.4)))

        for daily_index in range(1, arrivals + 1):
            room_roll = rng.random()
            room_type = "SIMPLE" if room_roll < 0.58 else "DOBLE" if room_roll < 0.88 else "SUITE"
            stay = rng.randint(2, 6)
            cancelled = rng.random() < 0.075
            status = "CANCELADA" if cancelled else "COMPLETADA"
            price = 0.0 if cancelled else ROOMS[room_type] * stay
            rows.append(
                {
                    "id": reservation_id,
                    "check_in_date": current.isoformat(),
                    "check_out_date": (current + timedelta(days=stay)).isoformat(),
                    "room_type": room_type,
                    "status": status,
                    "total_price": f"{price:.2f}",
                }
            )
            reservation_id += 1

        current += timedelta(days=1)

    with output.open("w", newline="", encoding="utf-8") as csv_file:
        writer = csv.DictWriter(csv_file, fieldnames=list(rows[0].keys()))
        writer.writeheader()
        writer.writerows(rows)

    metadata_output.write_text(
        json.dumps(
            {
                "capacity": capacity,
                "source": "synthetic_demo",
                "start_date": start_date.isoformat(),
                "end_date": end_date.isoformat(),
                "reservations": len(rows),
                "seed": seed,
            },
            indent=2,
        ),
        encoding="utf-8",
    )
    return len(rows)


def parse_args() -> argparse.Namespace:
    ml_dir = Path(__file__).resolve().parents[1]
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, default=ml_dir / "data/raw/reservations.csv")
    parser.add_argument("--metadata", type=Path, default=ml_dir / "data/raw/metadata.json")
    return parser.parse_args()


if __name__ == "__main__":
    args = parse_args()
    count = generate_demo_dataset(args.output, args.metadata)
    print(f"Dataset sintético generado: {count:,} reservas en {args.output}")

