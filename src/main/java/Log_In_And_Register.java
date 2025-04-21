// 这个类用于处理登录界面发来的验证请求,通过发来的post请求回复新账号或者登录密钥。
// 后续修改密码，退出，也从这里走
// 使用@WebServlet注解来映射Servlet的URL

import com.fasterxml.jackson.databind.ObjectMapper;
import tool.UserHandleClass;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


@WebServlet("/Log_In_And_Register")
public class Log_In_And_Register extends HttpServlet {
    private static final long serialVersionUID = 1L;//序列化对象版本,确保发送方和接收方的序列化对象版本兼容

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应内容类型为JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {//获取可以向客户端发送字符文本的PrintWriter
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> requestData = objectMapper.readValue(request.getReader(), Map.class);
            String verify_type = requestData.get("verify_type");//无返回null
            Map<String, String> r_Data = new HashMap<>();
            switch (verify_type) {
                case "logOnSubmit":// 登录
                    // 逻辑：查看令牌无误，删除令牌，
                    // 查看账号密码正确，设置登录表，整个新保持登录密钥，若发现有人在登录，顶号。
                    UserHandleClass logOn = new UserHandleClass(request, requestData);
                    r_Data = logOn.log_On_Account();
                    break;
                case "registerSubmit":// 注册
                    UserHandleClass register = new UserHandleClass(request, requestData);
                    r_Data = register.register_New_User();
                    break;
                case "Log_Out_Submit"://退出
                    UserHandleClass Log_Out = new UserHandleClass(request, requestData);
                    r_Data.put("state", Log_Out.log_Out_Account());
                    break;
                case "Change_Password":
                    UserHandleClass change_p = new UserHandleClass(request, requestData);
                    r_Data.put("state", change_p.change_Password());
                    break;
                default:
                    r_Data.put("state", "verify_type error");//类型错误
            }

            // 创建响应JSON对象
            Map<String, String> responseData = new HashMap<>();

            //添加字段
            responseData.put("message", "200");
            if (r_Data.get("state") != null) {
                responseData.put("state", r_Data.get("state"));// 状态字符串
            }
            if (r_Data.get("state").equals("OK")) {// 正常执行
                // 添加字段和cookie
                // 用户名字段
                if (r_Data.get("username") != null) {
                    responseData.put("username", r_Data.get("username"));// 用户名
                    // 创建Cookie对象
                    Cookie secret_key_Cookie = new Cookie("username", r_Data.get("username"));
                    // 设置Cookie的属性有效期（以秒为单位）
                    secret_key_Cookie.setMaxAge(60 * 60 * 48); // 有效期为2天
                    // 将Cookie添加到响应中
                    response.addCookie(secret_key_Cookie);
                }
                // 登陆密钥字段
                if (r_Data.get("secret_key") != null) {// 本次登录的密钥
                    responseData.put("secret_key",r_Data.get("secret_key"));
                    // 创建Cookie对象
                    Cookie secret_key_Cookie = new Cookie("secret_key", r_Data.get("secret_key"));
                    // 设置Cookie的属性有效期（以秒为单位）
                    secret_key_Cookie.setMaxAge(60 * 60 * 48); // 有效期为2天
                    // 设置 HttpOnly 属性，防止客户端脚本访问
                    secret_key_Cookie.setHttpOnly(true);
                    // 将Cookie添加到响应中
                    response.addCookie(secret_key_Cookie);
                }
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
}
