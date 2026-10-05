document.addEventListener('DOMContentLoaded', () => {
    const track = document.querySelector('.animate-slide');
    const images = track.querySelectorAll('img');
    let loaded = 0;
    const startAnimation = () => {
        loaded++;
        if (loaded === images.length) {
            track.classList.add('is-ready');
        }
    };
    images.forEach(img => {
        if (img.complete) {
            startAnimation();
        } else {
            img.addEventListener('load', startAnimation);
            img.addEventListener('error', startAnimation);
        }
    });
});
