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
    const openModal = (id = 'playlistModal') => {
        const target = document.getElementById(id);
        if (target) {
            target.classList.remove('hidden');
            target.classList.add('flex');
            document.body.style.overflow = 'hidden';
        }
    };
    const closeModal = (id = 'playlistModal') => {
        const target = document.getElementById(id);
        if (target) {
            target.classList.add('hidden');
            target.classList.remove('flex');
            document.body.style.overflow = '';
        }
    };
    if (openBtn) {
        openBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            openModal();
        });
    }
    if (closeBtn) {
        closeBtn.addEventListener('click', () => closeModal());
    }
    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => closeModal());
    }
    if (modal) {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeModal();
            }
        });
    }
    if (toggleCreateBtn && createForm) {
        toggleCreateBtn.addEventListener('click', () => {
            createForm.classList.toggle('hidden');
            if (!createForm.classList.contains('hidden') && newPlaylistInput) {
                newPlaylistInput.focus();
            }
        });
    }
    if (confirmCreateBtn && newPlaylistInput && playlistsList) {
        confirmCreateBtn.addEventListener('click', () => {
            const name = newPlaylistInput.value.trim();
            if (!name) {
                return;
            }
            const template = document.getElementById('playlistItemTemplate');
            if (template) {
                const item = template.content.cloneNode(true);
                const nameElement = item.querySelector('[data-playlist-name]');
                if (nameElement) {
                    nameElement.textContent = name;
                }
                playlistsList.prepend(item);
            }
            newPlaylistInput.value = '';
            if (createForm) {
                createForm.classList.add('hidden');
            }
        });
    }
    if (searchInput && playlistsList) {
        searchInput.addEventListener('input', (e) => {
            const query = e.target.value.toLowerCase();
            const items = playlistsList.querySelectorAll('[data-playlist-name]');
            items.forEach((item) => {
                const name = item.textContent.toLowerCase();
                const row = item.closest('.group') || item.closest('label');
                if (row) {
                    row.style.display = name.includes(query) ? '' : 'none';
                }
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
    window.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            closeModal('playlistModal');
            closeModal('editModal');
            closeModal('deleteModal');
        }
    });
    window.openModal = openModal;
    window.closeModal = closeModal;
    window.toggleRemoveRow = (btn) => {
        const isRemoved = btn.getAttribute('data-removed') === 'true';
        if (!isRemoved) {
            btn.textContent = 'Undo';
            btn.setAttribute('data-removed', 'true');
            btn.classList.add('border-white', 'text-white');
            btn.parentElement.classList.add('opacity-40');
        } else {
            btn.textContent = 'Remove';
            btn.setAttribute('data-removed', 'false');
            btn.classList.remove('border-white', 'text-white');
            btn.parentElement.classList.remove('opacity-40');
        }
    };
    window.removeItem = (btn) => {
        const card = btn.closest('article');
        if (card) {
            card.style.opacity = '0';
            card.style.transform = 'scale(0.95)';
            card.style.transition = 'all 0.25s ease';
            setTimeout(() => {
                card.remove();
            }, 250);
        }
    };
});