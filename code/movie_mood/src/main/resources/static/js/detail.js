document.addEventListener('DOMContentLoaded', () => {
    const backdrop = document.getElementById('detail-backdrop');
    const poster = document.getElementById('detail-poster');
    const title = document.getElementById('detail-title');
    const year = document.getElementById('detail-year');
    const rating = document.getElementById('detail-rating');
    const genres = document.getElementById('detail-genres');
    const synopsis = document.getElementById('detail-synopsis');
    const trailerBtn = document.getElementById('trailerActionBtn');
    const watchlistBtn = document.getElementById('watchlistActionBtn');
    const watchedBtn = document.getElementById('watchedActionBtn');
    const params = new URLSearchParams(window.location.search);
    const tmdbMovieId = params.get('id');

    if (!tmdbMovieId) {
        showError('Movie ID is missing.');
        return;
    }

    let trailerKey = null;

    async function loadMovieDetails() {
        try {
            const response = await fetch(`/api/v1/movies/${encodeURIComponent(tmdbMovieId)}`);
            if (!response.ok) {
                throw new Error(`Failed to load movie: ${response.status}`);
            }
            const movie = await response.json();
            renderMovie(movie);
            await loadTrailer();
        } catch (error) {
            console.error('Failed to load movie details:', error);
            showError('Unable to load movie information.');
        }
    }

    function renderMovie(movie) {
        if (title) {
            title.textContent = movie.title || 'Untitled Movie';
        }
        if (year) {
            year.textContent = movie.releaseDate
                ? movie.releaseDate.substring(0, 4)
                : '—';
        }
        if (rating) {
            rating.textContent = movie.rating != null
                ? movie.rating.toFixed(1)
                : '—';
        }
        if (genres) {
            genres.textContent = movie.genres?.length
                ? movie.genres.join(', ')
                : '—';
        }
        if (synopsis) {
            synopsis.textContent =
                movie.synopsis || 'No synopsis available.';
        }
        if (poster) {
            poster.src = movie.posterPath
                ? `https://image.tmdb.org/t/p/w500${movie.posterPath}`
                : '/img/movie-placeholder.png';

            poster.alt = movie.title || 'Movie poster';

            poster.onerror = () => {
                poster.onerror = null;
                poster.src = '/img/movie-placeholder.png';
            };
        }
        if (backdrop) {
            if (movie.backdropPath) {
                backdrop.src =
                    `https://image.tmdb.org/t/p/w1280${movie.backdropPath}`;

                backdrop.alt = '';
                backdrop.style.display = 'block';

                backdrop.onerror = () => {
                    backdrop.onerror = null;
                    backdrop.removeAttribute('src');
                    backdrop.style.display = 'none';
                };
            } else {
                backdrop.removeAttribute('src');
                backdrop.style.display = 'none';
            }
        }
    }

    async function loadTrailer() {
        if (!trailerBtn) return;
        try {
            const response = await fetch(
                `/api/v1/movies/${encodeURIComponent(tmdbMovieId)}/trailer`
            );
            if (!response.ok) {
                throw new Error(`Trailer not found: ${response.status}`);
            }
            const trailer = await response.json();
            trailerKey = trailer.key;
            if (trailerKey) {
                trailerBtn.disabled = false;
                trailerBtn.classList.remove('opacity-50', 'cursor-not-allowed');
                trailerBtn.addEventListener('click', () => {
                    openTrailer(trailerKey);
                });
            }
        } catch (error) {
            console.warn('Trailer unavailable:', error);
            trailerBtn.disabled = true;
            trailerBtn.classList.add('opacity-50', 'cursor-not-allowed');
            trailerBtn.textContent = 'Trailer Unavailable';
        }
    }

    function showError(message) {
        if (title) {
            title.textContent = 'Unable to load movie';
        }
        if (synopsis) {
            synopsis.textContent = message;
        }
        if (trailerBtn) {
            trailerBtn.disabled = true;
            trailerBtn.classList.add('opacity-50', 'cursor-not-allowed');
        }
    }

    if (watchlistBtn && watchedBtn) {
        let inWatchlist = true;
        let isWatched = false;
        const renderWatchlist = () => {
            watchlistBtn.innerHTML = inWatchlist
                ? `<span class="material-symbols-outlined text-[20px]" style="font-variation-settings:'FILL' 1">bookmark</span><span>In Playlist</span>`
                : `<span class="material-symbols-outlined text-[20px]">bookmark_add</span><span>Add to Playlist</span>`;
            watchlistBtn.className = `
                flex-1 min-w-[120px] h-[44px] md:h-[46px] px-3
                flex items-center justify-center gap-2
                border text-[11px] font-medium tracking-[0.1em] uppercase
                transition-all duration-200 focus:outline-none whitespace-nowrap
                ${inWatchlist
                    ? 'bg-amber-400 text-black border-amber-400 hover:bg-amber-300'
                    : 'bg-white/5 text-white border-white/20 hover:bg-white/10 hover:border-white/40'
                }
            `;
        };
        const renderWatched = () => {
            watchedBtn.innerHTML = isWatched
                ? `<span class="material-symbols-outlined text-[20px]" style="font-variation-settings:'FILL' 1">check_circle</span><span>Watched</span>`
                : `<span class="material-symbols-outlined text-[20px]">visibility</span><span>Mark as Watched</span>`;
            watchedBtn.className = `
                flex-1 min-w-[120px] h-[44px] md:h-[46px] px-3
                flex items-center justify-center gap-2
                border text-[11px] font-medium tracking-[0.1em] uppercase
                transition-all duration-200 focus:outline-none whitespace-nowrap
                ${isWatched
                    ? 'bg-emerald-400/10 text-emerald-300 border-emerald-400/50 hover:bg-emerald-400/15'
                    : 'bg-white/5 text-white/85 border-white/20 hover:bg-white/10 hover:border-white/40'
                }
            `;
        };

        watchlistBtn.addEventListener('click', () => {
            inWatchlist = !inWatchlist;
            renderWatchlist();
        });

        watchedBtn.addEventListener('click', () => {
            isWatched = !isWatched;
            renderWatched();
        });

        renderWatchlist();
        renderWatched();
    }

    loadMovieDetails();
});
