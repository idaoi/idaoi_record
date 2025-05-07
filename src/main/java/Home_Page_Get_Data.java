// 用于处理主页发来的数据获取请求，
// 比如初始化的获取用户数据，根据主页的选项前往某个页面
// 心跳检测也从这走

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.entity.登录表;
import dao.impl.登录表_实现;
import tool.UserHandleClass;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebServlet(urlPatterns = {"/heartbeat_detection", "/get_userdata", "/post_userdata"})
public class Home_Page_Get_Data extends HttpServlet {

    private static final long serialVersionUID = 1L;//序列化对象版本,确保发送方和接收方的序列化对象版本兼容

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应内容类型为JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String path = request.getRequestURI();

        if (path.endsWith("/heartbeat_detection")) {
            // 处理 心跳检测
            PrintWriter out = getPrintWriter(request, response);
            out.flush();
        } else if (path.endsWith("/get_userdata")) {
            // 处理 获取数据
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> requestData = objectMapper.readValue(request.getReader(), Map.class);
                String verify_type = requestData.get("verify_type");//无返回null
                Map<String, String> r_Data = new HashMap<>();
                switch (verify_type) {
                    case "Initialization":// 初始化，获取标签，所有随笔索引内容
                        UserHandleClass initialization = new UserHandleClass(request, requestData);
                        r_Data = initialization.Initialization();
                        break;
                    case "Overview":// 总览界面，获取几个基础数据
                        UserHandleClass Overview = new UserHandleClass(request, requestData);
                        r_Data = Overview.get_Overview_data();
                        break;
                    case "Essays_Content":// 随笔内容
                        UserHandleClass Essays_Content = new UserHandleClass(request, requestData);
                        r_Data = Essays_Content.get_Essays_Content();
                        break;
                    case "File_Content":// 文件内容
                        break;
                    default:
                        r_Data.put("state", "verify_type error");//类型错误
                }

                Map<String, String> responseData;
                // 创建响应JSON对象
                if (Objects.equals(r_Data.get("state"), "OK")) {
                    responseData = r_Data;
                } else {
                    responseData = new HashMap<>();
                    responseData.put("state", r_Data.get("state"));
                    // 防止泄露信息
                }
                //添加字段
                responseData.put("message", "200");

                // 将响应JSON对象写入响应
                PrintWriter out = response.getWriter();
                // 转为json字符串发送
                String jsonResponse = objectMapper.writeValueAsString(responseData);
                // 设置 Content-Type 和 Content-Length
                response.setContentType("application/json");
                response.setContentLength(jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                out.write(jsonResponse);
                out.flush();
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
            }
        } else if (path.endsWith("/post_userdata")) {// 提交内容
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> requestData = objectMapper.readValue(request.getReader(), Map.class);
                String verify_type = requestData.get("verify_type");//无返回null
                Map<String, String> r_Data = new HashMap<>();
                switch (verify_type) {
                    case "post_Essays_Content":// 随笔内容
                        UserHandleClass post_Essays_Content = new UserHandleClass(request, requestData);
                        r_Data = post_Essays_Content.post_Essays_Content();
                        break;
                    case "delete_Essays_Content":// 随笔内容
                        UserHandleClass delete_Essays_Content = new UserHandleClass(request, requestData);
                        r_Data = delete_Essays_Content.delete_Essays_Content();
                        break;
                    case "File_Content":// 文件内容
                        break;
                    case "label_Content":// 标签内容
                        UserHandleClass Change_Label = new UserHandleClass(request, requestData);
                        r_Data = Change_Label.Change_Label();
                        break;
                    default:
                        r_Data.put("state", "verify_type error");//类型错误
                }
                Map<String, String> responseData;
                // 创建响应JSON对象
                if (Objects.equals(r_Data.get("state"), "OK")) {
                    responseData = r_Data;
                } else {
                    responseData = new HashMap<>();
                    responseData.put("state", r_Data.get("state"));
                    // 防止泄露信息
                }
                //添加字段
                responseData.put("message", "200");

                // 将响应JSON对象写入响应
                PrintWriter out = response.getWriter();
                // 转为json字符串发送
                String jsonResponse = objectMapper.writeValueAsString(responseData);
                // 设置 Content-Type 和 Content-Length
                response.setContentType("application/json");
                response.setContentLength(jsonResponse.getBytes(StandardCharsets.UTF_8).length);
                out.write(jsonResponse);
                out.flush();
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
            }
        }
    }

    /**
     * 心跳检测方法
     */
    private static PrintWriter getPrintWriter(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        return out;
    }

}
