
document.addEventListener('DOMContentLoaded', () => {
    const actionsBar = document.getElementById('profile-actions');
    const saveButton = document.getElementById('save-changes');
    const cancelButton = document.getElementById('cancel-changes');

    const notification = document.getElementById('success-notification');
    const closeButton = document.getElementById('close-success-notification');

    const inputs = [
        document.getElementById('username'),
        document.getElementById('email'),
        document.getElementById('current-pass'),
        document.getElementById('new-pass')
    ].filter(Boolean);

    if (!actionsBar || !saveButton || !cancelButton) {
        console.error('Profile actions elements not found.');
        return;
    }

    // เก็บค่าเริ่มต้นของช่องกรอกข้อมูล
    let originalValues = inputs.map(input => input.value);
    let notificationTimeout;

    // ตรวจสอบว่ามีการเปลี่ยนแปลงข้อมูลหรือไม่
    function checkForChanges() {
        const hasChanges = inputs.some(
            (input, index) => input.value !== originalValues[index]
        );

        if (hasChanges) {
            actionsBar.classList.remove('hidden');
            actionsBar.classList.add('flex');
        } else {
            actionsBar.classList.add('hidden');
            actionsBar.classList.remove('flex');
        }
    }

    // แสดง Notification
    function showSuccessNotification() {
        if (!notification) return;

        clearTimeout(notificationTimeout);
        notification.classList.remove('hidden');

        notificationTimeout = setTimeout(() => {
            notification.classList.add('hidden');
        }, 3000);
    }

    // ซ่อน Notification
    function hideNotification() {
        if (!notification) return;

        clearTimeout(notificationTimeout);
        notification.classList.add('hidden');
    }

    // ตรวจจับการพิมพ์หรือเปลี่ยนข้อมูลทุกช่อง
    inputs.forEach(input => {
        input.addEventListener('input', checkForChanges);
        input.addEventListener('change', checkForChanges);
    });

    // กด Save Changes
    saveButton.addEventListener('click', () => {
        // ซ่อนกล่องปุ่ม
        actionsBar.classList.add('hidden');
        actionsBar.classList.remove('flex');

        // อัปเดตค่าเริ่มต้นเป็นค่าปัจจุบัน
        originalValues = inputs.map(input => input.value);

        // ล้างช่องรหัสผ่านหลังบันทึก (ตัวอย่าง UI)
        const currentPassword = document.getElementById('current-pass');
        const newPassword = document.getElementById('new-pass');

        if (currentPassword) currentPassword.value = '';
        if (newPassword) newPassword.value = '';

        originalValues = inputs.map(input => input.value);

        // แสดงข้อความสำเร็จ
        showSuccessNotification();
    });

    // กด Cancel คืนค่าก่อนแก้ไข
    cancelButton.addEventListener('click', () => {
        inputs.forEach((input, index) => {
            input.value = originalValues[index];
        });

        actionsBar.classList.add('hidden');
        actionsBar.classList.remove('flex');

        hideNotification();
    });

    // กด X เพื่อปิด Notification
    if (closeButton) {
        closeButton.addEventListener('click', hideNotification);
    }


    const genreCheckboxes = document.querySelectorAll(
        'input[name="dislikedGenres"]'
    );
    const genreCount = document.getElementById('disliked-genre-count');

    // เก็บค่า Genre เริ่มต้นไว้สำหรับตรวจสอบการเปลี่ยนแปลง
    let originalGenreValues = Array.from(genreCheckboxes)
        .filter(checkbox => checkbox.checked)
        .map(checkbox => checkbox.value)
        .sort();

    function updateGenreSelection() {
        const selectedGenres = Array.from(genreCheckboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.value)
            .sort();

        // อัปเดตจำนวน Genre ที่เลือก
        if (genreCount) {
            genreCount.textContent = `${selectedGenres.length} selected`;
        }

        // ตรวจสอบว่ารายการ Genre เปลี่ยนไปหรือไม่
        const genresChanged =
            JSON.stringify(selectedGenres) !==
            JSON.stringify(originalGenreValues);

        if (genresChanged) {
            actionsBar.classList.remove('hidden');
            actionsBar.classList.add('flex');
        } else {
            // ถ้ารายการกลับเป็นค่าเดิม ให้ตรวจสอบช่องอื่นด้วย
            checkForChanges();
        }
    }

    genreCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateGenreSelection);
    });
});