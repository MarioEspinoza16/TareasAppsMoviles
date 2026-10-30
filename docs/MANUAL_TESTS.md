# Recorrido de prueba en el dispositivo

Usar datos ficticios para comprobar el funcionamiento antes de ingresar información personal.

| Paso | Acción | Resultado esperado |
| --- | --- | --- |
| 1 | Abrir la aplicación recién instalada | Totales en cero, sin movimientos precargados |
| 2 | Cambiar el idioma a español y luego inglés | Etiquetas y mensajes traducidos; datos conservados |
| 3 | Registrar ingreso de ₡30 000 del día actual | Aparece en resumen e historial |
| 4 | Registrar gasolina de ₡8 000, litros y kilometraje opcionales | Gastos ₡8 000; vehículo ₡8 000; saldo ₡22 000 |
| 5 | Registrar comida de ₡3 000 | Gastos ₡11 000; vehículo sigue en ₡8 000; saldo ₡19 000 |
| 6 | Elegir mantenimiento sin descripción y guardar | El formulario pide describir el trabajo |
| 7 | Guardar mantenimiento de ₡5 000 con descripción | Gastos ₡16 000; vehículo ₡13 000; saldo ₡14 000 |
| 8 | Registrar otro gasto en el mes anterior | No altera el resumen del mes actual |
| 9 | Filtrar por mes, tipo y categoría | Solo aparecen los movimientos correspondientes |
| 10 | Editar gasolina a ₡9 000 | Se actualiza el mismo movimiento, sin duplicarlo |
| 11 | Adjuntar foto desde galería y desde cámara | Vista previa; foto disponible al reabrir el detalle |
| 12 | Cancelar selección de imagen o cámara | Formulario utilizable sin adjunto nuevo |
| 13 | Abrir mapa, tocar un punto y confirmar | Coordenadas guardadas; visibles desde el detalle |
| 14 | Sin internet, escribir coordenadas válidas | Se puede confirmar aunque no carguen mosaicos |
| 15 | Ingresar cero, texto, monto negativo o tres decimales | No se guarda un monto inválido |
| 16 | Cancelar un diálogo de eliminación | Movimiento conservado |
| 17 | Confirmar eliminación | Movimiento y su comprobante eliminados; totales actualizados |
| 18 | Cerrar el proceso y volver a abrir | Los registros guardados permanecen |
| 19 | Girar la pantalla durante la edición, sin importar una foto | Se conservan campos, fecha y selección |
| 20 | Usar teclado abierto y letra grande | Se puede desplazar el formulario y alcanzar Guardar |
| 21 | Agregar gastos superiores a ingresos | Se muestra saldo negativo correctamente |

La prueba física de cámara, permisos del sistema, orientación y visualización en HONOR queda a cargo de la ejecución real en ese dispositivo. No marcarla como realizada solo por pasar pruebas unitarias.
