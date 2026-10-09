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

    function renderPlaylistError(message) {
        const playlistName = document.getElementById('playlist-name');
        const playlistDescription = document.getElementById('playlist-description');
        const playlistCount = document.getElementById('playlist-count');

        if (playlistName) {
            playlistName.textContent = 'Playlist unavailable';
        }
        if (playlistDescription) {
            playlistDescription.textContent = message;
            playlistDescription.classList.remove('hidden');
        }
        if (playlistCount) {
            playlistCount.textContent = 'Access unavailable';
        }

        playlistMovies.innerHTML = `
            <div class="col-span-full py-20 text-center">
                <span class="material-symbols-outlined text-5xl text-white/20">
                    error
                </span>
                <p class="text-white/40 mt-4">
                    ${message}
                </p>
            </div>
        `;
    }

    async function loadPlaylistDetail() {
        const playlistId = new URLSearchParams(window.location.search).get('playlistId');
        if (!playlistId) {
            console.error('Playlist ID not found');
            renderPlaylistError('Playlist ID not found in the current page context.');
            return;
        }

        try {
            const response = await apiFetch(`/v1/playlists/${playlistId}`);

            if (!response) {
                throw new Error('No response received from the server.');
            }

            if (response.status === 401) {
                throw new Error('Your session has expired. Please sign in again.');
            }

            if (response.status === 403) {
                throw new Error('You do not have permission to view this playlist.');
            }

            if (!response.ok) {
                const data = await response.json().catch(() => ({}));
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
                playlistName.textContent = playlist.playlistName || 'Untitled Playlist';
            }
            if (playlistDescription) {
                if (playlist.detail) {
                    playlistDescription.textContent = playlist.detail;
                    playlistDescription.classList.remove('hidden');
                } else {
                    playlistDescription.textContent = 'No playlist description yet.';
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
            renderPlaylistError(error.message || 'Failed to load playlist.');
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
    async function removePlaylistCover() {
        const input = document.getElementById('playlistCoverInput');
        const preview = document.getElementById('coverPreview');
        const placeholder = document.getElementById('coverPlaceholder');
        const fileName = document.getElementById('coverFileName');
        const removeButton = document.getElementById('removeCoverButton');

        // If the user only selected a new file but has not saved it yet,
        // remove the local selection without calling the backend.
        if (input && input.files.length > 0) {
            input.value = '';

            if (currentPlaylist && currentPlaylist.coverImagePath) {
                preview.src = currentPlaylist.coverImagePath;
                preview.classList.remove('hidden');
                placeholder.classList.add('hidden');
                removeButton.classList.remove('hidden');
                fileName.textContent = 'Current cover image';
            } else {
                preview.src = '';
                preview.classList.add('hidden');
                placeholder.classList.remove('hidden');
                removeButton.classList.add('hidden');
                fileName.textContent = 'No cover image';
            }

            return;
        }

        // No saved cover image exists.
        if (!currentPlaylist || !currentPlaylist.coverImagePath) {
            return;
        }

        const playlistId =
            new URLSearchParams(window.location.search).get('playlistId');

        if (!playlistId) {
            console.error('Playlist ID not found');
            return;
        }

        try {
            const response = await apiFetch(
                `/v1/playlists/${playlistId}/image`,
                {
                    method: 'DELETE'
                }
            );

            if (!response || !response.ok) {
                const data = response ? await response.json() : {};
                throw new Error(
                    data.message || 'Failed to delete playlist image'
                );
            }

            const updatedPlaylist = await response.json();
            currentPlaylist = updatedPlaylist;

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

            const playlistCover =
                document.getElementById('playlist-cover');

            const playlistCoverIcon =
                document.getElementById('playlist-cover-icon');

            if (playlistCover) {
                playlistCover.src = '';
                playlistCover.classList.add('hidden');
            }

            if (playlistCoverIcon) {
                playlistCoverIcon.classList.remove('hidden');
            }

        } catch (error) {
            console.error(
                'Failed to delete playlist image:',
                error
            );
        }
    }

    window.removePlaylistCover = removePlaylistCover;
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
    function renderPosterUrl(posterPath) {
        return posterPath
            ? `https://image.tmdb.org/t/p/w500${posterPath}`
            : '/img/movie-placeholder.png';
    }

    function renderMovieCard(movie) {
        const movieId = movie.id ?? movie.tmdbMovieId ?? '';
        const movieName = movie.name ?? movie.title ?? 'Untitled Movie';
        const posterPath = movie.poster_path ?? movie.posterPath ?? '';

        return `
            <a href="/movie/detail?id=${encodeURIComponent(movieId)}" class="group relative block flex flex-col gap-2.5">
                <div class="relative aspect-[2/3] w-full overflow-hidden bg-[#111111] border border-[#262626] transition-all duration-200 group-hover:border-[#F5F5F5] group-hover:-translate-y-1 rounded-sm">
                    <img
                        alt="${movieName.replace(/"/g, '&quot;')} poster frame"
                        class="w-full h-full object-cover contrast-125 brightness-95 transition-transform duration-300 group-hover:scale-105"
                        src="${renderPosterUrl(posterPath)}"
                        onerror="this.onerror=null;this.src='/img/movie-placeholder.png';"
                    />
                </div>
                <div class="flex flex-col">
                    <h3 class="text-sm font-medium text-[#F5F5F5] truncate tracking-wide">
                        ${movieName}
                    </h3>
                </div>
            </a>
        `;
    }

    async function renderPlaylistMovies(items) {
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

        playlistMovies.innerHTML = `
            <div class="col-span-full py-10 text-center text-sm text-[#A3A3A3]">
                Loading movies...
            </div>
        `;

        const movieIds = items
            .map(item => item?.tmdbMovieId || item?.movieId || item?.id)
            .filter(Boolean);

        if (movieIds.length === 0) {
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

        try {
            const response = await apiFetch('/v1/movies/batch', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(movieIds)
            });

            if (!response) {
                throw new Error('Your session has expired. Please sign in again.');
            }
            if (response.status === 401) {
                throw new Error('Your session has expired. Please sign in again.');
            }
            if (response.status === 403) {
                throw new Error('You do not have permission to view this playlist.');
            }
            if (!response.ok) {
                const data = await response.json().catch(() => ({}));
                throw new Error(data.message || 'Unable to load playlist movies.');
            }

            const movies = await response.json();

            if (!Array.isArray(movies) || movies.length === 0) {
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

            playlistMovies.innerHTML = movies
                .map(movie => renderMovieCard(movie))
                .join('');
        } catch (error) {
            console.error('Failed to load playlist movie details:', error);
            playlistMovies.innerHTML = `
                <div class="col-span-full py-20 text-center">
                    <span class="material-symbols-outlined text-5xl text-white/20">
                        error
                    </span>
                    <p class="text-white/40 mt-4">
                        Unable to load playlist movies.
                    </p>
                </div>
            `;
        }
    }
    loadPlaylistDetail();
});