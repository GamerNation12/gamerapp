const sharp = require('sharp');
const fs = require('fs').promises;

async function generateFavicons() {
    try {
        // Create a simple game controller icon
        const svg = `
            <svg width="32" height="32" viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg">
                <defs>
                    <linearGradient id="rainbow" x1="0%" y1="0%" x2="100%" y2="0%">
                        <stop offset="0%" style="stop-color:#FF69B4"/>
                        <stop offset="20%" style="stop-color:#FF0000"/>
                        <stop offset="40%" style="stop-color:#FFFF00"/>
                        <stop offset="60%" style="stop-color:#00FF00"/>
                        <stop offset="80%" style="stop-color:#00FFFF"/>
                        <stop offset="100%" style="stop-color:#8A2BE2"/>
                    </linearGradient>
                </defs>
                <path d="M28 10c0-1.1-.9-2-2-2H6c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h20c1.1 0 2-.9 2-2V10zm-5 6c0 .6-.4 1-1 1h-2v2c0 .6-.4 1-1 1s-1-.4-1-1v-2h-2c-.6 0-1-.4-1-1s.4-1 1-1h2v-2c0-.6.4-1 1-1s1 .4 1 1v2h2c.6 0 1 .4 1 1zm-12 0c0 .6-.4 1-1 1H8c-.6 0-1-.4-1-1s.4-1 1-1h2c.6 0 1 .4 1 1z" 
                    fill="url(#rainbow)" />
            </svg>`;

        // Save SVG file
        await fs.writeFile('favicon.svg', svg);

        // Generate PNG versions
        const svgBuffer = Buffer.from(svg);

        // Generate 32x32 PNG
        await sharp(svgBuffer)
            .resize(32, 32)
            .png()
            .toFile('favicon-32x32.png');

        // Generate 16x16 PNG
        await sharp(svgBuffer)
            .resize(16, 16)
            .png()
            .toFile('favicon-16x16.png');

        // Generate ICO file (16x16)
        await sharp(svgBuffer)
            .resize(16, 16)
            .toFormat('ico')
            .toFile('favicon.ico');

        // Generate Apple Touch Icon
        await sharp(svgBuffer)
            .resize(180, 180)
            .png()
            .toFile('apple-touch-icon.png');

        console.log('Favicons generated successfully!');
    } catch (error) {
        console.error('Error generating favicons:', error);
    }
}

generateFavicons(); 