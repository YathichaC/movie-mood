document.addEventListener('DOMContentLoaded', () => {
    const actionsBar = document.getElementById('profile-actions');
    const saveButton = document.getElementById('save-changes');
    const cancelButton = document.getElementById('cancel-changes');

    const notification = document.getElementById('success-notification');
    const closeButton = document.getElementById('close-success-notification');

    const inputs = [
        document.getElementById('username'),
        document.getElementById('email'),
        document.getElementById('current-pass'),
        document.getElementById('new-pass')
    ].filter(Boolean);

    if (!actionsBar || !saveButton || !cancelButton) {
        console.error('Profile actions elements not found.');
        return;
    }

    let originalValues = inputs.map(input => input.value);
    let notificationTimeout;

    function checkForChanges() {
        const hasChanges = inputs.some(
            (input, index) => input.value !== originalValues[index]
        );

        if (hasChanges) {
            actionsBar.classList.remove('hidden');
            actionsBar.classList.add('flex');
        } else {
            actionsBar.classList.add('hidden');
            actionsBar.classList.remove('flex');
        }
    }

    function showSuccessNotification() {
        if (!notification) return;

        clearTimeout(notificationTimeout);
        notification.classList.remove('hidden');

        notificationTimeout = setTimeout(() => {
            notification.classList.add('hidden');
        }, 3000);
    }

    function hideNotification() {
        if (!notification) return;

        clearTimeout(notificationTimeout);
        notification.classList.add('hidden');
    }

    inputs.forEach(input => {
        input.addEventListener('input', checkForChanges);
        input.addEventListener('change', checkForChanges);
    });

    saveButton.addEventListener('click', () => {
        actionsBar.classList.add('hidden');
        actionsBar.classList.remove('flex');

        originalValues = inputs.map(input => input.value);

        const currentPassword = document.getElementById('current-pass');
        const newPassword = document.getElementById('new-pass');

        if (currentPassword) currentPassword.value = '';
        if (newPassword) newPassword.value = '';

        originalValues = inputs.map(input => input.value);

        showSuccessNotification();
    });

    cancelButton.addEventListener('click', () => {
        inputs.forEach((input, index) => {
            input.value = originalValues[index];
        });

        actionsBar.classList.add('hidden');
        actionsBar.classList.remove('flex');

        hideNotification();
    });

    if (closeButton) {
        closeButton.addEventListener('click', hideNotification);
    }

    const genreCheckboxes = document.querySelectorAll(
        'input[name="dislikedGenres"]'
    );
    const genreCount = document.getElementById('disliked-genre-count');

    let originalGenreValues = Array.from(genreCheckboxes)
        .filter(checkbox => checkbox.checked)
        .map(checkbox => checkbox.value)
        .sort();

    function updateGenreSelection() {
        const selectedGenres = Array.from(genreCheckboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.value)
            .sort();

        if (genreCount) {
            genreCount.textContent = `${selectedGenres.length} selected`;
        }

        const genresChanged =
            JSON.stringify(selectedGenres) !==
            JSON.stringify(originalGenreValues);

        if (genresChanged) {
            actionsBar.classList.remove('hidden');
            actionsBar.classList.add('flex');
        } else {
            checkForChanges();
        }
    }

    genreCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateGenreSelection);
    });
});