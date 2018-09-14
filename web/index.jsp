<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/9/11
  Time: 12:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>secured</title>
</head>
<body>
<div class="container">
    <h1>This is secured!</h1><!-- 其实这里的首页无需登录验证即可访问，且登录后默认跳转到根路径-->
    <p>
        Hello <b><c:out value="${pageContext.request.remoteUser}"/></b>
    </p>
    <c:url var="logoutUrl" value="/logout"/>
    <form class="form-inline" action="${logoutUrl}" method="post"><!-- 简单的jstl表达式，上面c:url标签将"/logout"赋值给jsp页面变量logoutUrl，下面的action使用EL表达式取这个值，基础生疏反应缓慢，需要全面复习补充！！-->
        <input type="submit" value="Log out" />
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
</div>
</body>
</html>
