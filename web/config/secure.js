// Admin credentials (in production, store these securely in Firebase)
const ADMIN_CREDENTIALS = {
    username: "MGN",
    // This is a hashed version of the password 'MGN'
    passwordHash: "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92"
};

function verifyAdmin(username, password) {
    const hashedPassword = CryptoJS.SHA256(password).toString();
    return username === ADMIN_CREDENTIALS.username && hashedPassword === ADMIN_CREDENTIALS.passwordHash;
} 