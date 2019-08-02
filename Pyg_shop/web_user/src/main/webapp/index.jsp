<%--
  Created by IntelliJ IDEA.
  User: DLF
  Date: 2019/6/29
  Time: 12:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>跳转页</title>
</head>
<body>
    <%
        response.sendRedirect(request.getContextPath()+"/home-index.html");
    %>
</body>
</html>
