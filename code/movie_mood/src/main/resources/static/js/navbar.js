document.addEventListener("DOMContentLoaded", () => {
    const usernameElement =
        document.getElementById("navbarUsername");

    if (!usernameElement) return;

    const username = localStorage.getItem("username");

    if (username) {
        usernameElement.textContent = username;
    }
});
