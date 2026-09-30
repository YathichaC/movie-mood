
document.addEventListener('DOMContentLoaded', () => {
    const banner = document.getElementById('feedbackBanner');
    const undoBtn = document.getElementById('undoBtn');
    const watchlistBtn = document.getElementById('watchlistActionBtn');
    const watchedBtn = document.getElementById('watchedActionBtn');
    let inWatchlist = true;
    let isWatched = false;
    undoBtn.addEventListener('click', () => {
        inWatchlist = false;
        banner.style.display = 'none';
        watchlistBtn.innerHTML = `
          <span class="material-symbols-outlined text-[20px]">bookmark_add</span>
          <span>Add to Playlist</span>
        `;
        watchlistBtn.classList.remove('amber-glow-box');
    });
    watchlistBtn.addEventListener('click', () => {
        inWatchlist = !inWatchlist;
        if (inWatchlist) {
            watchlistBtn.innerHTML = `
            <span class="material-symbols-outlined text-[20px]" style="font-variation-settings: 'FILL' 1;">bookmark</span>
            <span>In Playlist</span>
          `;
            watchlistBtn.classList.add('amber-glow-box');
            banner.style.display = 'block';
        } else {
            watchlistBtn.innerHTML = `
            <span class="material-symbols-outlined text-[20px]">bookmark_add</span>
            <span>Add to Playlist</span>
          `;
            watchlistBtn.classList.remove('amber-glow-box');
            banner.style.display = 'none';
        }
    });
    watchedBtn.addEventListener('click', () => {
        isWatched = !isWatched;
        if (isWatched) {
            watchedBtn.innerHTML = `
            <span class="material-symbols-outlined text-[20px] text-secondary" style="font-variation-settings: 'FILL' 1;">check_circle</span>
            <span class="text-secondary font-bold">Watched</span>
          `;
            watchedBtn.classList.add('border-secondary');
        } else {
            watchedBtn.innerHTML = `
            <span class="material-symbols-outlined text-[20px]">visibility</span>
            <span>Mark as Watched</span>
          `;
            watchedBtn.classList.remove('border-secondary');
        }
    });
    
});