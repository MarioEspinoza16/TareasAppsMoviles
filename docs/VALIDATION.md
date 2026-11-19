# Validación de MiBalance — Tarea 5

Desde PowerShell en la raíz del proyecto:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

En Linux se usa `./gradlew` con las mismas tareas. La configuración local del SDK y la red no forma parte del repositorio.

## Resultados — 19 de noviembre de 2026

- `testDebugUnitTest`: 11 pruebas de lógica aprobadas con JUnit, sin fallos.
- `lintDebug`: completado sin errores; el informe conserva advertencias no bloqueantes.
- `assembleDebug`: APK generado en `app/build/outputs/apk/debug/app-debug.apk`.
- `git diff --check`: sin errores de formato.

## Cobertura

- Montos, validaciones, categorías, totales y selección mensual del resumen.
- IDs consecutivos compartidos entre ingresos y gastos; actualización que conserva el ID; eliminación sin reutilizar IDs ni renumerar registros.
- Lista con 50 movimientos de distintas fechas y búsqueda exacta por ID.

Mario confirmó en su HONOR que el flujo actualizado funciona como esperaba. Ver [MANUAL_TESTS.md](MANUAL_TESTS.md). El repositorio sigue en memoria: finalizar el proceso borra los movimientos y reinicia la secuencia.

## Alcance de clase

Esta revisión entrega únicamente las pruebas JUnit de lógica y las herramientas Android utilizadas en los ejemplos compartidos hasta semana 9. El flujo principal fue confirmado por Mario en su HONOR. Las imágenes del README se renderizaron externamente para la documentación; el generador no es una dependencia de la aplicación.
