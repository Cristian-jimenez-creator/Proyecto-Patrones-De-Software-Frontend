const state = { pools: [], cameras: [], alerts: [], users: [], statistics: null, page: 'dashboard', alertFilter: 'all' };
const titleMap = { dashboard: 'Resumen general', pools: 'Piscinas', cameras: 'Cámaras', alerts: 'Alertas', incidents: 'Incidentes', statistics: 'Estadísticas', users: 'Usuarios' };
let csrfToken = null;
let toastTimer;

const escapeHtml = value => String(value ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c]);
const cls = value => String(value ?? '').toLowerCase().replace(/[^a-z0-9-]/g, '-');
const pretty = value => String(value ?? '').toLowerCase().replaceAll('_', ' ').replace(/\b\w/g, c => c.toUpperCase());
const shortDate = value => value ? new Date(`${value}T00:00:00`).toLocaleDateString('es-CO', { day: '2-digit', month: 'short' }) : '';
const notify = (message, error = false) => {
  const toast = document.querySelector('#toast');
  toast.textContent = message;
  toast.className = `toast show${error ? ' error' : ''}`;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.className = 'toast', 3000);
};

async function getCsrf() {
  if (csrfToken) return csrfToken;
  try {
    const response = await fetch('/api/csrf', { credentials: 'same-origin' });
    if (response.ok) csrfToken = await response.json();
  } catch { /* Local development can run without CSRF when auth is disabled. */ }
  return csrfToken;
}

async function api(path, options = {}) {
  const headers = new Headers(options.headers || {});
  if (options.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json');
  if (options.method && !['GET', 'HEAD'].includes(options.method.toUpperCase())) {
    const csrf = await getCsrf();
    if (csrf?.token && csrf?.headerName) headers.set(csrf.headerName, csrf.token);
  }
  const response = await fetch(path, { ...options, headers, credentials: 'same-origin' });
  if (response.status === 401 || (response.redirected && response.url.includes('/login.html'))) {
    location.assign('/login.html');
    throw new Error('Inicia sesión para continuar.');
  }
  if (!response.ok) {
    const text = await response.text();
    let message = `Error del servidor (${response.status})`;
    try { message = JSON.parse(text).message || message; } catch { /* keep generic message */ }
    throw new Error(message);
  }
  if (response.status === 204) return null;
  return response.json();
}

const badge = (value, prefix = 'status') => `<span class="badge ${prefix}-${cls(value)}">${escapeHtml(pretty(value))}</span>`;
const statCard = (label, value, foot, tint, icon) => `<article class="stat-card" style="--tint:${tint};--accent:${icon[1]}"><span class="stat-icon">${icon[0]}</span><div class="stat-label">${label}</div><div class="stat-value">${value}</div><div class="stat-foot">${foot}</div></article>`;

function alertRows(alerts) {
  if (!alerts.length) return '<div class="empty-state">No hay alertas en esta lista.</div>';
  return `<div class="alert-list">${alerts.slice(0, 6).map(a => `<div class="alert-row"><span class="alert-marker ${cls(a.risk)}"></span><div class="alert-main"><b>${escapeHtml(a.description)}</b><small>${escapeHtml(a.pool)} · ${escapeHtml(a.camera)} · ${escapeHtml(pretty(a.status))}</small></div><span class="alert-time">${escapeHtml(a.time || '')}</span></div>`).join('')}</div>`;
}

function poolCards(pools) {
  if (!pools.length) return '<div class="empty-state">No hay piscinas.</div>';
  return `<div class="pool-grid">${pools.map(p => `<article class="pool-card"><div class="pool-card-top"><div><h3>${escapeHtml(p.name)}</h3><p>${escapeHtml(p.location)}</p></div>${badge(p.status)}</div><div class="pool-metrics"><span><b>${p.people}</b> personas</span><span><b>${p.cameras}</b> cámaras</span><span>${badge(p.risk, 'risk')}</span></div></article>`).join('')}</div>`;
}

function dashboard() {
  const stats = state.statistics || {};
  const activeAlerts = state.alerts.filter(a => a.status !== 'CLOSED');
  const critical = activeAlerts.find(a => a.risk === 'CRITICAL');
  return `${critical ? `<div class="critical-banner"><span>⚑</span><div><b>Alerta crítica activa</b><br><span>${escapeHtml(critical.description)} · ${escapeHtml(critical.pool)}</span></div><button class="button small" data-page-link="alerts">Ver alerta</button></div>` : ''}
    <div class="section-heading"><div><h2>Estado de operación</h2><p>Monitoreo de piscinas, cámaras y eventos de seguridad.</p></div><button class="button" data-action="simulate">＋&nbsp; Simular alerta</button></div>
    <div class="stats-grid">${statCard('PISCINAS MONITOREADAS', state.pools.length, 'Instalaciones registradas', '#e8f8fb', ['◈','#16b9d2'])}${statCard('CÁMARAS EN LÍNEA', `${stats.activeCameras ?? state.cameras.filter(c=>c.status==='ONLINE').length}<small> / ${stats.totalCameras ?? state.cameras.length}</small>`, 'Conectividad de cámaras', '#eaf3ff', ['◉','#4085f5'])}${statCard('ALERTAS ACTIVAS', activeAlerts.length, 'Pendientes de atención', '#fff4e6', ['⚑','#f0a521'])}${statCard('INCIDENTES CERRADOS', stats.resolvedIncidents ?? 0, 'Historial de incidentes', '#eaf8f2', ['✓','#19aa79'])}</div>
    <div class="two-col"><section class="panel"><div class="panel-title"><h3>Piscinas</h3><a href="#pools">Ver todas →</a></div>${poolCards(state.pools.slice(0,3))}</section><section class="panel"><div class="panel-title"><h3>Actividad reciente</h3><a href="#alerts">Ver alertas →</a></div>${alertRows(state.alerts)}</section></div>
    <section class="panel"><div class="panel-title"><h3>Actividad de detección semanal</h3><a href="#statistics">Más estadísticas →</a></div>${miniChart(stats.weeklyDetections || [])}</section>`;
}

function miniChart(values) {
  if (!values.length) return '<div class="empty-state">Sin estadísticas disponibles.</div>';
  const labels = ['Lun','Mar','Mié','Jue','Vie','Sáb','Dom'];
  const max = Math.max(...values, 1);
  return `<div class="chart">${values.map((value, i) => `<div class="bar-wrap"><span class="bar-value">${value}</span><div class="bar" style="height:${Math.max(5, value/max*72)}%"></div><span class="bar-label">${labels[i] || `D${i+1}`}</span></div>`).join('')}</div>`;
}

function poolsPage() {
  return `<div class="section-heading"><div><h2>Instalaciones acuáticas</h2><p>Estado actual y ocupación de cada piscina.</p></div></div>${poolCards(state.pools)}`;
}

function camerasPage() {
  return `<div class="section-heading"><div><h2>Monitoreo de cámaras</h2><p>Vista simulada; las cámaras físicas no están conectadas en este proyecto.</p></div></div><div class="camera-grid">${state.cameras.map(c => `<article class="camera-card"><div class="camera-preview"><span class="camera-live">● ${escapeHtml(pretty(c.status))}</span><span>◎</span><div class="camera-name">${escapeHtml(c.id)} · ${escapeHtml(c.pool)}</div></div><div class="camera-info"><p>${escapeHtml(c.location)} · ${escapeHtml(c.resolution || '')} · ${escapeHtml(c.monitoring || '')}</p><div class="camera-foot">${badge(c.status)}<button class="button secondary small" data-action="analyze" data-id="${escapeHtml(c.id)}">Analizar</button></div></div></article>`).join('')}</div>`;
}

function alertTable(alerts, includeActions = true) {
  if (!alerts.length) return '<div class="empty-state">No hay registros para mostrar.</div>';
  return `<div class="table-wrap"><table class="data-table"><thead><tr><th>Evento</th><th>Piscina / cámara</th><th>Fecha</th><th>Riesgo</th><th>Estado</th>${includeActions ? '<th>Acciones</th>' : ''}</tr></thead><tbody>${alerts.map(a => `<tr><td><strong>${escapeHtml(a.description)}</strong>${a.observations?.length ? `<span class="table-sub">${escapeHtml(a.observations.length)} observación(es)</span>` : ''}</td><td>${escapeHtml(a.pool)}<span class="table-sub">${escapeHtml(a.camera)}</span></td><td>${escapeHtml(shortDate(a.date))}<span class="table-sub">${escapeHtml(a.time || '')}</span></td><td>${badge(a.risk, 'risk')}</td><td>${badge(a.status)}</td>${includeActions ? `<td><div class="actions">${a.status==='NEW' ? `<button class="button secondary small" data-action="confirm" data-id="${escapeHtml(a.id)}">Confirmar</button>` : ''}${!['ATTENDED','CLOSED'].includes(a.status) ? `<button class="button secondary small" data-action="attend" data-id="${escapeHtml(a.id)}">Atender</button>` : ''}${a.status!=='CLOSED' ? `<button class="button small" data-action="close" data-id="${escapeHtml(a.id)}">Cerrar</button>` : ''}<button class="button secondary small" data-action="observe" data-id="${escapeHtml(a.id)}">＋ Nota</button></div></td>` : ''}</tr>`).join('')}</tbody></table></div>`;
}

function alertsPage(incidents = false) {
  const rows = incidents ? state.alerts.filter(a => a.status === 'CLOSED') : state.alerts.filter(a => a.status !== 'CLOSED');
  return `<div class="section-heading"><div><h2>${incidents ? 'Historial de incidentes' : 'Alertas activas'}</h2><p>${incidents ? 'Incidentes atendidos y cerrados.' : 'Revisa los eventos y registra su atención.'}</p></div>${!incidents ? '<button class="button" data-action="simulate">＋&nbsp; Simular alerta</button>' : ''}</div>
    ${!incidents ? `<div class="filters"><select id="alert-filter"><option value="all">Todos los riesgos</option><option value="CRITICAL">Crítico</option><option value="HIGH">Alto</option><option value="MEDIUM">Medio</option><option value="LOW">Bajo</option></select><span class="table-sub">${rows.length} alerta(s)</span></div>` : ''}<section class="panel">${alertTable(state.alertFilter==='all' || incidents ? rows : rows.filter(a=>a.risk===state.alertFilter), !incidents)}</section>`;
}

function statisticsPage() {
  const s = state.statistics || {};
  const risk = s.alertsByRisk || {};
  return `<div class="section-heading"><div><h2>Estadísticas de seguridad</h2><p>Resumen de actividad y alertas registradas.</p></div></div><div class="stats-grid">${statCard('DETECCIONES ESTA SEMANA', s.totalDetections ?? 0, 'Valores simulados del proyecto', '#e8f8fb', ['▥','#16b9d2'])}${statCard('ALERTAS TOTALES', s.totalAlerts ?? 0, 'Registradas en la plataforma', '#eaf3ff', ['⚑','#4085f5'])}${statCard('RIESGO CRÍTICO', risk.CRITICAL ?? 0, 'Alertas críticas', '#fff0f0', ['!','#e45d61'])}${statCard('CÁMARAS ACTIVAS', `${s.activeCameras ?? 0}/${s.totalCameras ?? 0}`, 'Estado actual', '#eaf8f2', ['◉','#19aa79'])}</div><section class="panel"><div class="panel-title"><h3>Detecciones por día</h3></div>${miniChart(s.weeklyDetections || [])}</section><section class="panel" style="margin-top:16px"><div class="panel-title"><h3>Alertas por nivel de riesgo</h3></div><div class="kpi-row">${Object.entries(risk).map(([key,value])=>`<div>${badge(key,'risk')}<b style="margin-top:8px">${value}</b><small>alertas</small></div>`).join('')}</div></section>`;
}

function usersPage() {
  const rows = state.users.length ? `<div class="table-wrap"><table class="data-table"><thead><tr><th>Nombre</th><th>Correo</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr></thead><tbody>${state.users.map(u=>`<tr><td><strong>${escapeHtml(u.name)}</strong></td><td>${escapeHtml(u.email)}</td><td>${escapeHtml(pretty(u.role))}</td><td>${badge(u.active?'ONLINE':'OFFLINE',u.active?'status':'status')}</td><td><button class="button secondary small" data-action="toggle-user" data-id="${u.id}" data-active="${!u.active}">${u.active?'Desactivar':'Activar'}</button> <button class="button danger small" data-action="delete-user" data-id="${u.id}">Eliminar</button></td></tr>`).join('')}</tbody></table></div>` : '<div class="empty-state">No hay usuarios.</div>';
  return `<div class="section-heading"><div><h2>Usuarios</h2><p>Personal registrado en el panel. El acceso al sitio se gestiona con las credenciales privadas de Render.</p></div></div><section class="panel"><div class="panel-title"><h3>Personal</h3></div>${rows}<form id="new-user" class="inline-form"><div><label class="form-label" for="user-name">Nombre</label><input class="form-input" id="user-name" name="name" required></div><div><label class="form-label" for="user-email">Correo</label><input class="form-input" id="user-email" name="email" type="email" required></div><div><label class="form-label" for="user-role">Rol</label><select class="form-input" id="user-role" name="role"><option>OPERATOR</option><option>SUPERVISOR</option><option>ADMINISTRATOR</option></select></div><button class="button" type="submit">＋ Agregar</button></form></section>`;
}

function render() {
  const page = location.hash.slice(1) || 'dashboard';
  state.page = titleMap[page] ? page : 'dashboard';
  document.querySelector('#page-title').textContent = titleMap[state.page];
  document.querySelectorAll('.nav-link').forEach(a=>a.classList.toggle('active',a.dataset.page===state.page));
  const content = document.querySelector('#content');
  content.innerHTML = ({ dashboard, pools: poolsPage, cameras: camerasPage, alerts: ()=>alertsPage(false), incidents: ()=>alertsPage(true), statistics: statisticsPage, users: usersPage })[state.page]();
  document.querySelector('#alert-count').textContent = state.alerts.filter(a=>a.status!=='CLOSED').length;
  document.querySelector('#alert-count').hidden = !state.alerts.some(a=>a.status!=='CLOSED');
  document.querySelector('#today-label').textContent = new Date().toLocaleDateString('es-CO',{weekday:'long',day:'numeric',month:'long'}).toLocaleUpperCase('es-CO');
  const filter = document.querySelector('#alert-filter');
  if (filter) { filter.value=state.alertFilter; filter.addEventListener('change',()=>{state.alertFilter=filter.value;render()}); }
  const form = document.querySelector('#new-user');
  if (form) form.addEventListener('submit', createUser);
}

async function refresh() {
  try {
    const [pools,cameras,alerts,statistics,users] = await Promise.all([
      api('/api/pools'),api('/api/cameras'),api('/api/alerts'),api('/api/statistics'),api('/api/users')
    ]);
    Object.assign(state,{pools,cameras,alerts,statistics,users});
    render();
  } catch (error) {
    if (error.message.includes('Inicia sesión')) return;
    document.querySelector('#content').innerHTML = `<section class="panel empty-state">No se pudieron cargar los datos.<br><br>${escapeHtml(error.message)}<br><br><button class="button" onclick="location.reload()">Reintentar</button></section>`;
    notify(error.message,true);
  }
}

async function updateAlert(id, action) {
  const updated = await api(`/api/alerts/${encodeURIComponent(id)}/${action}`,{method:'POST'});
  state.alerts=state.alerts.map(a=>a.id===updated.id?updated:a); await refresh(); notify('Alerta actualizada.');
}
async function createUser(event) {
  event.preventDefault(); const data=Object.fromEntries(new FormData(event.currentTarget)); data.active=true;
  try { await api('/api/users',{method:'POST',body:JSON.stringify(data)}); await refresh(); notify('Usuario agregado.'); }
  catch(error){notify(error.message,true)}
}

document.addEventListener('click', async event => {
  const nav = event.target.closest('[data-page-link]');
  if (nav) { location.hash=nav.dataset.pageLink; return; }
  const button=event.target.closest('[data-action]');
  if (!button) return;
  const {action,id}=button.dataset;
  try {
    if(action==='simulate'){await api('/api/alerts/simulate-critical',{method:'POST'});await refresh();notify('Alerta crítica simulada.');}
    else if(['confirm','attend','close'].includes(action)) await updateAlert(id,action);
    else if(action==='observe') {const text=prompt('Escribe una observación para el incidente:');if(text?.trim()){await api(`/api/alerts/${encodeURIComponent(id)}/observations`,{method:'POST',body:JSON.stringify({text:text.trim()})});await refresh();notify('Observación agregada.')}}
    else if(action==='analyze'){const a=await api(`/api/cameras/${encodeURIComponent(id)}/analysis`);notify(`Análisis simulado: ${a.people} personas · riesgo ${pretty(a.risk)} · confianza ${a.confidence}%.`);}
    else if(action==='toggle-user'){await api(`/api/users/${id}/active?value=${button.dataset.active}`,{method:'PATCH'});await refresh();notify('Estado del usuario actualizado.');}
    else if(action==='delete-user'&&confirm('¿Eliminar este usuario del listado?')){await api(`/api/users/${id}`,{method:'DELETE'});await refresh();notify('Usuario eliminado.');}
  } catch(error){notify(error.message,true)}
});

document.querySelector('#logout').addEventListener('click', async()=>{
  try { await api('/logout',{method:'POST'}); location.assign('/login.html?loggedOut'); }
  catch { location.assign('/login.html'); }
});
document.querySelector('#menu-toggle').addEventListener('click',()=>document.querySelector('#sidebar').classList.toggle('open'));
document.querySelectorAll('.nav-link').forEach(a=>a.addEventListener('click',()=>document.querySelector('#sidebar').classList.remove('open')));
window.addEventListener('hashchange',render);

refresh().then(()=>{
  try { const stream = new EventSource('/api/alerts/stream'); stream.onmessage=event=>{const alert=JSON.parse(event.data);if(!state.alerts.some(a=>a.id===alert.id)){state.alerts.unshift(alert);render();notify('Nueva alerta recibida.')}};stream.onerror=()=>stream.close(); }
  catch { /* Browser does not support event streams. */ }
});
