function toggleTheme() {
    const html = document.documentElement;
    const themeIcon = document.getElementById('theme-icon');
    const currentTheme = html.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    
    html.setAttribute('data-theme', newTheme);
    themeIcon.textContent = newTheme === 'dark' ? '🌙' : '☀️';
    
    localStorage.setItem('theme', newTheme);
}

function loadTheme() {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
        document.documentElement.setAttribute('data-theme', savedTheme);
        document.getElementById('theme-icon').textContent = savedTheme === 'dark' ? '🌙' : '☀️';
    } else {
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        document.documentElement.setAttribute('data-theme', prefersDark ? 'dark' : 'light');
        document.getElementById('theme-icon').textContent = prefersDark ? '🌙' : '☀️';
    }
}

document.addEventListener('DOMContentLoaded', loadTheme); 