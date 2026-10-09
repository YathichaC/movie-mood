let currentPage = 1;
let totalPages = 1;
let pageSize = 15;

const HISTORY_API = '/api/v1/history';
const MAX_PAGE_SIZE = 100;

// ========================================
// Authentication
// ========================================

function getAuthHeaders() {
    const token = localStorage.getItem('token');

    return token
        ? { Authorization: `Bearer ${token}` }
        : {};
}

// ========================================
// Fetch Watch History
// ========================================

async function fetchHistory(page = 1) {
    currentPage = page;

    const container = document.getElementById('history-container');
    const emptyState = document.getElementById('empty-state');
    const paginationWrapper = document.getElementById('pagination-wrapper');
    const clearBtn = document.getElementById('clear-all-btn');
    const paginationInfo = document.getElementById('pagination-info');
    const paginationContainer = document.getElementById('pagination-container');

    if (!container || !emptyState || !paginationWrapper) {
        console.error('Watch history page elements were not found.');
        return;
    }

    try {
        container.classList.add('hidden');
        emptyState.classList.add('hidden');
        emptyState.classList.remove('flex');

        container.innerHTML = `
            <div class="col-span-full flex justify-center py-20">
                <span class="material-symbols-outlined animate-spin text-3xl text-neutral-500">
                    progress_activity
                </span>
            </div>
        `;

        const response = await fetch(
            `${HISTORY_API}?page=${page}`,
            {
                headers: getAuthHeaders()
            }
        );

        if (response.status === 401 || response.status === 403) {
            throw new Error(
                `Authentication/authorization failed: HTTP ${response.status}`
            );
        }

        if (!response.ok) {
            throw new Error(
                `Failed to fetch watch history: HTTP ${response.status}`
            );
        }

        const historyItems = await response.json();
        const totalItemsHeader = response.headers.get('X-Total-Count');
        const totalPagesHeader = response.headers.get('X-Total-Pages');
        const currentPageHeader = response.headers.get('X-Current-Page');

        const totalItems = Number.parseInt(totalItemsHeader || '', 10);
        const totalItemsFromApi = Number.isFinite(totalItems) && totalItems >= 0
            ? totalItems
            : historyItems.length;

        currentPage = Number.parseInt(currentPageHeader || `${page}`, 10);
        if (!Number.isFinite(currentPage) || currentPage < 1) {
            currentPage = page;
        }

        const totalPagesHeaderValue = Number.parseInt(totalPagesHeader || '', 10);
        totalPages = Number.isFinite(totalPagesHeaderValue) && totalPagesHeaderValue > 0
            ? totalPagesHeaderValue
            : Math.max(1, Math.ceil(totalItemsFromApi / pageSize));

        if (historyItems.length === 0 || totalItemsFromApi === 0) {
            container.innerHTML = '';
            container.classList.add('hidden');

            paginationWrapper.classList.add('hidden');

            if (clearBtn) {
                clearBtn.classList.add('hidden');
            }

            if (paginationInfo) {
                paginationInfo.textContent = '';
            }

            if (paginationContainer) {
                paginationContainer.innerHTML = '';
            }

            emptyState.innerHTML = `
                <span
                    class="material-symbols-outlined mb-5 text-6xl text-neutral-600"
                    aria-hidden="true"
                >
                    movie
                </span>

                <h2 class="mb-2 text-xl font-medium text-white">
                    ยังไม่มีหนังที่ดูแล้ว
                </h2>

                <p class="max-w-sm text-sm leading-relaxed text-neutral-400">
                    หนังที่คุณทำเครื่องหมายว่าดูแล้วจะแสดงอยู่ที่นี่
                </p>
            `;

            emptyState.classList.remove('hidden');
            emptyState.classList.add('flex');

            return;
        }

        emptyState.classList.add('hidden');
        emptyState.classList.remove('flex');

        container.classList.remove('hidden');
        if (totalItemsFromApi > pageSize) {
            paginationWrapper.classList.remove('hidden');
        } else {
            paginationWrapper.classList.add('hidden');
        }

        if (clearBtn) {
            clearBtn.classList.remove('hidden');
        }

        renderMovieCards(historyItems);
        updatePagination({
            page: currentPage,
            totalPages,
            totalElements: totalItemsFromApi
        });

        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });

    } catch (error) {
        console.error('Error fetching watch history:', error);

        container.innerHTML = '';
        container.classList.add('hidden');
        paginationWrapper.classList.add('hidden');

        if (clearBtn) {
            clearBtn.classList.add('hidden');
        }

        if (paginationInfo) {
            paginationInfo.textContent = '';
        }

        if (paginationContainer) {
            paginationContainer.innerHTML = '';
        }

        emptyState.innerHTML = `
            <span
                class="material-symbols-outlined mb-4 text-5xl text-neutral-600"
                aria-hidden="true"
            >
                error_outline
            </span>

            <h2 class="mb-2 text-xl font-medium text-white">
                Unable to load watch history
            </h2>

            <p class="mb-5 text-sm text-neutral-400">
                Please try again.
            </p>

            <button
                type="button"
                onclick="fetchHistory(currentPage)"
                class="rounded-sm bg-white px-5 py-2 text-sm font-medium text-black transition hover:bg-neutral-200"
            >
                Try again
            </button>
        `;

        emptyState.classList.remove('hidden');
        emptyState.classList.add('flex');
    }
}

// ========================================
// Render Movie Cards
// ========================================

function renderMovieCards(movies) {
    const container = document.getElementById('history-container');
    const historyCount = document.getElementById('history-count');

    if (!container) return;

    if (historyCount) {
        historyCount.textContent = movies.length
            ? `Showing ${movies.length} movie${movies.length > 1 ? 's' : ''}`
            : 'No watch history yet';
    }

    container.innerHTML = movies.map(movie => {
        const title = escapeHtml(movie.title || 'Untitled');
        const movieId = movie.id ?? movie.tmdbMovieId ?? '';
        const rawPoster = movie.posterPath;

        const poster = rawPoster
            ? (rawPoster.startsWith('http')
                ? rawPoster
                : `https://image.tmdb.org/t/p/w500${rawPoster}`)
            : '/img/movie-placeholder.png';

        const rawRating = movie.rating ?? movie.voteAverage;
        const rating = rawRating != null && Number(rawRating) > 0
            ? Number(rawRating).toFixed(1)
            : 'N/A';

        const year = movie.releaseDate
            ? String(movie.releaseDate).substring(0, 4)
            : 'N/A';

        return `
            <article
                class="group cursor-pointer"
                data-movie-id="${escapeHtml(String(movieId))}"
                tabindex="0"
                role="button"
                aria-label="View ${title}"
            >
                <div class="relative aspect-[2/3] overflow-hidden rounded-xl bg-neutral-900">
                    <img
                        src="${poster}"
                        alt="${title}"
                        class="h-full w-full object-cover transition duration-500 group-hover:scale-105"
                        loading="lazy"
                        onerror="this.onerror=null;this.src='/img/movie-placeholder.png';"
                    >
                    <div class="absolute inset-0 bg-gradient-to-t from-black/90 via-transparent to-transparent opacity-0 transition group-hover:opacity-100"></div>
                    <div class="absolute bottom-3 left-3 flex items-center gap-1 opacity-0 transition group-hover:opacity-100">
                        <span class="material-symbols-outlined text-sm text-yellow-400">
                            star
                        </span>
                        <span class="text-xs font-medium text-white">${rating}</span>
                    </div>
                </div>
                <div class="mt-3">
                    <h3 class="line-clamp-1 text-sm font-medium text-white">${title}</h3>
                    <p class="mt-1 text-xs text-neutral-500">${escapeHtml(year)}</p>
                </div>
            </article>
        `;
    }).join('');

    container.querySelectorAll('[data-movie-id]').forEach(card => {
        const movieId = card.getAttribute('data-movie-id');
        if (!movieId) return;

        card.addEventListener('click', () => openMovieDetail(movieId));
        card.addEventListener('keydown', event => {
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                openMovieDetail(movieId);
            }
        });
    });
}

function openMovieDetail(movieId) {
    if (!movieId) return;
    window.location.href = `/movie/detail?id=${encodeURIComponent(movieId)}`;
}

// ========================================
// Escape HTML
// ========================================

function escapeHtml(value) {
    return String(value).replace(/[&<>"']/g, char => {
        const entities = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;'
        };

        return entities[char];
    });
}

// ========================================
// Pagination
// ========================================

function updatePagination(data) {
    const pagination = document.getElementById('pagination-container');
    const info = document.getElementById('pagination-info');

    if (!pagination || !info) return;

    const page = data.page || currentPage || 1;
    const pages = data.totalPages || totalPages || 1;
    const total = data.totalElements || 0;

    if (total <= pageSize) {
        pagination.classList.add('hidden');
        info.textContent = 'Page 1 of 1';
        pagination.innerHTML = '';
        return;
    }

    pagination.classList.remove('hidden');
    info.textContent = `Page ${page} of ${pages}`;

    const previousDisabled = page <= 1;
    const nextDisabled = page >= pages;

    pagination.innerHTML = `
        <button
            type="button"
            aria-label="Previous page"
            ${previousDisabled ? 'disabled' : ''}
            onclick="goToPage(${page - 1})"
            class="flex h-9 w-9 items-center justify-center rounded-lg border border-white/10 text-neutral-400 transition hover:border-white/30 hover:text-white disabled:cursor-not-allowed disabled:text-neutral-700"
        >
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
            class="flex h-9 w-9 items-center justify-center rounded-lg border border-white/10 text-neutral-400 transition hover:border-white/30 hover:text-white disabled:cursor-not-allowed disabled:text-neutral-700"
        >
            <span class="material-symbols-outlined text-lg">
                chevron_right
            </span>
        </button>
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
                    : 'border-white/10 text-neutral-400 hover:border-white/30 hover:text-white'}"
                ${active ? 'disabled' : ''}
            >
                ${page}
            </button>
        `;
    }).join('');
}

function goToPage(page) {
    if (page < 1 || page > totalPages || page === currentPage) return;

    fetchHistory(page);
}

// ========================================
// Clear History Modal
// ========================================

function openClearModal() {
    const modal = document.getElementById('clear-modal');

    if (!modal) return;

    modal.classList.remove('hidden');
    modal.classList.add('flex');
}

function closeClearModal() {
    const modal = document.getElementById('clear-modal');

    if (!modal) return;

    modal.classList.add('hidden');
    modal.classList.remove('flex');
}

async function confirmClearHistory() {
    try {
        const response = await fetch(HISTORY_API, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (response.status === 401 || response.status === 403) {
            throw new Error(
                `Authentication/authorization failed: HTTP ${response.status}`
            );
        }

        if (!response.ok) {
            const message = await response.text();

            throw new Error(
                `Failed to clear history (${response.status}): ${message}`
            );
        }

        closeClearModal();
        await fetchHistory(1);

    } catch (error) {
        console.error('Error clearing history:', error);
        alert('Unable to clear watch history. Please try again.');
    }
}

// ========================================
// Page Size Input & Initialization
// ========================================

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('page-size-form');
    const input = document.getElementById('page-size');

    if (input) {
        input.value = pageSize;
    }

    form?.addEventListener('submit', event => {
        event.preventDefault();

        if (!input) return;

        const size = Number(input.value);

        if (
            !Number.isInteger(size) ||
            size < 1 ||
            size > MAX_PAGE_SIZE
        ) {
            input.setCustomValidity(
                `Enter a whole number between 1 and ${MAX_PAGE_SIZE}.`
            );

            input.reportValidity();
            return;
        }

        input.setCustomValidity('');
        pageSize = size;

        fetchHistory(1);
    });

    fetchHistory(1);
});