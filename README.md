# Smart Pool Security — Frontend

Interfaz web estática del proyecto. Este repositorio contiene únicamente HTML, CSS, JavaScript e imágenes; el backend Spring Boot se mantiene en el repositorio `Proyecto-Patrones-De-Software-Bakend`.

## Uso local

Abre `index.html` mediante un servidor estático local (por ejemplo, Live Server). La interfaz consulta la API configurada en `assets/app.js` y `assets/login.js`; por defecto usa `https://smart-pool-security.onrender.com`.

## Publicación en Render

Este repositorio incluye `render.yaml` para publicar los archivos como un Static Site gratuito. La URL de la interfaz será distinta a la del backend. El backend debe permitir el origen exacto del sitio en su variable `SMARTPOOL_CORS_ALLOWED_ORIGINS` (por ejemplo, `https://NOMBRE-DEL-SITIO.onrender.com`). No se debe usar `*` porque la sesión de acceso usa cookies.

El inicio de sesión, CSRF, cookies de sesión y alertas en tiempo real se comunican con el backend mediante HTTPS. El frontend no contiene credenciales ni se conecta directamente a PostgreSQL.

## Estado de la demostración

El panel presenta información de demostración. No hay cámaras físicas ni un modelo real de IA conectados.
