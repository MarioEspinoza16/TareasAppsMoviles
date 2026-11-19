# Validación de MiBalance — Tarea 5

Comando desde la raíz del proyecto en PowerShell:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

En Linux se usa `./gradlew` con las mismas tareas. La configuración local del SDK y la red no forma parte del repositorio.

## Cobertura

- JUnit: montos decimales, validaciones, totales, categorías, filtros, actualización sin duplicados, eliminación y búsqueda por descripción/ID entre meses.
- Robolectric (Android API 28): CRUD del formulario, selección de resultados, confirmación/cancelación de actualización y eliminación, eliminación desde detalle recreación de la Activity después de cambiar a modo Nuevo y visualización/copia del ID correcto al reutilizar filas del listado.
- Android Lint y compilación del APK de depuración.

## Resultados — 19 de noviembre de 2026

- `testDebugUnitTest`: 16 pruebas aprobadas (10 de JUnit y 6 de Robolectric), sin fallos.
- `lintDebug`: completado sin errores. Las advertencias del informe no bloquean la compilación.
- `assembleDebug`: compilación correcta; APK generado en `app/build/outputs/apk/debug/app-debug.apk`.
- `git diff --check`: sin errores de formato.

El recorrido en un teléfono físico sigue pendiente y se detalla en [MANUAL_TESTS.md](MANUAL_TESTS.md).
