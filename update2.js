const fs = require('fs');
let html = fs.readFileSync('web/index.html', 'utf8');

// Remove orbs
html = html.replace(/<div class="bg-orbs">[\s\S]*?<\/div>\s*<\/div>/g, '');
html = html.replace(/<div class="bg-orbs">[\s\S]*?<\/div>/g, ''); 
html = html.replace(/<div class="orb.*?<\/div>/g, ''); 

// Add toast container
if (!html.includes('toast-container')) {
    html = html.replace('<body>', '<body>\n    <div id="toast-container"></div>');
}

// Add showToast function definition
const toastFunction = `
        function showToast(message, type = 'error') {
            // Support object/error payloads that shouldn't be stringified directly in alert() calls occasionally
            if (typeof message === 'object') return;
            
            let container = document.getElementById('toast-container');
            if(!container) return;
            
            const toast = document.createElement('div');
            toast.className = 'toast ' + type;
            toast.textContent = message;
            container.appendChild(toast);
            
            // Trigger reflow for animation
            void toast.offsetWidth;
            toast.classList.add('show');
            
            setTimeout(() => {
                toast.classList.remove('show');
                setTimeout(() => toast.remove(), 300);
            }, 3000);
        }
`;
if (!html.includes('function showToast')) {
    html = html.replace('<script>', '<script>\n' + toastFunction);
}

// Replace alert with showToast
html = html.replace(/alert\((.*?)\)/g, 'showToast($1, "error")');


fs.writeFileSync('web/index.html', html);

// Now update style.css
let css = fs.readFileSync('web/style.css', 'utf8');

// Change body background to a beautiful simple dark gradient instead of flat color, since they hated orbs
css = css.replace(/background: var\(--bg-base\);/g, 'background: linear-gradient(135deg, #0a0514 0%, #151025 100%);');

const toastCss = `
/* --- Toast Notifications --- */
#toast-container {
    position: fixed;
    top: 24px;
    right: 24px;
    z-index: 9999;
    display: flex;
    flex-direction: column;
    gap: 12px;
    pointer-events: none;
}

.toast {
    background: rgba(20, 15, 30, 0.95);
    backdrop-filter: blur(24px);
    -webkit-backdrop-filter: blur(24px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: white;
    padding: 14px 24px;
    border-radius: 12px;
    font-family: 'Outfit', sans-serif;
    font-size: 14px;
    font-weight: 500;
    box-shadow: 0 10px 40px rgba(0,0,0,0.6);
    transform: translateX(120%);
    opacity: 0;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.toast.show {
    transform: translateX(0);
    opacity: 1;
}

.toast.error {
    border-left: 4px solid #ff4d4f;
}

.toast.success {
    border-left: 4px solid #00e676;
}
`;

if (!css.includes('#toast-container')) {
    css += toastCss;
}

// Remove orb css
css = css.replace(/\/\* --- Animated Background Orbs --- \*\/[\s\S]*?\/\* --- Global Glass Definition --- \*\//, '/* --- Global Glass Definition --- */');

fs.writeFileSync('web/style.css', css);

console.log('Update complete');
