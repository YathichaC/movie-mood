const loginForm = document.getElementById("loginForm");

const togglePasswordBtn =
    document.getElementById("togglePassword");

const passwordInput =
    document.getElementById("password");

const toggleIcon =
    document.getElementById("toggleIcon");

const submitBtn =
    document.getElementById("submitBtn");

togglePasswordBtn?.addEventListener("click", () => {
    const type =
        passwordInput.getAttribute("type") === "password"
            ? "text"
            : "password";

    passwordInput.setAttribute("type", type);

    toggleIcon.textContent =
        type === "password"
            ? "visibility"
            : "visibility_off";
});

loginForm.addEventListener("submit", (event) => {
    event.preventDefault();
    handleLogin();
});

async function handleLogin() {
    const username =
        document.getElementById("username").value.trim();

    const password =
        document.getElementById("password").value;

    submitBtn.disabled = true;

    showToast(
        "Connecting to MovieMood API",
        "Checking your username and password...",
        "lock_open"
    );

    try {
        const response = await fetch("/api/v1/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Login failed");
        }

        // Store JWT and user information
        localStorage.setItem("token", data.token);

        if (data.userId != null) {
            localStorage.setItem("userId", data.userId);
        }

        if (data.username) {
            localStorage.setItem("username", data.username);
        }

        if (data.email) {
            localStorage.setItem("email", data.email);
        }

        submitBtn.innerHTML = `
            <span class="material-symbols-outlined text-[20px]">
                done
            </span>
            <span>Success! Redirecting...</span>
        `;

        submitBtn.classList.remove("bg-primary-container");

        submitBtn.classList.add(
            "bg-secondary-container",
            "text-on-secondary-container"
        );

        showToast(
            "Login Approved",
            "Login successful. Welcome to MovieMood!",
            "verified"
        );

        setTimeout(() => {
            window.location.href = "/home";
        }, 800);

    } catch (error) {
        showToast(
            "Login Failed",
            error.message,
            "error"
        );

        submitBtn.disabled = false;

        submitBtn.innerHTML = `
            <span>Sign In</span>
            <span class="material-symbols-outlined text-[20px]">
                arrow_forward
            </span>
        `;
    }
}

function showToast(
    title,
    message,
    icon = "check_circle"
) {
    const toast = document.getElementById("toast");
    const toastTitle = document.getElementById("toastTitle");
    const toastIcon = document.getElementById("toastIcon");

    if (!toast || !toastTitle || !toastIcon) {
        return;
    }

    toastTitle.textContent = `${title} — ${message}`;
    toastIcon.textContent = icon;

    toast.classList.remove("translate-y-24", "opacity-0");
    toast.classList.add("translate-y-0", "opacity-100");

    setTimeout(() => {
        toast.classList.remove("translate-y-0", "opacity-100");
        toast.classList.add("translate-y-24", "opacity-0");
    }, 4000);
}
