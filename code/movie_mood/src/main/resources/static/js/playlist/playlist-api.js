let playlistCache = null;
let playlistRequest = null;
let playlistPickerCache = null;
let playlistPickerRequest = null;

export async function request(url, options = {}) {
    if (typeof window.apiFetch !== 'function') {
        throw new Error('API helper is not loaded');
    }
    const response = await window.apiFetch(url, options);
    if (!response) throw new Error('No API response');
    if (!response.ok) {
        let message = 'Request failed';
        try {
            const data = await response.json();
            message = data.message || message;
        } catch { }
        throw new Error(message);
    }
    return response;
}

function normalizePlaylists(data) {
    if (!Array.isArray(data)) {
        return [];
    }

    return data.map(playlist => ({
        ...playlist,
        items: Array.isArray(playlist.items)
            ? playlist.items
            : []
    }));
}

export async function loadPlaylists(forceRefresh = false) {
    if (!forceRefresh && playlistCache) {
        return playlistCache;
    }

    if (!forceRefresh && playlistRequest) {
        return playlistRequest;
    }

    playlistRequest = (async () => {
        const response = await request('/v1/playlists');
        const data = await response.json();
        const playlists = normalizePlaylists(data);
        playlistCache = playlists;
        return playlists;
    })();

    try {
        return await playlistRequest;
    } finally {
        playlistRequest = null;
    }
}

export function clearPlaylistCache() {
    playlistCache = null;
    playlistRequest = null;
}

export async function loadPlaylistPicker(forceRefresh = false, tmdbMovieId = null) {
    const cacheKey = tmdbMovieId ? String(tmdbMovieId) : '__all__';

    if (!forceRefresh && playlistPickerCache && playlistPickerCache.key === cacheKey) {
        return playlistPickerCache.data;
    }

    if (!forceRefresh && playlistPickerRequest && playlistPickerRequest.key === cacheKey) {
        return playlistPickerRequest.promise;
    }

    const query = tmdbMovieId ? `?tmdbMovieId=${encodeURIComponent(tmdbMovieId)}` : '';

    playlistPickerRequest = {
        key: cacheKey,
        promise: (async () => {
            const response = await request(`/v1/playlists/picker${query}`);
            const data = await response.json();
            const playlists = Array.isArray(data)
                ? data.map(item => {
                    const containsCurrentMovie = Boolean(
                        item.containsCurrentMovie ?? item.containsMovie ?? item.inPlaylist ?? false
                    );

                    return {
                        playlistId: item.playlistId,
                        playlistName: item.playlistName || 'Untitled Playlist',
                        itemCount: Number(item.itemCount || 0),
                        containsCurrentMovie,
                        containsMovie: containsCurrentMovie,
                        inPlaylist: containsCurrentMovie
                    };
                })
                : [];

            playlistPickerCache = { key: cacheKey, data: playlists };
            return playlists;
        })()
    };

    try {
        return await playlistPickerRequest.promise;
    } finally {
        playlistPickerRequest = null;
    }
}

export function clearPlaylistPickerCache() {
    playlistPickerCache = null;
    playlistPickerRequest = null;
}

export async function createPlaylist(playlistName) {
    const response = await request('/v1/playlists', {
        method: 'POST',
        body: JSON.stringify({ playlistName })
    });
    if (response.status === 204) return null;
    try {
        return await response.json();
    } catch {
        return null;
    }
}

export async function updateMoviePlaylists({
    tmdbMovieId,
    addToPlaylistIds = [],
    removeFromPlaylistIds = []
}) {
    return request('/v1/playlists/movies', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            tmdbMovieId: String(tmdbMovieId),
            addToPlaylistIds,
            removeFromPlaylistIds
        })
    });
}
