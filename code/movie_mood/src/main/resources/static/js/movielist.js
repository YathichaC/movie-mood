document.addEventListener('DOMContentLoaded', () => {
    const playlistMovies = document.getElementById('playlist-movies');
    if (!playlistMovies) {
        return;
    }
    let currentPlaylist = null;
    window.openModal = function (modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('hidden');
            modal.classList.add('flex');
        }
    };
    window.closeModal = function (modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('hidden');
            modal.classList.remove('flex');
        }
    };
    async function loadPlaylistDetail() {
        const playlistId = new URLSearchParams(window.location.search).get('playlistId');
        if (!playlistId) {
            console.error('Playlist ID not found');
            return;
        }
        try {
            const response = await apiFetch(`/v1/playlists/${playlistId}`);
            if (!response || !response.ok) {
                const data = response ? await response.json() : {};
                throw new Error(data.message || 'Failed to load playlist');
            }
            const playlist = await response.json();
            currentPlaylist = playlist;
            const playlistName = document.getElementById('playlist-name');
            const playlistDescription = document.getElementById('playlist-description');
            const playlistCount = document.getElementById('playlist-count');
            const playlistCover = document.getElementById('playlist-cover');
            const playlistCoverIcon = document.getElementById('playlist-cover-icon');
            if (playlistName) {
                playlistName.textContent = playlist.playlistName || '';
            }
            if (playlistDescription) {
                if (playlist.detail) {
                    playlistDescription.textContent = playlist.detail;
                    playlistDescription.classList.remove('hidden');
                } else {
                    playlistDescription.classList.add('hidden');
                }
            }
            if (playlistCount) {
                playlistCount.textContent = `${playlist.itemCount || 0} movies`;
            }
            if (playlistCover && playlistCoverIcon) {
                if (playlist.coverImagePath) {
                    playlistCover.src = playlist.coverImagePath;
                    playlistCover.classList.remove('hidden');
                    playlistCoverIcon.classList.add('hidden');
                } else {
                    playlistCover.src = '';
                    playlistCover.classList.add('hidden');
                    playlistCoverIcon.classList.remove('hidden');
                }
            }
            renderPlaylistMovies(playlist.items);
        } catch (error) {
            console.error('Failed to load playlist:', error);
            playlistMovies.innerHTML = `
                <div class="col-span-full py-20 text-center">
                    <span class="material-symbols-outlined text-5xl text-white/20">
                        error
                    </span>
                    <p class="text-white/40 mt-4">
                        Failed to load playlist.
                    </p>
                </div>
            `;
        }
    }
    function openEditModal() {
        const playlist = currentPlaylist;
        if (!playlist) {
            return;
        }
        const playlistName = document.getElementById('playlistName');
        const playlistDetail = document.getElementById('playlistDetail');
        const preview = document.getElementById('coverPreview');
        const placeholder = document.getElementById('coverPlaceholder');
        const removeButton = document.getElementById('removeCoverButton');
        const fileName = document.getElementById('coverFileName');
        if (playlistName) {
            playlistName.value = playlist.playlistName || '';
        }
        if (playlistDetail) {
            playlistDetail.value = playlist.detail || '';
        }
        if (playlist.coverImagePath) {
            preview.src = playlist.coverImagePath;
            preview.classList.remove('hidden');
            placeholder.classList.add('hidden');
            removeButton.classList.remove('hidden');
            fileName.textContent = 'Current cover image';
        } else {
            preview.src = '';
            preview.classList.add('hidden');
            placeholder.classList.remove('hidden');
            removeButton.classList.add('hidden');
            fileName.textContent = 'PNG, JPG, WEBP up to 5MB';
        }
        openModal('editModal');
    }
    window.openEditModal = openEditModal;
    function removePlaylistCover() {
        const input = document.getElementById('playlistCoverInput');
        const preview = document.getElementById('coverPreview');
        const placeholder = document.getElementById('coverPlaceholder');
        const fileName = document.getElementById('coverFileName');
        const removeButton = document.getElementById('removeCoverButton');
        if (input) {
            input.value = '';
        }
        if (preview) {
            preview.src = '';
            preview.classList.add('hidden');
        }
        if (placeholder) {
            placeholder.classList.remove('hidden');
        }
        if (removeButton) {
            removeButton.classList.add('hidden');
        }
        if (fileName) {
            fileName.textContent = 'No cover image';
        }
    }
    window.removePlaylistCover = removePlaylistCover;
    async function deletePlaylist() {
        const playlistId = new URLSearchParams(window.location.search).get('playlistId');
        if (!playlistId) {
            console.error('Playlist ID not found');
            return;
        }
        try {
            const response = await apiFetch(`/v1/playlists/${playlistId}`, {
                method: 'DELETE'
            });
            if (!response || !response.ok) {
                const data = response ? await response.json() : {};
                throw new Error(data.message || 'Failed to delete playlist');
            }
            window.location.href = '/playlist';
        } catch (error) {
            console.error('Failed to delete playlist:', error);
        }
    }
    window.deletePlaylist = deletePlaylist;
    async function updatePlaylist() {
        const playlistId = new URLSearchParams(window.location.search).get('playlistId');
        if (!playlistId) {
            console.error('Playlist ID not found');
            return;
        }
        const playlistName = document.getElementById('playlistName');
        const playlistDetail = document.getElementById('playlistDetail');
        const playlistCoverInput = document.getElementById('playlistCoverInput');
        if (!playlistName) {
            return;
        }
        const formData = new FormData();
        formData.append('playlistName', playlistName.value.trim());
        formData.append('detail', playlistDetail ? playlistDetail.value.trim() : '');
        if (playlistCoverInput && playlistCoverInput.files.length > 0) {
            formData.append('coverImage', playlistCoverInput.files[0]);
        }
        try {
            const response = await apiFetch(`/v1/playlists/${playlistId}`, {
                method: 'PUT',
                body: formData
            });
            if (!response || !response.ok) {
                const data = response ? await response.json() : {};
                throw new Error(data.message || 'Failed to update playlist');
            }
            closeModal('editModal');
            await loadPlaylistDetail();
        } catch (error) {
            console.error('Failed to update playlist:', error);
        }
    }
    window.updatePlaylist = updatePlaylist;
    function previewPlaylistCover(input) {
        const file = input.files[0];
        const preview = document.getElementById('coverPreview');
        const placeholder = document.getElementById('coverPlaceholder');
        const fileName = document.getElementById('coverFileName');
        const removeButton = document.getElementById('removeCoverButton');
        if (!file) {
            removePlaylistCover();
            return;
        }
        const reader = new FileReader();
        reader.onload = function (event) {
            preview.src = event.target.result;
            preview.classList.remove('hidden');
            placeholder.classList.add('hidden');
            removeButton.classList.remove('hidden');
            fileName.textContent = file.name;
        };
        reader.readAsDataURL(file);
    }
    window.previewPlaylistCover = previewPlaylistCover;
    function renderPlaylistMovies(items) {
        playlistMovies.innerHTML = '';
        if (!Array.isArray(items) || items.length === 0) {
            playlistMovies.innerHTML = `
                <div class="col-span-full py-20 text-center">
                    <span class="material-symbols-outlined text-5xl text-white/20">
                        movie
                    </span>
                    <p class="text-white/40 mt-4">
                        No movies in this playlist yet.
                    </p>
                </div>
            `;
            return;
        }
        console.log('Playlist movies:', items);
    }
    loadPlaylistDetail();
});