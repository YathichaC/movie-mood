function toggleState() {
    const populated = document.getElementById('populatedWatchHistoryView');
    const empty = document.getElementById('emptyWatchHistoryView');
    const btnText = document.getElementById('toggleButtonText');

    if (populated.classList.contains('hidden')) {
        populated.classList.remove('hidden');
        empty.classList.add('hidden');
        btnText.innerText = 'Preview Empty State';
    } else {
        populated.classList.add('hidden');
        empty.classList.remove('hidden');
        btnText.innerText = 'View Watched Grid';
    }
}