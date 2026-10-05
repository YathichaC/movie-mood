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

function updateRatingSlider() {
    const slider = document.getElementById('ratingSlider');
    const valueEl = document.getElementById('ratingValue');
    const trackEl = document.getElementById('ratingTrack');
    if (!slider) return;

    const min = parseFloat(slider.min) || 0;
    const max = parseFloat(slider.max) || 10;
    const val = parseFloat(slider.value) || 0;

    if (valueEl) valueEl.textContent = val.toFixed(1) + '+';
    if (trackEl) {
        const percentage = ((val - min) / (max - min)) * 100;
        trackEl.style.left = '0%';
        trackEl.style.right = (100 - percentage) + '%';
    }
}

function updateYearSlider(type) {
    const min = 1900, max = 2026;
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

    if (yearValue) yearValue.textContent = start + ' - ' + end;
    if (yearTrack) {
        yearTrack.style.left = ((start - min) / (max - min) * 100) + '%';
        yearTrack.style.right = (100 - ((end - min) / (max - min) * 100)) + '%';
    }
}

const ratingSlider = document.getElementById('ratingSlider');
if (ratingSlider) {
    ratingSlider.addEventListener('input', updateRatingSlider);
    updateRatingSlider();
}

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
        input.closest('label').classList.toggle('text-white', input.checked);
        input.closest('label').classList.toggle('text-neutral-400', !input.checked);
    });
});

if (typeof updateYearSlider === 'function') {
    updateYearSlider('start');
}
