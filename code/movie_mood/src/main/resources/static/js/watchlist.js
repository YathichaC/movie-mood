
let stagedCount = 0;
let modalAction = null;

function closeModal() {
    const modal = document.getElementById('confirmation-modal');
    if (!modal) return;
    modal.classList.add('hidden');
    modal.classList.remove('flex');
    modalAction = null;
}

function showModal({
    title = 'Confirmation',
    message = '',
    confirmText = 'Confirm',
    icon = 'info',
    type = 'danger',
    onConfirm = null,
    showConfirm = true
} = {}) {
    const modal = document.getElementById('confirmation-modal');
    const modalTitle = document.getElementById('confirmation-modal-title');
    const modalMessage = document.getElementById('confirmation-modal-message');
    const modalIcon = document.getElementById('confirmation-modal-icon');
    const iconWrapper = document.getElementById('confirmation-modal-icon-wrapper');
    const confirmBtn = document.getElementById('confirmation-modal-confirm');
    const cancelBtn = document.getElementById('confirmation-modal-cancel');
    if (!modal || !modalTitle || !modalMessage || !modalIcon || !iconWrapper || !confirmBtn || !cancelBtn) {
        console.error('Confirmation modal is missing from the page.');
        return;
    }
    modalTitle.textContent = title;
    modalMessage.textContent = message;
    modalIcon.textContent = icon;
    confirmBtn.textContent = confirmText;
    cancelBtn.textContent = showConfirm ? 'Cancel' : 'Close';
    modalAction = onConfirm;
    if (type === 'success') {
        iconWrapper.className = 'w-12 h-12 rounded-full bg-emerald-500/10 flex items-center justify-center mb-5';
        modalIcon.className = 'material-symbols-outlined text-emerald-500 text-3xl';
        confirmBtn.className = 'hidden px-4 py-2.5 rounded-lg text-sm font-semibold bg-emerald-600 text-white hover:bg-emerald-700 transition-colors';
    } else {
        iconWrapper.className = 'w-12 h-12 rounded-full bg-red-500/10 flex items-center justify-center mb-5';
        modalIcon.className = 'material-symbols-outlined text-red-500 text-3xl';
        confirmBtn.className = 'px-4 py-2.5 rounded-lg text-sm font-semibold bg-red-600 text-white hover:bg-red-700 transition-colors';
    }
    confirmBtn.classList.toggle('hidden', !showConfirm);
    modal.classList.remove('hidden');
    modal.classList.add('flex');
    cancelBtn.focus();
}

function updateStagedCounter() {
    const counter = document.getElementById('counter-num');
    if (counter) counter.textContent = stagedCount;
}

function toggleAddMovie(btn, title) {
    const isStaged = btn.dataset.staged === 'true';
    if (!isStaged) {
        btn.dataset.staged = 'true';
        btn.innerHTML = `
            <span class="material-symbols-outlined text-base">check</span>
            <span>Staged (Click to Undo)</span>
        `;
        btn.classList.remove('bg-primary-container', 'text-on-primary-container');
        btn.classList.add('bg-secondary/15', 'text-secondary', 'border', 'border-secondary');
        stagedCount++;
    } else {
        btn.dataset.staged = 'false';
        btn.innerHTML = `
            <span class="material-symbols-outlined text-base">add</span>
            <span>+ Add to Playlist</span>
        `;
        btn.classList.remove('bg-secondary/15', 'text-secondary', 'border', 'border-secondary');
        btn.classList.add('bg-primary-container', 'text-on-primary-container');
        stagedCount = Math.max(0, stagedCount - 1);
    }
    updateStagedCounter();
}

function commitBatchChanges() {
    if (stagedCount === 0) {
        showModal({
            title: 'No Movies Selected',
            message: 'Please add at least one movie to your selection before continuing.',
            icon: 'movie',
            type: 'danger',
            showConfirm: false
        });
        return;
    }
    showModal({
        title: 'Ready to Add Movies',
        message: `${stagedCount} movie(s) have been staged. Saving them to the playlist is not connected to the backend yet.`,
        icon: 'check_circle',
        type: 'success',
        showConfirm: false
    });
}

document.addEventListener('DOMContentLoaded', () => {
    let isEditMode = false;
    const editBtn = document.getElementById('playlist-edit-btn');
    const editBtnIcon = document.getElementById('playlist-edit-btn-icon');
    const editBtnLabel = document.getElementById('playlist-edit-btn-label');
    const titleDisplay = document.getElementById('playlist-title-display');
    const titleEditContainer = document.getElementById('playlist-title-edit-container');
    const titleInput = document.getElementById('playlist-title-input');
    const coverImage = document.getElementById('playlist-cover');
    const coverBtn = document.getElementById('playlist-cover-btn');
    const coverInput = document.getElementById('playlist-cover-input');
    const countDisplay = document.getElementById('playlist-count-display');
    const moviesGrid = document.getElementById('movies-grid');
    const deletePlaylistBtn = document.getElementById('playlist-delete-btn');
    const modal = document.getElementById('confirmation-modal');
    const modalCancel = document.getElementById('confirmation-modal-cancel');
    const modalConfirm = document.getElementById('confirmation-modal-confirm');
    const modalBackdrop = document.getElementById('confirmation-modal-backdrop');

    if (modalCancel) modalCancel.addEventListener('click', closeModal);
    if (modalBackdrop) modalBackdrop.addEventListener('click', closeModal);
    if (modalConfirm) {
        modalConfirm.addEventListener('click', () => {
            const action = modalAction;
            closeModal();
            if (typeof action === 'function') action();
        });
    }
    document.addEventListener('keydown', event => {
        if (event.key === 'Escape' && modal && !modal.classList.contains('hidden')) {
            closeModal();
        }
    });
    if (!editBtn || !titleDisplay || !titleInput || !moviesGrid) {
        updateStagedCounter();
        return;
    }
    let originalTitle = titleDisplay.textContent.trim();
    let originalCover = coverImage ? coverImage.src : '';

    function updateMovieCount() {
        const count = moviesGrid.querySelectorAll('.movie-card').length;
        if (countDisplay) countDisplay.textContent = `${count} Movie${count === 1 ? '' : 's'}`;
    }

    function showRemoveButtons(show) {
        moviesGrid.querySelectorAll('.movie-remove-btn').forEach(btn => {
            if (show) {
                btn.classList.remove('hidden');
                btn.classList.add('flex', 'items-center', 'justify-center');
            } else {
                btn.classList.add('hidden');
                btn.classList.remove('flex', 'items-center', 'justify-center');
            }
        });
    }

    function enterEditMode() {
        originalTitle = titleDisplay.textContent.trim();
        originalCover = coverImage ? coverImage.src : '';
        titleInput.value = originalTitle;
        titleDisplay.classList.add('hidden');
        titleEditContainer.classList.remove('hidden');
        if (coverBtn) {
            coverBtn.classList.remove('hidden');
            coverBtn.classList.add('flex');
        }
        showRemoveButtons(true);
        if (editBtnIcon) editBtnIcon.textContent = 'check';
        if (editBtnLabel) editBtnLabel.textContent = 'Save';
        editBtn.classList.add('bg-primary-container', 'text-on-primary-container');
        isEditMode = true;
        titleInput.focus();
    }

    function saveEditMode() {
        const newTitle = titleInput.value.trim();
        if (!newTitle) {
            showModal({
                title: 'Playlist Name Required',
                message: 'Playlist name cannot be empty. Please enter a name before saving.',
                icon: 'edit',
                type: 'danger',
                showConfirm: false
            });
            titleInput.focus();
            return;
        }
        titleDisplay.textContent = newTitle;
        titleDisplay.classList.remove('hidden');
        titleEditContainer.classList.add('hidden');
        if (coverBtn) {
            coverBtn.classList.add('hidden');
            coverBtn.classList.remove('flex');
        }
        showRemoveButtons(false);
        if (editBtnIcon) editBtnIcon.textContent = 'edit';
        if (editBtnLabel) editBtnLabel.textContent = 'Edit';
        editBtn.classList.remove('bg-primary-container', 'text-on-primary-container');
        originalTitle = newTitle;
        originalCover = coverImage ? coverImage.src : '';
        isEditMode = false;
    }

    function cancelEditMode() {
        titleInput.value = originalTitle;
        titleDisplay.textContent = originalTitle;
        titleDisplay.classList.remove('hidden');
        titleEditContainer.classList.add('hidden');
        if (coverImage && originalCover) coverImage.src = originalCover;
        if (coverBtn) {
            coverBtn.classList.add('hidden');
            coverBtn.classList.remove('flex');
        }
        showRemoveButtons(false);
        if (editBtnIcon) editBtnIcon.textContent = 'edit';
        if (editBtnLabel) editBtnLabel.textContent = 'Edit';
        editBtn.classList.remove('bg-primary-container', 'text-on-primary-container');
        isEditMode = false;
    }

    editBtn.addEventListener('click', () => {
        if (!isEditMode) {
            enterEditMode();
        } else {
            saveEditMode();
        }
    });

    titleInput.addEventListener('keydown', event => {
        if (event.key === 'Enter') {
            event.preventDefault();
            if (isEditMode) saveEditMode();
        }
        if (event.key === 'Escape' && isEditMode) cancelEditMode();
    });

    if (coverBtn && coverInput) {
        coverBtn.addEventListener('click', () => {
            if (isEditMode) coverInput.click();
        });
        coverInput.addEventListener('change', () => {
            const file = coverInput.files?.[0];
            if (!file || !coverImage || !isEditMode) return;
            if (!file.type.startsWith('image/')) {
                showModal({
                    title: 'Invalid Image',
                    message: 'Please select a valid image file for the playlist cover.',
                    icon: 'image',
                    type: 'danger',
                    showConfirm: false
                });
                coverInput.value = '';
                return;
            }
            const reader = new FileReader();
            reader.onload = event => {
                coverImage.src = event.target.result;
            };
            reader.onerror = () => {
                showModal({
                    title: 'Image Upload Failed',
                    message: 'Unable to read this image. Please try another file.',
                    icon: 'broken_image',
                    type: 'danger',
                    showConfirm: false
                });
            };
            reader.readAsDataURL(file);
        });
    }

    moviesGrid.addEventListener('click', event => {
        const removeBtn = event.target.closest('.movie-remove-btn');
        if (!removeBtn || !isEditMode) return;
        event.preventDefault();
        event.stopPropagation();
        const card = removeBtn.closest('.movie-card');
        if (!card) return;
        const movieTitle = card.querySelector('h3, h2')?.textContent.trim() || 'this movie';
        showModal({
            title: 'Remove Movie?',
            message: `Are you sure you want to remove "${movieTitle}" from this playlist?`,
            confirmText: 'Remove Movie',
            icon: 'movie',
            type: 'danger',
            onConfirm: () => {
                card.style.transition = 'opacity 200ms ease, transform 200ms ease';
                card.style.opacity = '0';
                card.style.transform = 'translateY(8px)';
                setTimeout(() => {
                    card.remove();
                    updateMovieCount();
                }, 200);
            }
        });
    });

    if (deletePlaylistBtn) {
        deletePlaylistBtn.addEventListener('click', () => {
            const playlistName = titleDisplay.textContent.trim();
            showModal({
                title: 'Delete Playlist?',
                message: `Are you sure you want to delete "${playlistName}"?`,
                confirmText: 'Delete Playlist',
                icon: 'delete_forever',
                type: 'danger',
                onConfirm: () => {
                    window.location.href = '/playlist';
                }
            });
        });
    }
    showRemoveButtons(false);
    updateMovieCount();
    updateStagedCounter();
});