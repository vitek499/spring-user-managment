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
            <label for="passwordold">Old Password:</label>
            <input type="password" name="passwordold" id="passwordold" />
            
            <label for="password">New Password:</label>
            <input type="password" name="password" id="password" />
            
            <input type="submit" value="Change" />
        </form>
    </body>
</html>