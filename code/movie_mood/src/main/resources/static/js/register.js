const registerForm =
    document.getElementById("registerForm");

const usernameInput =
    document.getElementById("username");

const emailInput =
    document.getElementById("email");

const passwordInput =
    document.getElementById("password");

const confirmPasswordInput =
    document.getElementById("confirmPassword");

const togglePasswordBtn =
    document.getElementById("togglePassword");

const toggleConfirmPasswordBtn =
    document.getElementById("toggleConfirmPassword");

const toggleIcon =
    document.getElementById("toggleIcon");

const toggleConfirmIcon =
    document.getElementById("toggleConfirmIcon");

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

toggleConfirmPasswordBtn.addEventListener("click", () => {

    const type =
        confirmPasswordInput.getAttribute("type") === "password"
            ? "text"
            : "password";

    confirmPasswordInput.setAttribute("type", type);

    toggleConfirmIcon.textContent =
        type === "password"
            ? "visibility"
            : "visibility_off";
});

registerForm.addEventListener("submit", (event) => {

    event.preventDefault();

    const username =
        usernameInput.value.trim();

    const email =
        emailInput.value.trim();

    const password =
        passwordInput.value;

    const confirmPassword =
        confirmPasswordInput.value;

    if (password !== confirmPassword) {

        showToast(
            "Password Mismatch",
            "Passwords do not match.",
            "error"
        );

        confirmPasswordInput.focus();

        return;
    }

    if (username.length < 3) {

        showToast(
            "Invalid Username",
            "Username must be at least 3 characters.",
            "error"
        );

        usernameInput.focus();

        return;
    }

    submitBtn.disabled = true;

    submitBtn.innerHTML = `
        <span class="material-symbols-outlined animate-spin text-[20px]">
            progress_activity
        </span>

        <span>
            Creating account...
        </span>
    `;

    showToast(
        "Creating Account",
        "Please wait while we create your MovieMood account.",
        "person_add"
    );

    setTimeout(() => {

        submitBtn.innerHTML = `
            <span class="material-symbols-outlined text-[20px]">
                done
            </span>

            <span>
                Account Created!
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
            "Registration Successful",
            "Your MovieMood account has been created.",
            "verified"
        );

    }, 1200);

});

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