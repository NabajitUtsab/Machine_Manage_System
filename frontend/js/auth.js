// ==================== LOGIN FUNCTION ====================
async function loginUser() {
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const msg = document.getElementById("msg");

    if (!username || !password) {
        showMessage('msg', 'Please enter username and password', 'error');
        return;
    }

    try {
        const res = await fetch(BASE_URL + "/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (!res.ok) {
            const errorText = await res.text();
            showMessage('msg', errorText || "Login failed!", 'error');
            return;
        }

        const data = await res.json();

        // Store user data
        localStorage.setItem("jwt", data.jwt);
        localStorage.setItem("role", data.role);
        localStorage.setItem("concernId", data.concernId || "");
        localStorage.setItem("concernName", data.concernName || "");
        localStorage.setItem("username", data.username);

        showMessage('msg', "Login successful! Redirecting...", 'success');

        // Redirect based on role
        setTimeout(() => {
            if (data.role === "SUPER_ADMIN") {
                window.location.href = "superadmin-dashboard.html";
            } else if (data.role === "ADMIN") {
                window.location.href = "admin-dashboard.html";
            } else if (data.role === "USER") {
                window.location.href = "user-dashboard.html";
            }
        }, 500);

    } catch (err) {
        showMessage('msg', "Error: " + err.message, 'error');
    }
}

// Allow Enter key to submit
document.addEventListener('DOMContentLoaded', function() {
    const passwordInput = document.getElementById('password');
    if (passwordInput) {
        passwordInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                loginUser();
            }
        });
    }

    const usernameInput = document.getElementById('username');
    if (usernameInput) {
        usernameInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                loginUser();
            }
        });
    }
});
