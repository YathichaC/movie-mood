export function escapeHtml(value) {
    const element = document.createElement('div');
    element.textContent = value ?? '';
    return element.innerHTML;
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

export function renderPlaylistCheckboxes(playlists, container, movieId) {
    if (!container) return;

    container.replaceChildren();

    if (!playlists.length) {
        container.innerHTML =
            '<p class="py-6 text-center text-[12px] text-[#A1A1AA]">No playlists yet. Create one to get started.</p>';
        return;
    }

    playlists.forEach(playlist => {
        const name = playlist.playlistName || 'Untitled Playlist';
        const items = Array.isArray(playlist.items) ? playlist.items : [];

        const exists = items.some(
            item => String(item.tmdbMovieId) === String(movieId)
        );

        const label = document.createElement('label');
        label.dataset.playlistName = name;
        label.className =
            'flex items-center gap-3 px-2 py-3 cursor-pointer hover:bg-white/[0.03] transition-colors border-b border-[#1A1A1A]';

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.dataset.playlistId = playlist.playlistId;
        checkbox.checked = exists;
        checkbox.dataset.originalChecked = String(exists);
        checkbox.disabled = false;
        checkbox.className = 'w-4 h-4 shrink-0 rounded cursor-pointer';

        const content = document.createElement('div');
        content.className = 'min-w-0 flex-1';

        const title = document.createElement('p');
        title.className = 'truncate text-[13px] text-[#F5F5F5]';
        title.textContent = name;

        const description = document.createElement('p');
        description.className = 'mt-0.5 text-[11px] text-[#737373]';
        description.textContent = `${items.length} movies${exists ? ' · Already added' : ''}`;

        content.append(title, description);
        label.append(checkbox, content);
        container.appendChild(label);
    });

}
