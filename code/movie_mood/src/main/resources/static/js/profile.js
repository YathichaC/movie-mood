let currentUsername = null;

document.addEventListener("DOMContentLoaded", async () => {
    const modal = document.getElementById("delete-modal");
    const openButton = document.getElementById("open-delete-modal-btn");
    const closeButton = document.getElementById("modal-close-icon");
    const cancelButton = document.getElementById("modal-cancel-btn");
    const usernameInput = document.getElementById("modal-username");
    const deleteButton = document.getElementById("modal-delete-confirm-btn");
    const errorMessage = document.getElementById("modal-error-msg");

    if (!modal || !openButton) {
        return;
    }

    try {
        const response = await apiFetch("/v1/auth/me");

        if (!response || !response.ok) {
            return;
        }

        const user = await response.json();
        currentUsername = user.username;

    } catch (error) {
        console.error("Failed to load current user:", error);
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

        const isMatch =
            currentUsername &&
            enteredUsername === currentUsername;

        deleteButton.disabled = !isMatch;

        if (isMatch) {
            deleteButton.classList.remove(
                "opacity-40",
                "cursor-not-allowed"
            );

            deleteButton.classList.add("opacity-100");

            errorMessage.classList.add("hidden");
        } else {
            deleteButton.classList.add(
                "opacity-40",
                "cursor-not-allowed"
            );

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
                throw new Error(
                    data.message || "Failed to delete account"
                );
            }

            // Remove JWT after successful deletion
            localStorage.removeItem("token");

            window.location.href = "/auth/login";

        } catch (error) {
            console.error("Delete account failed:", error);

            deleteButton.disabled = false;
            deleteButton.textContent = "Delete Account";

            errorMessage.textContent =
                error.message || "Failed to delete account.";

            errorMessage.classList.remove("hidden");
        }
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
