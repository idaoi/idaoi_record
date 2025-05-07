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
    <title>搜索</title>
    <link rel="stylesheet" type="text/css" href="搜索界面样式.css">
    <script src="../js/jquery-3.6.0.min.js"></script>
    <script src="../js/jsencrypt.min.js"></script>
    <script type="text/javascript">
        let now_Search_Data;
        let now_show_Essays_Index;

        function Initialization_search() {// 初始化搜索
            // 先获得所有分组，另添加全部、回收和未分组
            const selectElement = document.getElementById('All_Label_select');// 设置随笔分组
            const optionElement_all = document.createElement('option');
            optionElement_all.value = "all";
            optionElement_all.textContent = "全部";
            selectElement.appendChild(optionElement_all);
            const optionElement_1 = document.createElement('option');
            optionElement_1.value = "-1";
            optionElement_1.textContent = "回收";
            selectElement.appendChild(optionElement_1);
            const optionElement0 = document.createElement('option');
            optionElement0.value = "0";
            optionElement0.textContent = "未分组";
            selectElement.appendChild(optionElement0);
            for (const label in window.parent.All_Label) {// 动态生成所有分组选项
                const optionElement = document.createElement('option');
                optionElement.value = window.parent.All_Label[label]["标签号"];
                optionElement.textContent = window.parent.All_Label[label]["标签名"];
                selectElement.appendChild(optionElement);
            }
            // 根据父窗口的参数，来进行默认选择
            // now_Search_Data = {"时间":"编辑时间","星标":false,"标签":"all"};
            now_Search_Data = JSON.parse(JSON.stringify(window.parent.now_Search_Data));// 深拷贝，防止修改产生问题。
            if (now_Search_Data["时间"] === "编辑时间") {
                document.getElementById('look_time').value = "编辑时间";
            } else {
                document.getElementById('look_time').value = "创建时间";
            }
            document.getElementById('asterisk').checked = !!now_Search_Data["星标"];// 选中为true
            document.getElementById('All_Label_select').value = now_Search_Data["标签"];
            document.getElementById('time_num_input').value = new Date().toISOString().split('T')[0];

            // 选择完过滤，显示内容
            change_essays_label();
        }

        function change_essays_label() {// 按类别过滤更改显示的界面
            // 根据过滤来生成now_show_Essays_Index，然后排序并生成组件

            now_Search_Data["标签"] = document.getElementById('All_Label_select').value;
            now_Search_Data["时间"] = document.getElementById('look_time').value;
            now_Search_Data["具体时间"] = document.getElementById('time_num_input').value;
            now_Search_Data["时间过滤"] = document.getElementById('before_after').value;
            now_Search_Data["加密"] = document.getElementById('encryption').value;
            now_Search_Data["星标"] = document.getElementById('asterisk').checked;
            now_Search_Data["搜索"] = document.getElementById("search_input").value;

            if (now_Search_Data["标签"] === "all") {// 显示全部，从没有回收的正常随笔列表找
                search_compliance_data(window.parent.normal_Essays_Index);
            } else if (now_Search_Data["标签"] === "-1") {// 从回收找数据
                search_compliance_data(window.parent.recycle_Essays_Index);
            } else {// 从相应的Comprehensive_List找数据
                search_compliance_data(window.parent.Comprehensive_List[now_Search_Data["标签"]]);
            }
            // 已经获取完成数据，动态生成列表组件
            create_select_div();
        }

        function search_compliance_data(data_Index) {// 查找合规数据
            now_show_Essays_Index = [];
            // 星标，加密，时间
            let select_time = new Date(document.getElementById("time_num_input").value);// 选择的时间
            // 特殊处理，之前之后包含当天。
            if (now_Search_Data["时间过滤"] === "之前") {
                select_time.setDate(select_time.getDate() + 1)
            }
            select_time = select_time.getTime()
            let select_time_str;
            if (now_Search_Data["时间"] === "创建时间") {
                select_time_str = "第一次编辑时间"
            } else {
                select_time_str = "最后编辑时间"
            }

            for (const x in data_Index) {// 遍历所有数据，拿出符合要求的数据
                // 不符合要求的跳过
                if (now_Search_Data["星标"]) {// 选中星标，只看星标
                    if (!window.parent.startsWithStr(data_Index[x]["随笔名"], "⭐")) {// 没有星标
                        continue;
                    }
                }
                switch (now_Search_Data["加密"]) {
                    case "无视加密"://什么都不做
                        break;
                    case "只看加密":
                        if (data_Index[x]["加密"] === null || data_Index[x]["加密"] === "") {
                            continue;
                        }
                        break;
                    case "不看加密":
                        if (!(data_Index[x]["加密"] === null || data_Index[x]["加密"] === "")) {
                            continue;
                        }
                        break;
                }
                if (now_Search_Data["时间过滤"] === "之前") {
                    if (!(data_Index[x][select_time_str] < select_time)) {// 时间比设定小，在之前
                        continue;
                    }
                } else {
                    if (!(data_Index[x][select_time_str] > select_time)) {// 时间比设定大，在之后
                        continue;
                    }
                }
                if (now_Search_Data["搜索"] !== null && now_Search_Data["搜索"] !== "") {
                    if (!data_Index[x]["随笔名"].includes(now_Search_Data["搜索"])) {// 没有搜索的字符串
                        continue;
                    }
                }
                //顶得住过滤，添加进now_show_Essays_Index
                now_show_Essays_Index.push(data_Index[x]);
            }
            // 循环完毕，按时间进行排序
            if (now_Search_Data["时间过滤"] === "之前") {
                now_show_Essays_Index = now_show_Essays_Index.sort((a, b) => b[select_time_str] - a[select_time_str])
            } else {
                now_show_Essays_Index = now_show_Essays_Index.sort((a, b) => a[select_time_str] - b[select_time_str])
            }
        }

        function create_select_div() {// 动态生成所有div
            const down_show_div = document.getElementById("down_show_div");
            down_show_div.innerHTML = ''; // 清空所有子元素
            for (const index in now_show_Essays_Index) {
                const list_div = document.createElement('div');
                list_div.className = "list_div";
                const dark_point = document.createElement('div');
                dark_point.className = "dark_point";
                const list_span = document.createElement('div');
                list_span.className = "list_span";
                if (now_Search_Data["时间"] === "创建时间") {
                    list_span.textContent = new Date(now_show_Essays_Index[index]["第一次编辑时间"]).toLocaleString() +
                        "  " + now_show_Essays_Index[index]["随笔名"];
                } else {
                    list_span.textContent = new Date(now_show_Essays_Index[index]["最后编辑时间"]).toLocaleString() +
                        "  " + now_show_Essays_Index[index]["随笔名"];
                }
                list_div.addEventListener('click', function () {// 点击跳转到随笔
                    window.parent.now_Essays_Index = now_show_Essays_Index[index];
                    window.parent.changeIframeSource("iframe_html/随笔.jsp")// 跳转到随笔
                });
                list_div.appendChild(dark_point);
                list_div.appendChild(list_span);
                down_show_div.appendChild(list_div);
            }
        }
    </script>
</head>
<body>
<div class="big_div">
    <div class="dark_color_div">
        <div class="up_div"><!--上面的标签选择，搜索和更多按钮-->
            <label>
                <select class="up_select" id="All_Label_select" onchange="change_essays_label()">
                </select>
            </label>
            <label for="search_input" style="width: 50%;"><!--搜索框-->
                <input type="search" id="search_input" name="query" placeholder="搜索" class="search_input"/>
            </label>
            <button class="up_button"></button><!--更多功能，我没想好放啥，先在这占个位-->
        </div>
        <div class="line_div"></div>
        <div class="middle_div"><!--更多按钮的展开-->
            <label style="margin-left:5%;">
                <select class="dark_component" id="look_time" style="margin-left:0;">
                    <option name="编辑时间" value="编辑时间">📅编辑时间</option>
                    <option name="创建时间" value="创建时间">📅创建时间</option>
                </select>
            </label>
            <label style="margin-left:2%;"><!--时间选择-->
                <input class="dark_component" type="date" id="time_num_input" style="color-scheme:dark;">
            </label>
            <label style="margin-left:2%;">
                <select class="dark_component" id="before_after" style="margin-left:0;">
                    <option name="之前" value="之前">之前</option>
                    <option name="之后" value="之后">之后</option>
                </select>
            </label>
            <label style="margin-left:2%;"><!--加密-->
                <select class="dark_component" id="encryption" style="margin-left:0;">
                    <option name="无视加密" value="无视加密">无视加密🔑</option>
                    <option name="只看加密" value="只看加密">只看加密🔒</option>
                    <option name="不看加密" value="不看加密">不看加密🔓</option>
                </select>
            </label>
            <label class="dark_component"><!--只看星标复选框-->
                <input type="checkbox" id="asterisk" value="星标" style="color-scheme:dark;">只看星标⭐
            </label>
            <button class="dark_component" style="margin-left:2%;color: rgb(191, 191, 191);"
                    onclick="change_essays_label()">开始过滤
            </button>
        </div>
        <div class="line_div"></div>
        <div class="down_line"></div>
        <div class="down_div" id="down_show_div">

            <%--            <div class="list_div">--%>

            <%--                <div class="dark_point"></div>--%>
            <%--                <span class="list_span">更多-></span>--%>

            <%--            </div>--%>

            <%--            <div class="list_div">--%>

            <%--                <div class="dark_point"></div>--%>
            <%--                <span class="list_span">更多-></span>--%>

            <%--            </div>--%>

        </div>
    </div>

</div>
</body>
<script type="text/javascript">
    document.getElementById('search_input').addEventListener('keydown', function (event) {
        if (event.key === "Enter" || event.code === "Enter") {
            // 执行搜索
            change_essays_label();
        }
    });
    Initialization_search();
</script>
</html>