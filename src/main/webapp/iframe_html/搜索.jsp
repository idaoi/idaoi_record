<%--
  Created by IntelliJ IDEA.
  User: 86135
  Date: 2025/4/10
  Time: 17:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>日期选择</title>
    <link rel="stylesheet" type="text/css" href="搜索界面样式.css">
</head>
<body>
<div class="big_div">
    <div class="dark_color_div">
        <div class="up_div"><!--上面的标签选择，搜索和更多按钮-->
            <label>
                <select class="up_select">
                    <option name="未分组">未分组</option>
                </select>
            </label>
            <label for="search_input" style="width: 50%;"><!--搜索框-->
                <input type="search" id="search_input" name="query" placeholder="搜索" class="search_input"/>
            </label>
            <button class="up_button">更多</button>
        </div>
        <div class="line_div"></div>
        <div class="middle_div"><!--更多按钮的展开-->
            <label  style="margin-left:5%;">
                <select class="dark_component">
                    <option name="编辑时间">📅编辑时间</option>
                    <option name="创建时间">📅创建时间</option>
                </select>
            </label>
            <label  style="margin-left:2%;">
                <input class="dark_component" type="date" style="color-scheme:dark;">
            </label>

            <label class="dark_component" style="margin-left:2%;">
                <input type="checkbox" id="asterisk" value="星标" style="color-scheme:dark;">星标⭐
            </label>
            <label class="dark_component" style="margin-left:2%;">
                <input type="checkbox" id="encryption" value="加密" style="color-scheme:dark;">加密🔒
            </label>
            <button class="dark_component" style="margin-left:2%;color: rgb(191, 191, 191);">开始过滤</button>
        </div>
        <div class="line_div"></div>
        <div class="down_div">
            <div class="down_line"></div>

            <div  class="list_div">

                <div class="dark_point"></div>
                <span class="list_span">更多-></span>

            </div>

            <div  class="list_div">

                <div class="dark_point"></div>
                <span class="list_span">更多-></span>

            </div>

        </div>
    </div>

</div>

</body>
</html>