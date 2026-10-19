// Set this to the public URL of the separately deployed backend on Render.
const isLocalSmartPool = ['localhost', '127.0.0.1'].includes(window.location.hostname);
window.SMARTPOOL_API_URL = isLocalSmartPool
  ? 'http://localhost:8080'
  : 'https://REEMPLAZA-POR-LA-URL-DEL-BACKEND.onrender.com';
