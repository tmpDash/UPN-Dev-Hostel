import sys
from pathlib import Path
import numpy as np
sys.path.insert(0,str(Path(__file__).parent/'src'))
from experimento_clusters import cargar_datos,crear_features,CLUSTER_COLS

def test_fechas_y_rangos():
    df=cargar_datos()
    assert df.fecha.is_monotonic_increasing
    assert len(df)>365
    assert (df.habitaciones_ocupadas>=0).all()
    assert (df.habitaciones_ocupadas<=180).all()

def test_no_fuga_tras_modificar_el_futuro():
    df=cargar_datos()
    fecha=df.fecha.iloc[300]
    x1=crear_features(df)
    fila1=x1[(x1.fecha==fecha)&(x1.horizonte==7)][CLUSTER_COLS].iloc[0].to_numpy()
    df.loc[df.fecha>fecha,'habitaciones_ocupadas']=0
    x2=crear_features(df)
    fila2=x2[(x2.fecha==fecha)&(x2.horizonte==7)][CLUSTER_COLS].iloc[0].to_numpy()
    assert np.allclose(fila1,fila2)

def test_horizontes():
    x=crear_features(cargar_datos())
    assert sorted(x.horizonte.unique().tolist())==[7,30]
    assert ((x.fecha_objetivo-x.fecha).dt.days==x.horizonte).all()

if __name__=='__main__':
    for test in [test_fechas_y_rangos,test_no_fuga_tras_modificar_el_futuro,test_horizontes]:
        test();print(test.__name__,'OK')
