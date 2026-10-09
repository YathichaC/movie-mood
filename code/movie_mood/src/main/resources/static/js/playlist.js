
import {
    loadPlaylistPicker,
    loadPlaylists
} from './playlist/playlist-api.js';
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

    function renderLoadingState() {
        if (modalList) {
            modalList.innerHTML = '<p class="py-6 text-center text-[12px] text-[#737373]">Loading playlists...</p>';
        }
    }

    async function reload(forceRefresh = false) {
        if (modalList) {
            renderLoadingState();
        }

        try {
            const currentMovieId = getCurrentMovieId();

            const [pickerPlaylists, pagePlaylists] = await Promise.all([
                modalList ? loadPlaylistPicker(forceRefresh, currentMovieId) : Promise.resolve([]),
                pageList ? loadPlaylists(forceRefresh) : Promise.resolve([])
            ]);

            if (modalList) {
                renderPlaylistCheckboxes(
                    pickerPlaylists,
                    modalList,
                    currentMovieId
                );
            }

            if (pageList) {
                renderPlaylistCards(pagePlaylists, pageList);
            }

            return { pickerPlaylists, pagePlaylists, currentMovieId };
        } catch (error) {
            console.error('Failed to load playlists:', error);

            if (modalList) {
                modalList.innerHTML = '<p class="py-6 text-center text-[12px] text-[#fca5a5]">Unable to load playlists. Please try again.</p>';
            }

            if (pageList) {
                pageList.innerHTML = '<div class="col-span-full py-20 text-center"><p class="text-white/40">Unable to load playlists.</p></div>';
            }

            return { pickerPlaylists: [], pagePlaylists: [], currentMovieId: getCurrentMovieId() };
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