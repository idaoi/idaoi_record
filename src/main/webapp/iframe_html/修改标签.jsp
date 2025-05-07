<%--
  Created by IntelliJ IDEA.
  User: 86135
  Date: 2025/5/5
  Time: 19:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>修改标签</title>
    <link rel="stylesheet" type="text/css" href="修改标签界面样式.css">
    <script src="../js/jquery-3.6.0.min.js"></script>
    <script src="../js/jsencrypt.min.js"></script>
    <script src="../js/crypto-js.min.js"></script><!--这个用于哈希-->
    <script src="../Encryption_Decryption.js"></script><!--AES加密-->
    <script type="text/javascript">
        let All_temporary_Label;
        let t_num = 0;
        let delete_Label = [];

        function Initialization_Change_Label() {// 初始化修改标签
            // 从主页获取信息，动态生成所有标签组件
            All_temporary_Label = JSON.parse(JSON.stringify(window.parent.All_Label));// 深拷贝，防止修改产生问题。
            const down_show_div = document.getElementById("down_show_div");
            down_show_div.innerHTML = ''; // 清空所有子元素
            for (const x in All_temporary_Label) {
                const now_label = All_temporary_Label[x];
                const list_div = document.createElement('div');
                list_div.className = "list_div";
                const list_label = document.createElement('label');
                list_label.className = "list_label";
                const label_name_input = document.createElement('input');
                label_name_input.value = now_label["标签名"];
                label_name_input.maxLength = 28;
                label_name_input.className = "label_name_input";
                label_name_input.onchange = function () {// 修改同步修改All_temporary_Label
                    now_label["标签名"] = this.value;
                }
                list_label.appendChild(label_name_input);
                const list_button = document.createElement('button');
                list_button.textContent = "删除";
                list_button.className = "list_button";
                list_button.dataset.Lable_num = now_label["标签号"];
                list_button.addEventListener('click', function () {// 点击删除该条标签
                    list_div.remove();//移除控件
                    All_temporary_Label = All_temporary_Label.filter(data => data["标签号"] !== list_button.dataset.Lable_num);
                    delete_Label.push(list_button.dataset.Lable_num);
                });
                list_div.appendChild(list_label);
                list_div.appendChild(list_button);
                down_show_div.appendChild(list_div);
            }
        }

        function add_label() {// 添加一个标签
            const t_Label = {"标签名": "", "临时号": t_num};
            All_temporary_Label.push(t_Label);
            t_num += 1;
            const down_show_div = document.getElementById("down_show_div");
            const list_div = document.createElement('div');
            list_div.className = "list_div";
            const list_label = document.createElement('label');
            list_label.className = "list_label";
            const label_name_input = document.createElement('input');
            label_name_input.value = t_Label["标签名"];
            label_name_input.maxLength = 28;
            label_name_input.className = "label_name_input";
            label_name_input.onchange = function () {// 修改同步修改All_temporary_Label
                t_Label["标签名"] = this.value;
            }
            list_label.appendChild(label_name_input);
            const list_button = document.createElement('button');
            list_button.textContent = "删除";
            list_button.className = "list_button";
            list_button.dataset.Lable_num = t_Label["临时号"];
            list_button.addEventListener('click', function () {// 点击删除该条标签
                list_div.remove();//移除控件
                All_temporary_Label = All_temporary_Label.filter(data => data["临时号"] !== list_button.dataset.Lable_num);
            });
            list_div.appendChild(list_label);
            list_div.appendChild(list_button);
            down_show_div.appendChild(list_div);
        }

        function cancel_change() {// 取消，回到总览界面
            window.parent.sidebar_homepage_ButtonClick();
        }

        function get_Ajax_Change_Label_Submit() {// 提交修改
            const now = new Date();
            now.setMinutes(now.getMinutes() + 2);// 增加2min作为失效时间
            const formattedTime = now.toISOString(); // ISO 8601 格式：YYYY-MM-DDTHH:mm:ss.sssZ
            const secret_key = window.parent.parent.server_publicKeyPem.encrypt(// 如果为空，则是undefined
                window.parent.parent.AES_Key + formattedTime);// 密钥
            let send_data = {// 要发送过去的内容
                verify_type: "label_Content",
                secret_key: secret_key,// 密钥+时间
                signature: signMessage(secret_key, window.parent.parent.client_privateKeyPem),//签名
                Label_data: AES_encrypt(JSON.stringify(All_temporary_Label), window.parent.parent.AES_Key,
                    generateIV(window.parent.parent.AES_Key)),
                delete_Label: AES_encrypt(JSON.stringify(delete_Label), window.parent.parent.AES_Key,
                    generateIV(window.parent.parent.AES_Key)),
            };
            $.ajax({
                url: "/post_userdata",
                async: true,// 进行异步
                type: "post",// post 提交
                timeout: 30000,// 超时事件30s
                data: JSON.stringify(send_data),
                success: function (response) {//请求成功的回调
                    let state = response["state"];
                    switch (state) {
                        case "OK":
                            window.parent.Initialization_interface();// 重新初始化
                    }
                },
                error: function (xhr, status, error) {  // 请求失败的回调函数
                },
            });
        }
    </script>
</head>
<body>
<div class="big_div">
    <div class="dark_color_div">
        <div class="up_div">
            修改标签
            <div class="up_div_right">
                <button class="up_button" onclick="add_label()">添加</button>
                <button class="up_button" onclick="get_Ajax_Change_Label_Submit()">保存</button>
                <button class="up_button" onclick="cancel_change()">取消</button>
            </div>
        </div>
        <div class="line_div"></div>
        <div class="down_div" id="down_show_div">
            <%--            <div class="list_div">--%>
            <%--                <label class="list_label">--%>
            <%--                    <input class="label_name_input" placeholder="请输入标签名" maxlength="28">--%>
            <%--                </label>--%>
            <%--                <button class="list_button">删除</button>--%>
            <%--            </div>--%>

        </div>
    </div>

</div>
</body>
<script>
    Initialization_Change_Label();
</script>
</html>
