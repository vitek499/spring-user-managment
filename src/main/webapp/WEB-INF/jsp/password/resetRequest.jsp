<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Forgotten Password</title>
    </head>
    <body>
        <h1>Forgotten Password</h1>
        <form method="POST">
            <label for="email">Your Email:</label>
            <input type="text" name="email" id="email" />
            
            <input type="submit" value="Send email validation" />
        </form>
    </body>
</html>