document.addEventListener('DOMContentLoaded', () => {
    const container = document.getElementById('recommendationsContainer');
    const strategySelect = document.getElementById('strategySelect');

    if (!container) {
        return;
    }
    strategySelect?.addEventListener('change', async () => {
        const mood = new URLSearchParams(window.location.search).get('mood')
            || sessionStorage.getItem('selectedMood');

        if (!mood) {
            showMessage('Please select a mood first.');
            return;
        }

        strategySelect.disabled = true;
        container.innerHTML = `
        <p class="col-span-full text-center text-neutral-500">
            Loading recommendations...
        </p>
    `;

        try {
            const response = await apiFetch(
                `/v1/recommendations?mood=${encodeURIComponent(mood)}&strategy=${encodeURIComponent(strategySelect.value)}`
            );

            if (!response || !response.ok) {
                throw new Error('Failed to fetch recommendations');
            }

            const movies = await response.json();

            sessionStorage.setItem('recommendations', JSON.stringify(movies));
            renderRecommendations(movies);

        } catch (error) {
            console.error('Failed to change strategy:', error);
            showMessage('Failed to load recommendations.');
        } finally {
            strategySelect.disabled = false;
        }
    });


    const storedRecommendations =
        sessionStorage.getItem('recommendations');

    if (!storedRecommendations) {
        showMessage('No recommendations found.');
        return;
    }

    try {
        const recommendations = JSON.parse(storedRecommendations);

        renderRecommendations(recommendations);

    } catch (error) {
        console.error('Failed to parse recommendations:', error);
        showMessage('Failed to load recommendations.');
    }

    function renderRecommendations(movies) {
        if (!Array.isArray(movies) || movies.length === 0) {
            showMessage('No movies found.');
            return;
        }

        container.innerHTML = movies
            .slice(0, 10)
            .map(movie => createMovieCard(movie))
            .join('');
    }

    function createMovieCard(movie) {
        const title = escapeHtml(movie.title ?? 'Untitled');
        const matchScore = movie.matchScore != null
            ? `${Number(movie.matchScore).toFixed(0)}% Match`
            : '';

        const rating = movie.rating != null
            ? Number(movie.rating).toFixed(1)
            : 'N/A';

        const year = movie.releaseDate
            ? movie.releaseDate.substring(0, 4)
            : 'N/A';

        const poster = movie.posterPath
            ? `https://image.tmdb.org/t/p/w500${movie.posterPath}`
            : '/img/movie-placeholder.png';

        return `
            <a href="/movie/detail?id=${encodeURIComponent(movie.tmdbMovieId)}"
                class="block cursor-pointer group">

                <article
                    class="movie-card w-full relative rounded-xl overflow-hidden bg-black border border-outline-variant/30 hover:border-primary transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-primary-container/10 flex flex-col">

                    <div class="relative aspect-[2/3] w-full overflow-hidden bg-black">

                        <img
                            src="${poster}"
                            alt="${title} movie poster"
                            class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500">

                        <div class="absolute inset-0 bg-gradient-to-b from-transparent via-black/10 to-black/95"></div>

                        <div class="absolute top-3 left-3 right-3 flex items-center justify-between">

                            <div
                                class="flex items-center gap-1 px-2 py-0.5 rounded-md bg-black/70 backdrop-blur-md border border-primary/40 text-primary">

                                <span
                                    class="material-symbols-outlined text-[14px]"
                                    data-weight="fill"
                                    style="font-variation-settings: 'FILL' 1;">
                                    star
                                </span>

                                <span class="font-label-sm text-label-sm font-bold">
                                    ${rating}
                                </span>

                            </div>

                            <span
                                class="px-2 py-0.5 rounded-md bg-black/70 backdrop-blur-md font-label-sm text-label-sm text-white border border-outline-variant/30">
                                ${year}
                            </span>

                        </div>
                    </div>

                    <div class="p-4 flex-1 flex flex-col justify-between space-y-3 bg-black">
                        <div>
                            <h3
                                class="font-title-md text-title-md text-white font-semibold group-hover:text-primary transition-colors line-clamp-1">
                                ${title}
                            </h3>
                            ${matchScore ? `
            <p class="mt-2 text-sm font-medium text-primary">
                ${matchScore}
            </p>
        ` : ''}
                        </div>
                    </div>

                </article>
            </a>
        `;
    }

    function showMessage(message) {
        container.innerHTML = `
            <p class="col-span-full text-center text-neutral-500">
                ${message}
            </p>
        `;
    }

    function escapeHtml(value) {
        const div = document.createElement('div');
        div.textContent = value;
        return div.innerHTML;
    }
});