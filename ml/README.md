# Módulo IA/ML - pronóstico de ocupación

Este módulo implementa el experimento solicitado para Hotel Luxury:

- **M0:** baseline estacional semanal.
- **M1:** `DecisionTreeRegressor(max_depth=5, min_samples_leaf=5, random_state=42)`.
- **M2:** `RandomForestRegressor(n_estimators=100, max_depth=10, min_samples_leaf=5, random_state=42)`.
- Horizontes: 7 y 30 días.
- Validación: cinco cortes temporales con una separación igual al horizonte para evitar fuga de información.
- Métricas: MAE, RMSE y sesgo.
- Rango: percentiles 10 y 90 de los residuos de validación.

## Alcance y supuesto principal

La entidad `Reservation` no tiene una relación con `Hotel`. Por ello, esta primera versión pronostica la ocupación **global** y usa como capacidad la suma de `hoteles.habitaciones` (180 habitaciones con los datos actuales). Para pronosticar por sede será necesario agregar `hotel_id` a las reservas y migrar los datos.

El entrenamiento no usa nombres de clientes. La consulta solo extrae identificador, fechas, tipo de habitación, estado y precio. Los estados `CONFIRMADA` y `COMPLETADA` ocupan habitación; `PENDIENTE` y `CANCELADA` no se consideran ocupación realizada.

## Estructura

```text
ml/
  data/raw/                         CSV local (no se versiona)
  models/                           modelos joblib (no se versionan)
  notebooks/ocupacion_hotel_luxury.ipynb
  reports/                          métricas y gráficos generados
  sql/seed_historical_reservations.sql
  src/export_reservations.py
  src/generate_demo_dataset.py
  src/train_models.py
  tests/test_train_models.py
```

## Instalación

Desde la raíz del repositorio:

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -r ml\requirements.txt
```

## Opción A: ejecutar con datos sintéticos

Los datos demo están identificados con el prefijo `ML-SYN-` y no contienen datos personales.

```powershell
python ml\src\generate_demo_dataset.py
python ml\src\train_models.py --input ml\data\raw\reservations.csv --capacity 180
jupyter nbconvert --to notebook --execute --inplace ml\notebooks\ocupacion_hotel_luxury.ipynb
```

## Opción B: ejecutar con MySQL

Se recomienda crear un usuario con permiso `SELECT` únicamente. Configure las variables sin guardarlas en Git:

```powershell
$env:ML_DB_HOST = "servidor"
$env:ML_DB_PORT = "3306"
$env:ML_DB_NAME = "db_hotel_reservas"
$env:ML_DB_USER = "hotel_ml_reader"
$env:ML_DB_PASSWORD = "contraseña"
python ml\src\export_reservations.py
python ml\src\train_models.py --input ml\data\raw\reservations.csv --metadata ml\data\raw\metadata.json
```

También se acepta `DB_URL` en formato `jdbc:mysql://host:3306/base`, junto con `DB_USER` y `DB_PASSWORD`.

La extracción abre una transacción de solo lectura y ejecuta exclusivamente `SELECT`. No escribe reservas, tarifas ni inventario del PMS.

## Poblar históricos

Ejecute `sql/seed_historical_reservations.sql` desde MySQL Workbench o el cliente `mysql`. El script:

- genera reservas desde 2024-01-01 hasta el menor valor entre ayer y 2026-10-31;
- crea estacionalidad semanal y mensual;
- añade cancelaciones sintéticas;
- se detiene si ya encuentra registros con prefijo `ML-SYN-`;
- no borra ni modifica registros existentes.

La carga es sintética y debe declararse así en el informe académico. Antes de ejecutarla en un entorno compartido, haga una copia de seguridad y pruebe primero en desarrollo.

## Resultados

`train_models.py` produce:

- `reports/model_comparison.csv`: comparación agregada por modelo y horizonte.
- `reports/fold_metrics.csv`: métricas de cada corte temporal.
- `reports/experiment_summary.json`: modelo seleccionado, intervalo y cobertura.
- `reports/model_comparison.png`: gráfico para evidencia.
- `reports/temporal_validation.png`: últimos resultados fuera de muestra.
- `models/occupancy_h7.joblib` y `models/occupancy_h30.joblib`.

Los archivos `joblib` solo deben cargarse si fueron generados por este proyecto; este formato no es seguro para artefactos de origen desconocido.

