# Smart Pool Security — versión web para Render

El proyecto incluye una interfaz web adaptable a celular y escritorio, backend Spring Boot, inicio de sesión y PostgreSQL. Las credenciales de acceso se configuran como secretos en Render; no están guardadas en estos archivos.

## Publicación

1. Sube esta carpeta a un repositorio **privado** de GitHub. Conserva `Dockerfile` y `render.yaml` en la raíz.
2. En Render, crea un Blueprint desde ese repositorio. Render leerá `render.yaml` y pedirá el usuario y la contraseña de Smart Pool.
3. Elige una contraseña única de al menos 16 caracteres. Render publicará la página en una dirección `onrender.com` y servirá la interfaz y la API desde el mismo sitio.

El servicio se configura con el plan gratuito y región de Virginia. Tras 15 minutos sin visitas, Render duerme el servicio; la próxima visita lo reactiva y puede tardar alrededor de un minuto. El PostgreSQL gratuito vence a los 30 días; si no se actualiza dentro del plazo de gracia, Render elimina la base y sus datos. La base tiene límite de 1 GB. Es adecuado para probar, no para conservar datos a largo plazo. [Límites del plan gratuito de Render](https://render.com/docs/free).

## Contenido de demostración

Los datos iniciales son ficticios. El panel de cámaras, el análisis de IA, las detecciones y el flujo de alertas son simulados: estos ZIP no conectan cámaras ni incluyen un modelo de IA real. La base PostgreSQL evita que se reinicien los cambios mientras esté activa.

La cuenta de inicio de sesión del sitio es independiente de la lista de usuarios que aparece dentro del panel. El usuario y la contraseña del sitio se definen en el asistente de Render y no se muestran en la aplicación.

## Ejecución local

El iniciador local de Windows (`iniciar.ps1`) y el cliente JavaFX original siguen incluidos en `desktop/`. La versión de navegador se sirve desde Spring Boot; al ejecutarla localmente, `SMARTPOOL_AUTH_ENABLED` queda desactivado para que el cliente JavaFX original pueda seguir usando la API.
