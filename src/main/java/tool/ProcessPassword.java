// 这个文件处理所有密码相关，
package tool;

import dao.entity.令牌表;
import dao.entity.所有用户表;
import dao.impl.令牌表_实现;
import dao.impl.所有用户表_实现;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.annotation.WebListener;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.Base64;

@WebListener
public class ProcessPassword {  // 就不设置什么密钥管理了，先生成一次，值赋给这
    private static final KeyPair all_Key_RSA = generateKeyPair_RSA(2048);// 获取密钥
    private static final PublicKey publicKey_RSA = all_Key_RSA.getPublic();// 获取公钥
    private static final PrivateKey privateKey_RSA = all_Key_RSA.getPrivate();// 获取私钥

    public static final String sending_publicKey_RSA = Base64.getEncoder().encodeToString(publicKey_RSA.getEncoded());
    // 发给客户的公钥Base64字符串

    /**
     * 生成一对RSA加密密钥
     */
    public static KeyPair generateKeyPair_RSA(int keySize) {
        try {
            // 生成一对RSA非对称加密密钥,长度一般用2048位
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstanceStrong();
            keyGen.initialize(keySize, random); // 密钥大小可配置
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA默认密钥加密
     */
    public static String encrypt_RSA(String plainText) throws Exception {// 加密
        return encrypt_RSA(plainText, publicKey_RSA);
    }

    /**
     * RSA自定义密钥加密
     */
    public static String encrypt_RSA(String plainText, PublicKey publicKey) throws Exception {// 加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * RSA默认密钥解密
     */
    public static String decrypt_RSA(String encryptedText) throws Exception {// 解密
        return decrypt_RSA(encryptedText, privateKey_RSA);
    }

    /**
     * RSA自定义密钥解密
     */
    public static String decrypt_RSA(String encryptedText, PrivateKey privateKey) throws Exception {// 解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 解码 Base64 字符串为公钥
     */
    public static PublicKey RSAPublicKeyHandler(String base64PublicKey) throws Exception {
        // 解码 Base64 字符串为字节数组
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        // 使用 X509EncodedKeySpec 解析公钥
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // 生成 PublicKey 对象
        return keyFactory.generatePublic(spec);
    }

    /**
     * 验证签名
     */
    public static boolean verifySignature(String message, String signature, String base64PublicKey) throws Exception {
        // 获取公钥
        PublicKey publicKey = RSAPublicKeyHandler(base64PublicKey);

        // 使用 SHA-256withRSA 算法进行验证
        Signature signatureVerifier = Signature.getInstance("SHA256withRSA");
        signatureVerifier.initVerify(publicKey);
        signatureVerifier.update(message.getBytes("UTF-8"));

        // 将 Base64 编码的签名解码为字节数组
        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        // 验证签名
        return signatureVerifier.verify(signatureBytes);
    }

//    public static void main(String[] args) {
//        KeyPair key;
//        key = ProcessPassword.generateKeyPair_RSA(256);
//        System.out.println(key);
//    }

    // 客户端不好进行EC加密，转为使用RSA


    /**
     * 根据字符串生成AES密钥和 IV
     */
    public static SecretKeySpec generateKey(String keyStr) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(keyStr.getBytes(StandardCharsets.UTF_8));  // 字符串生成 256 位密钥
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * 获取字符串前16字节为AES加密中的IV
     */
    public static byte[] generateIV(String ivStr) throws UnsupportedEncodingException {
        return ivStr.substring(0, 16).getBytes(StandardCharsets.UTF_8);  // IV 直接使用字符串前 16 字节
    }

    /**
     * AES加密
     */
    public static String encrypt_AES(String plainText, SecretKeySpec key, byte[] ivBytes) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * AES解密
     */
    public static String decrypt_AES(String encryptedText, SecretKeySpec key, byte[] ivBytes) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * 直接用字符串进行AES加密
     */
    public static String encrypt_AES_Str(String plainText, String AES_str) throws Exception {
        SecretKeySpec AES_key = generateKey(AES_str);
        byte[] AES_iv = generateIV(AES_str);
        return encrypt_AES(plainText, AES_key, AES_iv);
    }

    /**
     * 直接用字符串和iv进行AES加密
     */
    public static String encrypt_AES_Str(String plainText, String AES_str,String iv) throws Exception {
        SecretKeySpec AES_key = generateKey(AES_str);
        return encrypt_AES(plainText, AES_key, iv.getBytes());
    }

    /**
     * 直接用字符串进行AES解密
     */
    public static String decrypt_AES_Str(String encryptedText, String AES_str) throws Exception {
        SecretKeySpec AES_key = generateKey(AES_str);
        byte[] AES_iv = generateIV(AES_str);
        return decrypt_AES(encryptedText, AES_key, AES_iv);
    }

    /**
     * 直接用字符串和iv进行AES解密
     */
    public static String decrypt_AES_Str(String encryptedText, String AES_str,String iv) throws Exception {
        SecretKeySpec AES_key = generateKey(AES_str);
        return decrypt_AES(encryptedText, AES_key, iv.getBytes());
    }

    /**
     * 判断是否有令牌，无论是否登录或注册完成都会删除令牌。未注册时num使用-1
     */
    public static boolean View_Token(String uid, String ip, int num) {
        // 判断是否有令牌，无论是否登录或注册完成都会删除令牌。
        令牌表_实现 tokenTableImpl = new 令牌表_实现();
        令牌表 tokenTable = tokenTableImpl.get_令牌表(uid);// 获取令牌
        tokenTableImpl.delete令牌表(uid);// 删除令牌
        java.sql.Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return !tokenTable.get失效时间().before(currentTime) && ip.equals(tokenTable.getIp()) && num == tokenTable.get账号();
    }

    /**
     * 对密码进行加盐
     */
    public static String password_Salt(String password, String salt) {
        // 密码在服务器端还会加盐
        try {
            // 获取SHA-256 MessageDigest实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 将字符串转换为字节数组，并计算哈希值
            byte[] hashBytes = digest.digest((password + salt).getBytes(StandardCharsets.UTF_8));

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断账号密码是否正确
     */
    public static int Determine_Account_Password(int id, String password) {
        try {
            // 判断账号密码是否正确，无账户0，密码错误1，正确2，
            所有用户表_实现 allUserTableImpl = new 所有用户表_实现();
            所有用户表 allUserTable = allUserTableImpl.get_所有用户表(id);
            if (allUserTable == null) {//无账户0
                return 0;
            }
            // 将保存的密码同用户输入的密码加盐结果比较是否相同（密码相同）
            if (ProcessingBlob.Blob_String(allUserTable.get密码()).equals(ProcessPassword.password_Salt(password, allUserTable.get密码盐值()))) {
                //密码正确
                return 2;
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}



