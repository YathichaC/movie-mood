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
    nextButton?.addEventListener('click', async () => {
        if (!selectedMood) {
            return;
        }

        const mood = selectedMood.toUpperCase();

        nextButton.disabled = true;
        nextButton.textContent = 'Loading...';

        try {
            const response = await fetch(
                `/api/v1/recommendations?mood=${encodeURIComponent(mood)}`
            );

            if (!response.ok) {
                throw new Error('Failed to fetch recommendations');
            }

            const recommendations = await response.json();

            sessionStorage.setItem(
                'recommendations',
                JSON.stringify(recommendations)
            );

            sessionStorage.setItem('selectedMood', mood);

            window.location.href =
                `/recommendations?mood=${encodeURIComponent(mood)}`;

        } catch (error) {
            console.error('Recommendation API error:', error);

            nextButton.disabled = false;
            nextButton.textContent = 'Continue';
        }
    });
});