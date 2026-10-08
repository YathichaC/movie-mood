export function setupModal(onOpen) {
    const modal = document.getElementById('playlistModal');
    const close = () => closeModal();
    document.getElementById('closePlaylistModal')?.addEventListener('click', close);
    document.getElementById('cancelPlaylistModal')?.addEventListener('click', close);
    modal?.addEventListener('click', event => {
        if (event.target === modal) closeModal();
    });
    window.addEventListener('keydown', event => {
        if (event.key === 'Escape') closeModal();
    });
    window.openModal = async (id = 'playlistModal') => {
        const target = document.getElementById(id);
        if (!target) return;
        target.classList.remove('hidden');
        target.classList.add('flex');
        target.style.display = 'flex';
        document.body.style.overflow = 'hidden';
        if (id === 'playlistModal') await onOpen?.();
    };
    window.closeModal = closeModal;
}

export function closeModal(id = 'playlistModal') {
    const target = document.getElementById(id);
    if (!target) return;
    target.classList.add('hidden');
    target.classList.remove('flex');
    target.style.display = 'none';
    if (!document.querySelector('.fixed.flex:not(.hidden)')) {
        document.body.style.overflow = '';
    }
}
