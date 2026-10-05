document.addEventListener('DOMContentLoaded', () => {
    const toast = document.getElementById('notification-toast');
    const saveAccountBtn = document.getElementById('save-account-btn');
    const saveGenresBtn = document.getElementById('save-genres-btn');
    const cancelAccountBtn = document.getElementById('cancel-account-btn');
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const newPasswordInput = document.getElementById('new-password');
    const confirmPasswordInput = document.getElementById('confirm-password');
    const matchStatus = document.getElementById('match-status-msg');
    const inputs = [usernameInput, emailInput, newPasswordInput, confirmPasswordInput].filter(Boolean);
    const genreCheckboxes = document.querySelectorAll('input[name="dislikedGenres"]');
    const genreCounter = document.getElementById('genre-counter');
    let originalValues = inputs.map(input => input.value);
    let originalGenres = [];
    let toastTimeout;
    function showToast(message, type = 'success') {
        if (!toast) return;
        const icon = toast.querySelector('.material-symbols-outlined');
        const text = toast.querySelector('span:last-child');
        clearTimeout(toastTimeout);
        toast.className = 'fixed top-6 right-6 z-50 transform transition-all duration-300 flex items-center gap-3 bg-[#0A0A0A] border border-[#262626] px-5 py-3.5 rounded-sm shadow-2xl backdrop-blur-md font-outfit text-[#F5F5F5]';
        if (icon) {
            icon.textContent = type === 'error' ? 'error' : 'check_circle';
            icon.className = type === 'error'
                ? 'material-symbols-outlined text-red-500 text-xl'
                : 'material-symbols-outlined text-primary text-xl';
        }
        if (text) text.textContent = message;
        toast.classList.remove('translate-x-full', 'opacity-0', 'pointer-events-none');
        toast.classList.add('translate-x-0', 'opacity-100');
        toastTimeout = setTimeout(() => {
            toast.classList.remove('translate-x-0', 'opacity-100');
            toast.classList.add('translate-x-full', 'opacity-0', 'pointer-events-none');
        }, 3000);
    }
    function setButtonVisibility(button, visible) {
        if (!button) return;
        button.classList.toggle('hidden', !visible);
    }
    function hasAccountChanges() {
        return inputs.some((input, index) => input.value !== originalValues[index]);
    }
    function updateAccountSaveButton() {
        setButtonVisibility(saveAccountBtn, hasAccountChanges());
    }
    function checkPasswordRequirements(password) {
        return {
            length: password.length >= 8,
            upper: /[A-Z]/.test(password),
            lower: /[a-z]/.test(password),
            number: /\d/.test(password),
            special: /[^A-Za-z0-9]/.test(password)
        };
    }
    function isPasswordValid() {
        const password = newPasswordInput?.value || '';
        const confirmPassword = confirmPasswordInput?.value || '';
        if (!password && !confirmPassword) return true;
        const requirements = checkPasswordRequirements(password);
        return requirements.length &&
            requirements.upper &&
            requirements.lower &&
            requirements.number &&
            requirements.special &&
            password === confirmPassword;
    }
    function evaluatePassword() {
        const password = newPasswordInput?.value || '';
        const confirmPassword = confirmPasswordInput?.value || '';
        const requirements = checkPasswordRequirements(password);
        const requirementMap = {
            'req-length': requirements.length,
            'req-upper': requirements.upper,
            'req-lower': requirements.lower,
            'req-number': requirements.number,
            'req-special': requirements.special
        };
        const hasPassword = password.length > 0 || confirmPassword.length > 0;
        Object.entries(requirementMap).forEach(([id, valid]) => {
            const element = document.getElementById(id);
            if (!element) return;
            element.classList.toggle('text-primary', hasPassword && valid);
            element.classList.toggle('text-[#737373]', !hasPassword || !valid);
            const bullet = element.querySelector('.bullet');
            if (bullet) {
                bullet.textContent = (hasPassword && valid) ? '✓' : '○';
                bullet.classList.toggle('border-primary', hasPassword && valid);
                bullet.classList.toggle('border-[#404040]', !hasPassword || !valid);
                bullet.classList.toggle('text-primary', hasPassword && valid);
            }
        });
        if (matchStatus) {
            const matched = hasPassword && password === confirmPassword;
            matchStatus.textContent = matched ? 'Passwords match' : (hasPassword ? 'Passwords do not match' : 'Both entries must correspond accurately');
            matchStatus.classList.toggle('text-primary', matched);
            matchStatus.classList.toggle('text-red-500', hasPassword && !matched);
            matchStatus.classList.toggle('text-[#737373]', !hasPassword);
        }
    }
    const toggleNewPwVis = document.getElementById('toggle-new-pw-vis');
    const newPwIcon = document.getElementById('new-pw-icon');
    const toggleConfirmPwVis = document.getElementById('toggle-confirm-pw-vis');
    const confirmPwIcon = document.getElementById('confirm-pw-icon');
    toggleNewPwVis?.addEventListener('click', () => {
        if (!newPasswordInput) return;
        const isPassword = newPasswordInput.type === 'password';
        newPasswordInput.type = isPassword ? 'text' : 'password';
        if (newPwIcon) newPwIcon.textContent = isPassword ? 'visibility_off' : 'visibility';
    });
    toggleConfirmPwVis?.addEventListener('click', () => {
        if (!confirmPasswordInput) return;
        const isPassword = confirmPasswordInput.type === 'password';
        confirmPasswordInput.type = isPassword ? 'text' : 'password';
        if (confirmPwIcon) confirmPwIcon.textContent = isPassword ? 'visibility_off' : 'visibility';
    });
    [usernameInput, emailInput, newPasswordInput, confirmPasswordInput].forEach(input => {
        input?.addEventListener('input', () => {
            evaluatePassword();
            updateAccountSaveButton();
        });
    });
    saveAccountBtn?.classList.add('hidden');
    saveAccountBtn?.addEventListener('click', event => {
        event.preventDefault();
        if (!hasAccountChanges()) return;
        const password = newPasswordInput?.value || '';
        const confirmPassword = confirmPasswordInput?.value || '';
        const passwordChanged = password.length > 0 || confirmPassword.length > 0;
        if (passwordChanged) {
            const requirements = checkPasswordRequirements(password);
            if (!requirements.length) {
                showToast('Password must be at least 8 characters.', 'error');
                evaluatePassword();
                return;
            }
            if (!requirements.upper) {
                showToast('Password must contain an uppercase letter.', 'error');
                evaluatePassword();
                return;
            }
            if (!requirements.lower) {
                showToast('Password must contain a lowercase letter.', 'error');
                evaluatePassword();
                return;
            }
            if (!requirements.number) {
                showToast('Password must contain a number.', 'error');
                evaluatePassword();
                return;
            }
            if (!requirements.special) {
                showToast('Password must contain a special character.', 'error');
                evaluatePassword();
                return;
            }
            if (password !== confirmPassword) {
                showToast('Passwords do not match.', 'error');
                evaluatePassword();
                return;
            }
        }
        originalValues = inputs.map(input => input.value);
        if (newPasswordInput) newPasswordInput.value = '';
        if (confirmPasswordInput) confirmPasswordInput.value = '';
        originalValues = inputs.map(input => input.value);
        evaluatePassword();
        updateAccountSaveButton();
        showToast('Save Successful', 'success');
    });
    cancelAccountBtn?.addEventListener('click', () => {
        inputs.forEach((input, index) => {
            if (input && originalValues[index] !== undefined) {
                input.value = originalValues[index];
            }
        });
        evaluatePassword();
        updateAccountSaveButton();
    });
    function getCurrentGenres() {
        return Array.from(genreCheckboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.value)
            .sort();
    }
    function hasGenreChanges() {
        return JSON.stringify(getCurrentGenres()) !== JSON.stringify(originalGenres);
    }
    function updateGenreBoxes() {
        let count = 0;
        genreCheckboxes.forEach(checkbox => {
            const box = checkbox.closest('.genre-box');
            const labelText = box?.querySelector('span');
            if (checkbox.checked) {
                count++;
                box?.classList.remove('border-[#262626]', 'bg-[#121212]', 'hover:border-[#404040]');
                box?.classList.add('border-[#d8c4a7]', 'bg-[#d8c4a7]/10', 'hover:border-[#d8c4a7]');
                labelText?.classList.remove('text-[#F5F5F5]');
                labelText?.classList.add('text-[#d8c4a7]');
            } else {
                box?.classList.remove('border-[#d8c4a7]', 'bg-[#d8c4a7]/10', 'hover:border-[#d8c4a7]');
                box?.classList.add('border-[#262626]', 'bg-[#121212]', 'hover:border-[#d8c4a7]');
                labelText?.classList.remove('text-[#d8c4a7]');
                labelText?.classList.add('text-[#F5F5F5]');
            }
            checkbox.style.accentColor = '#d8c4a7';
        });
        if (genreCounter) {
            genreCounter.textContent = `${count} categories excluded`;
        }
        setButtonVisibility(saveGenresBtn, hasGenreChanges());
    }
    genreCheckboxes.forEach(checkbox => {
        checkbox.classList.remove('accent-primary');
        checkbox.addEventListener('change', updateGenreBoxes);
    });
    originalGenres = getCurrentGenres();
    saveGenresBtn?.classList.add('hidden');
    saveGenresBtn?.addEventListener('click', event => {
        event.preventDefault();
        if (!hasGenreChanges()) return;
        originalGenres = getCurrentGenres();
        updateGenreBoxes();
        showToast('Save Successful', 'success');
    });
    const openModalBtn = document.getElementById('open-delete-modal-btn');
    const modal = document.getElementById('delete-modal');
    const modalCloseIcon = document.getElementById('modal-close-icon');
    const modalCancelBtn = document.getElementById('modal-cancel-btn');
    const modalUsername = document.getElementById('modal-username');
    const modalPassword = document.getElementById('modal-password');
    const modalConfirmBtn = document.getElementById('modal-delete-confirm-btn');
    const modalError = document.getElementById('modal-error-msg');
    const modalUserHint = document.getElementById('modal-user-hint');
    const modalPwToggle = document.getElementById('modal-pw-vis-toggle');
    const modalPwIcon = document.getElementById('modal-pw-icon');
    function validateModal() {
        if (!modalUsername || !modalPassword || !modalConfirmBtn) return;
        const usernameValid = modalUsername.value.trim() === (usernameInput?.value.trim() || '');
        const passwordValid = modalPassword.value.length > 0;
        const valid = usernameValid && passwordValid;
        modalConfirmBtn.disabled = !valid;
        modalConfirmBtn.classList.toggle('opacity-40', !valid);
        modalConfirmBtn.classList.toggle('cursor-not-allowed', !valid);
        modalConfirmBtn.classList.toggle('cursor-pointer', valid);
        if (modalError) {
            modalError.classList.toggle('hidden', valid || (!modalUsername.value && !modalPassword.value));
        }
    }
    function openDeleteModal() {
        if (!modal) return;
        modal.classList.remove('hidden');
        setTimeout(() => modal.classList.remove('opacity-0'), 10);
        if (modalUsername) modalUsername.value = '';
        if (modalPassword) modalPassword.value = '';
        if (modalError) modalError.classList.add('hidden');
        if (modalUserHint) {
            const currentUsername = usernameInput?.value.trim() || '';
            modalUserHint.innerHTML = `Enter your username <span class="text-white font-semibold">${currentUsername}</span> to confirm`;
        }
        validateModal();
        modalUsername?.focus();
    }
    function closeDeleteModal() {
        if (!modal) return;
        modal.classList.add('opacity-0');
        setTimeout(() => modal.classList.add('hidden'), 150);
    }
    openModalBtn?.addEventListener('click', openDeleteModal);
    modalCloseIcon?.addEventListener('click', closeDeleteModal);
    modalCancelBtn?.addEventListener('click', closeDeleteModal);
    modal?.addEventListener('click', event => {
        if (event.target === modal) closeDeleteModal();
    });
    modalUsername?.addEventListener('input', validateModal);
    modalPassword?.addEventListener('input', validateModal);
    modalPwToggle?.addEventListener('click', () => {
        if (!modalPassword) return;
        const isPassword = modalPassword.type === 'password';
        modalPassword.type = isPassword ? 'text' : 'password';
        if (modalPwIcon) modalPwIcon.textContent = isPassword ? 'visibility_off' : 'visibility';
    });
    modalConfirmBtn?.addEventListener('click', () => {
        if (modalConfirmBtn.disabled) return;
        modalConfirmBtn.textContent = 'Decommissioning...';
        setTimeout(() => {
            showToast('Account decommissioned successfully', 'error');
            closeDeleteModal();
            modalConfirmBtn.textContent = 'Delete Account';
        }, 1000);
    });
    document.addEventListener('keydown', event => {
        if (event.key === 'Escape' && modal && !modal.classList.contains('hidden')) {
            closeDeleteModal();
        }
    });
    evaluatePassword();
    updateGenreBoxes();
    updateAccountSaveButton();
});
