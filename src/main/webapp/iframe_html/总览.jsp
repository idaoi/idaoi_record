<%--
  Created by IntelliJ IDEA.
  User: 86135
  Date: 2025/3/26
  Time: 16:37
  To change this template use File | Settings | File Templates.
  总览界面，开局iframe中第一个显示的页面
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>总览界面</title>
    <link rel="stylesheet" type="text/css" href="总览界面样式.css">
    <script src="../js/jquery-3.6.0.min.js"></script>
    <script src="../js/jsencrypt.min.js"></script>
    <script src="../js/crypto-js.min.js"></script><!--这个用于哈希-->
    <script src="../Encryption_Decryption.js"></script><!--AES加密-->
    <script type="text/javascript">
        function get_Ajax_Overview_data() {
            const now = new Date();
            now.setMinutes(now.getMinutes() + 2);// 增加2min作为失效时间
            const formattedTime = now.toISOString(); // ISO 8601 格式：YYYY-MM-DDTHH:mm:ss.sssZ
            const secret_key = window.parent.parent.server_publicKeyPem.encrypt(window.parent.parent.AES_Key + formattedTime);// 密钥
            let date = {
                verify_type: "Overview",
                secret_key: secret_key,// 密钥
                signature: signMessage(secret_key, window.parent.parent.client_privateKeyPem),//签名
            }
            $.ajax({
                url: "/get_userdata",
                async: true,// 进行异步
                type: "post",// post 提交
                timeout: 10000,// 超时事件10s
                data: JSON.stringify(date),
                success: function (response) {//请求成功的回调
                    let state = response["state"];
                    if (state === "OK") {
                        // 右下角部分
                        let word_number = parseInt(response['字数']);
                        if (word_number >= 100000000) {// 字数单位的转换
                            word_number = (word_number / 100000000).toFixed(2) + '亿';
                        } else if (word_number >= 10000) {
                            word_number = (word_number / 10000).toFixed(2) + '万';
                        } else if (word_number >= 1000) {
                            word_number = (word_number / 1000).toFixed(2) + '千';
                        } else {
                            word_number = word_number.toString();
                        }
                        document.getElementById("data_word_number").textContent = word_number;
                        document.getElementById("data_articles_number").textContent = response['篇数'];
                        document.getElementById("data_log_on_number").textContent = response['次数'];
                        // 发来的是分钟数，转为小时数目
                        document.getElementById("data_days_number").textContent = (parseInt(response['时间']) / 60).toFixed(2);

                        // 左下角部分
                        let star_num = 0;
                        for (const index in window.parent.normal_Essays_Index) {
                            if (window.parent.startsWithStr(window.parent.normal_Essays_Index[index]["随笔名"], "⭐")) {
                                // 是星标
                                const span_star = document.createElement('span');
                                span_star.className = "star_shape_span";
                                span_star.textContent = "⭐";
                                const span_star_data = document.createElement('span');
                                span_star_data.className = "star_data_span";
                                span_star_data.textContent = window.parent.normal_Essays_Index[index]["随笔名"];
                                const now_num = star_num;
                                const span_star_div = document.getElementById(`star_button_${now_num}`);
                                span_star_div.appendChild(span_star);
                                span_star_div.appendChild(span_star_data);
                                span_star_div.addEventListener('click', function () {// 点击跳转到随笔
                                    window.parent.now_Essays_Index = window.parent.normal_Essays_Index[index];
                                    window.parent.changeIframeSource("iframe_html/随笔.jsp")// 跳转到随笔
                                });
                                star_num += 1;
                                if (star_num >= 3) {
                                    break;
                                }
                            }
                        }
                        // 左下角的最后一个更多标签
                        const more_span_star = document.createElement('span');
                        more_span_star.className = "star_shape_span";
                        more_span_star.textContent = "⭐";
                        const more_span_star_data = document.createElement('span');
                        more_span_star_data.className = "star_data_span";
                        const more_span_star_data_text = document.createElement('span');
                        more_span_star_data_text.className = "italic";
                        more_span_star_data_text.textContent = "更多->";
                        more_span_star_data.appendChild(more_span_star_data_text);
                        const more_now_num = star_num;
                        const more_span_star_div = document.getElementById(`star_button_${more_now_num}`);
                        more_span_star_div.appendChild(more_span_star);
                        more_span_star_div.appendChild(more_span_star_data);
                        more_span_star_div.addEventListener('click', function () {// 点击跳转到搜索
                            window.parent.now_Search_Data = {"时间": "编辑时间", "星标": true, "标签": "all"};
                            window.parent.changeIframeSource("iframe_html/搜索.jsp")// 跳转到搜索
                        });

                        // 正上方部分
                        // 根据“时间”字段降序排序
                        const sortedItems = window.parent.normal_Essays_Index.sort((a, b) => b.最后编辑时间 - a.最后编辑时间)
                        // 获取前三个项目
                        const topFourItems = sortedItems.slice(0, 3);
                        let time_num = 0;
                        for (const index in topFourItems) {
                            const div_time = document.createElement('div');
                            div_time.className = "dark_point";
                            const span_time_data = document.createElement('span');
                            span_time_data.className = "time_data_span";
                            // 显示什么时候前+上次编辑时间+随笔名
                            span_time_data.textContent = timeAgo(topFourItems[index]["最后编辑时间"]) + "  " +
                                new Date(topFourItems[index]["最后编辑时间"]).toLocaleString() + "  " +
                                topFourItems[index]["随笔名"];

                            const now_num = time_num;
                            const span_time_div = document.getElementById(`time_button_${now_num}`);
                            span_time_div.appendChild(div_time);
                            span_time_div.appendChild(span_time_data);
                            span_time_div.addEventListener('click', function () {// 点击跳转到随笔
                                window.parent.now_Essays_Index = topFourItems[index];
                                window.parent.changeIframeSource("iframe_html/随笔.jsp")// 跳转到随笔
                            });
                            time_num += 1;
                        }
                        // 正上方的最后一个更多标签
                        const more_div_time = document.createElement('div');
                        more_div_time.className = "dark_point";
                        const more_span_time_data = document.createElement('span');
                        more_span_time_data.className = "time_data_span";

                        const more_span_time_data_text = document.createElement('span');
                        more_span_time_data_text.className = "italic";
                        more_span_time_data_text.textContent = "更多->";
                        more_span_time_data.appendChild(more_span_time_data_text);
                        const more_now_num_time = time_num;
                        const more_span_time_div = document.getElementById(`time_button_${more_now_num_time}`);
                        more_span_time_div.appendChild(more_div_time);
                        more_span_time_div.appendChild(more_span_time_data);
                        more_span_time_div.addEventListener('click', function () {// 点击跳转到搜索
                            window.parent.now_Search_Data = {"时间": "编辑时间", "星标": false, "标签": "all"};
                            window.parent.changeIframeSource("iframe_html/搜索.jsp")// 跳转到搜索
                        });
                    }
                },
                error: function (xhr, status, error) {  // 请求失败的回调函数
                },
            });
        }

        function timeAgo(date) {
            const now = new Date();
            const diffInMilliseconds = now - new Date(date);

            // 转换为秒
            const diffInSeconds = Math.floor(diffInMilliseconds / 1000);

            // 转换为分钟
            const diffInMinutes = Math.floor(diffInSeconds / 60);

            // 转换为小时
            const diffInHours = Math.floor(diffInMinutes / 60);

            // 转换为天
            const diffInDays = Math.floor(diffInHours / 24);

            if (diffInMinutes < 60) {
                return `${diffInMinutes}分钟前`;
            } else if (diffInHours < 24) {
                return `${diffInHours}小时前`;
            } else {
                return `${diffInDays}天前`;
            }
        }
    </script>
</head>

<body>
<div class="big_div">
    <div class="dark_color_div" style="width:90%;height:40%;margin-bottom:2.5%;">
        <div class="dark_line"></div><!--一条时间线-->
        <!-- 时间总览 -->
        <div id="time_button_0" class="show_button">
            <!--
            <div class="dark_point"></div>
            <span class="time_data_span"><span class="italic">更多-></span></span>
             -->
        </div>
        <div id="time_button_1" class="show_button">

        </div>
        <div id="time_button_2" class="show_button">

        </div>
        <div id="time_button_3" class="show_button">
        </div>
    </div>
    <div class="down_div">
        <div class="dark_color_div" style="height:80%;width:55%;margin-top:2.5%;margin-right: 2.5%;">
            <!-- 收藏总览 -->

            <div id="star_button_0" class="show_button">
                <!--
                    <span class="star_shape_span">⭐</span>
                    <span class="star_data_span"><span class="italic">更多-></span></span>
                -->
            </div>
            <div id="star_button_1" class="show_button">

            </div>
            <div id="star_button_2" class="show_button">

            </div>
            <div id="star_button_3" class="show_button">

            </div>
        </div>
        <div style="height:80%;width:35%;margin: 2.5% 5% 5% 2.5%;">
            <!-- 数据总览 -->
            <div class="show_div">
                <span id="data_word_number" class="div_data_span">0</span>
                <span class="div_label_span">字</span>
            </div>
            <div class="show_div">
                <span id="data_articles_number" class="div_data_span">0</span>
                <span class="div_label_span">篇</span>
            </div>
            <div class="show_div">
                <span id="data_log_on_number" class="div_data_span">0</span>
                <span class="div_label_span">次</span>
            </div>
            <div class="show_div">
                <span id="data_days_number" class="div_data_span">0</span>
                <span class="div_label_span">时</span>
            </div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    get_Ajax_Overview_data();
</script>
</html>
