// 这个文件用于前端AES的加解密,要crypto-js.min.js aes.min.js sha256.min.js

// 根据字符串生成密钥和 IV

// 生成随机32位字符串
function generate32BitRandomString() {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charactersLength = characters.length;
    let result = '';

    for (let i = 0; i < 32; i++) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }

    return result;
}


// 生成 SHA-256 哈希密钥
function generateKey(keyStr) {
    return CryptoJS.SHA256(keyStr); // 直接返回 WordArray 对象
}

// 生成 IV（取字符串前 16 字节）
function generateIV(ivStr) {
    const wordArray = CryptoJS.enc.Utf8.parse(ivStr);

    // 手动截取前 16 字节
    const clamped = wordArray.clone();
    clamped.words = wordArray.words.slice(0, 4); // 每个 word 是 4 字节
    clamped.sigBytes = 16; // 设置有效字节数为 16

    return clamped;
}

// 生成 IV（随机生成）
function generateIV_random() {
    return CryptoJS.lib.WordArray.random(16);
}

// 加密
function AES_encrypt(plainText, key, iv) {

    // 执行加密
    const encrypted = CryptoJS.AES.encrypt(plainText, key, {
        iv: iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });

    return encrypted.ciphertext.toString(CryptoJS.enc.Base64); // 返回 Base64 字符串
}

// AES-CBC 解密
function AES_decrypt(encryptedBase64, key, iv) {
    // 将 Base64 字符串转换为 CipherParams 对象
    const encrypted = CryptoJS.lib.CipherParams.create({
        ciphertext: CryptoJS.enc.Base64.parse(encryptedBase64)
    });
    // 执行解密
    const decrypted = CryptoJS.AES.decrypt(encrypted, key, {
        iv: iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });
    return decrypted.toString(CryptoJS.enc.Utf8); // 返回解密后的明文
}