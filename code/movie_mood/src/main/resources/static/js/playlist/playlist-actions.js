import {
    createPlaylist,
    updateMoviePlaylists,
    clearPlaylistCache,
    clearPlaylistPickerCache
} from './playlist-api.js';

import { closeModal } from './playlist-modal.js';
import { resetPlaylistCheckboxCount } from './playlist-render.js';

export function setupPlaylistActions({ reload }) {
    const list = document.getElementById('playlistsList');
    const search = document.getElementById('searchPlaylistsInput');
    const createToggle = document.getElementById('toggleCreatePlaylistBtn');
    const createForm = document.getElementById('createPlaylistForm');
    const nameInput = document.getElementById('newPlaylistNameInput');
    const createButton = document.getElementById('confirmCreatePlaylistBtn');
    const saveButton = document.getElementById('confirmAddPlaylistsBtn');
    const cancelButton = document.getElementById('cancelAddPlaylistsBtn');

    // คืน checkbox เป็นสถานะเดิมจาก API
    function resetPlaylistCheckboxes() {
        list?.querySelectorAll(
            'input[type="checkbox"][data-playlist-id]'
        ).forEach(checkbox => {
            resetPlaylistCheckboxCount(checkbox);
        });
    }

    // เปิด/ปิดฟอร์มสร้าง Playlist
    createToggle?.addEventListener('click', () => {
        createForm?.classList.toggle('hidden');

        if (!createForm?.classList.contains('hidden')) {
            nameInput?.focus();
        }
    });

    // สร้าง Playlist
    createButton?.addEventListener('click', async () => {
        const name = nameInput?.value.trim();

        if (!name) {
            notify('Playlist name required', 'Enter a playlist name.');
            return;
        }

        createButton.disabled = true;

        try {
            await createPlaylist(name);

            if (nameInput) nameInput.value = '';
            createForm?.classList.add('hidden');

            if (search) search.value = '';

            clearPlaylistCache();
            clearPlaylistPickerCache();
            await reload();

            notify(
                'Playlist created',
                'Your playlist is ready.',
                'playlist_add_check'
            );
        } catch (error) {
            notify('Failed to create playlist', error.message);
        } finally {
            createButton.disabled = false;
        }
    });

    saveButton?.addEventListener('click', async () => {
        const movieId = getCurrentMovieId();

        const checkboxes = list?.querySelectorAll(
            'input[type="checkbox"][data-playlist-id]'
        ) || [];

        if (!movieId) {
            notify('Unable to update playlists', 'Movie ID was not found.');
            return;
        }

        const changedCheckboxes = Array.from(checkboxes).filter(checkbox => {
            const wasChecked =
                checkbox.dataset.originalChecked === 'true';

            return wasChecked !== checkbox.checked;
        });

        if (!changedCheckboxes.length) {
            closeModal();
            return;
        }

        const addToPlaylistIds = [];
        const removeFromPlaylistIds = [];

        changedCheckboxes.forEach(checkbox => {
            const playlistId = checkbox.dataset.playlistId;
            if (!playlistId) return;

            if (checkbox.checked) {
                addToPlaylistIds.push(playlistId);
            } else {
                removeFromPlaylistIds.push(playlistId);
            }
        });

        closeModal();

        const saveLabel = saveButton.querySelector('span')?.textContent || 'Save';
        saveButton.disabled = true;
        saveButton.innerHTML = '<span>Saving...</span>';

        try {
            await updateMoviePlaylists({
                tmdbMovieId: movieId,
                addToPlaylistIds,
                removeFromPlaylistIds
            });

            clearPlaylistCache();
            clearPlaylistPickerCache();

            const localPlaylists = Array.from(
                list?.querySelectorAll('input[type="checkbox"][data-playlist-id]') || []
            ).map(checkbox => ({
                playlistId: checkbox.dataset.playlistId,
                containsCurrentMovie: checkbox.checked,
                containsMovie: checkbox.checked,
                inPlaylist: checkbox.checked
            }));

            Array.from(
                list?.querySelectorAll('input[type="checkbox"][data-playlist-id]') || []
            ).forEach(checkbox => {
                checkbox.dataset.originalChecked = String(checkbox.checked);
            });

            window.dispatchEvent(new CustomEvent('playlist-updated', {
                detail: {
                    tmdbMovieId: String(movieId),
                    playlists: localPlaylists
                }
            }));

            notify(
                'Playlists updated',
                'Your changes have been saved.',
                'playlist_add_check'
            );

            if (document.getElementById('playlist-items-list')) {
                await reload();
            }
        } catch (error) {
            console.error('Failed to update playlists:', error);

            notify(
                'Failed to update playlists',
                error.message || 'Please try again.'
            );

            try {
                if (document.getElementById('playlist-items-list')) {
                    await reload();
                }
            } catch (reloadError) {
                console.error('Failed to reload playlists:', reloadError);
            }
        } finally {
            saveButton.disabled = false;
            saveButton.innerHTML = `<span>${saveLabel}</span>`;
        }
    });


    // Cancel: คืน checkbox แล้วปิด Modal โดยไม่เรียก API
    cancelButton?.addEventListener('click', () => {
        resetPlaylistCheckboxes();
        closeModal();
    });

    // ค้นหา Playlist
    search?.addEventListener('input', () => {
        const query = search.value.trim().toLowerCase();

        list?.querySelectorAll('label[data-playlist-name]').forEach(label => {
            label.style.display =
                label.dataset.playlistName.toLowerCase().includes(query)
                    ? ''
                    : 'none';
        });
    });
}

export function getCurrentMovieId() {
    const element = document.querySelector('[data-tmdb-movie-id]');

    return element?.dataset.tmdbMovieId
        || new URLSearchParams(location.search).get('id');
}

function notify(title, message, icon = 'error') {
    if (typeof window.showToast === 'function') {
        window.showToast(title, message, icon);
    }
}
