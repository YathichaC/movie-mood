function openTrailer(videoId) {
    const modal = document.getElementById('trailerModal');
    const frame = document.getElementById('trailerFrame');

    frame.src = `https://www.youtube-nocookie.com/embed/${videoId}?autoplay=1&rel=0`;

    modal.classList.remove('hidden');
    modal.classList.add('flex');

    document.body.classList.add('overflow-hidden');
}

function closeTrailer() {
    const modal = document.getElementById('trailerModal');
    const frame = document.getElementById('trailerFrame');

    frame.src = '';

    modal.classList.add('hidden');
    modal.classList.remove('flex');

    document.body.classList.remove('overflow-hidden');
}