document.addEventListener('DOMContentLoaded', () => {
    const moodCards = document.querySelectorAll('[data-mood]');
    const recommendationAction = document.getElementById('recommendationAction');
    const nextButton = document.getElementById('nextButton');
    let selectedMood = null;
    moodCards.forEach((card) => {
        card.addEventListener('click', () => {
            moodCards.forEach((item) => {
                const selectedClasses = item.dataset.selectedClass;

                if (selectedClasses) {
                    item.classList.remove(...selectedClasses.split(' '));
                }
            });
            const selectedClasses = card.dataset.selectedClass;

            if (selectedClasses) {
                card.classList.add(...selectedClasses.split(' '));
            }
            selectedMood = card.querySelector('h3').textContent.trim();
            recommendationAction?.classList.remove('hidden');
        });
    });
    nextButton?.addEventListener('click', () => {
        if (!selectedMood) {
            return;
        }
        window.location.href =
            `/recommendations?id=${encodeURIComponent(selectedMood)}`;
    });
});