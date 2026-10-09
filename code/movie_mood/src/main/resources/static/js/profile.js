let currentUsername = null;
let originalUsername = "";
let originalEmail = "";

function getStoredProfile() {
    return {
        username: localStorage.getItem("username")?.trim() || "",
        email: localStorage.getItem("email")?.trim() || ""
    };
}

function updatePasswordRequirement(elementId, isValid) {
    const item = document.getElementById(elementId);
    if (!item) return;

    const icon = item.querySelector(".material-symbols-outlined");

    if (isValid) {
        item.classList.remove("text-[#737373]", "text-on-surface-variant");
        item.classList.add("text-[#B8A58A]");
        if (icon) {
            icon.textContent = "check_circle";
            icon.classList.remove("text-[#737373]", "text-outline");
            icon.classList.add("text-[#B8A58A]");
        }
    } else {
        item.classList.remove("text-[#B8A58A]", "text-on-surface");
        item.classList.add("text-[#737373]");
        if (icon) {
            icon.textContent = "radio_button_unchecked";
            icon.classList.remove("text-[#B8A58A]");
            icon.classList.add("text-[#737373]");
        }
    }
}

function validateProfilePassword(password) {
    return {
        length: password.length >= 8,
        upper: /[A-Z]/.test(password),
        lower: /[a-z]/.test(password),
        number: /[0-9]/.test(password),
        special: /[^A-Za-z0-9]/.test(password)
    };
}

function updatePasswordValidationState() {
    const newPassword = document.getElementById("new-password");
    const confirmPassword = document.getElementById("confirm-password");
    const matchStatusMsg = document.getElementById("match-status-msg");

    if (!newPassword || !confirmPassword || !matchStatusMsg) {
        return true;
    }

    const password = newPassword.value;
    const confirm = confirmPassword.value;
    const rules = validateProfilePassword(password);

    updatePasswordRequirement("req-length", rules.length);
    updatePasswordRequirement("req-upper", rules.upper);
    updatePasswordRequirement("req-lower", rules.lower);
    updatePasswordRequirement("req-number", rules.number);
    updatePasswordRequirement("req-special", rules.special);

    const dot = matchStatusMsg.querySelector("span:first-child");
    const text = matchStatusMsg.querySelector("span:last-child");
    const hasPasswordInput = password.length > 0 || confirm.length > 0;

    if (!hasPasswordInput) {
        if (dot) {
            dot.className = "w-1.5 h-1.5 rounded-full bg-[#525252]";
        }
        if (text) {
            text.textContent = "Both entries must correspond accurately";
            text.className = "text-[#737373]";
        }
        return true;
    }

    const allRulesPassed = Object.values(rules).every(Boolean);
    const passwordsMatch = password === confirm && password.length > 0;

    if (!allRulesPassed) {
        if (dot) {
            dot.className = "w-1.5 h-1.5 rounded-full bg-red-500";
        }
        if (text) {
            text.textContent = "Password does not meet the required criteria";
            text.className = "text-red-400";
        }
        return false;
    }

    if (!passwordsMatch) {
        if (dot) {
            dot.className = "w-1.5 h-1.5 rounded-full bg-red-500";
        }
        if (text) {
            text.textContent = "Passwords do not match";
            text.className = "text-red-400";
        }
        return false;
    }

    if (dot) {
        dot.className = "w-1.5 h-1.5 rounded-full bg-green-500";
    }
    if (text) {
        text.textContent = "Password meets all requirements";
        text.className = "text-green-400";
    }
    return true;
}

document.addEventListener("DOMContentLoaded", async () => {
    const storedProfile = getStoredProfile();

    try {
        const response = await apiFetch("/v1/auth/me");
        if (response && response.ok) {
            const user = await response.json();
            currentUsername = user.username;
            const usernameInput = document.getElementById("username");
            const emailInput = document.getElementById("email");
            if (usernameInput) {
                const usernameValue = user.username || storedProfile.username || "";
                usernameInput.value = usernameValue;
                originalUsername = usernameValue;
                localStorage.setItem("username", usernameValue);
            }
            if (emailInput) {
                const emailValue = user.email || storedProfile.email || "";
                emailInput.value = emailValue;
                originalEmail = emailValue;
                localStorage.setItem("email", emailValue);
            }
            setupAccountChangeDetection();
        } else {
            const usernameInput = document.getElementById("username");
            const emailInput = document.getElementById("email");
            if (usernameInput) {
                usernameInput.value = storedProfile.username;
                originalUsername = storedProfile.username;
            }
            if (emailInput) {
                emailInput.value = storedProfile.email;
                originalEmail = storedProfile.email;
            }
            setupAccountChangeDetection();
        }
    } catch (error) {
        console.error("Failed to load current user:", error);
        const usernameInput = document.getElementById("username");
        const emailInput = document.getElementById("email");
        if (usernameInput) {
            usernameInput.value = storedProfile.username;
            originalUsername = storedProfile.username;
        }
        if (emailInput) {
            emailInput.value = storedProfile.email;
            originalEmail = storedProfile.email;
        }
        setupAccountChangeDetection();
    }

    const modal = document.getElementById("delete-modal");
    const openButton = document.getElementById("open-delete-modal-btn");
    const closeButton = document.getElementById("modal-close-icon");
    const cancelButton = document.getElementById("modal-cancel-btn");
    const usernameInput = document.getElementById("modal-username");
    const deleteButton = document.getElementById("modal-delete-confirm-btn");
    const errorMessage = document.getElementById("modal-error-msg");

    if (!modal || !openButton || !closeButton || !cancelButton || !usernameInput || !deleteButton || !errorMessage) {
        return;
    }

    function openModal() {
        modal.classList.remove("hidden");
        requestAnimationFrame(() => {
            modal.classList.remove("opacity-0");
        });
        usernameInput.value = "";
        updateDeleteButton();
    }

    function closeModal() {
        modal.classList.add("opacity-0");
        setTimeout(() => {
            modal.classList.add("hidden");
        }, 150);
        usernameInput.value = "";
        updateDeleteButton();
    }

    function updateDeleteButton() {
        const enteredUsername = usernameInput.value.trim();
        const isMatch = currentUsername && enteredUsername === currentUsername;
        deleteButton.disabled = !isMatch;

        if (isMatch) {
            deleteButton.classList.remove("opacity-40", "cursor-not-allowed");
            deleteButton.classList.add("opacity-100");
            errorMessage.classList.add("hidden");
        } else {
            deleteButton.classList.add("opacity-40", "cursor-not-allowed");
            deleteButton.classList.remove("opacity-100");

            if (enteredUsername.length > 0) {
                errorMessage.classList.remove("hidden");
            } else {
                errorMessage.classList.add("hidden");
            }
        }
    }

    async function deleteAccount() {
        if (!currentUsername) {
            return;
        }

        if (usernameInput.value.trim() !== currentUsername) {
            return;
        }

        deleteButton.disabled = true;
        deleteButton.textContent = "Deleting...";

        try {
            const response = await apiFetch("/v1/users/me", {
                method: "DELETE"
            });

            if (!response) {
                return;
            }

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || "Failed to delete account");
            }

            localStorage.removeItem("token");
            window.location.href = "/auth/login";
        } catch (error) {
            console.error("Delete account failed:", error);
            deleteButton.disabled = false;
            deleteButton.textContent = "Delete Account";
            errorMessage.textContent = error.message || "Failed to delete account.";
            errorMessage.classList.remove("hidden");
        }
    }

    function setupAccountChangeDetection() {
        const usernameInput = document.getElementById("username");
        const emailInput = document.getElementById("email");
        const newPasswordInput = document.getElementById("new-password");
        const confirmPasswordInput = document.getElementById("confirm-password");
        const saveButton = document.getElementById("save-account-btn");

        if (!usernameInput || !emailInput || !saveButton) {
            return;
        }

        function checkForChanges() {
            const username = usernameInput.value.trim();
            const email = emailInput.value.trim();
            const newPassword = newPasswordInput ? newPasswordInput.value : "";
            const confirmPassword = confirmPasswordInput ? confirmPasswordInput.value : "";
            const usernameChanged = username !== originalUsername;
            const emailChanged = email !== originalEmail;
            const passwordChanged = newPassword.length > 0 || confirmPassword.length > 0;
            const usernameValid = username.length >= 3 && /^[A-Za-z0-9_]+$/.test(username);
            const emailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
            const passwordValid = !passwordChanged || updatePasswordValidationState();
            const hasChanges = usernameChanged || emailChanged || passwordChanged;
            const canSave = hasChanges && usernameValid && emailValid && passwordValid;

            saveButton.disabled = !canSave;
            if (hasChanges && canSave) {
                saveButton.classList.remove("hidden");
            } else {
                saveButton.classList.add("hidden");
            }
        }

        usernameInput.addEventListener("input", checkForChanges);
        emailInput.addEventListener("input", checkForChanges);
        if (newPasswordInput) {
            newPasswordInput.addEventListener("input", checkForChanges);
            newPasswordInput.addEventListener("input", updatePasswordValidationState);
        }
        if (confirmPasswordInput) {
            confirmPasswordInput.addEventListener("input", checkForChanges);
            confirmPasswordInput.addEventListener("input", updatePasswordValidationState);
        }
        const toggleNewPwBtn = document.getElementById("toggle-new-pw-vis");
        const toggleConfirmPwBtn = document.getElementById("toggle-confirm-pw-vis");
        if (toggleNewPwBtn) {
            toggleNewPwBtn.addEventListener("click", () => {
                const input = document.getElementById("new-password");
                const icon = document.getElementById("new-pw-icon");
                if (!input || !icon) return;
                const isPassword = input.type === "password";
                input.type = isPassword ? "text" : "password";
                icon.textContent = isPassword ? "visibility_off" : "visibility";
            });
        }
        if (toggleConfirmPwBtn) {
            toggleConfirmPwBtn.addEventListener("click", () => {
                const input = document.getElementById("confirm-password");
                const icon = document.getElementById("confirm-pw-icon");
                if (!input || !icon) return;
                const isPassword = input.type === "password";
                input.type = isPassword ? "text" : "password";
                icon.textContent = isPassword ? "visibility_off" : "visibility";
            });
        }
        saveButton.addEventListener("click", saveProfile);
        checkForChanges();
        updatePasswordValidationState();
    }

    async function saveProfile() {
        const usernameInput = document.getElementById("username");
        const emailInput = document.getElementById("email");
        const newPasswordInput = document.getElementById("new-password");
        const confirmPasswordInput = document.getElementById("confirm-password");
        const saveButton = document.getElementById("save-account-btn");

        if (!usernameInput || !emailInput || !saveButton) {
            return;
        }

        const username = usernameInput.value.trim();
        const email = emailInput.value.trim();
        const newPassword = newPasswordInput ? newPasswordInput.value : "";
        const confirmPassword = confirmPasswordInput ? confirmPasswordInput.value : "";

        if (!username || !email) {
            showNotification("Username and email are required.", true);
            return;
        }

        if (!/^[A-Za-z0-9_]+$/.test(username) || username.length < 3) {
            showNotification("Username must be at least 3 characters and use letters, numbers, or underscores.", true);
            return;
        }

        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            showNotification("Please enter a valid email address.", true);
            return;
        }

        if (newPassword || confirmPassword) {
            const passwordRules = validateProfilePassword(newPassword);
            const validPassword = Object.values(passwordRules).every(Boolean);
            if (!validPassword) {
                showNotification("Password must be at least 8 characters and include uppercase, lowercase, a number, and a special character.", true);
                return;
            }
            if (newPassword !== confirmPassword) {
                showNotification("Passwords do not match.", true);
                return;
            }
        }

        saveButton.disabled = true;
        saveButton.textContent = "Saving...";

        try {
            if (username !== originalUsername || email !== originalEmail) {
                const response = await apiFetch("/v1/users/profile", {
                    method: "PATCH",
                    body: JSON.stringify({
                        username: username,
                        email: email
                    })
                });

                if (!response) {
                    return;
                }

                const data = await response.json();

                if (!response.ok) {
                    throw new Error(data.message || "Failed to update profile");
                }

                originalUsername = data.username;
                originalEmail = data.email;
                currentUsername = data.username;
                localStorage.setItem("username", data.username);
                localStorage.setItem("email", data.email);
            }

            if (newPassword) {
                const passwordResponse = await apiFetch("/v1/users/password", {
                    method: "PATCH",
                    body: JSON.stringify({
                        newPassword: newPassword
                    })
                });

                if (!passwordResponse) {
                    return;
                }

                const passwordData = await passwordResponse.json();

                if (!passwordResponse.ok) {
                    throw new Error(passwordData.message || "Failed to update password");
                }
            }

            if (newPasswordInput) newPasswordInput.value = "";
            if (confirmPasswordInput) confirmPasswordInput.value = "";
            updatePasswordValidationState();
            saveButton.classList.add("hidden");
            saveButton.disabled = false;
            saveButton.textContent = "Save Changes";
            showNotification("Profile saved successfully.");
        } catch (error) {
            console.error("Failed to save profile:", error);
            saveButton.disabled = false;
            saveButton.textContent = "Save Changes";
            showNotification(error.message || "Failed to save profile.", true);
        }
    }

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
            icon.textContent = "error";
            icon.classList.remove("text-green-400");
            icon.classList.add("text-red-400");
        } else {
            icon.textContent = "check_circle";
            icon.classList.remove("text-red-400");
            icon.classList.add("text-green-400");
        }

        toast.classList.remove("translate-x-full", "opacity-0");

        setTimeout(() => {
            toast.classList.add("translate-x-full", "opacity-0");
        }, 3000);
    }

    openButton.addEventListener("click", openModal);
    closeButton.addEventListener("click", closeModal);
    cancelButton.addEventListener("click", closeModal);
    usernameInput.addEventListener("input", updateDeleteButton);
    deleteButton.addEventListener("click", deleteAccount);

    modal.addEventListener("click", (event) => {
        if (event.target === modal) {
            closeModal();
        }
    });

});
