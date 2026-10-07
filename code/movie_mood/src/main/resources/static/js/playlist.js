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

    const playlistItemsList =
        document.getElementById('playlist-items-list');

    const createPlaylistModal =
        document.getElementById('create-playlist-modal');


    /* =========================
       Modal
    ========================= */

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


    /* =========================
       Load Playlists
    ========================= */

    async function loadPlaylists() {

        if (!playlistItemsList && !playlistsList) {
            return;
        }

        try {

            const response = await apiFetch('/v1/playlists');

            if (!response || !response.ok) {

                const data = response
                    ? await response.json()
                    : {};

                throw new Error(
                    data.message || 'Failed to load playlists'
                );
            }

            const playlists = await response.json();

            // หน้า /playlist
            if (playlistItemsList) {
                renderPlaylistCards(
                    Array.isArray(playlists)
                        ? playlists
                        : []
                );
            }

            // Modal add to playlist
            if (playlistsList) {
                renderPlaylistCheckboxes(
                    Array.isArray(playlists)
                        ? playlists
                        : []
                );
            }

        } catch (error) {

            console.error(
                'Failed to load playlists:',
                error
            );

            if (playlistItemsList) {

                playlistItemsList.innerHTML = `
                    <div class="col-span-full py-20 text-center">
                        <span class="material-symbols-outlined text-5xl text-white/20">
                            error
                        </span>

                        <p class="text-white/40 mt-4">
                            Failed to load playlists.
                        </p>
                    </div>
                `;
            }
        }
    }


    /* =========================
       Render Playlist Cards
    ========================= */

    function renderPlaylistCards(playlists) {

        if (!playlistItemsList) {
            return;
        }

        playlistItemsList.innerHTML = '';

        if (playlists.length === 0) {

            playlistItemsList.innerHTML = `
                <div class="col-span-full py-20 text-center">

                    <span class="material-symbols-outlined text-5xl text-white/20">
                        playlist_play
                    </span>

                    <p class="text-white/40 mt-4">
                        No playlists yet.
                    </p>

                </div>
            `;

            return;
        }

        playlists.forEach((playlist) => {

            const card = document.createElement('a');

            card.href =
                `/playlist/movielist?playlistId=${playlist.playlistId}`;

            card.className = 'block group';

            const coverImage = playlist.coverImagePath
                ? `
                    <img
                        src="${playlist.coverImagePath}"
                        alt="${escapeHtml(playlist.playlistName)}"
                        class="absolute inset-0 w-full h-full object-cover opacity-70 group-hover:opacity-85 group-hover:scale-[1.03] transition duration-500"
                    >
                `
                : `
                    <div class="absolute inset-0 flex items-center justify-center text-white/20">
                        <span class="material-symbols-outlined text-5xl">
                            movie
                        </span>
                    </div>
                `;

            card.innerHTML = `
                <article
                    class="relative overflow-hidden rounded-2xl border border-white/[0.08] bg-[#101116] hover:border-white/[0.16] transition duration-300"
                >

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


    /* =========================
       Render Playlist Checkboxes
    ========================= */

    function renderPlaylistCheckboxes(playlists) {

        if (!playlistsList) {
            return;
        }

        // ตรงนี้ให้เอา HTML checkbox
        // ที่คุณใช้อยู่ใน modal มา render ตาม structure เดิม

        playlistsList.innerHTML = '';

        playlists.forEach((playlist) => {

            const label = document.createElement('label');

            label.className =
                'flex items-center gap-3 cursor-pointer';

            label.dataset.playlistName =
                playlist.playlistName;

            label.innerHTML = `
                <input
                    type="checkbox"
                    data-playlist-id="${playlist.playlistId}"
                    class="size-4"
                >

                <span class="text-sm text-white">
                    ${escapeHtml(playlist.playlistName)}
                </span>
            `;

            playlistsList.appendChild(label);
        });
    }


    /* =========================
       Create Playlist
    ========================= */

    if (toggleCreateBtn && createForm) {

        toggleCreateBtn.addEventListener('click', () => {

            createForm.classList.toggle('hidden');

            if (
                !createForm.classList.contains('hidden') &&
                newPlaylistInput
            ) {
                newPlaylistInput.focus();
            }
        });
    }


    if (confirmCreateBtn && newPlaylistInput) {

        confirmCreateBtn.addEventListener(
            'click',
            async () => {

                const name =
                    newPlaylistInput.value.trim();

                if (!name) {
                    return;
                }

                confirmCreateBtn.disabled = true;

                try {

                    const response = await apiFetch(
                        '/v1/playlists',
                        {
                            method: 'POST',

                            body: JSON.stringify({
                                playlistName: name
                            })
                        }
                    );

                    if (!response || !response.ok) {

                        const data = response
                            ? await response.json()
                            : {};

                        throw new Error(
                            data.message ||
                            'Failed to create playlist'
                        );
                    }

                    newPlaylistInput.value = '';

                    createForm.classList.add('hidden');

                    closeModal('create-playlist-modal');

                    await loadPlaylists();

                } catch (error) {

                    showPlaylistFeedback(
                        'Failed to create playlist',
                        error.message
                    );

                } finally {

                    confirmCreateBtn.disabled = false;
                }
            }
        );
    }


    /* =========================
       Add Movie To Playlist
    ========================= */

    if (saveBtn) {

        saveBtn.addEventListener(
            'click',
            async () => {

                const selected = playlistsList
                    ? playlistsList.querySelectorAll(
                        'input[type="checkbox"]:checked'
                    )
                    : [];

                if (selected.length === 0) {

                    showPlaylistFeedback(
                        'No playlist selected',
                        'Please select at least one playlist'
                    );

                    return;
                }

                const movieId =
                    getCurrentMovieId();

                if (!movieId) {

                    showPlaylistFeedback(
                        'Unable to add movie',
                        'Movie ID was not found'
                    );

                    return;
                }

                saveBtn.disabled = true;

                try {

                    for (const checkbox of selected) {

                        const playlistId =
                            checkbox.dataset.playlistId;

                        if (!playlistId) {
                            continue;
                        }

                        const response = await apiFetch(
                            `/v1/playlists/${playlistId}/movies`,
                            {
                                method: 'POST',

                                body: JSON.stringify({
                                    tmdbMovieId:
                                        String(movieId)
                                })
                            }
                        );

                        if (!response || !response.ok) {

                            const data = response
                                ? await response.json()
                                : {};

                            throw new Error(
                                data.message ||
                                'Failed to add movie to playlist'
                            );
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

                    showPlaylistFeedback(
                        'Failed to save playlist',
                        error.message
                    );

                } finally {

                    saveBtn.disabled = false;
                }
            }
        );
    }


    /* =========================
       Search Playlist
    ========================= */

    if (searchInput && playlistsList) {

        searchInput.addEventListener(
            'input',
            (event) => {

                const query =
                    event.target.value.toLowerCase();

                const items =
                    playlistsList.querySelectorAll(
                        '[data-playlist-name]'
                    );

                items.forEach((item) => {

                    const name =
                        item.textContent.toLowerCase();

                    const row =
                        item.closest('.group') ||
                        item.closest('label') ||
                        item.closest('div');

                    if (row) {

                        row.style.display =
                            name.includes(query)
                                ? ''
                                : 'none';
                    }
                });
            }
        );
    }


    /* =========================
       Modal Events
    ========================= */

    if (closeBtn) {

        closeBtn.addEventListener(
            'click',
            () => closeModal()
        );
    }

    if (cancelBtn) {

        cancelBtn.addEventListener(
            'click',
            () => closeModal()
        );
    }

    if (modal) {

        modal.addEventListener(
            'click',
            (event) => {

                if (event.target === modal) {
                    closeModal();
                }
            }
        );
    }

    if (createPlaylistModal) {

        createPlaylistModal.addEventListener(
            'click',
            (event) => {

                if (
                    event.target === createPlaylistModal
                ) {
                    closeModal(
                        'create-playlist-modal'
                    );
                }
            }
        );
    }


    /* =========================
       Global Functions
    ========================= */

    window.openModal = openModal;
    window.closeModal = closeModal;


    window.addEventListener(
        'keydown',
        (event) => {

            if (event.key === 'Escape') {

                closeModal('playlistModal');
                closeModal('editModal');
                closeModal('deleteModal');
                closeModal('create-playlist-modal');
            }
        }
    );


    function getCurrentMovieId() {

        const movieElement =
            document.querySelector(
                '[data-tmdb-movie-id]'
            );

        return movieElement
            ? movieElement.dataset.tmdbMovieId
            : null;
    }


    function escapeHtml(value) {

        const div =
            document.createElement('div');

        div.textContent = value || '';

        return div.innerHTML;
    }


    function showPlaylistFeedback(title, message) {

        if (typeof showToast === 'function') {

            showToast(
                title,
                message,
                'error'
            );
        }
    }


    /* =========================
       Initial Load
    ========================= */

    if (playlistItemsList) {
        loadPlaylists();
    }

    if (playlistsList) {
        loadPlaylists();
    }

});