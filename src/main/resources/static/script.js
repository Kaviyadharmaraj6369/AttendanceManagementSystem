
document.addEventListener('DOMContentLoaded', function() {
    const dateInputs = document.querySelectorAll('input[type="date"]');
    const today = new Date().toISOString().split('T')[0];
    dateInputs.forEach(input => {
        if (!input.value) {
            input.value = today;
        }
    });
});


function confirmDelete(message) {
    return confirm(message || 'Are you sure you want to delete this?');
}


function toggleAll(source) {
    const checkboxes = document.querySelectorAll('input[name="studentUsernames"]');
    checkboxes.forEach(cb => cb.checked = source.checked);
}