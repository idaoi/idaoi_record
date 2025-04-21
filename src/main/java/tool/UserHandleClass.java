// 起名为用户处理类，重写之前的大部分代码，变更面向过程为面向对象。
// 考虑使用对象池（未实现）
package tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.entity.*;
import dao.impl.*;
import org.json.JSONArray;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.PublicKey;
import java.sql.Blob;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

public class UserHandleClass {
    //  用户处理类，
    //  一个用户未登录时需要所有用户表的数据，使用令牌表验证
    //  登陆后需要用户信息表的数据，并通过随笔索引表获取随笔内容表的数据，使用登陆表验证。
    //  一个用户会有注册，登录，验证，退出，获取信息，修改信息这些操作，
    public String problem = null;// 用于记录发生的问题。
    public HttpServletRequest request;// 发来的数据
    public int name = -1;   // 用户名
    public String ip;  // ip地址
    public String secretKey = null; // 保持登录密钥
    public Map<String, String> requestData;// 用户发来的json数据

    /**
     * 各种有提交数据的情况，输入requestData
     */
    public UserHandleClass(HttpServletRequest request, Map<String, String> requestData) {
        this.request = request;
        this.requestData = requestData;
    }

    /**
     * 其他方式
     */
    public UserHandleClass(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 判断令牌是否有效
     */
    public boolean IsView_Token(String server_Token) {
        return ProcessPassword.View_Token(server_Token, this.ip, this.name);
    }

    /**
     * 登录，返回一个Map，外面通过这个Map设置返回值和cookie
     */
    public Map<String, String> log_On_Account() {
        // 逻辑：查看令牌无误，删除令牌，
        // 查看账号密码正确，设置登录表，整个新保持登录密钥，若发现有人在登录，顶号。
        Map<String, String> responseData = new HashMap<>();
        try {
            // 获取账户，密码，ip
            this.name = Integer.parseInt(requestData.get("username"));
            this.ip = UserHandleClass.getUserIp(request);// 获取ip
            String password = requestData.get("password");// 获取密码
            String server_Token, AES_str;// 密钥在账号后面放着，为了防止重放攻击
            password = ProcessPassword.decrypt_RSA(password);// 解密密码
            if (password.length() > 8 + 36 + 32) {// 长度大于密码+令牌+密钥
                AES_str = password.substring(password.length() - 32);//客户端发来的临时AES密钥字符串
                server_Token = password.substring(password.length() - 36 - 32, password.length() - 32);// 一次性服务器令牌
                password = password.substring(0, password.length() - 36 - 32);// 哈希后的密码
            } else {
                responseData.put("state", "password error");// 状态字符串
                return responseData;
            }
            if (IsView_Token(server_Token)) {// 判断令牌没问题
                switch (ProcessPassword.Determine_Account_Password(this.name, password)) {// 判断账号密码是否正确，无账户0，密码错误1，正确2，
                    case 0:
                        responseData.put("state", "Account error");//无账户0
                        break;
                    case 1:
                        responseData.put("state", "Password error");//密码错误1
                        break;
                    case 2:
                        //正确2
                        String user_uid = UUID.randomUUID().toString();// 生成本次登录密钥
                        String clientKey_str = requestData.get("client_publicKey");
                        clientKey_str = ProcessPassword.decrypt_AES_Str(clientKey_str, AES_str);// 解密出客户端公钥
                        PublicKey clientKey = ProcessPassword.RSAPublicKeyHandler(clientKey_str);// 客户端 RSA 加密公钥
                        String encryption_uid = ProcessPassword.encrypt_RSA(user_uid, clientKey);
                        Modify_Login_Status(this.name, this.ip, encryption_uid, user_uid, clientKey_str);// 正在登陆
                        responseData.put("secret_key", encryption_uid);// 本次登录的密钥，也是此次登录的AES加密密钥
//                        responseData.put("secret_key", user_uid);// 本次登录的密钥
                        responseData.put("username", String.format("%09d", this.name));// 用户名
                        responseData.put("state", "OK");// 没问题
                        break;
                }
            } else {
                responseData.put("state", "Token error");//令牌错误
            }
        } catch (Exception e) {
            responseData.put("state", "logOnSubmit error");// 状态字符串
            return responseData;
        }
        return responseData;
    }

    /**
     * 修改登录表内容。为正在登录
     */
    private void Modify_Login_Status(int num, String ip, String uid, String real_uid, String clientKey) {
        // 修改登录表内容。以保持登录
        登录表_实现 loginTableImpl = new 登录表_实现();
        登录表 loginTable = loginTableImpl.get_登录表(num);// 查找账号的登录表
        if (loginTable == null) {// 没找到创建一个
            loginTable = new 登录表(num, ip, new Timestamp(System.currentTimeMillis()), null, clientKey, uid, real_uid, 1);
            loginTableImpl.insert登录表(loginTable);
        } else {// 找到了，修改数据
            loginTable.setIp(ip);
            loginTable.set登录时间(new Timestamp(System.currentTimeMillis()));
            loginTable.set退出时间(null);
            loginTable.set保持登录密钥(uid);
            loginTable.set实际保持登录密钥(real_uid);
            loginTable.set附加值(clientKey);
            loginTable.set保持链接(1);
            loginTableImpl.update登录表(loginTable);
        }
    }

    /**
     * 注册账户，返回一个Map，外面通过这个Map设置返回值和cookie
     */
    public Map<String, String> register_New_User() {
        // 逻辑：查看令牌无误，删除令牌，
        // 创一个用户，告诉用户账号。
        Map<String, String> responseData = new HashMap<>();
        try {
            this.ip = getUserIp(this.request);// 获取ip
            String password = requestData.get("password");// 获取密码
            String server_Token;// 密钥在账号后面放着，为了防止重放攻击
            password = ProcessPassword.decrypt_RSA(password);// 解密密码
            if (password.length() > 8+36) {
                server_Token = password.substring(password.length() - 36);
                password = password.substring(0, password.length() - 36);
            } else {
                responseData.put("state", "password error");// 状态字符串
                return responseData;
            }
            // 用户名默认-1
            if (IsView_Token(server_Token)) {// 判断令牌没问题
                所有用户表_实现 allUserTableImpl = new 所有用户表_实现();
                String salt = UUID.randomUUID().toString();// 加的盐
                String save_password = ProcessPassword.password_Salt(password, salt);
                if (save_password != null) {// 加上盐了
                    Blob null_blob = ProcessingBlob.String_Blob("");
                    所有用户表 allUserTable = new 所有用户表(-1, ProcessingBlob.String_Blob("默认用户名"),
                            ProcessingBlob.String_Blob(save_password), null_blob, salt);
                    int user = allUserTableImpl.insert所有用户表(allUserTable);// 注册账号
                    this.name = user;
                    if (user < 0) {
                        // 记录错误日志，，，，，，，我还没写记日志方法。
                        responseData.put("state", "registerSubmit error");
                        return responseData;
                    }
                    // 设置用户的默认信息
                    用户信息表_实现 userInformation_TableImpl = new 用户信息表_实现();
                    // 生成默认的AES加密密钥
                    String AES_Key = UUID.randomUUID().toString();
                    用户信息表 userInformation_Table = new 用户信息表(user, null,
                            AES_Key, null_blob, null_blob, 0, 0, 0, 0);
                    userInformation_TableImpl.insert用户信息表(userInformation_Table);// 创建一条用户信息表数据
                    responseData.put("username", String.format("%09d", this.name));// 账号为9位字符串
                    标签表_实现 tag_TableImpl = new 标签表_实现();
                    String[] tag =new String[]{"记录","完成","待办","紧急"};
                    for (String string : tag) {
                        tag_TableImpl.insert标签表(new 标签表(-1, this.name, string));
                    }
                    responseData.put("state", "OK");// 没问题
                    return responseData;
                } else {
                    responseData.put("state", "registerSubmit error");
                    return responseData;
                }
            } else {
                responseData.put("state", "Token error");//令牌错误
                return responseData;
            }
        } catch (Exception e) {
            responseData.put("state", "registerSubmit error");
            return responseData;
        }
    }

    /**
     * 退出登录
     */
    public String log_Out_Account() {
        if (this.get_detection_data()) {// 获取登陆检测所需数据
            switch (this.login_Detection()) {
                //-1=未知错误 0=OK，1=无用户，2=登陆失效，3=ip错误，4=密钥错误
                case 0:
                    登录表_实现 loginTableImpl = new 登录表_实现();
                    loginTableImpl.update登录表(this.name, new Timestamp(System.currentTimeMillis()));// 设置退出时间
                    loginTableImpl.update登录表(this.name, 0);// 设置已经退出
                    return "OK";
                case 1:
                    return "用户名无效，账号10min内会自动退出";
                case 2:
                    return "账户已退出";
                case 3:
                    return "ip错误，账号10min内会自动退出";
                case 4:
                    return "密钥错误,账号10min内会自动退出";
                case -1:
                default:
                    return "未知错误,账号10min内会自动退出";

            }
        }
        return "未知错误,账号10min内会自动退出";
    }

    /**
     * 修改密码
     */
    public String change_Password() {
        // 传说中的V字形代码
        try {
            if (this.get_detection_data()) {    // 获取检测所需数据
                String old_password = requestData.get("old_password");// 获取密码
                old_password = ProcessPassword.decrypt_RSA(old_password);// 解密密码
                Instant clientTime = Instant.parse(old_password.substring(old_password.length() - 24));// 失效时间长24
                String AES_Key = old_password.substring(old_password.length() - 36 - 24, old_password.length() - 24);// 密钥长36
                if (this.login_Detection(AES_Key, clientTime) == 0) {    // 检测密钥是否正确
                    old_password = old_password.substring(0, old_password.length() - 36 - 24);
                    if (ProcessPassword.Determine_Account_Password(this.name, old_password) == 2) {// 判断账号密码是否正确，无账户0，密码错误1，正确2，
                        // 账号密码正确了，修改新密码
                        String new_password = requestData.get("new_password");// 获取密码
                        new_password = ProcessPassword.decrypt_RSA(new_password);// 解密密码
                        if (!AES_Key.equals(new_password.substring(new_password.length() - 36 - 24, new_password.length() - 24))) {
                            return "错误";
                        }
                        new_password = new_password.substring(0, new_password.length() - 36 - 24);
                        所有用户表_实现 allUserTableImpl = new 所有用户表_实现();
                        String salt = UUID.randomUUID().toString();// 加的新盐
                        String save_password = ProcessPassword.password_Salt(new_password, salt);// 加新盐后的新密码
                        if (save_password != null) {// 加上盐了
                            // 修改密码
                            allUserTableImpl.update所有用户表(this.name, ProcessingBlob.String_Blob(save_password), salt);
                            return "OK";
                        } else {
                            return "新密码错误";
                        }
                    } else {
                        return "旧密码错误";
                    }
                } else {
                    return "错误登录";
                }
            }
        } catch (Exception e) {
            return "错误登录";
        }
        return "错误";
    }

    /**
     * 心跳检测
     */
    public String heartbeat_detection() {
        if (this.get_detection_data()) {// 获取登陆检测所需数据
            switch (this.login_Detection()) {
                //-1=未知错误 0=OK，1=无用户，2=登陆失效，3=ip错误，4=密钥错误
                case 0:
                    // 确认登录了，将保持登录链接置为1
                    登录表_实现 loginTableImpl = new 登录表_实现();
                    loginTableImpl.update登录表(this.name, 1);
                    return "OK";
                case 2:
                    return "账户已退出，请重新登录";
                case 3:
                    return "ip错误，请重新登录";
                case 1:
                    return "用户名无效，请重新登录";
                case 4:
                    return "密钥错误，请重新登录";
                case -1:
                default:
                    return "未知错误，请重新登录";

            }
        }
        return "未知错误，请重新登录";
    }

    /**
     * 初始化获取数据
     */
    public Map<String, String> Initialization() {
        Map<String, String> responseData = new HashMap<>();
        try {
            if (this.get_detection_data()) {// 获取登陆检测所需数据
                String message_AES_key = this.requestData.get("secret_key");// 获取消息
                String AES_key = ProcessPassword.decrypt_RSA(message_AES_key);// 解密消息
                Instant clientTime = Instant.parse(AES_key.substring(AES_key.length() - 24));// 失效时间长24
                AES_key = AES_key.substring(0, 36);// 密钥长36
                String signature = this.requestData.get("signature");// 签名
                if (this.login_Detection(AES_key, clientTime, message_AES_key, signature) == 0) {// 消息没问题
                    用户信息表_实现 userInformation_TableImpl = new 用户信息表_实现();// 获取用户信息表数据
                    用户信息表 userInformation = userInformation_TableImpl.get_用户信息表(this.name);
                    // 用户默认AES密钥
                    responseData.put("default_AES", ProcessPassword.encrypt_AES_Str(userInformation.get附加值1(), AES_key));
                    // 使用SQL 获取该用户的所有随笔索引表它需要知道的内容
                    随笔索引表_实现 essaysIndexTable_Impl = new 随笔索引表_实现();
                    List<随笔索引表> essays_index = essaysIndexTable_Impl.get_all_随笔索引表(this.name);
                    // 之后考虑加个压缩
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonString = objectMapper.writeValueAsString(essays_index);
                    responseData.put("index", ProcessPassword.encrypt_AES_Str(jsonString, AES_key));
                    // 标签
                    标签表_实现 tag_TableImpl = new 标签表_实现();
                    List<标签表> tag =  tag_TableImpl.get_all_标签表(this.name);
                    responseData.put("label",objectMapper.writeValueAsString(tag));// 所有标签

                    responseData.put("state", "OK");// 没问题
                    return responseData;
                }
            }
            Map<String, String> responseData_out = new HashMap<>();
            responseData.put("state", "Initialization error");//错误
            return responseData_out;
        } catch (Exception e) {
            Map<String, String> responseData_out = new HashMap<>();
            responseData.put("state", "Initialization error");//错误
            return responseData_out;
        }
    }

    /**
     * 获取检测所需的数据，
     * 检测需要数据 （账号，ip，保持登录密钥)都从cookies中获取
     */
    public boolean get_detection_data() {
        // 获取cookie,
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                this.ip = UserHandleClass.getUserIp(request);
                for (Cookie cookie : cookies) {
                    if ("username".equals(cookie.getName())) {
                        this.name = Integer.parseInt(cookie.getValue());
                    }
                    if ("secret_key".equals(cookie.getName())) {
                        this.secretKey = cookie.getValue();
                        break;
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 检测登录验证
     * 需要执行过get_detection_data()，验证用户名，ip，密钥
     * 返回一个状态码（觉着不舒服的可以换成枚举），-1=未知错误 0=OK，1=无用户，2=登陆失效，3=ip错误，4=密钥错误
     */
    private int login_Detection() {
        try {
            if (this.name != -1) {
                登录表_实现 loginTableImpl = new 登录表_实现();
                登录表 loginTable = loginTableImpl.get_登录表(this.name);// 查找账号的登录表
                if (loginTable == null) {
                    return 1;
                }
                if (loginTable.get退出时间() != null) {
                    return 2;
                }
                if (!ip.equals(loginTable.getIp())) {
                    return 3;
                }
                if (!secretKey.equals(loginTable.get保持登录密钥())) {
                    return 4;
                }
                return 0;
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 检测登录验证 带密钥参数
     * 需要执行过get_detection_data()，验证用户名，ip，密钥
     * 返回一个状态码（觉着不舒服的可以换成枚举），-1=未知错误 0=OK，1=无用户，2=登陆失效，3=ip错误，4=密钥错误
     */
    private int login_Detection(String AES_key, Instant clientTime) {
        try {
            // 获取服务器当前时间
            Instant serverTime = Instant.now();
            // 比较时间（判断是否失效）
            if (serverTime.getEpochSecond() > clientTime.getEpochSecond()) {
                return 4;
            }
            if (this.name != -1) {
                登录表_实现 loginTableImpl = new 登录表_实现();
                登录表 loginTable = loginTableImpl.get_登录表(this.name);// 查找账号的登录表
                if (loginTable == null) {
                    return 1;
                }
                if (loginTable.get退出时间() != null) {
                    return 2;
                }
                if (!ip.equals(loginTable.getIp())) {
                    return 3;
                }
                if (!secretKey.equals(loginTable.get保持登录密钥())) {
                    return 4;
                }
                if (!AES_key.equals(loginTable.get实际保持登录密钥())) {
                    return 4;
                }
                return 0;
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 检测登录验证 带密钥参数,带签名
     * 需要执行过get_detection_data()，验证用户名，ip，密钥
     * 返回一个状态码（觉着不舒服的可以换成枚举），-1=未知错误 0=OK，1=无用户，2=登陆失效，3=ip错误，4=密钥错误
     */
    private int login_Detection(String AES_key, Instant clientTime, String message, String signature) {
        try {
            // 获取服务器当前时间
            Instant serverTime = Instant.now();
            // 比较时间（判断是否失效）
            if (serverTime.getEpochSecond() > clientTime.getEpochSecond()) {
                return 4;
            }
            if (this.name != -1) {
                登录表_实现 loginTableImpl = new 登录表_实现();
                登录表 loginTable = loginTableImpl.get_登录表(this.name);// 查找账号的登录表
                if (loginTable == null) {
                    return 1;
                }
                if (loginTable.get退出时间() != null) {
                    return 2;
                }
                if (!ip.equals(loginTable.getIp())) {
                    return 3;
                }
                if (!secretKey.equals(loginTable.get保持登录密钥())) {
                    return 4;
                }
                if (!AES_key.equals(loginTable.get实际保持登录密钥())) {
                    return 4;
                }
                if (!ProcessPassword.verifySignature(message, signature, loginTable.get附加值())) {
                    return 4;
                }
                return 0;
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 静态，获取信息来源的ip
     */
    public static String getUserIp(HttpServletRequest request) {
        // 获得ip
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        } else {
            // X-Forwarded-For头可能包含多个IP地址（逗号分隔），通常第一个IP是原始客户端的IP
            String[] addresses = ipAddress.split(",");
            if (addresses.length > 0) {
                ipAddress = addresses[0].trim();
            }
        }
        return ipAddress;
    }

}

