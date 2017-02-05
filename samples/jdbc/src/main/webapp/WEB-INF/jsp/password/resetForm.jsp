<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Change Password</title>
    </head>
    <body>
        <h1>Change Password</h1>
        <form method="POST">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            
            <input type="hidden" name="token" value="${PasswordMessanger.token}" />
            <input type="hidden" name="email" value="${PasswordMessanger.username}" />
                        
            <label for="password">New Password:</label>
            <input type="password" name="password" id="password" />
            
            <input type="submit" value="Change" />
        </form>
    </body>
</html>