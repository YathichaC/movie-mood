let currentUsername = null;
let originalUsername = "";
let originalEmail = "";

document.addEventListener("DOMContentLoaded", async () => {
    try {
        const response = await apiFetch("/v1/auth/me");
        if (response && response.ok) {
            const user = await response.json();
            currentUsername = user.username;
            const usernameInput = document.getElementById("username");
            const emailInput = document.getElementById("email");
            if (usernameInput) {
                usernameInput.value = user.username || "";
                originalUsername = user.username || "";
            }
            if (emailInput) {
                emailInput.value = user.email || "";
                originalEmail = user.email || "";
            }
            setupAccountChangeDetection();
        }
    } catch (error) {
        console.error("Failed to load current user:", error);
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
        const saveButton = document.getElementById("save-account-btn");

        if (!usernameInput || !emailInput || !saveButton) {
            return;
        }

        function checkForChanges() {
            const usernameChanged = usernameInput.value.trim() !== originalUsername;
            const emailChanged = emailInput.value.trim() !== originalEmail;
            const hasChanges = usernameChanged || emailChanged;

            if (hasChanges) {
                saveButton.classList.remove("hidden");
            } else {
                saveButton.classList.add("hidden");
            }
        }

        usernameInput.addEventListener("input", checkForChanges);
        emailInput.addEventListener("input", checkForChanges);
        saveButton.addEventListener("click", saveProfile);
        checkForChanges();
    }

    async function saveProfile() {
        const usernameInput = document.getElementById("username");
        const emailInput = document.getElementById("email");
        const saveButton = document.getElementById("save-account-btn");

        if (!usernameInput || !emailInput || !saveButton) {
            return;
        }

        const username = usernameInput.value.trim();
        const email = emailInput.value.trim();

        if (!username || !email) {
            showNotification("Username and email are required.", true);
            return;
        }

        saveButton.disabled = true;
        saveButton.textContent = "Saving...";

        try {
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
