<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registration</title>
    </head>
    <body>
        <h1>Registration Page</h1>
        <form method="POST">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            
            <label for="email">Email:</label>
            <input type="text" name="email" id="email" />
            
            <label for="password">Password:</label>
            <input type="password" name="password" id="password" />
            
            <input type="submit" value="Register" />
        </form>
    </body>
</html>