<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>All user orders</title>
</head>
<body>
<h1>Your orders</h1>

<table border="1">
    <tr>
        <th>Orders</th>
    </tr>
    <c:forEach var="order" items="${orders}">
        <tr>
            <td>
                <c:out value="Order ${order.id}"/>
            </td>
            <td>
                <h3><a href="${pageContext.request.contextPath}/orders/details?orderId=${order.id}">Show details</a> </h3>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
