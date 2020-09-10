<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add product</title>
</head>
<body>
<h1>Add product</h1>

<form method="post" action="${pageContext.request.contextPath}/products/add">
    Product name: <input type="text" name="name">
    Price: <input type="text" name="price">

    <button type="submit">Add</button>
</form>
</body>
</html>
