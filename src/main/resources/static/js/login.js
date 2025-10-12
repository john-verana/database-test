document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const loginBtn = document.getElementById('loginBtn');
    const registerBtn = document.getElementById('registerBtn');
    const messageEl = document.getElementById('message');

    function setMessage(text, type = 'info') {
        messageEl.textContent = text;
        messageEl.style.color = type === 'error' ? '#c0392b' : type === 'success' ? '#1e8449' : '#333';
    }

    function setLoading(isLoading) {
        loginBtn.disabled = isLoading;
        loginBtn.textContent = isLoading ? 'Checking…' : 'Login';
    }

    function showRegister(show) {
        registerBtn.style.display = show ? 'block' : 'none';
    }

    const USERNAME_RE = /^[a-zA-Z0-9._-]{3,20}$/;
    function validateUsername(raw) {
        const value = raw.trim();
        if (!value) return { ok: false, reason: 'Username is required.' };
        if (!USERNAME_RE.test(value)) return { ok: false, reason: '3–20 letters/numbers/._- only.' };
        return { ok: true, value };
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        showRegister(false);

        const v = validateUsername(usernameInput.value);
        if (!v.ok) {
            setMessage(v.reason, 'error');
            return;
        }

        try {
            setLoading(true);
            setMessage('Checking username…');

            const res = await fetch('/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: v.value })
            });

            const data = await res.json().catch(() => ({}));

            if (res.ok && data && data.found) {
                setMessage(data.message || `Welcome back, ${v.value}!`, 'success');
            } else if (res.status === 404 || data?.found === false) {
                setMessage(data.message || 'User not found. You can register.', 'info');
                showRegister(true);
            } else {
                setMessage(data.message || 'Something went wrong. Please try again.', 'error');
            }
        } catch (err) {
            console.error(err);
            setMessage('Network error. Check if the backend is running.', 'error');
        } finally {
            setLoading(false);
        }
    });

    registerBtn.addEventListener('click', () => {
        const v = validateUsername(usernameInput.value);
        if (!v.ok) {
            setMessage('Enter a valid username before registering.', 'error');
            return;
        }
        setMessage(`Ready to register "${v.value}". (Hook this up to /register next.)`, 'success');
        // later: POST /register or navigate to a registration page
        // window.location.href = `/register?username=${encodeURIComponent(v.value)}`;
    });
});
