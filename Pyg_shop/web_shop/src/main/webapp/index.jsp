<%--
  Created by IntelliJ IDEA.
  User: DLF
  Date: 2019/5/5
  Time: 18:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="true" language="java" %>
<html>
<head>
    <title>跳转</title>
</head>
<body>
    <% response.sendRedirect(request.getContextPath()+"/admin/goods.html");%>
</body>
</html>
