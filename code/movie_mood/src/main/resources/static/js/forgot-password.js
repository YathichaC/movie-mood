document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("reset-request-form");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const emailInput = document.getElementById("email");
        const email = emailInput ? emailInput.value : "";

        try {
            await fetch("/api/v1/auth/forgot-password", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email: email })
            });

            window.location.href = "/auth/password-email";
        } catch (error) {
            console.error("Error requesting password reset:", error);
            window.location.href = "/auth/password-email";
        }
    });
});