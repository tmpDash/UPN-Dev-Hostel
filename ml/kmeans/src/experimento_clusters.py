"""Extensión demostrativa de clustering de ocupación para Hotel Luxury.
No reemplaza el dataset ni las métricas del repositorio original.
Ejecutar desde la raíz del paquete: python src/experimento_clusters.py
"""
from pathlib import Path
import json
import numpy as np
import pandas as pd
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import silhouette_score, mean_absolute_error, mean_squared_error
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler

ROOT=Path(__file__).resolve().parents[1]
DATA=ROOT/'data'; REPORTS=ROOT/'reports'
DATA.mkdir(exist_ok=True); REPORTS.mkdir(exist_ok=True)
RANDOM_STATE=42
CLUSTER_COLS=['ocup_lag1','ocup_media_7','ocup_media_28','ocup_std_7','ocup_diff_7','dia_semana_sin','dia_semana_cos']
BASE_COLS=CLUSTER_COLS+['mes_sin','mes_cos','horizonte']

def generar_demo():
    """Serie ficticia INDEPENDIENTE, NO es el dataset del informe de 12 282 reservas."""
    rng=np.random.default_rng(RANDOM_STATE)
    dias=pd.date_range('2024-01-01','2026-10-31',freq='D')
    dow=dias.dayofweek.to_numpy(); doy=dias.dayofyear.to_numpy()
    temporada=15*np.sin(2*np.pi*doy/365.25-0.7)
    semanal=9*np.cos(2*np.pi*(dow-5)/7)
    eventos=np.where(((doy%80)<5),12,0)
    ocup=np.clip(np.rint(105+temporada+semanal+eventos+rng.normal(0,8,len(dias))),5,180).astype(int)
    df=pd.DataFrame({'fecha':dias,'habitaciones_ocupadas':ocup,'capacidad':180})
    df.to_csv(DATA/'ocupacion_diaria_demo_independiente.csv',index=False)
    return df

def cargar_datos(ruta=None):
    if ruta:
        df=pd.read_csv(ruta,parse_dates=['fecha'])
        requeridas={'fecha','habitaciones_ocupadas'}
        if not requeridas.issubset(df.columns):
            raise ValueError(f'CSV requiere columnas {sorted(requeridas)}')
    else:
        df=generar_demo()
    df=df.sort_values('fecha').drop_duplicates('fecha')
    if df['habitaciones_ocupadas'].isna().any():raise ValueError('Ocupación faltante')
    if len(df)<365:raise ValueError('Se necesitan al menos 365 días para este ejercicio')
    if not (df['fecha'].diff().dropna()==pd.Timedelta(days=1)).all():
        raise ValueError('La serie debe ser diaria y consecutiva')
    return df.reset_index(drop=True)

def crear_features(df):
    s=df['habitaciones_ocupadas'].astype(float)
    f=pd.DataFrame({'fecha':df.fecha,'ocup_lag1':s.shift(1),
        'ocup_media_7':s.shift(1).rolling(7).mean(),
        'ocup_media_28':s.shift(1).rolling(28).mean(),
        'ocup_std_7':s.shift(1).rolling(7).std(),
        'ocup_diff_7':s.shift(1)-s.shift(8)})
    f['dia_semana_sin']=np.sin(2*np.pi*f.fecha.dt.dayofweek/7)
    f['dia_semana_cos']=np.cos(2*np.pi*f.fecha.dt.dayofweek/7)
    f['mes_sin']=np.sin(2*np.pi*f.fecha.dt.month/12)
    f['mes_cos']=np.cos(2*np.pi*f.fecha.dt.month/12)
    f=f.dropna().reset_index(drop=True)
    # cada X(fecha_consulta) usa solo ocupación <= fecha_consulta-1
    base=df.set_index('fecha')['habitaciones_ocupadas']
    frames=[]
    for h in (7,30):
        g=f.copy();g['horizonte']=h
        g['fecha_objetivo']=g.fecha+pd.to_timedelta(h,unit='D')
        g['objetivo']=g.fecha_objetivo.map(base)
        frames.append(g.dropna(subset=['objetivo']))
    return pd.concat(frames,ignore_index=True).sort_values(['fecha','horizonte']).reset_index(drop=True)

def experimento(ruta=None):
    d=crear_features(cargar_datos(ruta))
    # separación por FECHA DE CONSULTA; los targets entrenables tienen que
    # estar conocidos antes del inicio de cada bloque (embargo de h días).
    fechas=sorted(d.fecha.unique()); n=len(fechas)
    corte_val=fechas[int(n*0.65)]; corte_test=fechas[int(n*0.82)]
    resultados=[]; patrones=[]; perfiles=[]; asignaciones=[]
    for h in (7,30):
        q=d[d.horizonte==h].copy()
        train=q[q.fecha+pd.Timedelta(days=h)<corte_val].copy()
        val=q[(q.fecha>=corte_val)&(q.fecha<corte_test)].copy()
        trainval=q[q.fecha+pd.Timedelta(days=h)<corte_test].copy()
        test=q[q.fecha>=corte_test].copy()
        # selección de k SOLO usando datos previos al periodo de validación
        scaler=StandardScaler().fit(train[CLUSTER_COLS])
        Xtr=scaler.transform(train[CLUSTER_COLS]);sil=[]
        for k in (2,3,4,5):
            km=KMeans(n_clusters=k,random_state=RANDOM_STATE,n_init=10).fit(Xtr)
            sil.append((k,silhouette_score(Xtr,km.labels_)))
        kopt=max(sil,key=lambda x:x[1])[0]
        for k,score in sil: patrones.append({'horizonte':h,'k':k,'silhouette_train':score,'seleccionado':k==kopt})
        # Clusters generados sin observar variable objetivo futura.
        scaler=StandardScaler().fit(trainval[CLUSTER_COLS]);km=KMeans(n_clusters=kopt,random_state=RANDOM_STATE,n_init=10)
        km.fit(scaler.transform(trainval[CLUSTER_COLS]))
        for part in (trainval,test):
            part['cluster']=km.predict(scaler.transform(part[CLUSTER_COLS]))
        for c,group in trainval.groupby('cluster'):
            perfiles.append({'horizonte':h,'cluster':int(c),'dias_train':len(group),
                'ocup_media_7':round(group.ocup_media_7.mean(),2),'variabilidad_7':round(group.ocup_std_7.mean(),2)})
        test['pred_baseline_semanal']=test.ocup_lag1 # no usamos referencia del futuro
        # mismo RF e hiperparámetros; única diferencia es variable segmentación
        for nombre,cols in [('RF_sin_clustering',BASE_COLS),('RF_con_clustering',BASE_COLS+['cluster'])]:
            modelo=RandomForestRegressor(n_estimators=100,max_depth=10,min_samples_leaf=5,random_state=RANDOM_STATE,n_jobs=-1)
            modelo.fit(trainval[cols],trainval.objetivo)
            pred=modelo.predict(test[cols])
            resultados.append({'horizonte':h,'modelo':nombre,'MAE_habitaciones':mean_absolute_error(test.objetivo,pred),
                'RMSE_habitaciones':np.sqrt(mean_squared_error(test.objetivo,pred)),
                'sesgo_habitaciones':np.mean(pred-test.objetivo),'n_test':len(test)})
            if nombre=='RF_con_clustering':
                test['pred_con_clustering']=pred
        for _,row in test.iterrows():
            asignaciones.append({'fecha_consulta':str(row.fecha.date()),'horizonte':h,'cluster':int(row.cluster),
                'real':row.objetivo,'pred_con_clustering':row.pred_con_clustering})
    resumen=pd.DataFrame(resultados).round(4)
    clusters=pd.DataFrame(patrones).round(4)
    perfiles=pd.DataFrame(perfiles)
    resumen.to_csv(REPORTS/'metricas_comparativas.csv',index=False)
    clusters.to_csv(REPORTS/'seleccion_clusters.csv',index=False)
    perfiles.to_csv(REPORTS/'perfiles_clusters.csv',index=False)
    pd.DataFrame(asignaciones).to_csv(REPORTS/'predicciones_prueba.csv',index=False)
    fig,ax=plt.subplots(figsize=(8,4))
    for modelo,g in resumen.groupby('modelo'):
        ax.plot(g.horizonte,g.MAE_habitaciones,'o-',label=modelo)
    ax.set_xticks([7,30]);ax.set_xlabel('Horizonte (días)');ax.set_ylabel('MAE (habitaciones)');ax.set_title('Prueba temporal: efecto de añadir cluster al Random Forest');ax.legend();fig.tight_layout()
    fig.savefig(REPORTS/'comparacion_mae.png',dpi=140);plt.close(fig)
    fig,ax=plt.subplots(figsize=(8,4))
    for h,g in clusters.groupby('horizonte'):
        ax.plot(g.k,g.silhouette_train,'o-',label=f'{h} días')
    ax.set_xlabel('Número de clusters (k)');ax.set_ylabel('Silhouette (solo entrenamiento)')
    ax.set_title('Selección de k sin utilizar datos de prueba');ax.set_xticks([2,3,4,5]);ax.legend();fig.tight_layout()
    fig.savefig(REPORTS/'silhouette.png',dpi=140);plt.close(fig)
    mejora={}
    for h in (7,30):
        z=resumen[resumen.horizonte==h].set_index('modelo')
        a=z.loc['RF_sin_clustering','MAE_habitaciones'];b=z.loc['RF_con_clustering','MAE_habitaciones']
        mejora[str(h)]=round(100*(a-b)/a,3)
    info={'origen':'demo_independiente' if ruta is None else str(ruta),'nota':'No corresponde al experimento original del repositorio',
          'semilla':RANDOM_STATE,'mejora_pct_por_horizonte':mejora,'k_por_horizonte':{str(h):int(g[g.seleccionado].k.iloc[0]) for h,g in clusters.groupby('horizonte')}}
    (REPORTS/'resumen.json').write_text(json.dumps(info,ensure_ascii=False,indent=2),encoding='utf-8')
    print(resumen.to_string(index=False));print('\nSelección de k:\n',clusters[clusters.seleccionado].to_string(index=False))
    print('\nMejora relativa en MAE (%):',mejora)
    return resumen,clusters,perfiles

if __name__=='__main__':
    experimento()
