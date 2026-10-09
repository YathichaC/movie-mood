document.addEventListener('DOMContentLoaded', async () => {
    const genreCheckboxes = document.querySelectorAll('input[name="dislikedGenres"]');
    const saveGenresBtn = document.getElementById('save-genres-btn');
    const genreCounter = document.getElementById('genre-counter');
    const toast = document.getElementById('notification-toast');

    if (!genreCheckboxes.length) {
        return;
    }

    let originalGenres = [];
    let toastTimeout;

    function showToast(message, type = 'success') {
        if (!toast) return;

        const icon = toast.querySelector('.material-symbols-outlined');
        const text = toast.querySelector('span:last-child');

        clearTimeout(toastTimeout);

        toast.className =
            'fixed top-6 right-6 z-50 transform transition-all duration-300 flex items-center gap-3 bg-[#0A0A0A] border border-[#262626] px-5 py-3.5 rounded-sm shadow-2xl backdrop-blur-md font-outfit text-[#F5F5F5]';

        if (icon) {
            icon.textContent = type === 'error'
                ? 'error'
                : 'check_circle';

            icon.className = type === 'error'
                ? 'material-symbols-outlined text-red-500 text-xl'
                : 'material-symbols-outlined text-primary text-xl';
        }

        if (text) {
            text.textContent = message;
        }

        toast.classList.remove(
            'translate-x-full',
            'opacity-0',
            'pointer-events-none'
        );

        toast.classList.add(
            'translate-x-0',
            'opacity-100'
        );

        toastTimeout = setTimeout(() => {
            toast.classList.remove(
                'translate-x-0',
                'opacity-100'
            );

            toast.classList.add(
                'translate-x-full',
                'opacity-0',
                'pointer-events-none'
            );
        }, 3000);
    }

    function getCurrentGenres() {
        return Array.from(genreCheckboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.value)
            .sort();
    }

    function hasGenreChanges() {
        return JSON.stringify(getCurrentGenres()) !==
            JSON.stringify(originalGenres);
    }

    function updateGenreBoxes() {
        let count = 0;

        genreCheckboxes.forEach(checkbox => {
            const box = checkbox.closest('.genre-box');
            const labelText = box?.querySelector('span');

            if (checkbox.checked) {
                count++;

                box?.classList.remove(
                    'border-[#262626]',
                    'bg-[#121212]',
                    'hover:border-[#404040]'
                );

                box?.classList.add(
                    'border-[#d8c4a7]',
                    'bg-[#d8c4a7]/10',
                    'hover:border-[#d8c4a7]'
                );

                labelText?.classList.remove('text-[#F5F5F5]');
                labelText?.classList.add('text-[#d8c4a7]');
            } else {
                box?.classList.remove(
                    'border-[#d8c4a7]',
                    'bg-[#d8c4a7]/10',
                    'hover:border-[#d8c4a7]'
                );

                box?.classList.add(
                    'border-[#262626]',
                    'bg-[#121212]',
                    'hover:border-[#d8c4a7]'
                );

                labelText?.classList.remove('text-[#d8c4a7]');
                labelText?.classList.add('text-[#F5F5F5]');
            }

            checkbox.style.accentColor = '#d8c4a7';
        });

        if (genreCounter) {
            genreCounter.textContent = `${count} categories excluded`;
        }

        saveGenresBtn?.classList.toggle(
            'hidden',
            !hasGenreChanges()
        );
    }

    async function loadDislikedGenres() {
        try {
            const userId = localStorage.getItem("userId");

            if (!userId) {
                showToast("Please log in again.", "error");
                return;
            }

            const preferenceResponse = await apiFetch(
                `/v1/${userId}/preferences/disliked-genres`
            );

            if (!preferenceResponse || !preferenceResponse.ok) {
                showToast("Failed to load preferences.", "error");
                return;
            }

            const preference = await preferenceResponse.json();
            const dislikedGenreIds = preference.dislikedGenreIds || [];

            genreCheckboxes.forEach(checkbox => {
                checkbox.checked = dislikedGenreIds.includes(
                    checkbox.value
                );
            });

            originalGenres = getCurrentGenres();
            updateGenreBoxes();
        } catch (error) {
            console.error(
                'Failed to load disliked genres:',
                error
            );
        }
    }

    genreCheckboxes.forEach(checkbox => {
        checkbox.classList.remove('accent-primary');

        checkbox.addEventListener(
            'change',
            updateGenreBoxes
        );
    });

    saveGenresBtn?.classList.add('hidden');

    saveGenresBtn?.addEventListener('click', async event => {
        event.preventDefault();

        if (!hasGenreChanges()) {
            return;
        }

        try {
            const userId = localStorage.getItem("userId");

            if (!userId) {
                showToast("Please log in again.", "error");
                return;
            }

            const dislikedGenreIds = getCurrentGenres();

            const response = await apiFetch(
                `/v1/${userId}/preferences/disliked-genres`,
                {
                    method: 'PUT',
                    body: JSON.stringify(dislikedGenreIds)
                }
            );

            if (!response || !response.ok) {
                showToast(
                    'Failed to save preferences.',
                    'error'
                );
                return;
            }

            originalGenres = getCurrentGenres();
            updateGenreBoxes();

            showToast(
                'Preferences saved successfully',
                'success'
            );
        } catch (error) {
            console.error(
                'Failed to save disliked genres:',
                error
            );

            showToast(
                'Failed to save preferences.',
                'error'
            );
        }
    });

    await loadDislikedGenres();
});