
function togglePasswordVisibility(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icon = document.getElementById(iconId);
    if (input.type === 'password') {
        input.type = 'text';
        icon.textContent = 'visibility_off';
    } else {
        input.type = 'password';
        icon.textContent = 'visibility';
    }
}

function updateRequirement(elId, met) {
    const item = document.getElementById(elId);
    const icon = item.querySelector('.material-symbols-outlined');
    if (met) {
        icon.textContent = 'check_circle';
        icon.className = 'material-symbols-outlined text-[16px] text-secondary';
        item.classList.add('text-on-surface');
        item.classList.remove('text-on-surface-variant');
    } else {
        icon.textContent = 'radio_button_unchecked';
        icon.className = 'material-symbols-outlined text-[16px] text-outline';
        item.classList.remove('text-on-surface');
        item.classList.add('text-on-surface-variant');
    }
}

function validatePasswordStrength(pwd) {
    const hasLength = pwd.length >= 8;
    const hasCase = /[a-z]/.test(pwd) && /[A-Z]/.test(pwd);
    const hasNum = /[0-9]/.test(pwd);
    const hasSpecial = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(pwd);

    updateRequirement('req-length', hasLength);
    updateRequirement('req-case', hasCase);
    updateRequirement('req-number', hasNum);
    updateRequirement('req-special', hasSpecial);

    const score = [hasLength, hasCase, hasNum, hasSpecial].filter(Boolean).length;

    const bars = [
        document.getElementById('bar-1'),
        document.getElementById('bar-2'),
        document.getElementById('bar-3'),
        document.getElementById('bar-4')
    ];
    const label = document.getElementById('strength-label');

    // Reset styles
    bars.forEach(b => {
        b.className = 'h-full rounded-full bg-surface-container-high transition-colors duration-300';
    });

    if (!pwd) {
        label.textContent = 'Empty';
        label.className = 'font-manrope text-[11px] text-on-surface-variant font-semibold';
        return;
    }

    if (score === 1) {
        label.textContent = 'Weak';
                    label.className = 'font-manrope text-[11px] text-on-surface-variant font-semibold';
        bars[0].className = 'h-full rounded-full bg-error transition-colors duration-300';
    } else if (score === 2) {
        label.textContent = 'Fair';
        label.className = 'font-manrope text-[11px] text-on-surface-variant font-semibold';
        bars[0].className = 'h-full rounded-full bg-primary-container transition-colors duration-300';
        bars[1].className = 'h-full rounded-full bg-primary-container transition-colors duration-300';
    } else if (score === 3) {
        label.textContent = 'Good';
        label.className = 'font-manrope text-[11px] text-on-surface-variant font-semibold';
        bars[0].className = 'h-full rounded-full bg-secondary transition-colors duration-300';
        bars[1].className = 'h-full rounded-full bg-secondary transition-colors duration-300';
        bars[2].className = 'h-full rounded-full bg-secondary transition-colors duration-300';
    } else if (score === 4) {
        label.textContent = 'Strong';
        label.className = 'font-manrope text-[11px] text-on-surface-variant font-semibold';
        bars.forEach(b => {
            b.className = 'h-full rounded-full bg-secondary transition-colors duration-300';
        });
    }

    validateMatch();
}

function validateMatch() {
    const newPwd = document.getElementById('new-password').value;
    const confPwd = document.getElementById('confirm-password').value;
    const badge = document.getElementById('match-badge');

    if (newPwd && confPwd && newPwd === confPwd) {
        badge.classList.remove('hidden');
        badge.classList.add('flex');
    } else {
        badge.classList.add('hidden');
        badge.classList.remove('flex');
    }
}