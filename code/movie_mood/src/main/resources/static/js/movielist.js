document.addEventListener('DOMContentLoaded', () => {

    const playlistMovies =
        document.getElementById('playlist-movies');

    if (!playlistMovies) {
        return;
    }

    window.openModal = function (modalId) {
        const modal =
            document.getElementById(modalId);

        if (modal) {
            modal.classList.remove('hidden');
            modal.classList.add('flex');
        }
    };

    window.closeModal = function (modalId) {
        const modal =
            document.getElementById(modalId);

        if (modal) {
            modal.classList.add('hidden');
            modal.classList.remove('flex');
        }
    };

    async function loadPlaylistDetail() {

        const playlistId =
            new URLSearchParams(
                window.location.search
            ).get('playlistId');

        if (!playlistId) {
            console.error(
                'Playlist ID not found'
            );
            return;
        }

        try {
            const response =
                await apiFetch(
                    `/v1/playlists/${playlistId}`
                );

            if (!response || !response.ok) {
                const data = response
                    ? await response.json()
                    : {};

                throw new Error(
                    data.message ||
                    'Failed to load playlist'
                );
            }

            const playlist =
                await response.json();

            /*
             * =========================
             * Playlist information
             * =========================
             */

            const playlistName =
                document.getElementById(
                    'playlist-name'
                );

            const playlistDescription =
                document.getElementById(
                    'playlist-description'
                );

            const playlistCount =
                document.getElementById(
                    'playlist-count'
                );

            const playlistCover =
                document.getElementById(
                    'playlist-cover'
                );

            const playlistCoverIcon =
                document.getElementById(
                    'playlist-cover-icon'
                );

            if (playlistName) {
                playlistName.textContent =
                    playlist.playlistName || '';
            }

            if (playlistDescription) {
                if (playlist.detail) {
                    playlistDescription.textContent =
                        playlist.detail;

                    playlistDescription.classList.remove(
                        'hidden'
                    );
                } else {
                    playlistDescription.classList.add(
                        'hidden'
                    );
                }
            }

            if (playlistCount) {
                playlistCount.textContent =
                    `${playlist.itemCount || 0} movies`;
            }

            /*
             * =========================
             * Playlist cover
             * =========================
             */

            if (
                playlistCover &&
                playlistCoverIcon
            ) {
                if (playlist.coverImagePath) {

                    playlistCover.src =
                        playlist.coverImagePath;

                    playlistCover.classList.remove(
                        'hidden'
                    );

                    playlistCoverIcon.classList.add(
                        'hidden'
                    );

                } else {

                    playlistCover.classList.add(
                        'hidden'
                    );

                    playlistCoverIcon.classList.remove(
                        'hidden'
                    );
                }
            }

            /*
             * =========================
             * Edit playlist form
             * =========================
             */

            const editPlaylistName =
                document.getElementById(
                    'playlistName'
                );

            const editPlaylistDetail =
                document.getElementById(
                    'playlistDetail'
                );

            const coverPreview =
                document.getElementById(
                    'coverPreview'
                );

            const coverFileName =
                document.getElementById(
                    'coverFileName'
                );

            if (editPlaylistName) {
                editPlaylistName.value =
                    playlist.playlistName || '';
            }

            if (editPlaylistDetail) {
                editPlaylistDetail.value =
                    playlist.detail || '';
            }

            if (
                coverPreview &&
                playlist.coverImagePath
            ) {
                coverPreview.src =
                    playlist.coverImagePath;

                coverPreview.classList.remove(
                    'hidden'
                );

                if (coverFileName) {
                    coverFileName.textContent =
                        'Current cover image';
                }
            }

            /*
             * =========================
             * Movies
             * =========================
             */

            renderPlaylistMovies(
                playlist.items
            );

        } catch (error) {

            console.error(
                'Failed to load playlist:',
                error
            );

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

    /*
     * =========================
     * Delete playlist
     * =========================
     */

    async function deletePlaylist() {

        const playlistId =
            new URLSearchParams(
                window.location.search
            ).get('playlistId');

        if (!playlistId) {
            console.error(
                'Playlist ID not found'
            );
            return;
        }

        try {

            const response =
                await apiFetch(
                    `/v1/playlists/${playlistId}`,
                    {
                        method: 'DELETE'
                    }
                );

            if (!response || !response.ok) {

                const data = response
                    ? await response.json()
                    : {};

                throw new Error(
                    data.message ||
                    'Failed to delete playlist'
                );
            }

            window.location.href =
                '/playlist';

        } catch (error) {

            console.error(
                'Failed to delete playlist:',
                error
            );
        }
    }

    window.deletePlaylist =
        deletePlaylist;

    /*
     * =========================
     * Update playlist
     * =========================
     */

    async function updatePlaylist() {

        const playlistId =
            new URLSearchParams(
                window.location.search
            ).get('playlistId');

        if (!playlistId) {
            console.error(
                'Playlist ID not found'
            );
            return;
        }

        const playlistName =
            document.getElementById(
                'playlistName'
            );

        const playlistDetail =
            document.getElementById(
                'playlistDetail'
            );

        const playlistCoverInput =
            document.getElementById(
                'playlistCoverInput'
            );

        if (!playlistName) {
            return;
        }

        const formData =
            new FormData();

        formData.append(
            'playlistName',
            playlistName.value.trim()
        );

        formData.append(
            'detail',
            playlistDetail
                ? playlistDetail.value.trim()
                : ''
        );

        if (
            playlistCoverInput &&
            playlistCoverInput.files.length > 0
        ) {
            formData.append(
                'coverImage',
                playlistCoverInput.files[0]
            );
        }

        try {

            const response =
                await apiFetch(
                    `/v1/playlists/${playlistId}`,
                    {
                        method: 'PUT',
                        body: formData
                    }
                );

            if (!response || !response.ok) {

                const data = response
                    ? await response.json()
                    : {};

                throw new Error(
                    data.message ||
                    'Failed to update playlist'
                );
            }

            closeModal(
                'editModal'
            );

            /*
             * Reload the latest data
             * after successful update.
             */
            await loadPlaylistDetail();

        } catch (error) {

            console.error(
                'Failed to update playlist:',
                error
            );
        }
    }

    window.updatePlaylist =
        updatePlaylist;

    /*
     * =========================
     * Cover preview
     * =========================
     */

    function previewPlaylistCover(input) {

        const file =
            input.files[0];

        const preview =
            document.getElementById(
                'coverPreview'
            );

        const fileName =
            document.getElementById(
                'coverFileName'
            );

        if (!file) {
            return;
        }

        if (
            file.size >
            5 * 1024 * 1024
        ) {

            input.value = '';

            console.error(
                'Cover image must be smaller than 5MB'
            );

            return;
        }

        if (preview) {
            preview.src =
                URL.createObjectURL(file);

            preview.classList.remove(
                'hidden'
            );
        }

        if (fileName) {
            fileName.textContent =
                file.name;
        }
    }

    window.previewPlaylistCover =
        previewPlaylistCover;

    /*
     * =========================
     * Render movies
     * =========================
     */

    function renderPlaylistMovies(items) {

        playlistMovies.innerHTML =
            '';

        if (
            !Array.isArray(items) ||
            items.length === 0
        ) {

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

        console.log(
            'Playlist movies:',
            items
        );
    }

    loadPlaylistDetail();
});