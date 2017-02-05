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
package cz.cvut.springframework.security.service.password;

import cz.cvut.springframework.security.common.token.ConfirmationTokenImpl;
import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.format.datetime.joda.LocalDateTimeParser;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 * @link http://www.vs-point.cz
 */
public abstract class UserDetailsPasswordResetServiceAbs implements UserDetailsManager, UserDetailsPasswordResetService<UserDetailsPasswordExpanded> {

    @Override
    public boolean hasValidPasswordResetData(UserDetailsPasswordExpanded userDetails) {
        //check account state
        if (!userDetails.isEnabled()) {
            return false;
        }

        //check if has token
        if (!userDetails.hasToken() || userDetails.getToken().isEmpty()) {
            return false;
        }

        //time token validation
        if (userDetails.getPasswordTokenValidity().isAfter(LocalDateTime.now().minusDays(1))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void generatePasswordResetData(UserDetailsPasswordExpanded userDetails) throws AccountDisableException {
        if (!userDetails.isEnabled()) {
            throw new AccountDisableException(userDetails);
        }

        ConfirmationTokenImpl token = new ConfirmationTokenImpl();
        token.random();
        userDetails.setToken(token);
        userDetails.setPasswordTokenValidity(LocalDateTime.now());

        //save data
        this.updateUser(userDetails);
    }

    @Override
    public void changePassword(UserDetailsPasswordExpanded userDetails, String newPassword) throws AccountDisableException {
        if (!userDetails.isEnabled()) {
            throw new AccountDisableException(userDetails);
        }

        userDetails.setPassword(newPassword);
        userDetails.clearToken();

        //save data
        this.updateUser(userDetails);
    }

}
