// 这个文件用于前端的加解密,要crypto-js.min.js

//AES
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


// 生成 SHA-256 哈希密钥,直接32位
function generateAESKey(keyStr) {
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
function AES_encrypt(plainText, key_str, iv) {
    const  key = generateAESKey(key_str);
    // 执行加密
    const encrypted = CryptoJS.AES.encrypt(plainText, key, {
        iv: iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });

    return encrypted.ciphertext.toString(CryptoJS.enc.Base64); // 返回 Base64 字符串
}

// AES-CBC 解密
// key_str 未哈希的
function AES_decrypt(encryptedBase64, key_str, iv) {
    const  key = generateAESKey(key_str);

    // 将密钥和 IV 转换为 WordArray
    const keyWordArray = CryptoJS.enc.Utf8.parse(key);
    const ivWordArray = CryptoJS.enc.Utf8.parse(iv);
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

//RSA
// 签名消息，失败返回null
function signMessage(message, privateKey) {
    const signature = privateKey.sign(message, CryptoJS.SHA256, 'sha256'); // 使用 JSEncrypt 签名
    if (signature) {
        return signature;
    } else {
        return null;
    }
}
