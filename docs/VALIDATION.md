# Validación de MiBalance — Tarea 5

Desde PowerShell en la raíz del proyecto:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

En Linux se usa `./gradlew` con las mismas tareas. La configuración local del SDK y la red no forma parte del repositorio.

## Resultados — 19 de noviembre de 2026

- `testDebugUnitTest`: 19 pruebas aprobadas (11 de JUnit y 8 de Robolectric), sin fallos.
- `lintDebug`: completado sin errores; el informe conserva advertencias no bloqueantes.
- `assembleDebug`: APK generado en `app/build/outputs/apk/debug/app-debug.apk`.
- `git diff --check`: sin errores de formato.

## Cobertura

- Montos, validaciones, categorías, totales y selección mensual del resumen.
- IDs consecutivos compartidos entre ingresos y gastos; actualización que conserva el ID; eliminación sin reutilizar IDs ni renumerar registros.
- Lista con 50 movimientos de distintas fechas y búsqueda exacta por ID.
- Acceso a Movimientos desde inicio y formulario; retorno a la lista al guardar; selección de una fila y apertura del detalle correcto.
- Confirmar/cancelar actualización y eliminación; recreación de la Activity en modo Nuevo; copia del ID de la fila correcta.
- Restablecimiento de la lista al volver después de guardar o eliminar un movimiento buscado.

El flujo actualizado todavía debe revisarse en el HONOR. Ver [MANUAL_TESTS.md](MANUAL_TESTS.md). El repositorio sigue en memoria: finalizar el proceso borra los movimientos y reinicia la secuencia.
