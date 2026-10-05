const clearBtn = document.getElementById('clear-history-btn');
const modal = document.getElementById('clear-modal');
const cancelBtn = document.getElementById('cancel-clear-btn');
const confirmBtn = document.getElementById('confirm-clear-btn');
const container = document.getElementById('history-container');
const emptyState = document.getElementById('empty-state');

clearBtn?.addEventListener('click', () => {
    modal.classList.remove('hidden');
    modal.classList.add('flex');
});

cancelBtn?.addEventListener('click', () => {
    modal.classList.add('hidden');
    modal.classList.remove('flex');
});

confirmBtn?.addEventListener('click', () => {
    modal.classList.add('hidden');
    modal.classList.remove('flex');
    container.classList.add('hidden');
    emptyState.classList.remove('hidden');
    emptyState.classList.add('flex');
    clearBtn.classList.add('hidden');
});