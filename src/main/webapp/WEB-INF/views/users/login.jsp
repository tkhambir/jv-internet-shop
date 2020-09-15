<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h1>Hello! Please provide your user details</h1>

<h4 style="color: red">${errorMsg}</h4>

<form method="post" action="${pageContext.request.contextPath}/login">
    Please provide your login: <input type="text" name="login">
    Please provide your password: <input type="password" name="pwd">

    <button type="submit">Log in</button>
</form>
</body>
</html>
