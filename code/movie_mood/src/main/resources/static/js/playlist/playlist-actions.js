import {
    createPlaylist,
    addMovieToPlaylist,
    request
} from './playlist-api.js';

import { closeModal } from './playlist-modal.js';

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
            checkbox.checked =
                checkbox.dataset.originalChecked === 'true';
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

        // เก็บข้อมูลที่ต้องส่งก่อนปิด Modal
        const changes = changedCheckboxes.map(checkbox => ({
            playlistId: checkbox.dataset.playlistId,
            wasChecked: checkbox.dataset.originalChecked === 'true',
            isChecked: checkbox.checked
        }));

        // ปิด Modal ทันที ไม่ต้องรอ API
        closeModal();

        saveButton.disabled = true;

        try {
            await Promise.all(
                changes.map(change => {
                    const { playlistId, wasChecked, isChecked } = change;

                    if (!playlistId || wasChecked === isChecked) {
                        return Promise.resolve();
                    }

                    if (isChecked) {
                        // เพิ่มหนังด้วย POST
                        return addMovieToPlaylist(playlistId, movieId);
                    }

                    // นำหนังออกด้วย DELETE
                    return request(
                        `/v1/playlists/${encodeURIComponent(playlistId)}/movies/${encodeURIComponent(movieId)}`,
                        { method: 'DELETE' }
                    );
                })
            );


            window.dispatchEvent(new CustomEvent('playlist-updated', {
                detail: { tmdbMovieId: String(movieId) }
            }));

            notify(
                'Playlists updated',
                'Your changes have been saved.',
                'playlist_add_check'
            );

            // โหลดข้อมูลล่าสุดหลังบันทึกสำเร็จ
            await reload();

        } catch (error) {
            console.error('Failed to update playlists:', error);

            notify(
                'Failed to update playlists',
                error.message || 'Please try again.'
            );

            // โหลดสถานะล่าสุดใหม่ เผื่อบางรายการสำเร็จไปแล้ว
            try {
                await reload();
            } catch (reloadError) {
                console.error('Failed to reload playlists:', reloadError);
            }
        } finally {
            saveButton.disabled = false;
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
