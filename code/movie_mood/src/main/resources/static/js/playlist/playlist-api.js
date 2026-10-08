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


export async function loadPlaylists(withMovies = false) {
    const response = await request('/v1/playlists');
    const data = await response.json();

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

export async function addMovieToPlaylist(playlistId, tmdbMovieId) {
    return request(`/v1/playlists/${encodeURIComponent(playlistId)}/movies`, {
        method: 'POST',
        body: JSON.stringify({ tmdbMovieId: String(tmdbMovieId) })
    });
}
