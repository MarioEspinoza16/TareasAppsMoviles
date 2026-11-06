# Instalar, revisar y entregar MiBalance

## A. Abrir el proyecto en tu computadora

1. Descargá el ZIP del proyecto y extraelo en una carpeta nueva, por ejemplo `C:\Users\HP\Documents\Mario UTN\app_moviles\MiBalance`.
2. En Android Studio elegí **Open** y seleccioná la carpeta que contiene `settings.gradle.kts`, no solamente la carpeta `app`.
3. Esperá a que finalice Gradle Sync. Aceptá instalar el SDK 35 si Android Studio lo pide.
4. En la configuración de Gradle seleccioná JDK 17. No hace falta modificar tu instalación global de Java.
5. Seleccioná el módulo `app` para ejecutar.

Si trabajás desde un clon existente de TareasAppsMoviles, revisá primero `git status`. Con el árbol limpio, actualizá la rama de la tarea mediante `git switch task4-MiBalance-MarioEspinoza` y `git pull --ff-only`.

## B. Ejecutar en el HONOR

1. Confirmá la versión de Android en Ajustes → Acerca del teléfono. Se requiere Android 8.0 o superior.
2. Activá las opciones de desarrollador tocando varias veces el número de compilación; la ubicación y el nombre pueden variar en MagicOS.
3. En Opciones de desarrollador activá **Depuración USB**.
4. Conectá el teléfono con un cable USB que transfiera datos y aceptá la autorización de depuración en el teléfono.
5. Seleccioná el teléfono en Android Studio y presioná Run.
6. Si no aparece, probá el modo de transferencia de archivos, otro cable y el controlador USB correspondiente a tu equipo.

También podés transferir el APK de prueba al teléfono y abrirlo. Android puede solicitar autorización para instalar desde la aplicación con la que lo abriste. Esta app no necesita que habilités esa autorización permanentemente.

Si primero instalás el APK recibido y luego ejecutás otra compilación desde Android Studio, las firmas de prueba pueden ser diferentes. Android puede exigir desinstalar la anterior; eso borra sus registros. Usá datos ficticios durante esta etapa.

## C. Validación antes de entregar

Realizá el recorrido de `MANUAL_TESTS.md`. Tomá tus propias capturas de la app ejecutada para evidencias si el profesor las solicita. Los mockups no deben presentarse como evidencia de ejecución.

## D. Subir a tu repositorio sin reemplazar main

Si los cambios todavía están solo en tu computadora:

```powershell
git status
git switch -c task4-MiBalance-MarioEspinoza
git add .
git commit -m "Build MiBalance finance app and task 4 layouts"
git push -u origin task4-MiBalance-MarioEspinoza
```

Si la rama ya existe, usá `git switch task4-MiBalance-MarioEspinoza` y no repitas `git switch -c`. Verificá antes de usar los comandos que el remoto apunta a `https://github.com/MarioEspinoza16/TareasAppsMoviles.git` y que el directorio corresponde al proyecto correcto.

No subás `local.properties`, `.idea`, `.gradle`, carpetas `build`, claves de firma ni credenciales. El `.gitignore` ya los excluye.

## E. Configuración remota requerida

1. Desde los ajustes del repositorio, invitá a **ebarahonautn** como colaborador con permisos de escritura. La invitación debe ser aceptada para completar el acceso.
2. Creá una regla de protección o ruleset para `main`: exigir Pull Request y al menos una aprobación, sin permitir saltarse la revisión a quien entrega.
3. El objetivo es bloquear los cambios directos a `main` y permitir el merge mediante PR aprobado. No actives una opción que bloquee también todos los merges.
4. Verificá que el plan y la visibilidad del repositorio permitan aplicar la regla. No la des por activa solo porque se pudo guardar una configuración.

Estas acciones no quedan confirmadas por la existencia del código local. Deben comprobarse en GitHub.

## F. Pull Request y campus

- Base: `main`.
- Rama: `task4-MiBalance-MarioEspinoza`.
- Título: `task4-MiBalance-MarioEspinoza`.
- Descripción: copiar y ajustar `PR_DESCRIPTION.md` con los resultados reales.
- Reviewer: **ebarahonautn**, una vez que tenga acceso.
- Entrega en el campus: **URL del PR**, no solo el enlace general del repositorio.
- No hacer merge hasta contar con la aprobación del profesor.

El acceso inicial y la regla de protección son requisitos de la tarea 2 que deben completarse ahora. No se deben falsificar fechas de commits ni afirmar que las tareas anteriores se entregaron a tiempo.
