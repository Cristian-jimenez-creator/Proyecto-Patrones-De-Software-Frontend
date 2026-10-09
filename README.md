# Smart Pool Security

Smart Pool Security es un prototipo académico para apoyar la supervisión de piscinas. Su backend Spring Boot administra piscinas, cámaras de demostración, alertas, usuarios y estadísticas. La interfaz web de demostración se sirve desde el propio backend.

> **Estado y seguridad:** los datos y análisis de cámaras son de demostración. El sistema no se conecta a cámaras reales ni a un modelo de inteligencia artificial real, y no debe reemplazar a salvavidas ni a protocolos de emergencia.

## Qué contiene el repositorio

- `smart-pool-security (2)/backend/`: aplicación Spring Boot, API REST y recursos de la interfaz web.
- `Dockerfile`: imagen que construye el backend desde la carpeta anterior para Render.
- `modelo_piscina.sql`: modelo relacional de referencia diseñado para el proyecto. No es una migración automática del esquema JPA actual.
- `README.md`: descripción y guía rápida.

El diagrama de arquitectura muestra un cliente JavaFX como parte del diseño previsto. **El código JavaFX no está incluido en esta versión del repositorio**; el cliente disponible aquí es la página web servida por Spring Boot.

## Funciones disponibles en la demo

- Consultar piscinas y cámaras, y cambiar el estado de una cámara.
- Consultar un análisis simulado por cámara.
- Consultar, filtrar y atender alertas; añadir observaciones y generar una alerta crítica de prueba.
- Recibir eventos de alertas críticas mediante Server-Sent Events (SSE).
- Consultar estadísticas y administrar los usuarios que aparecen en los datos de demostración.
- Proteger la página desplegada con inicio de sesión, usando secretos configurados en Render.

La integración con vídeo de cámaras, un modelo de IA real, notificaciones externas, roles de autorización por usuario y todas las funciones del documento de requisitos todavía no están implementados.

## Ejecutar el backend localmente

Requisitos: JDK 17 y Maven.

En PowerShell, desde la raíz del repositorio:

```powershell
cd "smart-pool-security (2)\backend"
mvn spring-boot:run
```

Abre `http://localhost:8080`. La configuración local usa H2 en memoria y deja la autenticación desactivada por defecto. Los datos se reinician al detener la aplicación.

## Despliegue

El servicio está publicado en [Render](https://smart-pool-security.onrender.com). El chequeo de salud está en [`/health`](https://smart-pool-security.onrender.com/health). Las rutas de la aplicación requieren iniciar sesión en el despliegue.

Render usa PostgreSQL y las variables de entorno definidas en su panel. No guardes contraseñas ni claves en este repositorio. El servicio gratuito puede dormir por inactividad y su base de datos gratuita tiene límites de duración y almacenamiento; consulta los [límites actuales de Render](https://render.com/docs/free).

## API y documentación

- [Wiki del proyecto](https://github.com/Cristian-jimenez-creator/Proyecto-final-Patrones-De-Software/wiki)
- [API REST](https://github.com/Cristian-jimenez-creator/Proyecto-final-Patrones-De-Software/wiki/API-REST)
- [Arquitectura y estado](https://github.com/Cristian-jimenez-creator/Proyecto-final-Patrones-De-Software/wiki/Arquitectura)
- [Modelo de datos](https://github.com/Cristian-jimenez-creator/Proyecto-final-Patrones-De-Software/wiki/Modelo-de-datos)
- [Ejecución local](https://github.com/Cristian-jimenez-creator/Proyecto-final-Patrones-De-Software/wiki/Desarrollo-local)
- [Despliegue en Render](https://github.com/Cristian-jimenez-creator/Proyecto-final-Patrones-De-Software/wiki/Despliegue-en-Render)
- [Alcance y requisitos](https://github.com/Cristian-jimenez-creator/Proyecto-final-Patrones-De-Software/wiki/Alcance-y-requisitos)

## En porceso
Está hecho con Spring Boot y recibe las solicitudes del frontend mediante una API REST.
Procesa operaciones como consultar piscinas, cámaras y alertas.
Guarda y consulta datos en PostgreSQL cuando está en Render; localmente se usa H2.
El análisis de IA y las cámaras son simulados en la versión actual; todavía no detecta emergencias reales.



## Equipo 

- Cristian Alexis Jiménez Bastidas
- Armando Hernandes
