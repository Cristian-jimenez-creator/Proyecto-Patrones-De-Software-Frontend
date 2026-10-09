const form = document.querySelector('#login-form');
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
    const tokenResponse = await fetch('/api/csrf', { credentials: 'same-origin' });
    const csrf = tokenResponse.ok ? await tokenResponse.json() : null;
    const data = new URLSearchParams(new FormData(form));
    const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
    if (csrf?.token && csrf?.headerName) headers[csrf.headerName] = csrf.token;
    const response = await fetch('/login', { method: 'POST', body: data, headers, credentials: 'same-origin' });
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
