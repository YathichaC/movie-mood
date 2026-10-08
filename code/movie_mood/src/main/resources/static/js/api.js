const API_BASE = "/api";

document.addEventListener("DOMContentLoaded", async () => {
    const usernameElement = document.getElementById("navbarUsername");

    if (!usernameElement) return;

    try {
        const response = await apiFetch("/v1/auth/me");

        if (!response || !response.ok) return;

        const user = await response.json();

        if (user.username) {
            usernameElement.textContent = user.username;
        }
    } catch (error) {
        console.error("Unable to load username:", error);
    }
});

async function apiFetch(url, options = {}) {
    const token = localStorage.getItem("token");

    const headers = {
        ...(options.headers || {})
    };

    const isFormData = options.body instanceof FormData;

    if (!isFormData && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/json";
    }

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE}${url}`, {
        ...options,
        headers
    });

    if (response.status === 401) {
        localStorage.removeItem("token");
        window.location.href = "/auth/login";
        return;
    }

    return response;
}