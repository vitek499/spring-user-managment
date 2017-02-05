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
package cz.cvut.springframework.security.common.jdbc;

import cz.cvut.springframework.security.common.token.ConfirmationToken;
import cz.cvut.springframework.security.service.activation.UserDetailsActivationExpanded;
import cz.cvut.springframework.security.service.password.UserDetailsPasswordExpanded;
import java.time.LocalDateTime;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class UserEntity extends User implements UserDetailsPasswordExpanded, UserDetailsActivationExpanded {

    public UserEntity(String username, String password, ConfirmationToken token, LocalDateTime confirmationTokenCreation, boolean activate, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.password = password;
        this.confirmationToken = token;
        this.active = activate;
        this.confirmationTokenCreation = confirmationTokenCreation;
    }

    public UserEntity(String username, String password, ConfirmationToken token, LocalDateTime confirmationTokenCreation, boolean activate, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.password = password;
        this.confirmationToken = token;
        this.active = activate;
        this.confirmationTokenCreation = confirmationTokenCreation;
    }

    protected ConfirmationToken confirmationToken;
    protected LocalDateTime confirmationTokenCreation;
    protected String password;
    protected boolean active;

    @Override
    public boolean hasToken() {
        if (this.confirmationToken != null && !this.confirmationToken.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ConfirmationToken getToken() {
        return this.confirmationToken;
    }

    @Override
    public void setToken(ConfirmationToken token) {
        this.confirmationToken = token;
    }

    @Override
    public void clearToken() {
        this.confirmationToken = null;
    }

    @Override
    public void activate() {
        this.active = true;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPasswordTokenValidity(LocalDateTime date) {
        this.confirmationTokenCreation = date;
    }

    @Override
    public LocalDateTime getPasswordTokenValidity() {
        return this.confirmationTokenCreation;
    }
}
