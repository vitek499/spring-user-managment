<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        <h1>Login</h1>
        <form method="post">   
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <c:if test="${param.error!= null}">
                <div class="alert alert-error">    
                    Invalid email and password.
                </div>
            </c:if>
            <c:if test="${param.logout!= null}">
                <div class="alert alert-success"> 
                    You have been logged out.
                </div>
            </c:if>
            <label for="username">Email</label>
            <input type="text" id="username" name="username"/>        
            <label for="password">Password</label>
            <input type="password" id="password" name="password"/>    
            <div class="form-actions">
                <button type="submit" class="btn">Log in</button>
            </div>
        </form>
        <a href="/account/registration">Registration</a>
        <a href="/account/reset">Forgotten Password</a>
    </body>
</html>