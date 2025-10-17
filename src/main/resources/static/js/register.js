// Prefill username if query param exists
const params = new URLSearchParams(window.location.search);
const prefill = params.get('username');
if (prefill) {
    document.getElementById('username').value = prefill;
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registerForm');
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const messageEl = document.getElementById('message');

    function setMessage(text, type = 'info') {
        messageEl.textContent = text;
        messageEl.style.color = type === 'error' ? '#c0392b' : type === 'success' ? '#1e8449' : '#333';
    }

    const USERNAME_RE = /^[a-zA-Z0-9._]{3,20}$/;
    const EMAIL_RE = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const username = usernameInput.value.trim();
        const email = emailInput.value.trim();

        if(!USERNAME_RE.test(username)) {
            setMessage('Invalid username (3-20 letters/numbers/._ only).', 'error');
            return;
        }

        if(!EMAIL_RE.test(email)) {
            setMessage('Invalid email address.', 'error');
            return;
        }

        try {
            setMessage('Creating account...');
            const res = await fetch('/register', {
                method: 'POST',
                headers: { 'Content-type': 'application/json' },
                body: JSON.stringify({ username, email }),
            });

            const data = await res.json().catch(() => ({}));

            if(res.ok && data && data.success) {
                setMessage('Account created successfully!', 'success');
            } else {
                setMessage(data.message || 'Something went wrong. Please try again.', 'error');
            }
        } catch (err) {
            console.error(err);
            setMessage('Server error. Try again later.', 'error');
        }

    });
});

