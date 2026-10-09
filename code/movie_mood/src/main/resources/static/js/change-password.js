const urlParams = new URLSearchParams(window.location.search);
const resetToken = urlParams.get("token");

function togglePasswordVisibility(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icon = document.getElementById(iconId);
    if (!input || !icon) return;
    if (input.type === 'password') {
        input.type = 'text';
        icon.textContent = 'visibility_off';
    } else {
        input.type = 'password';
        icon.textContent = 'visibility';
    }
}

function validatePassword(password) {
    return {
        length: password.length >= 8,
        upper: /[A-Z]/.test(password),
        lower: /[a-z]/.test(password),
        number: /[0-9]/.test(password),
        special: /[^A-Za-z0-9]/.test(password)
    };
}

function updateRequirement(elementId, isValid) {
    const item = document.getElementById(elementId);
    if (!item) return;

    const bullet = item.querySelector(".bullet");
    const icon = item.querySelector(".material-symbols-outlined");

    if (isValid) {
        item.classList.remove("text-[#737373]");
        item.classList.add("text-[#B8A58A]");
        if (bullet) {
            bullet.textContent = "✓";
            bullet.classList.remove("border-[#404040]");
            bullet.classList.add("border-[#B8A58A]", "text-[#B8A58A]");
        }
        if (icon) {
            icon.textContent = "check_circle";
            icon.classList.remove("text-[#737373]");
            icon.classList.add("text-[#B8A58A]");
        }
    } else {
        item.classList.remove("text-[#B8A58A]");
        item.classList.add("text-[#737373]");
        if (bullet) {
            bullet.textContent = "○";
            bullet.classList.remove("border-[#B8A58A]", "text-[#B8A58A]");
            bullet.classList.add("border-[#404040]");
        }
        if (icon) {
            icon.textContent = "radio_button_unchecked";
            icon.classList.remove("text-[#B8A58A]");
            icon.classList.add("text-[#737373]");
        }
    }
}

function validatePasswordStrength(pwd) {
    const rules = validatePassword(pwd);

    updateRequirement('req-length', rules.length);
    updateRequirement('req-upper', rules.upper);
    updateRequirement('req-lower', rules.lower);
    updateRequirement('req-number', rules.number);
    updateRequirement('req-special', rules.special);

    validateMatch();
}

function validateMatch() {
    const newPwd = document.getElementById('new-password')?.value || "";
    const confPwd = document.getElementById('confirm-password')?.value || "";
    const badge = document.getElementById('match-badge');

    if (badge) {
        if (newPwd && confPwd && newPwd === confPwd) {
            badge.classList.remove('hidden');
            badge.classList.add('flex');
        } else {
            badge.classList.add('hidden');
            badge.classList.remove('flex');
        }
    }
}

let toastTimeout = null;

function showNotification(message, isError = false) {
    const toast = document.getElementById("notification-toast");

    if (!toast) {
        return;
    }

    const icon = toast.querySelector(".material-symbols-outlined");
    const text = toast.querySelector("span:last-child");

    if (text) {
        text.textContent = message;
    }

    if (isError) {
        if (icon) {
            icon.textContent = "error";
            icon.classList.remove("text-green-400");
            icon.classList.add("text-red-400");
        }
    } else {
        if (icon) {
            icon.textContent = "check_circle";
            icon.classList.remove("text-red-400");
            icon.classList.add("text-green-400");
        }
    }

    toast.classList.remove("translate-x-full", "opacity-0");

    if (toastTimeout) {
        clearTimeout(toastTimeout);
    }

    toastTimeout = setTimeout(() => {
        toast.classList.add("translate-x-full", "opacity-0");
    }, 3000);
}

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("password-form");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const newPassword = document.getElementById("new-password")?.value || "";
        const confirmPassword = document.getElementById("confirm-password")?.value || "";

        const rules = validatePassword(newPassword);
        const validPassword = Object.values(rules).every(Boolean);

        if (!validPassword) {
            showNotification("Password must be at least 8 characters and include uppercase, lowercase, a number, and a special character.", true);
            return;
        }

        if (newPassword !== confirmPassword) {
            showNotification("Passwords do not match!", true);
            return;
        }

        if (!resetToken) {
            showNotification("Invalid or missing reset token. Please request a new password reset link.", true);
            return;
        }

        try {
            const response = await fetch("/api/v1/auth/reset-password", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    token: resetToken,
                    newPassword: newPassword
                })
            });

            const data = await response.json();
            if (response.ok) {
                showNotification("Password has been reset successfully! Redirecting to login...");
                setTimeout(() => {
                    window.location.href = "/auth/login";
                }, 1500);
            } else {
                showNotification(data.message || "Failed to reset password.", true);
            }
        } catch (error) {
            console.error("Error resetting password:", error);
            showNotification("Error connecting to server.", true);
        }
    });
});