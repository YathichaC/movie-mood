function toggleEmptyState() {
    const results = document.getElementById('resultsContainer');
    const empty = document.getElementById('emptyStateContainer');
    const toggleText = document.getElementById('toggleText');
    if (empty.classList.contains('hidden')) {
        results.classList.add('hidden');
        empty.classList.remove('hidden');
        toggleText.innerText = "Show Movie Grid";
    } else {
        results.classList.remove('hidden');
        empty.classList.add('hidden');
        toggleText.innerText = "Test Empty State";
    }
}

function updateYearSlider(type) {
    const min = 1900, max = 2026;
    const startSlider = document.getElementById('yearStartSlider');
    const endSlider = document.getElementById('yearEndSlider');
    const yearValue = document.getElementById('yearValue');
    const yearTrack = document.getElementById('yearTrack');

    let start = parseInt(startSlider.value);
    let end = parseInt(endSlider.value);

    if (type === 'start' && start > end) {
        startSlider.value = end;
        start = end;
    } else if (type === 'end' && end < start) {
        endSlider.value = start;
        end = start;
    }

    if (yearValue) yearValue.textContent = start + ' - ' + end;
    if (yearTrack) {
        yearTrack.style.left = ((start - min) / (max - min) * 100) + '%';
        yearTrack.style.right = (100 - ((end - min) / (max - min) * 100)) + '%';
    }
}