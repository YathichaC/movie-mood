document.addEventListener('DOMContentLoaded', () => {
    const banner = document.getElementById('feedbackBanner');
    const undoBtn = document.getElementById('undoBtn');
    const watchlistBtn = document.getElementById('watchlistActionBtn');
    const watchedBtn = document.getElementById('watchedActionBtn');
    if (!banner || !undoBtn || !watchlistBtn || !watchedBtn) return;
    let inWatchlist = true;
    let isWatched = false;
    const renderWatchlist = () => {
        watchlistBtn.innerHTML = inWatchlist
            ? `<span class="material-symbols-outlined text-[20px]" style="font-variation-settings:'FILL' 1">bookmark</span><span>In Playlist</span>`
            : `<span class="material-symbols-outlined text-[20px]">bookmark_add</span><span>Add to Playlist</span>`;
        watchlistBtn.className = `inline-flex items-center justify-center gap-2 px-5 py-3 text-sm font-medium tracking-wide border transition-all duration-300 ${inWatchlist ? 'bg-amber-400 text-black border-amber-400 hover:bg-amber-300' : 'bg-white/5 text-white border-white/20 hover:bg-white/10 hover:border-white/40'}`;
        banner.style.display = inWatchlist ? 'flex' : 'none';
    };
    const renderWatched = () => {
        watchedBtn.innerHTML = isWatched
            ? `<span class="material-symbols-outlined text-[20px]" style="font-variation-settings:'FILL' 1">check_circle</span><span>Watched</span>`
            : `<span class="material-symbols-outlined text-[20px]">visibility</span><span>Mark as Watched</span>`;
        watchedBtn.className = `inline-flex items-center justify-center gap-2 px-5 py-3 text-sm font-medium tracking-wide border transition-all duration-300 ${isWatched ? 'bg-emerald-400/10 text-emerald-300 border-emerald-400/50 hover:bg-emerald-400/15' : 'bg-white/5 text-white/85 border-white/20 hover:bg-white/10 hover:border-white/40'}`;
    };
    undoBtn.className = 'text-sm font-medium text-amber-300 underline underline-offset-4 decoration-amber-300/40 transition-colors hover:text-amber-200';
    banner.className = 'items-center gap-3 border border-white/10 bg-black/60 px-4 py-3 text-sm text-white/75 backdrop-blur-md';
    undoBtn.addEventListener('click', () => {
        inWatchlist = false;
        renderWatchlist();
    });
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
});
