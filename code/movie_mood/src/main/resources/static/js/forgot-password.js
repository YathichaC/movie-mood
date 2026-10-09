document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("reset-request-form");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const emailInput = document.getElementById("email");
        const email = emailInput ? emailInput.value.trim() : "";

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
                alert(result.message || "Failed to send reset instructions. Please try again.");
                return;
            }
            sessionStorage.setItem("resetEmail", email);

            window.location.href = "/auth/password-reset";

        } catch (error) {
            console.error("Forgot password error:", error);
            alert("Unable to connect to the server. Please check your connection and try again.");
        }
    });
});