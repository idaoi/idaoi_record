// 用于处理主页发来的数据获取请求，
// 比如初始化的获取用户数据，根据主页的选项前往某个页面
// 心跳检测也从这走

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.entity.登录表;
import dao.impl.登录表_实现;
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

@WebServlet(urlPatterns = {"/heartbeat_detection", "/get_userdata"})
public class Home_Page_Get_Data extends HttpServlet {

    private static final long serialVersionUID = 1L;//序列化对象版本,确保发送方和接收方的序列化对象版本兼容

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应内容类型为JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String path = request.getRequestURI();

        if (path.endsWith("/heartbeat_detection")) {
            // 处理 心跳检测
            String heartbeat_str;
            try {
                UserHandleClass logOn = new UserHandleClass(request);
                heartbeat_str = logOn.heartbeat_detection();
            } catch (Exception e) {
                heartbeat_str = "错误，请重新登陆";
            }
            // 创建响应JSON对象
            Map<String, String> responseData = new HashMap<>();

            //添加字段
            responseData.put("message", "200");
            responseData.put("heartbeat_str", heartbeat_str);// 状态字符串
            // 将响应JSON对象写入响应
            PrintWriter out = response.getWriter();
            // 转为json字符串发送
            ObjectMapper objectMapper = new ObjectMapper();
            out.write(objectMapper.writeValueAsString(responseData));
            out.flush();
        } else if (path.endsWith("/get_userdata")) {
            // 处理 获取数据
            Cookie[] cookies = request.getCookies();
            int username = -1;
            String ip = UserHandleClass.getUserIp(request);
            String secretKey = null;
            String heartbeat_str = null;
            try {
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("username".equals(cookie.getName())) {
                            username = Integer.parseInt(cookie.getValue());
                        }
                        if ("secret_key".equals(cookie.getName())) {
                            secretKey = cookie.getValue();
                            break;
                        }
                    }
                }
                Map<String, String> dictionary = get_userdata(username, ip, secretKey);
            } catch (Exception e) {
                Map<String, String> dictionary = new HashMap<>();
                dictionary.put("heartbeat_str","错误，请重新登陆");
            }
            // 创建响应JSON对象
            Map<String, String> responseData = new HashMap<>();
            //添加字段
            responseData.put("message", "200");
//            responseData.put("heartbeat_str", heartbeat_str);// 状态字符串
            // 将响应JSON对象写入响应
            PrintWriter out = response.getWriter();
            // 转为json字符串发送
            ObjectMapper objectMapper = new ObjectMapper();
            out.write(objectMapper.writeValueAsString(responseData));
            out.flush();
        }
    }


    private String heartbeat_detection(int username, String ip, String secretKey) {
        // 心跳检测，判断cookie中的账号未有退出时间
        // ip相同，保持登录密钥相同，确认登录，将保持链接数据置为1，
        // 四种：已经退出，ip变化，密钥错误，登陆中，返回
        登录表_实现 loginTableImpl = new 登录表_实现();
        登录表 loginTable = loginTableImpl.get_登录表(username);// 查找账号的登录表
        if (loginTable == null) {
            return "错误，请重新登陆";
        }
        if (loginTable.get退出时间() != null) {
            return "登录失效，请重新登陆";
        }
        if (ip.equals(loginTable.getIp())) {
            return "IP错误，请重新登陆";
        }
        if (secretKey.equals(loginTable.get保持登录密钥())) {
            return "密钥错误，请重新登陆";
        }
        if (loginTable.get保持链接() == 0) {// 确认登录了，将保持登录链接置为1
            loginTableImpl.update登录表(username, 1);
        }
        return "OK";
    }

    private Map<String, String> get_userdata(int username, String ip, String secretKey) {
        // 根据输入字符获取想要的数据，
        // 所需数据有这几种，初始化获取信息，获取所要转到的页面url
        // 创建一个HashMap
        Map<String, String> dictionary = new HashMap<>();
        String heartbeat_str = heartbeat_detection(username, ip, secretKey);
        dictionary.put("heartbeat_str", heartbeat_str);
        if ("OK".equals(heartbeat_str)){

        }
        return dictionary;
    }

}
