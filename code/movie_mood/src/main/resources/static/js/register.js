const registerForm = document.getElementById("registerForm");
const usernameInput = document.getElementById("username");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const confirmPasswordInput = document.getElementById("confirmPassword");
const togglePasswordBtn = document.getElementById("togglePassword");
const toggleConfirmPasswordBtn = document.getElementById("toggleConfirmPassword");
const toggleIcon = document.getElementById("toggleIcon");
const toggleConfirmIcon = document.getElementById("toggleConfirmIcon");
const submitBtn = document.getElementById("submitBtn");
togglePasswordBtn.addEventListener("click", () => {
    const isPassword = passwordInput.type === "password";
    passwordInput.type = isPassword ? "text" : "password";
    toggleIcon.textContent = isPassword ? "visibility_off" : "visibility";
    togglePasswordBtn.setAttribute("aria-label", isPassword ? "Hide password" : "Show password");
});
toggleConfirmPasswordBtn.addEventListener("click", () => {
    const isPassword = confirmPasswordInput.type === "password";
    confirmPasswordInput.type = isPassword ? "text" : "password";
    toggleConfirmIcon.textContent = isPassword ? "visibility_off" : "visibility";
    toggleConfirmPasswordBtn.setAttribute("aria-label", isPassword ? "Hide confirm password" : "Show confirm password");
});
function validatePassword(password) {
    return {
        length: password.length >= 8,
        uppercase: /[A-Z]/.test(password),
        lowercase: /[a-z]/.test(password),
        number: /[0-9]/.test(password),
        special: /[^A-Za-z0-9]/.test(password)
    };
}
function updatePasswordRequirements() {
    const password = passwordInput.value;
    const rules = validatePassword(password);
    const requirements = [
        { id: "req-length", valid: rules.length },
        { id: "req-uppercase", valid: rules.uppercase },
        { id: "req-lowercase", valid: rules.lowercase },
        { id: "req-number", valid: rules.number },
        { id: "req-special", valid: rules.special }
    ];
    requirements.forEach(({ id, valid }) => {
        const item = document.getElementById(id);
        if (!item) return;
        const icon = item.querySelector(".material-symbols-outlined");
        if (valid) {
            item.classList.remove("text-[#A3A3A3]", "text-on-surface-variant");
            item.classList.add("text-[#B8A58A]");
            if (icon) {
                icon.textContent = "check_circle";
                icon.classList.remove("text-[#A3A3A3]", "text-outline");
                icon.classList.add("text-[#B8A58A]");
            }
        } else {
            item.classList.remove("text-[#B8A58A]", "text-on-surface");
            item.classList.add("text-[#A3A3A3]");
            if (icon) {
                icon.textContent = "radio_button_unchecked";
                icon.classList.remove("text-[#B8A58A]", "text-secondary");
                icon.classList.add("text-[#A3A3A3]");
            }
        }
    });
    updatePasswordMatch();
    return Object.values(rules).every(Boolean);
}
function updatePasswordMatch() {
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;
    let message = document.getElementById("password-match-message");
    if (!message) {
        message = document.createElement("p");
        message.id = "password-match-message";
        message.className = "mt-2 text-xs font-['Manrope']";
        confirmPasswordInput.closest("div").insertAdjacentElement("afterend", message);
    }
    if (!confirmPassword) {
        message.textContent = "";
        confirmPasswordInput.classList.remove("border-red-400", "border-[#B8A58A]");
        return false;
    }
    if (password === confirmPassword) {
        message.textContent = "Passwords match.";
        message.className = "mt-2 text-xs font-['Manrope'] text-[#B8A58A]";
        confirmPasswordInput.classList.remove("border-red-400");
        confirmPasswordInput.classList.add("border-[#B8A58A]");
        return true;
    }
    message.textContent = "Passwords do not match.";
    message.className = "mt-2 text-xs font-['Manrope'] text-red-400";
    confirmPasswordInput.classList.remove("border-[#B8A58A]");
    confirmPasswordInput.classList.add("border-red-400");
    return false;
}
passwordInput.addEventListener("input", updatePasswordRequirements);
confirmPasswordInput.addEventListener("input", updatePasswordMatch);

registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const username = usernameInput.value.trim();
    const email = emailInput.value.trim();
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    const rules = validatePassword(password);

    if (!Object.values(rules).every(Boolean)) {
        updatePasswordRequirements();
        showToast(
            "Invalid Password",
            "Password must be at least 8 characters and include uppercase, lowercase, a number, and a special character.",
            "error"
        );
        passwordInput.focus();
        return;
    }

    if (password !== confirmPassword) {
        updatePasswordMatch();
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
        <span>Creating account...</span>
    `;

    try {
        const response = await fetch("/api/v1/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username,
                email,
                password
            })
        });

        // อ่าน response เพียงครั้งเดียว
        const data = await response.json();

        if (!response.ok) {
            throw new Error(
                data.message || "Unable to create your account."
            );
        }

        showToast(
            "Registration Successful",
            "Your account has been created. Please log in.",
            "check_circle"
        );

        submitBtn.innerHTML = `
            <span class="material-symbols-outlined text-[20px]">
                done
            </span>
            <span>Account Created!</span>
        `;

        setTimeout(() => {
            window.location.href = "/auth/login";
        }, 1000);

    } catch (error) {
        showToast(
            "Registration Failed",
            error.message || "Unable to create your account.",
            "error"
        );

        submitBtn.disabled = false;
        submitBtn.innerHTML = `
            <span>Create Account</span>
            <span class="material-symbols-outlined text-[20px]">
                arrow_forward
            </span>
        `;
    }
});

function showToast(title, message, icon = "check_circle") {
    const toast = document.getElementById("toast");
    const toastTitle = document.getElementById("toastTitle");
    const toastMessage = document.getElementById("toastMessage");
    const toastIcon = document.getElementById("toastIcon");
    if (!toast || !toastTitle || !toastMessage || !toastIcon) return;
    toastTitle.textContent = title;
    toastMessage.textContent = message;
    toastIcon.textContent = icon;
    toast.classList.remove("translate-y-24", "opacity-0");
    toast.classList.add("translate-y-0", "opacity-100");
    setTimeout(() => {
        toast.classList.remove("translate-y-0", "opacity-100");
        toast.classList.add("translate-y-24", "opacity-0");
    }, 4000);
}
