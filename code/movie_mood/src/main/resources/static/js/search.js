let currentPage = 1;
let totalPages = 1;
let currentKeyword = '';
const API_BASE_URL = '/api/v1/movies';
let discoverMode = false;
function toggleEmptyState() {
    const results = document.getElementById('resultsContainer');
    const empty = document.getElementById('emptyStateContainer');
    const toggleText = document.getElementById('toggleText');
    if (empty.classList.contains('hidden')) {
        results.classList.add('hidden');
        empty.classList.remove('hidden');
        toggleText.innerText = 'Show Movie Grid';
    } else {
        results.classList.remove('hidden');
        empty.classList.add('hidden');
        toggleText.innerText = 'Test Empty State';
    }
}
function updateRatingSlider() {
    const slider = document.getElementById('ratingSlider');
    const valueEl = document.getElementById('ratingValue');
    const trackEl = document.getElementById('ratingTrack');
    if (!slider) return;
    const min = parseFloat(slider.min) || 0;
    const max = parseFloat(slider.max) || 10;
    const val = parseFloat(slider.value) || 0;
    if (valueEl) {
        valueEl.textContent = val.toFixed(1) + '+';
    }
    if (trackEl) {
        const percentage = ((val - min) / (max - min)) * 100;
        trackEl.style.left = '0%';
        trackEl.style.right = (100 - percentage) + '%';
    }
}
function updateYearSlider(type) {
    const min = 1900;
    const max = 2026;
    const startSlider = document.getElementById('yearStartSlider');
    const endSlider = document.getElementById('yearEndSlider');
    const yearValue = document.getElementById('yearValue');
    const yearTrack = document.getElementById('yearTrack');
    if (!startSlider || !endSlider) return;
    let start = parseInt(startSlider.value);
    let end = parseInt(endSlider.value);
    if (type === 'start' && start > end) {
        startSlider.value = end;
        start = end;
    } else if (type === 'end' && end < start) {
        endSlider.value = start;
        end = start;
    }
    if (yearValue) {
        yearValue.textContent = start + ' - ' + end;
    }
    if (yearTrack) {
        yearTrack.style.left = ((start - min) / (max - min) * 100) + '%';
        yearTrack.style.right = (100 - ((end - min) / (max - min) * 100)) + '%';
    }
}
async function loadPopularMovies(page = 1) {
    const resultsContainer = document.getElementById('resultsContainer');
    if (!resultsContainer) return;
    try {
        resultsContainer.innerHTML = `
            <div class="col-span-full flex justify-center py-20">
                <span class="material-symbols-outlined animate-spin text-3xl text-neutral-500">
                    progress_activity
                </span>
            </div>
        `;
        const response = await fetch(`${API_BASE_URL}?page=${page}`);
        if (!response.ok) {
            throw new Error(`Failed to load popular movies: ${response.status}`);
        }
        const data = await response.json();
        currentPage = data.page || page;
        totalPages = data.totalPages || 1;
        const movies = data.content || [];
        if (movies.length === 0) {
            resultsContainer.innerHTML = '';
            showEmptyState();
            updatePagination(data);
            return;
        }
        hideEmptyState();
        renderMovies(movies);
        updatePagination(data);
    } catch (error) {
        console.error('Failed to load popular movies:', error);
        resultsContainer.innerHTML = `
            <div class="col-span-full flex flex-col items-center justify-center py-20 text-center">
                <span class="material-symbols-outlined mb-3 text-4xl text-neutral-600">
                    error_outline
                </span>
                <p class="text-sm text-neutral-400">
                    Something went wrong while loading movies.
                </p>
            </div>
        `;
    }
}
async function searchMovies(page = 1) {
    const resultsContainer = document.getElementById('resultsContainer');
    if (!resultsContainer) return;
    const keyword = currentKeyword.trim();
    if (!keyword) {
        await loadPopularMovies(page);
        return;
    }
    try {
        resultsContainer.innerHTML = `
            <div class="col-span-full flex justify-center py-20">
                <span class="material-symbols-outlined animate-spin text-3xl text-neutral-500">
                    progress_activity
                </span>
            </div>
        `;
        const response = await fetch(`${API_BASE_URL}/search?keyword=${encodeURIComponent(keyword)}&page=${page}`);
        if (!response.ok) {
            throw new Error(`Search failed: ${response.status}`);
        }
        const data = await response.json();
        currentPage = data.page || page;
        totalPages = data.totalPages || 1;
        const movies = data.content || [];
        if (movies.length === 0) {
            resultsContainer.innerHTML = '';
            showEmptyState();
            updatePagination(data);
            return;
        }
        hideEmptyState();
        renderMovies(movies);
        updatePagination(data);
    } catch (error) {
        console.error('Failed to search movies:', error);
        resultsContainer.innerHTML = `
            <div class="col-span-full flex flex-col items-center justify-center py-20 text-center">
                <span class="material-symbols-outlined mb-3 text-4xl text-neutral-600">
                    error_outline
                </span>
                <p class="text-sm text-neutral-400">
                    Something went wrong while searching for movies.
                </p>
            </div>
        `;
        updatePagination({
            page: 1,
            totalPages: 1,
            totalElements: 0
        });
    }
}
async function discoverMovies(page = 1) {
    const resultsContainer = document.getElementById('resultsContainer');
    if (!resultsContainer) return;
    const selectedGenre = document.querySelector('input[name="genre"]:checked');
    const ratingSlider = document.getElementById('ratingSlider');
    const yearStartSlider = document.getElementById('yearStartSlider');
    const yearEndSlider = document.getElementById('yearEndSlider');
    const sortSelector = document.getElementById('sortSelector');
    const params = new URLSearchParams();
    if (selectedGenre) {
        params.set('genreId', selectedGenre.value);
    }
    if (yearStartSlider) {
        params.set('startYear', yearStartSlider.value);
    }
    if (yearEndSlider) {
        params.set('endYear', yearEndSlider.value);
    }
    if (ratingSlider) {
        params.set('minRating', ratingSlider.value);
    }


    if (sortSelector && sortSelector.value) {
    params.set('sortBy', sortSelector.value);
    }
    params.set('page', page);
    try {
        resultsContainer.innerHTML = `
            <div class="col-span-full flex justify-center py-20">
                <span class="material-symbols-outlined animate-spin text-3xl text-neutral-500">
                    progress_activity
                </span>
            </div>
        `;
        const response = await fetch(`${API_BASE_URL}/discover?${params.toString()}`);
        if (!response.ok) {
            throw new Error(`Discover failed: ${response.status}`);
        }
        const data = await response.json();
        currentPage = data.page || page;
        totalPages = data.totalPages || 1;
        const movies = data.content || [];
        if (movies.length === 0) {
            resultsContainer.innerHTML = '';
            showEmptyState();
            updatePagination(data);
            return;
        }
        hideEmptyState();
        renderMovies(movies);
        updatePagination(data);
    } catch (error) {
        console.error('Failed to discover movies:', error);
        resultsContainer.innerHTML = `
            <div class="col-span-full flex flex-col items-center justify-center py-20 text-center">
                <span class="material-symbols-outlined mb-3 text-4xl text-neutral-600">
                    error_outline
                </span>
                <p class="text-sm text-neutral-400">
                    Something went wrong while filtering movies.
                </p>
            </div>
        `;
    }
}
function renderMovies(movies) {
    const container = document.getElementById('resultsContainer');
    if (!container) return;
    container.innerHTML = movies.map(movie => {
        const poster = movie.posterPath
            ? `https://image.tmdb.org/t/p/w500${movie.posterPath}`
            : '/img/movie-placeholder.png';
        const rating = movie.rating != null
            ? movie.rating.toFixed(1)
            : 'N/A';
        const year = movie.releaseDate
            ? movie.releaseDate.substring(0, 4)
            : 'N/A';
        return `
            <article
                class="group cursor-pointer"
                onclick="openMovieDetail('${movie.tmdbMovieId}')">
                <div class="relative aspect-[2/3] overflow-hidden rounded-xl bg-neutral-900">
                    <img
                        src="${poster}"
                        alt="${escapeHtml(movie.title || 'Movie poster')}"
                        class="h-full w-full object-cover transition duration-500 group-hover:scale-105"
                        loading="lazy"
                        onerror="this.src='/img/movie-placeholder.png'">
                    <div class="absolute inset-0 bg-gradient-to-t from-black/90 via-transparent to-transparent opacity-0 transition group-hover:opacity-100"></div>
                    <div class="absolute bottom-3 left-3 flex items-center gap-1 opacity-0 transition group-hover:opacity-100">
                        <span class="material-symbols-outlined text-sm text-yellow-400">
                            star
                        </span>
                        <span class="text-xs font-medium text-white">
                            ${rating}
                        </span>
                    </div>
                </div>
                <div class="mt-3">
                    <h3 class="line-clamp-1 text-sm font-medium text-white">
                        ${escapeHtml(movie.title || 'Untitled')}
                    </h3>
                    <p class="mt-1 text-xs text-neutral-500">
                        ${year}
                    </p>
                </div>
            </article>
        `;
    }).join('');
}
function openMovieDetail(movieId) {
    window.location.href = `/movie/detail?id=${encodeURIComponent(movieId)}`;
}
function updatePagination(data) {
    const pagination = document.getElementById('paginationContainer');
    if (!pagination) return;
    const page = data.page || 1;
    const pages = data.totalPages || 1;
    const total = data.totalElements || 0;
    if (total === 0) {
        pagination.classList.add('hidden');
        return;
    }
    pagination.classList.remove('hidden');
    const previousDisabled = page <= 1;
    const nextDisabled = page >= pages;
    pagination.innerHTML = `
        <p class="text-xs text-neutral-600">
            Page ${page} of ${pages}
            ${total > 0 ? ` · ${total.toLocaleString()} movies` : ''}
        </p>
        <div class="flex items-center gap-2">
            <button
                type="button"
                aria-label="Previous page"
                ${previousDisabled ? 'disabled' : ''}
                onclick="goToPage(${page - 1})"
                class="flex h-9 w-9 items-center justify-center rounded-lg border border-white/10 text-neutral-400 transition hover:border-white/30 hover:text-white disabled:cursor-not-allowed disabled:text-neutral-700">
                <span class="material-symbols-outlined text-lg">
                    chevron_left
                </span>
            </button>
            ${renderPageNumbers(page, pages)}
            <button
                type="button"
                aria-label="Next page"
                ${nextDisabled ? 'disabled' : ''}
                onclick="goToPage(${page + 1})"
                class="flex h-9 w-9 items-center justify-center rounded-lg border border-white/10 text-neutral-400 transition hover:border-white/30 hover:text-white disabled:cursor-not-allowed disabled:text-neutral-700">
                <span class="material-symbols-outlined text-lg">
                    chevron_right
                </span>
            </button>
        </div>
    `;
}
function renderPageNumbers(current, total) {
    const pages = [];
    if (total <= 5) {
        for (let i = 1; i <= total; i++) {
            pages.push(i);
        }
    } else {
        pages.push(1);
        if (current > 3) {
            pages.push('...');
        }
        const start = Math.max(2, current - 1);
        const end = Math.min(total - 1, current + 1);
        for (let i = start; i <= end; i++) {
            pages.push(i);
        }
        if (current < total - 2) {
            pages.push('...');
        }
        pages.push(total);
    }
    return pages.map(page => {
        if (page === '...') {
            return `
                <span class="flex h-9 w-9 items-center justify-center text-sm text-neutral-600">
                    ...
                </span>
            `;
        }
        const active = page === current;
        return `
            <button
                type="button"
                aria-label="Page ${page}"
                ${active ? 'aria-current="page"' : ''}
                onclick="goToPage(${page})"
                class="flex h-9 min-w-9 items-center justify-center rounded-lg border text-sm font-medium transition ${active
                ? 'border-white/20 bg-white text-black'
                : 'border-white/10 text-neutral-400 hover:border-white/30 hover:text-white'}">
                ${page}
            </button>
        `;
    }).join('');
}
function goToPage(page) {
    if (page < 1 || page > totalPages || page === currentPage) {
        return;
    }
    if (currentKeyword.trim()) {
        searchMovies(page);
    } else if (discoverMode) {
        discoverMovies(page);
    } else {
        loadPopularMovies(page);
    }
    const resultsContainer = document.getElementById('resultsContainer');
    if (resultsContainer) {
        resultsContainer.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }
}
function showEmptyState() {
    const results = document.getElementById('resultsContainer');
    const empty = document.getElementById('emptyStateContainer');
    if (results) {
        results.classList.add('hidden');
    }
    if (empty) {
        empty.classList.remove('hidden');
    }
}
function hideEmptyState() {
    const results = document.getElementById('resultsContainer');
    const empty = document.getElementById('emptyStateContainer');
    if (results) {
        results.classList.remove('hidden');
    }
    if (empty) {
        empty.classList.add('hidden');
    }
}
function escapeHtml(value) {
    const div = document.createElement('div');
    div.textContent = value;
    return div.innerHTML;
}
const searchInput = document.getElementById('searchInput');
if (searchInput) {
    let searchTimeout;
    searchInput.addEventListener('input', () => {
        clearTimeout(searchTimeout);
        currentKeyword = searchInput.value;
        currentPage = 1;
        searchTimeout = setTimeout(() => {
            discoverMode = false;
            searchMovies(1);
        }, 400);
    });
}
const ratingSlider = document.getElementById('ratingSlider');
if (ratingSlider) {
    ratingSlider.addEventListener('input', updateRatingSlider);
    updateRatingSlider();
}
const yearStartSlider = document.getElementById('yearStartSlider');
const yearEndSlider = document.getElementById('yearEndSlider');
if (yearStartSlider) {
    yearStartSlider.addEventListener('input', () => updateYearSlider('start'));
}
if (yearEndSlider) {
    yearEndSlider.addEventListener('input', () => updateYearSlider('end'));
}
updateYearSlider('start');
const mobileFilterToggle = document.getElementById('mobileFilterToggle');
const filterSidebar = document.getElementById('filterSidebar');
if (mobileFilterToggle && filterSidebar) {
    mobileFilterToggle.addEventListener('click', () => {
        filterSidebar.classList.toggle('hidden');
        filterSidebar.classList.toggle('block');
    });
}
document.querySelectorAll('input[name="genre"]').forEach(input => {
    input.addEventListener('change', () => {
        if (input.checked) {
            document.querySelectorAll('input[name="genre"]').forEach(other => {
                if (other !== input) {
                    other.checked = false;
                }
            });
        }
        document.querySelectorAll('input[name="genre"]').forEach(genreInput => {
            const label = genreInput.closest('label');
            if (!label) return;
            label.classList.toggle('text-white', genreInput.checked);
            label.classList.toggle('text-neutral-400', !genreInput.checked);
        });
        currentKeyword = '';
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.value = '';
        }
        discoverMode = true;
        currentPage = 1;
        discoverMovies(1);
    });
});
let filterTimeout;
function scheduleDiscover() {
    clearTimeout(filterTimeout);
    currentKeyword = '';
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.value = '';
    }
    discoverMode = true;
    currentPage = 1;
    filterTimeout = setTimeout(() => {
        discoverMovies(1);
    }, 300);
}
if (ratingSlider) {
    ratingSlider.addEventListener('change', scheduleDiscover);
}
if (yearStartSlider) {
    yearStartSlider.addEventListener('change', scheduleDiscover);
}
if (yearEndSlider) {
    yearEndSlider.addEventListener('change', scheduleDiscover);
}
const sortSelector = document.getElementById('sortSelector');
if (sortSelector) {
    sortSelector.addEventListener('change', scheduleDiscover);
}
function resetFilters() {
    discoverMode = false;
    currentKeyword = '';
    currentPage = 1;
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.value = '';
    }
    document.querySelectorAll('input[name="genre"]').forEach(input => {
        input.checked = false;
        const label = input.closest('label');
        if (label) {
            label.classList.remove('text-white');
            label.classList.add('text-neutral-400');
        }
    });
    const ratingSlider = document.getElementById('ratingSlider');
    if (ratingSlider) {
        ratingSlider.value = '7';
        updateRatingSlider();
    }
    const yearStartSlider = document.getElementById('yearStartSlider');
    const yearEndSlider = document.getElementById('yearEndSlider');
    if (yearStartSlider) {
        yearStartSlider.value = '2000';
    }
    if (yearEndSlider) {
        yearEndSlider.value = '2026';
    }
    updateYearSlider('start');
    
    const sortSelector = document.getElementById('sortSelector');
    if (sortSelector) {
        sortSelector.value = '';
    }

    discoverMode = true;
    discoverMovies(1);
    }
const resetFiltersButton = document.getElementById('resetFiltersButton');
if (resetFiltersButton) {
    resetFiltersButton.addEventListener('click', resetFilters);
}
document.addEventListener('DOMContentLoaded', () => {
    discoverMode = true;
    discoverMovies(1);
});