
import { loadPlaylists } from './playlist/playlist-api.js';
import { setupModal } from './playlist/playlist-modal.js';
import {
    renderPlaylistCards,
    renderPlaylistCheckboxes
} from './playlist/playlist-render.js';
import {
    setupPlaylistActions,
    getCurrentMovieId
} from './playlist/playlist-actions.js';

document.addEventListener('DOMContentLoaded', () => {
    const modalList = document.getElementById('playlistsList');
    const pageList = document.getElementById('playlist-items-list');
    const detailButton = document.getElementById('watchlistActionBtn');

    async function reload() {
        const playlists = await loadPlaylists(Boolean(modalList));

        if (modalList) {
            renderPlaylistCheckboxes(
                playlists,
                modalList,
                getCurrentMovieId()
            );
        }

        if (pageList) {
            renderPlaylistCards(playlists, pageList);
        }
    }

    async function openPlaylistModal() {
        const modal = document.getElementById('playlistModal');
        if (!modal) return;

        try {
            const playlists = await loadPlaylists(true);

            if (modalList) {
                renderPlaylistCheckboxes(
                    playlists,
                    modalList,
                    getCurrentMovieId()
                );
            }

            window.openModal?.('playlistModal');
        } catch (error) {
            console.error('Failed to load playlists:', error);
        }
    }

    setupModal(reload);
    setupPlaylistActions({ reload });

    if (detailButton) {
        detailButton.addEventListener('click', async () => {
            const title = document.getElementById('detail-title')?.textContent;
            const modalTitle = document.getElementById('playlistModalMovieTitle');

            if (modalTitle && title) {
                modalTitle.textContent = title;
            }

            if (typeof window.openModal === 'function') {
                try {
                    await window.openModal('playlistModal');
                } catch (error) {
                    console.error('Failed to open playlist modal:', error);
                }
            } else {
                console.error('window.openModal is not initialized.');
            }
        });
    }

    if (pageList) {
        reload().catch(error => {
            console.error('Failed to load playlists:', error);
        });
    }
});