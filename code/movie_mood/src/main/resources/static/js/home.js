document.addEventListener("DOMContentLoaded", () => {
    loadMovies();
});

async function loadMovies() {

    try {

        const response =
            await fetch("/api/v1/movies?page=1");

        if (!response.ok) {
            throw new Error(
                `Failed to load movies (${response.status})`
            );
        }

        const moviePage = await response.json();

        const movies = moviePage.content || [];

        if (movies.length === 0) {
            showMovieError();
            return;
        }

        renderHero(movies[0]);
        renderTrendingMovies(movies);

    } catch (error) {

        console.error("Failed to load movies:", error);

        showMovieError();
    }
}

function renderHero(movie) {

    const title =
        movie.title || "Untitled";

    const synopsis =
        movie.synopsis || "No description available.";

    const rating =
        movie.rating != null
            ? Number(movie.rating).toFixed(1)
            : "N/A";

    const year =
        movie.releaseDate
            ? movie.releaseDate.substring(0, 4)
            : "";

    const backdrop =
        movie.backdropPath
            ? `https://image.tmdb.org/t/p/w1280${movie.backdropPath}`
            : "";

    const movieId =
        movie.tmdbMovieId;

    document.getElementById("hero-title").textContent =
        title;

    document.getElementById("hero-synopsis").textContent =
        synopsis;

    document.getElementById("hero-meta").textContent =
        `${year} · ${rating} ★`;

    const backdropElement =
        document.getElementById("hero-backdrop");

    if (backdrop) {
        backdropElement.src = backdrop;
        backdropElement.alt = title;
    }

    const exploreButton =
        document.getElementById("hero-explore");

    exploreButton.href =
        `/movie/detail?id=${encodeURIComponent(movieId)}`;

    const trailerButton =
        document.getElementById("hero-trailer");

    trailerButton.onclick = () => {
        openTrailerForMovie(movie);
    };
}

async function openTrailerForMovie(movie) {

    const trailerButton =
        document.getElementById("hero-trailer");

    try {
        trailerButton.disabled = true;

        const response = await fetch(
            `/api/v1/movies/${encodeURIComponent(movie.tmdbMovieId)}/trailer`
        );

        if (!response.ok) {
            throw new Error(
                `Trailer not found (${response.status})`
            );
        }

        const trailer = await response.json();

        if (!trailer.key) {
            throw new Error("Trailer key is missing");
        }

        openTrailer(trailer.key);

    } catch (error) {

        console.error(
            "Failed to load movie trailer:",
            error
        );

        trailerButton.disabled = false;
    }
}

function renderTrendingMovies(movies) {

    const container =
        document.getElementById("trending-movies");

    if (!container) {
        return;
    }

    container.innerHTML = movies
        .slice(0, 5)
        .map(movie => createMovieCard(movie))
        .join("");
}

function createMovieCard(movie) {

    const movieId =
        movie.tmdbMovieId;

    const title =
        escapeHtml(movie.title || "Untitled");

    const poster =
        movie.posterPath
            ? `https://image.tmdb.org/t/p/w500${movie.posterPath}`
            : "";

    const rating =
        movie.rating != null
            ? Number(movie.rating).toFixed(1)
            : "N/A";

    const year =
        movie.releaseDate
            ? movie.releaseDate.substring(0, 4)
            : "";

    return `
        <a
            href="/movie/detail?id=${encodeURIComponent(movieId)}"
            class="group block">

            <div class="aspect-[2/3] overflow-hidden bg-[#111]">

                ${poster
            ? `
                            <img
                                src="${poster}"
                                alt="${title}"
                                class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-[1.03]"
                                loading="lazy">
                          `
            : `
                            <div class="w-full h-full flex items-center justify-center">
                                <span class="material-symbols-outlined text-4xl text-white/20">
                                    movie
                                </span>
                            </div>
                          `
        }

            </div>

            <div class="pt-3">

                <h3
                    class="font-outfit text-sm font-medium text-[#F5F5F5] truncate group-hover:text-[#E8DCC9] transition-colors">
                    ${title}
                </h3>

                <div
                    class="flex items-center gap-2 mt-1 font-manrope text-[11px] text-[#737373]">

                    <span>${year}</span>

                    <span>·</span>

                    <span>★ ${rating}</span>

                </div>

            </div>

        </a>
    `;
}

function showMovieError() {

    const heroTitle =
        document.getElementById("hero-title");

    const heroSynopsis =
        document.getElementById("hero-synopsis");

    const heroMeta =
        document.getElementById("hero-meta");

    if (heroTitle) {
        heroTitle.textContent =
            "Unable to load movie";
    }

    if (heroSynopsis) {
        heroSynopsis.textContent =
            "Please try again later.";
    }

    if (heroMeta) {
        heroMeta.textContent =
            "MovieMood";
    }

    const container =
        document.getElementById("trending-movies");

    if (container) {
        container.innerHTML = `
            <div class="col-span-full py-16 text-center">
                <span class="material-symbols-outlined text-4xl text-white/20">
                    error_outline
                </span>

                <p class="mt-3 text-sm text-white/40">
                    Unable to load movies
                </p>
            </div>
        `;
    }
}

function escapeHtml(value) {

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}