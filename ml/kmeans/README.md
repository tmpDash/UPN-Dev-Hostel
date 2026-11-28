# Hotel Luxury · Experimento independiente de K-Means

Esta extensión usa una serie sintética diaria generada con semilla 42 y capacidad
de 180 habitaciones. Evalúa el efecto de añadir un cluster a un Random Forest
en horizontes de 7 y 30 días. Las cifras corresponden a esta serie y a sus
particiones temporales. El pronóstico supervisado usa su propio dataset de
12 282 reservas y su propio protocolo de evaluación.

## Reproducir

- [Notebook con salidas](notebooks/hotel_luxury_kmeans_avanzado.ipynb)
- [Abrir en Google Colab](https://colab.research.google.com/github/tmpDash/UPN-Dev-Hostel/blob/main/ml/kmeans/notebooks/hotel_luxury_kmeans_avanzado.ipynb)

Desde la raíz del repositorio:

```sh
python -m pip install -r ml/kmeans/requirements.txt
python ml/kmeans/test_experimento.py
python ml/kmeans/src/experimento_clusters.py
```

La serie se genera en `ml/kmeans/data/` y las salidas se reescriben en
`ml/kmeans/reports/`. En Colab, el notebook puede descargar los dos archivos
de arranque y generar las mismas evidencias en su carpeta de trabajo.

## Evidencias para el informe

| Evidencia | Archivo |
| --- | --- |
| Selección de K por Silhouette | [Gráfico](reports/silhouette.png) · [CSV](reports/seleccion_clusters.csv) |
| MAE con y sin clustering | [Gráfico](reports/comparacion_mae.png) · [CSV](reports/metricas_comparativas.csv) |
| Perfiles resultantes | [CSV](reports/perfiles_clusters.csv) |

K = 2 obtuvo el mayor Silhouette para ambos horizontes. La diferencia de MAE
fue de −0.58 % a 7 días y +0.02 % a 30 días, donde un porcentaje positivo
representa una reducción del error.
