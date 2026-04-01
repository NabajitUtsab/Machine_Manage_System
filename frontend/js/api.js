// ==================== API CONFIGURATION ====================
const BASE_URL = "http://localhost:8081";

// ==================== LOCAL STORAGE HELPERS ====================
function getToken() {
    return localStorage.getItem("jwt");
}

function getRole() {
    return localStorage.getItem("role");
}

function getConcernName() {
    return localStorage.getItem("concernName");
}

function getConcernId() {
    return localStorage.getItem("concernId");
}

function getUsername() {
    return localStorage.getItem("username");
}

// ==================== API REQUEST HANDLER ====================
async function apiRequest(endpoint, method = "GET", body = null) {
    const headers = {
        "Content-Type": "application/json",
    };

    const token = getToken();
    if (token) {
        headers["Authorization"] = "Bearer " + token;
    }

    const options = {
        method,
        headers,
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const res = await fetch(BASE_URL + endpoint, options);

        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(errorText || `HTTP Error ${res.status}`);
        }

        const contentType = res.headers.get("content-type");
        if (contentType?.includes("application/json")) {
            return res.json();
        }

        return res.text();
    } catch (error) {
        console.error("API Request Error:", error);
        throw error;
    }
}

// ==================== AUTHENTICATION ====================
function logout() {
    localStorage.clear();
    window.location.href = "login.html";
}

function goBack() {
    const role = getRole();
    if (role === "SUPER_ADMIN") {
        window.location.href = "superadmin-dashboard.html";
    } else if (role === "ADMIN") {
        window.location.href = "admin-dashboard.html";
    } else if (role === "USER") {
        window.location.href = "user-dashboard.html";
    } else {
        window.location.href = "login.html";
    }
}

// ==================== UI HELPERS ====================
function displayUserInfo() {
    const username = getUsername();
    const role = getRole();
    const concernName = getConcernName();

    const userInfoElement = document.getElementById('userInfo');
    if (userInfoElement && username) {
        let info = `${username} (${role})`;
        if (concernName && concernName !== 'null') {
            info += ` - ${concernName}`;
        }
        userInfoElement.textContent = info;
    }
}

// ==================== MODAL FUNCTIONS ====================
function showQRModal() {
    const modal = document.getElementById('qrModal');
    if (modal) {
        modal.style.display = 'block';
    }
}

function closeQRModal() {
    const modal = document.getElementById('qrModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

function printQR() {
    window.print();
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('qrModal');
    if (event.target == modal) {
        closeQRModal();
    }
}

// ==================== FORMAT HELPERS ====================
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return '-';
    try {
        const date = new Date(dateTimeString);
        return date.toLocaleString();
    } catch (e) {
        return dateTimeString;
    }
}

function formatDate(dateString) {
    if (!dateString) return '-';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString();
    } catch (e) {
        return dateString;
    }
}

// ==================== VALIDATION HELPERS ====================
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

function validatePassword(password) {
    return password && password.length >= 6;
}

function validateUsername(username) {
    return username && username.length >= 3;
}

// ==================== MESSAGE HELPERS ====================
function showMessage(elementId, message, type = 'info') {
    const element = document.getElementById(elementId);
    if (!element) return;

    element.textContent = message;
    element.style.display = 'block';

    switch(type) {
        case 'success':
            element.style.color = 'green';
            element.style.background = '#d4edda';
            break;
        case 'error':
            element.style.color = '#721c24';
            element.style.background = '#f8d7da';
            break;
        case 'warning':
            element.style.color = '#856404';
            element.style.background = '#fff3cd';
            break;
        default:
            element.style.color = '#004085';
            element.style.background = '#cce5ff';
    }

    element.style.padding = '10px';
    element.style.borderRadius = '5px';
    element.style.marginTop = '10px';
}

function clearMessage(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = '';
        element.style.display = 'none';
    }
}

// ==================== LOADING HELPERS ====================
function showLoading(buttonId) {
    const button = document.getElementById(buttonId);
    if (button) {
        button.disabled = true;
        button.dataset.originalText = button.textContent;
        button.textContent = 'Loading...';
    }
}

function hideLoading(buttonId) {
    const button = document.getElementById(buttonId);
    if (button) {
        button.disabled = false;
        if (button.dataset.originalText) {
            button.textContent = button.dataset.originalText;
        }
    }
}

// ==================== TABLE HELPERS ====================
function clearTable(tableId) {
    const tbody = document.querySelector(`#${tableId} tbody`);
    if (tbody) {
        tbody.innerHTML = '';
    }
}

function addTableRow(tableId, rowHtml) {
    const tbody = document.querySelector(`#${tableId} tbody`);
    if (tbody) {
        tbody.innerHTML += rowHtml;
    }
}

function showEmptyTable(tableId, message, colspan) {
    const tbody = document.querySelector(`#${tableId} tbody`);
    if (tbody) {
        tbody.innerHTML = `<tr><td colspan="${colspan}" style="text-align:center;">${message}</td></tr>`;
    }
}

// ==================== FORM HELPERS ====================
function resetForm(formId) {
    const form = document.getElementById(formId);
    if (form) {
        form.reset();
    }
}

function getFormData(formId) {
    const form = document.getElementById(formId);
    if (!form) return {};

    const formData = new FormData(form);
    const data = {};

    for (let [key, value] of formData.entries()) {
        data[key] = value;
    }

    return data;
}

// ==================== NAVIGATION HELPERS ====================
function setActiveNav(pageName) {
    const navLinks = document.querySelectorAll('.navigation a');
    navLinks.forEach(link => {
        if (link.href.includes(pageName)) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
}

// ==================== AUTHORIZATION CHECK ====================
function checkAuth() {
    const token = getToken();
    const role = getRole();

    if (!token || !role) {
        window.location.href = 'login.html';
        return false;
    }

    return true;
}

function checkRole(requiredRole) {
    const role = getRole();

    if (role !== requiredRole) {
        alert('Access denied. You do not have permission to access this page.');
        goBack();
        return false;
    }

    return true;
}

function checkRoles(requiredRoles) {
    const role = getRole();

    if (!requiredRoles.includes(role)) {
        alert('Access denied. You do not have permission to access this page.');
        goBack();
        return false;
    }

    return true;
}

// ==================== DEBOUNCE HELPER ====================
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// ==================== COPY TO CLIPBOARD ====================
function copyToClipboard(text) {
    if (navigator.clipboard) {
        navigator.clipboard.writeText(text).then(() => {
            alert('Copied to clipboard!');
        }).catch(err => {
            console.error('Failed to copy:', err);
        });
    } else {
        // Fallback
        const textArea = document.createElement('textarea');
        textArea.value = text;
        document.body.appendChild(textArea);
        textArea.select();
        try {
            document.execCommand('copy');
            alert('Copied to clipboard!');
        } catch (err) {
            console.error('Failed to copy:', err);
        }
        document.body.removeChild(textArea);
    }
}

// ==================== INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', () => {
    // Check auth on protected pages (NOT login page)
    const currentPage = window.location.pathname;
    const isLoginPage = currentPage.includes('login.html');
    const isPublicPage = currentPage.includes('register.html') || currentPage.includes('scan.html');

    // Only check auth if NOT on login or public pages
    if (!isLoginPage && !isPublicPage) {
        checkAuth();
    }

    // Display user info if element exists
    displayUserInfo();
});

// ==================== ERROR HANDLER ====================
window.addEventListener('error', (event) => {
    console.error('Global error:', event.error);
});

window.addEventListener('unhandledrejection', (event) => {
    console.error('Unhandled promise rejection:', event.reason);
});