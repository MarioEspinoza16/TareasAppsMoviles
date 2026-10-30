## Problema y resultado

MiBalance permite registrar ingresos y gastos personales y del vehículo para consultar un saldo mensual. Esta entrega agrega las cinco Activities y sus layouts XML y construye las bases pendientes de definición, mockups, modelos, interfaces, repositorio en memoria y ViewModels.

## Cambios

- Resumen mensual, historial con filtros, creación/edición, detalle y ubicación.
- Categorías para ingresos, gasolina, mantenimiento, reparaciones, comidas, salidas y otros gastos.
- Mantenimiento y reparaciones con descripción obligatoria.
- Cámara y galería para comprobantes; mapa OpenStreetMap con ubicación manual opcional.
- Persistencia SQLite local además de MemoryDataRepository.
- Recursos completos en inglés y español; código en inglés.
- README, mockups, pruebas y guía de instalación y entrega.

## Validación

Consultar `docs/VALIDATION.md` para los resultados exactos de compilación, pruebas y lint de esta versión. La prueba física en el HONOR debe registrarse después de ejecutarla.

## Límites y revisión

El mapa necesita internet para descargar mosaicos. No hay sincronización, exportación ni recuperación de datos tras desinstalar. No hay integración con Uber ni rastreo GPS.

Las bases de tareas 2 y 3 se completaron como parte de la preparación actual. Este PR no representa entregas anteriores. Solicito revisión de ebarahonautn antes del merge a main.
