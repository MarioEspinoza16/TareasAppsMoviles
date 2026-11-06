# Validación de MiBalance

La revisión ajustada a semana 7 usa JUnit para comprobar el manejo de montos, el cálculo del saldo, los filtros y las operaciones del repositorio en memoria.

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

La compilación, JUnit y Android Lint pasaron en la [ejecución de verificación](https://github.com/MarioEspinoza16/TareasAppsMoviles/actions/runs/42258646014) del commit `69e0614`. La revisión final conserva el mismo código de la app y retira el flujo de GitHub Actions.

El recorrido en el teléfono se describe en [Pruebas manuales](MANUAL_TESTS.md).
