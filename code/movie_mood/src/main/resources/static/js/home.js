const MAX_MOVIES_PER_PAGE = 10;
let currentPage = 1;
let totalPages = 1;
let activeRequestController = null;

document.addEventListener("DOMContentLoaded", () => {
    initPaginationControls();
    loadMovies(1);
});

function initPaginationControls() {
    const container = document.getElementById("pagination-container");

    if (!container) {
        return;
    }

    container.innerHTML = `
        <div class="flex items-center justify-center gap-2 sm:gap-3 flex-wrap">
            <button id="pagination-prev" type="button"
                class="inline-flex items-center justify-center min-w-[92px] rounded-full border border-[#B8A58A] px-4 py-2 text-[11px] font-semibold uppercase tracking-[0.2em] text-[#F5F5F5] transition-colors disabled:cursor-not-allowed disabled:border-[#3A3A3A] disabled:text-[#666666] hover:bg-[#B8A58A] hover:text-black disabled:hover:bg-transparent disabled:hover:text-[#666666]">
                Previous
            </button>
            <div id="pagination-pages" class="flex items-center gap-2"></div>
            <button id="pagination-next" type="button"
                class="inline-flex items-center justify-center min-w-[92px] rounded-full border border-[#B8A58A] px-4 py-2 text-[11px] font-semibold uppercase tracking-[0.2em] text-[#F5F5F5] transition-colors disabled:cursor-not-allowed disabled:border-[#3A3A3A] disabled:text-[#666666] hover:bg-[#B8A58A] hover:text-black disabled:hover:bg-transparent disabled:hover:text-[#666666]">
                Next
            </button>
        </div>
    `;

    document.getElementById("pagination-prev")
        .addEventListener("click", () => {
            if (currentPage > 1) {
                loadMovies(currentPage - 1);
            }
        });

    document.getElementById("pagination-next")
        .addEventListener("click", () => {
            if (currentPage < totalPages) {
                loadMovies(currentPage + 1);
            }
        });
}

async function loadMovies(pageNumber) {

    const page = Number(pageNumber) || 1;
    const safePage = Math.max(1, page);

    if (activeRequestController) {
        activeRequestController.abort();
    }

    const requestController = new AbortController();
    activeRequestController = requestController;
    setLoadingState();

    try {

        const response =
            await fetch(`/api/v1/movies?page=${safePage}`, {
                signal: requestController.signal,
            });

        if (!response.ok) {
            throw new Error(
                `Failed to load movies (${response.status})`
            );
        }

        const moviePage = await response.json();
        const movies = Array.isArray(moviePage.content) ? moviePage.content : [];

        currentPage = safePage;
        totalPages = Math.max(1, Number(moviePage.totalPages) || 1);

        if (movies.length === 0) {
            showMovieError();
            return;
        }

        const visibleMovies = movies.slice(0, MAX_MOVIES_PER_PAGE);

        renderHero(visibleMovies[0]);
        renderTrendingMovies(visibleMovies);
        renderPaginationControls();

    } catch (error) {

        if (error.name === "AbortError") {
            return;
        }

        console.error("Failed to load movies:", error);
        showMovieError();
    } finally {
        if (activeRequestController === requestController) {
            activeRequestController = null;
        }
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
        .slice(0, MAX_MOVIES_PER_PAGE)
        .map(movie => createMovieCard(movie))
        .join("");
}

function renderPaginationControls() {
    const prevButton = document.getElementById("pagination-prev");
    const nextButton = document.getElementById("pagination-next");
    const pageContainer = document.getElementById("pagination-pages");

    if (!prevButton || !nextButton || !pageContainer) {
        return;
    }

    const pageButtons = [];
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);

    if (startPage > 1) {
        pageButtons.push(1);
        if (startPage > 2) {
            pageButtons.push("...");
        }
    }

    for (let page = startPage; page <= endPage; page += 1) {
        pageButtons.push(page);
    }

    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            pageButtons.push("...");
        }
        pageButtons.push(totalPages);
    }

    prevButton.disabled = currentPage <= 1;
    nextButton.disabled = currentPage >= totalPages;

    pageContainer.innerHTML = pageButtons
        .map(pageNumber => {
            if (pageNumber === "...") {
                return `
                    <span class="inline-flex h-9 min-w-9 items-center justify-center text-[11px] text-[#A3A3A3]">
                        ...
                    </span>
                `;
            }

            const isActive = Number(pageNumber) === currentPage;

            return `
                <button
                    type="button"
                    data-page="${pageNumber}"
                    class="inline-flex h-9 min-w-9 items-center justify-center rounded-full border text-[11px] font-semibold uppercase tracking-[0.12em] transition-colors ${isActive
                        ? "border-[#B8A58A] bg-[#B8A58A] text-black"
                        : "border-[#2A2A2A] text-[#F5F5F5] hover:border-[#B8A58A] hover:text-[#E8DCC9]"}">
                    ${pageNumber}
                </button>
            `;
        })
        .join("");

    pageContainer.querySelectorAll("[data-page]")
        .forEach(button => {
            button.addEventListener("click", () => {
                const targetPage = Number(button.dataset.page);
                if (Number.isFinite(targetPage) && targetPage !== currentPage) {
                    loadMovies(targetPage);
                }
            });
        });
}

function setLoadingState() {
    const heroTitle = document.getElementById("hero-title");
    const heroSynopsis = document.getElementById("hero-synopsis");
    const heroMeta = document.getElementById("hero-meta");
    const movieGrid = document.getElementById("trending-movies");

    if (heroTitle) {
        heroTitle.textContent = "Loading movie...";
    }

    if (heroSynopsis) {
        heroSynopsis.textContent = "Loading movie information...";
    }

    if (heroMeta) {
        heroMeta.textContent = "Loading...";
    }

    if (movieGrid) {
        movieGrid.innerHTML = `
            <div class="col-span-full py-16 text-center">
                <span class="material-symbols-outlined text-4xl text-white/20">movie</span>
                <p class="mt-3 text-sm text-white/40">Loading movies...</p>
            </div>
        `;
    }
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