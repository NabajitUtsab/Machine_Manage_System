// ==================== UI COMPONENTS ====================

function buildHeader(pageTitle, notifLink) {
    const username = getUsername() || '?';
    const role = getRole() || '';
    const initial = username.charAt(0).toUpperCase();

    const header = document.querySelector('.main-header');
    if (!header) return;

    header.innerHTML = `
    <div class="header-brand">
      <div class="brand-logo">4H</div>
      <div class="brand-text">
        <span class="brand-name">Four H Group</span>
        <span class="brand-sub">Machine Management</span>
      </div>
    </div>

    <div class="header-center">${pageTitle || ''}</div>

    <div class="header-right">
      <div class="header-user">
        <div class="header-user-icon">${initial}</div>
        <div class="header-user-info">
          <span class="header-username">${username}</span>
          <span class="header-role">${role.replace('_', ' ')}</span>
        </div>
      </div>
      <a href="${notifLink}" class="btn-notif" id="notifBtn">
        <span id="notifDot" class="notif-dot" style="display:none;"></span>
        🔔 Alerts
      </a>
      <button class="btn-logout" onclick="logout()">Sign Out</button>
    </div>
  `;

    // Load notification count
    loadNotifBadge(notifLink);
}

async function loadNotifBadge(notifLink) {
    try {
        const role = getRole();
        let data;
        if (role === 'SUPER_ADMIN') {
            data = await apiRequest('/superadmin/notifications');
        } else if (role === 'ADMIN') {
            data = await apiRequest('/admin/notifications');
        } else {
            return;
        }

        const count =
            (data.readyToTransfer || []).length +
            (data.incomingTransfers || data.initiatedTransfers || []).length;

        const dot = document.getElementById('notifDot');
        const btn = document.getElementById('notifBtn');
        if (count > 0 && dot) {
            dot.style.display = 'block';
            if (btn) btn.innerHTML = `<span class="notif-dot"></span> 🔔 Alerts <span class="badge-count">${count}</span>`;
        }
    } catch (e) { /* silent */ }
}

// Apply showMessage with new styles
window.showMessage = function(elementId, message, type = 'info') {
    const el = document.getElementById(elementId);
    if (!el) return;
    el.textContent = message;
    el.style.display = 'block';
    el.className = 'message alert alert-' + (type === 'error' ? 'error' : type === 'success' ? 'success' : type === 'warning' ? 'warning' : 'info');
};

window.clearMessage = function(elementId) {
    const el = document.getElementById(elementId);
    if (el) { el.textContent = ''; el.style.display = 'none'; }
};