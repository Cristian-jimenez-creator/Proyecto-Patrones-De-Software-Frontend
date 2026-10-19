const form = document.querySelector('#register-form');
const message = document.querySelector('#register-message');
const button = document.querySelector('#register-button');
const apiBase = (window.SMARTPOOL_API_URL || '').replace(/\/+$/, '');

form.addEventListener('submit', async event => {
  event.preventDefault();
  message.hidden = true;
  const values = Object.fromEntries(new FormData(form));
  if (values.password !== values.passwordConfirmation) {
    message.textContent = 'Las contraseñas no coinciden.';
    message.className = 'login-error';
    message.hidden = false;
    return;
  }

  button.disabled = true;
  button.textContent = 'Creando cuenta…';
  try {
    const csrfResponse = await fetch(`${apiBase}/api/csrf`, { credentials: 'include' });
    const csrf = csrfResponse.ok ? await csrfResponse.json() : null;
    const headers = { 'Content-Type': 'application/json' };
    if (csrf?.token && csrf?.headerName) headers[csrf.headerName] = csrf.token;
    const response = await fetch(`${apiBase}/api/auth/register`, {
      method: 'POST',
      headers,
      credentials: 'include',
      body: JSON.stringify({ name: values.name, email: values.email, password: values.password })
    });
    const body = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(body.message || 'No se pudo crear la cuenta. Revisa los datos e inténtalo de nuevo.');
    message.textContent = 'Tu cuenta fue creada. Ahora puedes iniciar sesión.';
    message.className = 'login-note';
    message.hidden = false;
    form.reset();
    button.textContent = 'Ir a iniciar sesión';
    button.type = 'button';
    button.disabled = false;
    button.onclick = () => window.location.assign('/login.html');
  } catch (error) {
    message.textContent = error.message || 'No se pudo conectar con el servidor. Vuelve a intentarlo.';
    message.className = 'login-error';
    message.hidden = false;
    button.disabled = false;
    button.textContent = 'Crear cuenta';
  }
});
