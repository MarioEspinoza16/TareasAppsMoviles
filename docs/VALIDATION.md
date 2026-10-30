# Validación de MiBalance

Fecha: 30 de octubre de 2026. Verificación local automatizada; no representa una prueba en un teléfono físico.

## Resultado

La compilación limpia terminó con **BUILD SUCCESSFUL** usando JDK 17, Gradle 8.9, AGP 8.7.3, Kotlin 2.0.21 y Android SDK 35.

Comando reproducible en Windows:

```powershell
.\gradlew.bat clean testDebugUnitTest lintDebug assembleDebug
```

| Comprobación | Resultado observado |
| --- | --- |
| Pruebas de dominio y dinero | 10 aprobadas; importes exactos, montos inválidos, filtros, CRUD, saldo negativo y subtotal del vehículo |
| Pruebas Android | 12 aprobadas: seis pruebas en API 26/Android 8.0 y las mismas seis en API 35/Android 15 |
| Guardado desde la pantalla | Un ingreso escrito en el formulario se guarda y puede leerse desde el repositorio |
| Validación de mantenimiento | Se impide guardar sin describir el trabajo |
| Persistencia SQLite | Crear, cerrar y reabrir la base, editar y eliminar conserva el comportamiento esperado |
| Cinco pantallas | Layouts inflados sin error en ambos niveles Android de las pruebas |
| Recursos en español | Traducción comprobada automáticamente |
| Revisión visual virtual | Una prueba adicional renderiza las cinco pantallas Android en español con Robolectric, API 28 |
| Total de pruebas | **23 aprobadas, 0 fallidas, 0 omitidas** |
| Android Lint | **0 errores**, 11 advertencias: seis avisos de versiones más recientes de dependencias y cinco avisos de posible sobrepintado de fondo |
| APK debug | Generado y firma APK v2 verificada mediante apksigner |
| Compatibilidad declarada | minSdk 26 y targetSdk 35, comprobados dentro del APK con aapt |

Los avisos de dependencias no se resolvieron actualizando versiones indiscriminadamente: se conservó el conjunto con el que se realizó esta compilación. Los avisos de fondo no impiden la ejecución.

Los reportes completos se generan en `app/build/reports/` al ejecutar los comandos. Las vistas renderizadas para revisión se generan en `app/build/reports/screen-previews/`. Los datos ficticios se crean únicamente en el código de pruebas; el APK inicia sin movimientos precargados.

## Comprobaciones pendientes fuera de este entorno

- Instalar y ejecutar en el HONOR del estudiante.
- Comprobar cámara y selector de fotos del dispositivo, teclado y cambios de orientación durante el uso real.
- Comprobar descarga de mosaicos del mapa con la conexión del teléfono. La prueba virtual de diseño no demuestra que se hayan descargado mosaicos.
- Subir la rama y crear el PR cuando las herramientas autenticadas de GitHub estén disponibles.
- Verificar la invitación con permisos de escritura a ebarahonautn, su aceptación y la protección efectiva de main.
- Solicitar la revisión del profesor y entregar el enlace del PR en el campus. No hacer merge sin su aprobación.

El código, la documentación y los mockups incorporan las bases de las tareas 2 y 3 para dar continuidad a la 4. No se afirma que las tareas anteriores se entregaron previamente.
