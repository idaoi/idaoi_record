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
        <div style="height:80%;width:35%;margin:5%;margin-top:2.5%;margin-left: 2.5%;">
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
</html>
