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