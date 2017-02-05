/*
 * The MIT License
 *
 * Copyright 2016 Vit Stekly <stekly.vit@vs-point.cz>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.springframework.security.common.messanger;

import cz.cvut.springframework.security.common.token.ConfirmationToken;
import cz.cvut.springframework.security.common.token.ConfirmationTokenImpl;
import cz.cvut.springframework.security.service.UserDetailsToken;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class AccountActivationMessangerImpl implements AccountActivationMessanger {

    private String email;
    private String token;

    @Override
    public boolean isUsernameEmpty() {
        if (email == null || email.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean hasToken() {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public ConfirmationToken getToken() {
        ConfirmationTokenImpl token = new ConfirmationTokenImpl();
        try {
            token.fromString(this.token);
        } catch (ParseException ex) {
            Logger.getLogger(AccountActivationMessangerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return token;
    }

    @Override
    public AccountActivationMessanger fromRequest(HttpServletRequest request) {
        AccountActivationMessangerImpl accountActivationMessangerImpl = new AccountActivationMessangerImpl();
        accountActivationMessangerImpl.token = request.getParameter("token");
        accountActivationMessangerImpl.email = request.getParameter("email");
        return accountActivationMessangerImpl;
    }

    @Override
    public Map<String, String> createActivationLinkMap(UserDetailsToken userDetails) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("token", userDetails.getToken().toString());
        parameters.put("email", userDetails.getUsername());
        return parameters;
    }

}
