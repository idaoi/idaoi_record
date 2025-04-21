//这个类用于处理登录界面发来的验证请求,通过发来的post请求回复服务器令牌
//只用于分发免人机验证或者人机验证成功后的的服务器令牌，不处理登录
//谷歌验证说实话有点2，接下来考虑换
//添加了极验
//发过去的服务器令牌用于验证人机以及防止重放攻击

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.entity.令牌表;
import dao.entity.登录表;
import dao.impl.令牌表_实现;
import dao.impl.登录表_实现;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tool.ProcessPassword;
import tool.UserHandleClass;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

// 使用@WebServlet注解来映射Servlet的URL
@WebServlet("/Verify_Get_Server_Token")
public class Verify_Get_Server_Token extends HttpServlet {  // 继承处理http请求

    private static final long serialVersionUID = 1L;//序列化对象版本,确保发送方和接收方的序列化对象版本兼容
    private static final String secret = null;//谷歌人机验证id
    private static final String requestUrl = "https://www.recaptcha.net/recaptcha/api/siteverify";// 谷歌国内api
    //  private static final String requestUrl = "https://www.google.com/recaptcha/api/siteverify";

    // 初始化极验参数信息
    private static final String captchaId = null;
    private static final String captchaKey = null;
    private static final String domain = "http://gcaptcha4.geetest.com";

    // 如果不添加密钥，（密钥为null），则会直接给令牌，只是为了演示，建议自己修改

    // 收到post请求
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应内容类型为JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {//获取可以向客户端发送字符文本的PrintWriter
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> requestData = objectMapper.readValue(request.getReader(), Map.class);
            String verify_type = requestData.get("verify_type");//无返回null
            String out_string = null;


            switch (verify_type) {
                case "username_Toke":   // 判断是否需要进行人机验证
                    // 获取cookie,得到secret_key字段，上次登录后返回的保持登录密钥字段。
                    Cookie[] cookies = request.getCookies();
                    String secretKey = null;
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if ("secret_key".equals(cookie.getName())) {
                                secretKey = cookie.getValue();
                                break;
                            }
                        }
                    }
                    // 获取发送来信息的账号id
                    try {
                        String username = requestData.get("username");
                        int num = Integer.parseInt(username);
                        String ip =  UserHandleClass.getUserIp(request);
                        if (If_laissez_passer(num, secretKey, ip)) {
                            out_string = get_Token(ip, num);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    break;
                case "recaptchaToken":  // 谷歌登录分发令牌
                    String recaptcha_token = requestData.get("recaptchaToken");
                    try {
                        String username = requestData.get("username");
                        int num;
                        if (username == null) {
                            num = -1;
                        } else {
                            num = Integer.parseInt(username);
                        }
                        if (secret==null){
                            out_string = get_Token(UserHandleClass.getUserIp(request), num);
                        }
                        else {
                            out_string = reCaptchaValidator(recaptcha_token, UserHandleClass.getUserIp(request), num);
                        }
                        break;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                case "geeTestToken": //极验的令牌
                    try {
                        String  username = requestData.get("username");
                        int num;
                        if (username == null) {
                            num = -1;
                        } else {
                            num = Integer.parseInt(username);
                        }
                        if (captchaId==null||captchaKey==null){
                            out_string = get_Token(UserHandleClass.getUserIp(request), num);
                        }
                        else {
                            // 获取用户验证后前端传过来的验证流水号等参数
                            String lotNumber = requestData.get("lot_number");
                            String captchaOutput = requestData.get("captcha_output");
                            String passToken = requestData.get("pass_token");
                            String genTime = requestData.get("gen_time");
                            out_string = geeTestValidator(lotNumber, captchaOutput, passToken, genTime,
                                    UserHandleClass.getUserIp(request), num);// 执行极验处理
                        }
                        break;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                default:
//                    out_string = "verify_type error";//类型错误
            }

            // 创建响应JSON对象
            Map<String, String> responseData = new HashMap<>();

            //添加字段
            responseData.put("message", "200");
            if (out_string != null) {
                responseData.put("Token", out_string);// 服务器令牌
                responseData.put("publicKey_RSA", ProcessPassword.sending_publicKey_RSA);// 同时发送加密公钥用于后续密码的发送。
                // 创建Cookie对象
                Cookie secret_key_Cookie = new Cookie("publicKey_RSA", ProcessPassword.sending_publicKey_RSA);
                // 设置Cookie的属性有效期（以秒为单位）
                secret_key_Cookie.setMaxAge(60 * 60 * 48); // 有效期为2天
                // 将Cookie添加到响应中
                response.addCookie(secret_key_Cookie);
            }

            // 将响应JSON对象写入响应
            PrintWriter out = response.getWriter();
            // 转为json字符串发送
            out.write(objectMapper.writeValueAsString(responseData));
            out.flush();

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    //根据谷歌人机验证来处理
    private String reCaptchaValidator(String token, String userIpAddress, int user) {
        try {
            String urlParameters = String.format("?secret=%s&response=%s", URLEncoder.encode(secret, "UTF-8"), URLEncoder.encode(token, "UTF-8"));
            // 向谷歌发个请求问问令牌对不对，对就给个服务器令牌。
            // 创建URL对象并打开连接
            URL url = new URL(requestUrl + urlParameters);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.writeBytes(urlParameters);// 将URL参数写入输出流
                wr.flush();// 刷新输出流，确保所有数据都被发送
            }
            // 获取输出
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            boolean success = jsonResponse.getBoolean("success");
            if (success) {
                return get_Token(userIpAddress, user);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String geeTestValidator(String lotNumber, String captchaOutput, String passToken, String genTime,
                                    String userIpAddress, int user) {
        // 这个部分和极验官方文档大差不差，基本用的官方文档代码。有一说一，比谷歌文档看的方便。
        // 生成签名
        // 生成签名使用标准的hmac算法，使用用户当前完成验证的流水号lot_number作为原始消息message，使用客户验证私钥作为key
        // 采用sha256散列算法将message和key进行单向散列生成最终的签名
        String signToken = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, captchaKey).hmacHex(lotNumber);
        // 上传校验参数到极验二次验证接口, 校验用户验证状态
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lot_number", lotNumber);
        queryParams.add("captcha_output", captchaOutput);
        queryParams.add("pass_token", passToken);
        queryParams.add("gen_time", genTime);
        queryParams.add("sign_token", signToken);
        // captcha_id 参数建议放在 url 后面, 方便请求异常时可以在日志中根据id快速定位到异常请求
        String url = String.format(domain + "/validate" + "?captcha_id=%s", captchaId);
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        JSONObject jsonObject = new JSONObject();
        //注意处理接口异常情况，当请求极验二次验证接口异常时做出相应异常处理
        //保证不会因为接口请求超时或服务未响应而阻碍业务流程
        try {
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(queryParams, headers);
            ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
            String resBody = response.getBody();
            jsonObject = new JSONObject(resBody);
        }catch (Exception e){
            jsonObject.put("result","success");// 报错不管验证码了。
            jsonObject.put("reason","request geetest api fail");
        }

        // 根据极验返回的用户验证状态, 进行自己的业务逻辑
        if (jsonObject.getString("result").equals("success")) {// 验证成功
            return get_Token(userIpAddress, user);
        } else {// 验证失败
            return null;
        }
    }

    private boolean If_laissez_passer(int username, String secret_key, String ip) {
        // 根据发来的cookie和账号判断是否免验证，
        // 如果要修改保持登录时间还需要修改Log_In_And_Register.java中的cookie持续时间，最终效果会取最低值。
        登录表_实现 loginTableImpl = new 登录表_实现();
        登录表 loginTable = loginTableImpl.get_登录表(username);// 查找账号的登录表
        if (loginTable == null) {
            return false;
        }
        // 退出了而且保持登录密钥和ip没问题。
        return loginTable.get退出时间() != null && secret_key.equals(loginTable.get保持登录密钥())
                && ip.equals(loginTable.getIp()) && isWithinLastLongHours(loginTable.get退出时间(), 48);
    }

    private String get_Token(String ip, int num) {
        // 获取一个令牌。
        令牌表_实现 tokenTableImpl = new 令牌表_实现();
        Timestamp time = Timestamp.valueOf(LocalDateTime.now().plus(Duration.ofMinutes(2)));
        String uid;
        do {// 获得一个没有的登录令牌号
            uid = UUID.randomUUID().toString();
        } while (tokenTableImpl.get_令牌表(uid) != null);
        令牌表 tokenTable = new 令牌表(uid, ip, num, time);// 产生令牌
        tokenTableImpl.insert令牌表(tokenTable);//令牌入库
        return uid;// 返回令牌
    }


    private static boolean isWithinLastLongHours(Timestamp timestamp, int time) {
        //是否在之前time小时内
        Date date = new Date(timestamp.getTime());// 将Timestamp转换为Date
        Calendar calendar = Calendar.getInstance();// 获取当前时间的Calendar实例
        calendar.setTime(new Date());// 设置Calendar为当前时间
        calendar.add(Calendar.HOUR_OF_DAY, -time);// 减去小时
        Date seventyTwoHoursAgo = calendar.getTime();// 获取72小时前的时间
        return date.after(seventyTwoHoursAgo);// 比较时间
    }


}