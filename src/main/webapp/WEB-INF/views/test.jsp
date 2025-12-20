<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Test Page</title>
</head>
<body>
    <h1>Test Page</h1>
    <p>${message}</p>
    <p>Session ID: ${sessionId}</p>
    <p>User ID: ${userId}</p>
    <p>If you can see this, JSP rendering is working!</p>
    <a href="/login">Go to Login</a>
</body>
</html>