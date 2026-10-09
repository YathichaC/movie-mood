document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("reset-request-form");
    if (!form) return;

    const toast = document.getElementById("status-toast");
    const toastMessage = document.getElementById("status-message");
    let toastTimer;

    function showToast(message) {
        if (!toast || !toastMessage) return;

        toastMessage.textContent = message;

        toast.classList.remove(
            "opacity-0",
            "translate-x-10",
            "pointer-events-none"
        );

        toast.classList.add("opacity-100", "translate-x-0");

        clearTimeout(toastTimer);

        toastTimer = setTimeout(() => {
            toast.classList.add(
                "opacity-0",
                "translate-x-10",
                "pointer-events-none"
            );
            toast.classList.remove("opacity-100", "translate-x-0");
        }, 3500);
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const email = document.getElementById("email").value.trim();
        const submitBtn = form.querySelector("button[type='submit']");

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = "Sending...";
        }

        try {
            const response = await fetch("/api/v1/auth/forgot-password", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email })
            });

            const result = await response.json().catch(() => ({}));

            if (!response.ok) {
                showToast(
                    response.status === 404
                        ? "No account found with this email address."
                        : (result.message ||
                           "Failed to send reset instructions. Please try again.")
                );

                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.textContent = "Send Reset Instructions";
                }
                return;
            }

            sessionStorage.setItem("resetEmail", email);
            window.location.href = "/auth/password-reset";

        } catch (error) {
            console.error("Forgot password error:", error);
            showToast(
                "Unable to connect to the server. Please check your connection and try again."
            );

            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = "Send Reset Instructions";
            }
        }
    });
});