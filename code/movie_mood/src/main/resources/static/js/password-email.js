document.addEventListener('DOMContentLoaded', () => {
      let secondsLeft = 52;
      const countdownEl = document.getElementById('countdown');
      const resendBtn = document.getElementById('resend-btn');
      const resendText = document.getElementById('resend-text');
      const spinner = document.getElementById('spinner-icon');
      const toast = document.getElementById('toast');

      const timer = setInterval(() => {
        secondsLeft--;
        if (countdownEl) countdownEl.innerText = secondsLeft;

        if (secondsLeft <= 0) {
          clearInterval(timer);
          if (resendBtn && resendText && spinner) {
            resendBtn.disabled = false;
            resendBtn.classList.remove('opacity-60', 'cursor-not-allowed');
            resendBtn.classList.add('cursor-pointer');
            spinner.classList.remove('animate-spin');
            spinner.innerText = 'send';
            resendText.innerHTML = 'Resend email now';
          }
        }
      }, 1000);

      if (resendBtn) {
        resendBtn.addEventListener('click', () => {
          if (secondsLeft <= 0) {
            if (toast) {
              toast.classList.remove('translate-y-24', 'opacity-0', 'pointer-events-none');
              toast.classList.add('translate-y-0', 'opacity-100');

              setTimeout(() => {
                toast.classList.add('translate-y-24', 'opacity-0', 'pointer-events-none');
                toast.classList.remove('translate-y-0', 'opacity-100');
              }, 4000);
            }

            secondsLeft = 60;
            resendBtn.disabled = true;
            resendBtn.classList.add('opacity-60', 'cursor-not-allowed');
            resendBtn.classList.remove('cursor-pointer');
            spinner.classList.add('animate-spin');
            spinner.innerText = 'sync';
            resendText.innerHTML = `Resend email in <span id="countdown">${secondsLeft}</span>s`;

            const newTimer = setInterval(() => {
              secondsLeft--;
              const cd = document.getElementById('countdown');
              if (cd) cd.innerText = secondsLeft;
              if (secondsLeft <= 0) {
                clearInterval(newTimer);
                resendBtn.disabled = false;
                resendBtn.classList.remove('opacity-60', 'cursor-not-allowed');
                resendBtn.classList.add('cursor-pointer');
                spinner.classList.remove('animate-spin');
                spinner.innerText = 'send';
                resendText.innerHTML = 'Resend email now';
              }
            }, 1000);
          }
        });
      }
    });