# Plan de Rediseño UI/UX: Hotel Luxury (Preline UI + Tailwind CSS)

## 1. Definición de la Nueva Versión
El cambio total de interfaz, plantillas y migración de framework CSS (de Bootstrap a Tailwind/Preline) es un salto arquitectónico mayor para el frontend. 
- **Versión propuesta:** `v1.4.0 (Rediseño UI/UX)`. *(Nota: Si consideras que el cambio visual es tan grande que parece un sistema nuevo, podríamos saltar directamente a `v2.0.0`, pero técnicamente `v1.4.0` es lo más correcto al no tocar el backend).*

**Directorios que se actualizarán:**
- `frontend/package.json` y `angular.json` (Migración de librerías).
- `frontend/tailwind.config.js` (Nuevo archivo de configuración de diseño).
- `frontend/src/styles.css` (Capa base de Tailwind).
- Todas las vistas en `frontend/src/app/modules/` (Layouts, Login, Dashboard, etc.).

---

## 2. Recomendación de Estilos (Basado en Refero Design)

Revisando los patrones top de **Refero Design** y considerando que nuestra marca ahora es **"Hotel Luxury"**, te recomiendo aplicar una combinación de estas **dos tendencias top**:

1. **Clean & Minimal SaaS (Apple-like / Linear Style):**
   - **Por qué:** Da un aspecto extremadamente premium y moderno. Usa mucho espacio en blanco, bordes súper sutiles (gris claro), fuentes geométricas puras (como *Inter* o *Outfit*) y elimina las "cajas" innecesarias.
2. **Bento Box Grid (Para el Dashboard):**
   - **Por qué:** En lugar de tener las clásicas tarjetas cuadradas aburridas de Bootstrap, el "Bento Box" (popularizado por Apple) organiza los KPIs (Ocupación, Ingresos, Reservas) en una cuadrícula asimétrica y atractiva. Preline UI tiene componentes excelentes para esto.

> **Colores recomendados:** Fondo gris ultra claro (`bg-slate-50`), tarjetas blancas con sombra microscópica (`shadow-sm`), y acentos en un azul profundo de lujo (`text-slate-900` o `indigo-600`) u Oro oscuro si quieres un toque muy clásico.

---

> [!IMPORTANT]
> **User Review Required:** Revisa las siguientes preguntas para definir el alcance antes de que yo comience a programar los cambios.

## Open Questions

1. **¿Eliminamos Bootstrap por completo?**
   Actualmente usas Bootstrap 5. Tener Tailwind CSS (Preline) y Bootstrap al mismo tiempo causa conflictos visuales severos y hace que la web sea pesada. **(Recomendado: Eliminar Bootstrap y usar 100% Tailwind/Preline).**
2. **¿Versión `v1.4.0` o saltamos a la `v2.0.0`?**
   ¿Cómo quieres que etiquetemos este hito en el repositorio?
3. **Preferencia de Tema:**
   ¿Prefieres el estilo **Clean & Minimal (Claro)** o un **Modo Oscuro Premium (Dark Mode)** estilo dashboard nocturno?

---

## Proposed Changes

### [NEW] Configuración de Tailwind CSS y Preline
Instalaremos las herramientas de diseño modernas.
- **[NEW]** `frontend/tailwind.config.js`
- **[NEW]** `frontend/postcss.config.js`

### [MODIFY] Archivos de Configuración (Frontend)
- **`frontend/package.json`**: Se añadirá `tailwindcss`, `postcss`, `autoprefixer`, y `preline`. Se eliminará `bootstrap`.
- **`frontend/angular.json`**: Se removerán los scripts y estilos viejos de Bootstrap.
- **`frontend/src/styles.css`**: Se inyectarán las directivas de Tailwind (`@tailwind base; @tailwind components; @tailwind utilities;`).

### [MODIFY] Vistas del Sistema (Refactorización a Preline UI)
- **`frontend/src/index.html`**: Agregaremos la fuente *Inter* de Google Fonts para ese look SaaS moderno.
- **`frontend/src/app/modules/layout/navbar/navbar.html`**: Rediseño usando el componente "Header" de Preline (transparente, con efecto blur/glassmorphism).
- **`frontend/src/app/modules/layout/sidebar/sidebar.html`**: Rediseño a una barra lateral elegante con íconos flotantes.
- **`frontend/src/app/modules/auth/login/login.html`**: Un login "Split-screen" (Mitad formulario limpio, mitad imagen de hotel de lujo).
- **`frontend/src/app/modules/dashboard/dashboard.html`**: Implementación de la cuadrícula "Bento Box" para las estadísticas.

## Verification Plan
1. **Instalación:** Verificar que `npm install` pase sin errores.
2. **Compilación:** Correr `npm start` para confirmar que el compilador de Angular procesa el PostCSS y Tailwind correctamente.
3. **Visual:** Pedirte que abras el navegador y valides que el "look and feel" es digno de un producto comercial de alta gama según los estándares de Refero Design.
