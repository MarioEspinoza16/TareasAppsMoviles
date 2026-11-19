# Pruebas manuales de MiBalance — Tarea 5

Usar datos ficticios. Comprobar el flujo en el HONOR; los mockups son diseños históricos, no evidencias de esta versión.

1. Abrir la app en una sesión nueva. Debe verse el botón Movimientos cerca de Agregar ingreso/gasto. Movimientos muestra una lista vacía, búsqueda por ID arriba y Nuevo movimiento abajo.
2. Agregar ingreso de ₡25 000 con nombre `Trabajo del día`. Al guardar, se abre Movimientos y aparece `ID: 1` junto al nombre.
3. Agregar gasto de ₡12 500,50 con nombre `Gasolina Barranca`. Debe regresar a la lista con IDs 1 y 2. Ingresos y gastos comparten la misma secuencia.
4. Agregar un movimiento con fecha del año pasado. Debe aparecer también en Movimientos, con ID 3. La lista está ordenada por ID y no se limita al mes actual.
5. Escribir `2` en la búsqueda y tocar Buscar ID: debe aparecer únicamente Gasolina Barranca. Tocar Ver todos: deben volver los tres registros. Probar un ID inexistente y búsqueda vacía.
6. Seleccionar la fila de ID 2. El detalle debe mostrar el ID, nombre, categoría, fecha, monto y botones Editar y Eliminar.
7. Editar el monto a ₡10 000. Cancelar la confirmación: debe conservar ₡12 500,50. Repetir y confirmar: vuelve a Movimientos y conserva ID 2 sin duplicarlo. Volver al resumen: saldo del mes ₡15 000 si el movimiento del año pasado no pertenece al mes actual.
8. Seleccionar ID 2 y tocar Eliminar. Cancelar: debe permanecer. Confirmar: desaparece. Al regresar, la lista debe mostrar los movimientos restantes aunque se hubiera buscado ese ID.
9. Crear otro movimiento: debe recibir ID 4. No se debe reutilizar 2 ni cambiar el ID 3. Probar eliminar desde el formulario de edición y desde detalle.
10. Confirmar que los movimientos recientes del resumen también muestran ID y nombre. Copiar ID debe copiar solo el número y no abrir el detalle; tocar la fila sí lo abre.
11. Probar nombre vacío, monto cero o inválido, mantenimiento/reparación sin descripción y litros inválidos: no se deben guardar datos inválidos.
12. Probar Nuevo movimiento desde edición, girar el teléfono antes de guardar y comprobar que no sobrescribe el movimiento anterior.
13. Registrar o editar desde la lista después de una búsqueda: al guardar debe volver a mostrar todos los movimientos.
14. Probar los textos en español e inglés. El código conserva identificadores en inglés.
15. Finalizar el proceso desde ajustes de Android y volver a abrir: registros y contador se reinician porque esta entrega conserva el repositorio en memoria.
