document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('playlistModal');
    const openBtn = document.getElementById('watchlistActionBtn');
    const closeBtn = document.getElementById('closePlaylistModal');
    const cancelBtn = document.getElementById('cancelPlaylistModal');
    const saveBtn = document.getElementById('savePlaylistsBtn');
    const toggleCreateBtn = document.getElementById('toggleCreatePlaylistBtn');
    const createForm = document.getElementById('createPlaylistForm');
    const newPlaylistInput = document.getElementById('newPlaylistNameInput');
    const confirmCreateBtn = document.getElementById('confirmCreatePlaylistBtn');
    const playlistsList = document.getElementById('playlistsList');
    const searchInput = document.getElementById('searchPlaylistsInput');
    const feedbackBanner = document.getElementById('feedbackBanner');

    const openModal = () => {
        modal.style.display = 'flex';
        modal.classList.remove('hidden');
    };

    const closeModal = () => {
        modal.style.display = 'none';
    };

    if (openBtn) {
        openBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            openModal();
        });
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', closeModal);
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', closeModal);
    }

    if (modal) {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeModal();
            }
        });
    }

    if (toggleCreateBtn) {
        toggleCreateBtn.addEventListener('click', () => {
            createForm.classList.toggle('hidden');

            if (!createForm.classList.contains('hidden')) {
                newPlaylistInput.focus();
            }
        });
    }

    if (confirmCreateBtn) {
        confirmCreateBtn.addEventListener('click', () => {
            const name = newPlaylistInput.value.trim();

            if (!name) {
                return;
            }

            const template = document.getElementById('playlistItemTemplate');
            const item = template.content.cloneNode(true);
            const nameElement = item.querySelector('[data-playlist-name]');

            if (nameElement) {
                nameElement.textContent = name;
            }

            playlistsList.prepend(item);
            newPlaylistInput.value = '';
            createForm.classList.add('hidden');
        });
    }

    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            const query = e.target.value.toLowerCase();
            const items = playlistsList.querySelectorAll('label');

            items.forEach((item) => {
                const text = item.textContent.toLowerCase();

                item.style.display = text.includes(query) ? 'flex' : 'none';
            });
        });
    }

    if (saveBtn) {
        saveBtn.addEventListener('click', () => {
            closeModal();

            if (feedbackBanner) {
                feedbackBanner.style.display = 'block';
            }
        });
    }
});