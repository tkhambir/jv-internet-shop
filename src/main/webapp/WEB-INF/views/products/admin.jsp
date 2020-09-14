<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>All products</title>
</head>
<body>
<h1>All products</h1>

<table border="1">
    <tr>
        <th>Name</th>
        <th>Price</th>
    </tr>
    <c:forEach var="product" items="${products}">
        <tr>
            <td>
                <c:out value="${product.name}"/>
            </td>
            <td>
                <c:out value="${product.price}"/>
            </td>
            <td>
                <h3><a href="${pageContext.request.contextPath}/products/delete?productId=${product.id}">Delete</a> </h3>
            </td>
        </tr>
    </c:forEach>
</table>
<div>
    <a href="${pageContext.request.contextPath}/products/add">Add product</a>
</div>
</body>
</html>
