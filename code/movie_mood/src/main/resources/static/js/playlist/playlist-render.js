export function escapeHtml(value) {
    const element = document.createElement('div');
    element.textContent = value ?? '';
    return element.innerHTML;
}

export function renderPlaylistStatus(container, type, message) {
    if (!container) return;

    const className = type === 'error'
        ? 'text-[#fca5a5]'
        : type === 'empty'
            ? 'text-[#A1A1AA]'
            : 'text-[#737373]';

    container.innerHTML = `<p class="py-6 text-center text-[12px] ${className}">${escapeHtml(message)}</p>`;
}

export function renderPlaylistCards(playlists, container) {
    if (!container) return;
    container.replaceChildren();
    if (!playlists.length) {
        container.innerHTML = '<div class="col-span-full py-20 text-center"><p class="text-white/40">No playlists yet.</p></div>';
        return;
    }
    playlists.forEach(playlist => {
        const card = document.createElement('a');
        card.href = `/playlist/movielist?playlistId=${encodeURIComponent(playlist.playlistId)}`;
        card.className = 'block group';
        const cover = playlist.coverImagePath
            ? `<img src="${escapeHtml(playlist.coverImagePath)}" alt="${escapeHtml(playlist.playlistName)}" class="absolute inset-0 w-full h-full object-cover opacity-70 group-hover:opacity-85 group-hover:scale-[1.03] transition duration-500">`
            : '<div class="absolute inset-0 flex items-center justify-center text-white/20"><span class="material-symbols-outlined text-5xl">movie</span></div>';
        card.innerHTML = `<article class="relative overflow-hidden rounded-2xl border border-white/[0.08] bg-[#101116] hover:border-white/[0.16] transition duration-300"><div class="relative aspect-[16/10] overflow-hidden bg-[#0b0c0f]">${cover}<div class="absolute inset-0 bg-gradient-to-t from-[#101116] via-transparent to-transparent"></div><div class="absolute left-5 right-5 bottom-5"><h2 class="text-xl font-semibold truncate text-white">${escapeHtml(playlist.playlistName)}</h2><p class="text-xs text-white/45 mt-1">${playlist.itemCount ?? playlist.items?.length ?? 0} Movies</p></div></div></article>`;
        container.appendChild(card);
    });
}

function normalizePlaylistCount(value) {
    const number = Number(value ?? 0);
    if (!Number.isFinite(number)) {
        return 0;
    }
    return Math.max(0, Math.trunc(number));
}

export function updatePlaylistCheckboxCount(checkbox) {
    const label = checkbox.closest('label');
    if (!label) return;

    const countNode = label.querySelector('[data-role="playlist-count"]');
    if (!countNode) return;

    const count = normalizePlaylistCount(checkbox.dataset.displayCount);
    countNode.textContent = `${count} movies`;
}

export function resetPlaylistCheckboxCount(checkbox) {
    const originalCount = normalizePlaylistCount(checkbox.dataset.originalCount);
    checkbox.checked = checkbox.dataset.originalChecked === 'true';
    checkbox.dataset.previousChecked = checkbox.dataset.originalChecked;
    checkbox.dataset.displayCount = String(originalCount);
    updatePlaylistCheckboxCount(checkbox);
}

export function renderPlaylistCheckboxes(playlists, container, movieId) {
    if (!container) return;

    container.replaceChildren();

    if (!playlists.length) {
        renderPlaylistStatus(container, 'empty', 'No playlists yet. Create one to get started.');
        return;
    }

    playlists.forEach(playlist => {
        const name = playlist.playlistName || 'Untitled Playlist';
        const items = Array.isArray(playlist.items) ? playlist.items : [];
        const itemCount = items.length || Number(playlist.itemCount || 0);

        const exists = movieId
            ? Boolean(
                playlist.containsCurrentMovie ??
                playlist.containsMovie ??
                playlist.inPlaylist ??
                items.some(item => String(item.tmdbMovieId) === String(movieId))
            )
            : false;

        const label = document.createElement('label');
        label.dataset.playlistName = name;
        label.className =
            'flex items-center gap-3 px-2 py-3 cursor-pointer hover:bg-white/[0.03] transition-colors border-b border-[#1A1A1A]';

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.dataset.playlistId = playlist.playlistId;
        checkbox.checked = exists;
        checkbox.dataset.originalChecked = String(exists);
        checkbox.dataset.originalCount = String(normalizePlaylistCount(itemCount));
        checkbox.dataset.displayCount = String(normalizePlaylistCount(itemCount));
        checkbox.dataset.previousChecked = String(exists);
        checkbox.disabled = false;
        checkbox.className = 'w-4 h-4 shrink-0 rounded cursor-pointer';

        checkbox.addEventListener('change', () => {
            const previousChecked = checkbox.dataset.previousChecked === 'true';
            const nextChecked = checkbox.checked;

            if (previousChecked === nextChecked) {
                updatePlaylistCheckboxCount(checkbox);
                return;
            }

            const currentCount = normalizePlaylistCount(checkbox.dataset.displayCount);
            const delta = Number(nextChecked) - Number(previousChecked);
            const nextCount = Math.max(0, currentCount + delta);

            checkbox.dataset.displayCount = String(nextCount);
            checkbox.dataset.previousChecked = String(nextChecked);
            updatePlaylistCheckboxCount(checkbox);
        });

        const content = document.createElement('div');
        content.className = 'min-w-0 flex-1';

        const title = document.createElement('p');
        title.className = 'truncate text-[13px] text-[#F5F5F5]';
        title.textContent = name;

        const description = document.createElement('p');
        description.className = 'mt-0.5 text-[11px] text-[#737373]';
        description.dataset.role = 'playlist-count';
        description.textContent = `${normalizePlaylistCount(itemCount)} movies`;

        content.append(title, description);
        label.append(checkbox, content);
        container.appendChild(label);
    });

}
