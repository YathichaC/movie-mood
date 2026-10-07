document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('playlistModal');
    const closeBtn = document.getElementById('closePlaylistModal');
    const cancelBtn = document.getElementById('cancelPlaylistModal');
    const saveBtn = document.getElementById('watchlistActionBtn');
    const toggleCreateBtn = document.getElementById('toggleCreatePlaylistBtn');
    const createForm = document.getElementById('createPlaylistForm');
    const newPlaylistInput = document.getElementById('newPlaylistNameInput');
    const confirmCreateBtn = document.getElementById('confirmCreatePlaylistBtn');
    const playlistsList = document.getElementById('playlistsList');
    const searchInput = document.getElementById('searchPlaylistsInput');
    const feedbackBanner = document.getElementById('feedbackBanner');
    const playlistItemsList = document.getElementById('playlist-items-list');
    const createPlaylistModal = document.getElementById('create-playlist-modal');

    const openModal = async (id = 'playlistModal') => {
        const target = document.getElementById(id);
        if (!target) {
            return;
        }
        target.classList.remove('hidden');
        target.classList.add('flex');
        target.style.display = 'flex';
        document.body.style.overflow = 'hidden';
        if (id === 'playlistModal') {
            await loadPlaylists();
        }
    };

    const closeModal = (id = 'playlistModal') => {
        const target = document.getElementById(id);
        if (!target) {
            return;
        }
        target.classList.add('hidden');
        target.classList.remove('flex');
        target.style.display = 'none';
        document.body.style.overflow = '';
    };

    async function loadPlaylists() {
        try {
            const response = await apiFetch('/v1/playlists');
            if (!response || !response.ok) {
                const data = response ? await response.json() : {};
                throw new Error(data.message || 'Failed to load playlists');
            }

            const playlists = await response.json();

            if (playlistsList) {
                playlistsList.innerHTML = '';
                const template = document.getElementById('playlistItemTemplate');

                if (template) {
                    playlists.forEach((playlist) => {
                        const item = template.content.cloneNode(true);
                        const nameElement = item.querySelector('[data-playlist-name]');
                        const checkbox = item.querySelector('input[type="checkbox"]');

                        if (nameElement) {
                            nameElement.textContent = playlist.playlistName;
                        }

                        if (checkbox) {
                            checkbox.dataset.playlistId = playlist.playlistId;
                        }

                        const row = nameElement
                            ? nameElement.closest('.group, label, div')
                            : null;

                        if (row) {
                            row.dataset.playlistId = playlist.playlistId;
                        }

                        playlistsList.appendChild(item);
                    });
                }
            }

            if (playlistItemsList) {
                renderPlaylistCards(playlists);
            }
        } catch (error) {
            showPlaylistFeedback('Failed to load playlists', error.message);
        }
    }

    function renderPlaylistCards(playlists) {
        playlistItemsList.innerHTML = '';

        if (playlists.length === 0) {
            playlistItemsList.innerHTML = `
                <div class="col-span-full py-20 text-center">
                    <span class="material-symbols-outlined text-5xl text-white/20">playlist_play</span>
                    <p class="text-white/40 mt-4">No playlists yet.</p>
                </div>
            `;
            return;
        }

        playlists.forEach((playlist) => {
            const card = document.createElement('a');
            card.href = `/playlist/movielist?playlistId=${playlist.playlistId}`;
            card.className = 'block group';

            const coverImage = playlist.coverImagePath
                ? `<img src="${playlist.coverImagePath}" alt="${escapeHtml(playlist.playlistName)}" class="absolute inset-0 w-full h-full object-cover opacity-70 group-hover:opacity-85 group-hover:scale-[1.03] transition duration-500">`
                : `
                    <div class="absolute inset-0 flex items-center justify-center text-white/20">
                        <span class="material-symbols-outlined text-5xl">movie</span>
                    </div>
                `;

            card.innerHTML = `
                <article class="relative overflow-hidden rounded-2xl border border-white/[0.08] bg-[#101116] hover:border-white/[0.16] transition duration-300">
                    <div class="relative aspect-[16/10] overflow-hidden bg-[#0b0c0f]">
                        ${coverImage}
                        <div class="absolute inset-0 bg-gradient-to-t from-[#101116] via-transparent to-transparent"></div>
                        <div class="absolute left-5 right-5 bottom-5">
                            <h2 class="text-xl font-semibold truncate text-white">
                                ${escapeHtml(playlist.playlistName)}
                            </h2>
                            <p class="text-xs text-white/45 mt-1">
                                ${playlist.itemCount || 0} Movies
                            </p>
                        </div>
                    </div>
                </article>
            `;

            playlistItemsList.appendChild(card);
        });
    }

    function escapeHtml(value) {
        const div = document.createElement('div');
        div.textContent = value || '';
        return div.innerHTML;
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            closeModal();
        });
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => {
            closeModal();
        });
    }

    if (modal) {
        modal.addEventListener('click', (event) => {
            if (event.target === modal) {
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

    if (confirmCreateBtn && newPlaylistInput) {
        confirmCreateBtn.addEventListener('click', async () => {
            const name = newPlaylistInput.value.trim();

            if (!name) {
                return;
            }

            confirmCreateBtn.disabled = true;

            try {
                const response = await apiFetch('/v1/playlists', {
                    method: 'POST',
                    body: JSON.stringify({
                        playlistName: name
                    })
                });

                if (!response || !response.ok) {
                    const data = response ? await response.json() : {};
                    throw new Error(data.message || 'Failed to create playlist');
                }

                newPlaylistInput.value = '';
                createForm.classList.add('hidden');
                await loadPlaylists();
            } catch (error) {
                showPlaylistFeedback('Failed to create playlist', error.message);
            } finally {
                confirmCreateBtn.disabled = false;
            }
        });
    }

    if (createPlaylistModal) {
        createPlaylistModal.addEventListener('click', (event) => {
            if (event.target === createPlaylistModal) {
                closeModal('create-playlist-modal');
            }
        });
    }

    if (searchInput && playlistsList) {
        searchInput.addEventListener('input', (event) => {
            const query = event.target.value.toLowerCase();
            const items = playlistsList.querySelectorAll('[data-playlist-name]');

            items.forEach((item) => {
                const name = item.textContent.toLowerCase();
                const row = item.closest('.group') || item.closest('label') || item.closest('div');

                if (row) {
                    row.style.display = name.includes(query) ? '' : 'none';
                }
            });
        });
    }

    if (saveBtn) {
        saveBtn.addEventListener('click', async () => {
            const selected = playlistsList
                ? playlistsList.querySelectorAll('input[type="checkbox"]:checked')
                : [];

            if (selected.length === 0) {
                showPlaylistFeedback('No playlist selected', 'Please select at least one playlist');
                return;
            }

            const movieId = getCurrentMovieId();

            if (!movieId) {
                showPlaylistFeedback('Unable to add movie', 'Movie ID was not found');
                return;
            }

            saveBtn.disabled = true;

            try {
                for (const checkbox of selected) {
                    const playlistId = checkbox.dataset.playlistId;

                    if (!playlistId) {
                        continue;
                    }

                    const response = await apiFetch(`/v1/playlists/${playlistId}/movies`, {
                        method: 'POST',
                        body: JSON.stringify({
                            tmdbMovieId: String(movieId)
                        })
                    });

                    if (!response || !response.ok) {
                        const data = response ? await response.json() : {};
                        throw new Error(data.message || 'Failed to add movie to playlist');
                    }
                }

                closeModal();

                if (feedbackBanner) {
                    feedbackBanner.style.display = 'block';
                }

                if (typeof showToast === 'function') {
                    showToast(
                        'Added to Playlists',
                        'Movie added successfully.',
                        'playlist_add_check'
                    );
                }
            } catch (error) {
                showPlaylistFeedback('Failed to save playlist', error.message);
            } finally {
                saveBtn.disabled = false;
            }
        });
    }

    window.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            closeModal('playlistModal');
            closeModal('editModal');
            closeModal('deleteModal');
            closeModal('create-playlist-modal');
        }
    });

    window.openModal = openModal;
    window.closeModal = closeModal;

    window.toggleRemoveRow = (button) => {
        const isRemoved = button.getAttribute('data-removed') === 'true';

        if (!isRemoved) {
            button.textContent = 'Undo';
            button.setAttribute('data-removed', 'true');
            button.classList.add('border-white', 'text-white');
            button.parentElement.classList.add('opacity-40');
        } else {
            button.textContent = 'Remove';
            button.setAttribute('data-removed', 'false');
            button.classList.remove('border-white', 'text-white');
            button.parentElement.classList.remove('opacity-40');
        }
    };

    window.removeItem = (button) => {
        const card = button.closest('article');

        if (card) {
            card.style.opacity = '0';
            card.style.transform = 'scale(0.95)';
            card.style.transition = 'all 0.25s ease';

            setTimeout(() => {
                card.remove();
            }, 250);
        }
    };

    function getCurrentMovieId() {
        const match = window.location.pathname.match(/\/movie\/([^/]+)/);

        if (match) {
            return match[1];
        }

        const movieElement = document.querySelector('[data-tmdb-movie-id]');

        return movieElement ? movieElement.dataset.tmdbMovieId : null;
    }

    function showPlaylistFeedback(title, message) {
        if (typeof showToast === 'function') {
            showToast(title, message, 'error');
        }
    }

    if (playlistItemsList) {
        loadPlaylists();
    }
});