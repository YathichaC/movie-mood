async function logout() {
    try {
        await apiFetch('/v1/auth/logout', {
            method: 'POST'
        });
    } finally {
        localStorage.removeItem('token');
        window.location.href = '/auth/login';
    }
}