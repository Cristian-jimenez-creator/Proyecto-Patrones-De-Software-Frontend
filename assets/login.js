const form = document.querySelector('#login-form');
const API_BASE_URL = (window.SMARTPOOL_API_BASE || 'https://smart-pool-security.onrender.com').replace(/\/$/, '');
const errorBox = document.querySelector('#login-error');
const logoutNote = document.querySelector('#logged-out');
const button = document.querySelector('#login-button');
logoutNote.hidden = !new URLSearchParams(location.search).has('loggedOut');
errorBox.hidden = !new URLSearchParams(location.search).has('error');

form.addEventListener('submit', async event => {
  event.preventDefault();
  button.disabled = true;
  button.textContent = 'Conectando…';
  try {
    const tokenResponse = await fetch(`${API_BASE_URL}/api/csrf`, { credentials: 'include' });
    const csrf = tokenResponse.ok ? await tokenResponse.json() : null;
    const data = new URLSearchParams(new FormData(form));
    const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
    if (csrf?.token && csrf?.headerName) headers[csrf.headerName] = csrf.token;
    const response = await fetch(`${API_BASE_URL}/login`, { method: 'POST', body: data, headers, credentials: 'include' });
    if (response.redirected && response.url.includes('/login.html?error')) {
      errorBox.hidden = false;
      button.disabled = false;
      button.innerHTML = 'Entrar <span>→</span>';
      return;
    }
    location.assign('/');
  } catch {
    errorBox.textContent = 'No se pudo conectar con el servidor. Vuelve a intentarlo.';
    errorBox.hidden = false;
    button.disabled = false;
    button.innerHTML = 'Entrar <span>→</span>';
  }
});
