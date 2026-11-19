# Pruebas manuales de MiBalance — Tarea 5

Usar datos ficticios. Las pruebas del teléfono se deben realizar en el HONOR o un emulador; no confundir los mockups con capturas de ejecución.

1. Crear un ingreso de ₡25 000 en el mes actual. Después de guardar, el formulario debe mostrar su ID, el botón Actualizar y la opción Eliminar.
2. Tocar Nuevo movimiento. Crear un gasto de gasolina de ₡12 500,50 con descripción `Gasolina Barranca`. Volver al resumen: el saldo debe ser ₡12 499,50.
3. Desde el formulario, buscar `barranca`. Seleccionar el resultado. Debe cargar monto, tipo, categoría, fecha, descripción, kilometraje y litros del movimiento correcto. Buscar también por su ID, que se puede seleccionar y copiar en el formulario.
4. Cambiar el monto a ₡10 000 y tocar Actualizar. Cancelar el diálogo: al consultar de nuevo debe seguir en ₡12 500,50. Repetir y confirmar: debe quedar en ₡10 000, con el mismo ID y sin duplicados; saldo mensual ₡15 000.
5. Tocar Eliminar y cancelar: el movimiento debe continuar. Repetir y confirmar: el formulario pasa a Nuevo, el ID anterior desaparece y el historial/resumen reflejan la eliminación al volver.
6. Abrir otro movimiento desde el historial. En detalle, Eliminar debe mostrar un diálogo: cancelar conserva el registro; confirmar lo elimina.
7. Probar búsqueda vacía (validación), inexistente (no encontrado), con mayúsculas, con espacios alrededor y con varios resultados. Los resultados incluyen fecha, categoría, monto, descripción e ID. Cancelar la selección no cambia el formulario ni los datos.
8. Registrar otro movimiento en un mes diferente y buscarlo desde el formulario: la búsqueda no se limita al mes del historial.
9. Probar monto cero, negativo o con más de dos decimales; mantenimiento/reparación sin descripción; litros inválidos. No deben guardarse datos inválidos ni mostrarse una confirmación de actualización hasta corregirlos.
10. Cargar un movimiento, tocar Nuevo y crear otro: el anterior debe conservarse. Repetir girando el teléfono antes de guardar; repetir girándolo mientras se edita un movimiento buscado.
11. Revisar filtros de mes, tipo y categoría y probar el formulario en español e inglés.
12. Finalizar el proceso desde ajustes de Android y volver a abrir: el historial queda vacío porque esta entrega conserva datos en memoria.
