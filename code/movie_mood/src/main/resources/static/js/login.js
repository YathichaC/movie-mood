const loginForm = document.getElementById("loginForm");

const togglePasswordBtn =
    document.getElementById("togglePassword");

const passwordInput =
    document.getElementById("password");

const toggleIcon =
    document.getElementById("toggleIcon");

const submitBtn =
    document.getElementById("submitBtn");

togglePasswordBtn.addEventListener("click", () => {

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

function handleLogin() {

    submitBtn.disabled = true;

    submitBtn.innerHTML = `
        <span class="material-symbols-outlined animate-spin text-[20px]">
            progress_activity
        </span>

        <span>
            Authenticating...
        </span>
    `;

    showToast(
        "Connecting to MovieMood API",
        "Verifying Spring Boot JWT authorization token...",
        "lock_open"
    );

    setTimeout(() => {

        submitBtn.innerHTML = `
            <span class="material-symbols-outlined text-[20px]">
                done
            </span>

            <span>
                Success! Redirecting...
            </span>
        `;

        submitBtn.classList.remove(
            "bg-primary-container"
        );

        submitBtn.classList.add(
            "bg-secondary-container",
            "text-on-secondary-container"
        );

        showToast(
            "Login Approved",
            "Redirecting to your personalized movie ecosystem.",
            "verified"
        );

    }, 1200);

}

function showToast(
    title,
    message,
    icon = "check_circle"
) {

    const toast =
        document.getElementById("toast");

    const toastTitle =
        document.getElementById("toastTitle");

    const toastMessage =
        document.getElementById("toastMessage");

    const toastIcon =
        document.getElementById("toastIcon");

    toastTitle.textContent = title;

    toastMessage.textContent = message;

    toastIcon.textContent = icon;

    toast.classList.remove(
        "translate-y-24",
        "opacity-0"
    );

    toast.classList.add(
        "translate-y-0",
        "opacity-100"
    );

    setTimeout(() => {

        toast.classList.remove(
            "translate-y-0",
            "opacity-100"
        );

        toast.classList.add(
            "translate-y-24",
            "opacity-0"
        );

    }, 4000);
}