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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.springframework.security.common.jdbc.UserEntity;
import cz.cvut.springframework.security.common.token.ConfirmationTokenImpl;
import cz.cvut.springframework.security.service.UserDetailsToken;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 * @link http://www.vs-point.cz
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRestDetailsImpl implements UserMessanger, Serializable {

    @JsonProperty(value = "password")
    private String password;

    @JsonProperty(value = "email")
    private String email;

    @Override
    @JsonIgnore
    public UserDetailsToken getUserDetails() {
        return new UserEntity(this.email, this.password, new ConfirmationTokenImpl(), LocalDateTime.now(), false, true, true, true, true, AuthorityUtils.NO_AUTHORITIES);
    }

    @Override
    @JsonIgnore
    public boolean isUserDetailsEmpty() {
        if (password == null || email == null || password.isEmpty() || email.isEmpty()) {
            return true;
        }
        return false;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    @JsonIgnore
    public UserMessanger fromUserDetails(UserDetailsToken userDetails) {
        UserRestDetailsImpl userMessanger = new UserRestDetailsImpl();
        userMessanger.email = userDetails.getUsername();
        return userMessanger;
    }

    @Override
    @JsonIgnore
    public UserMessanger fromRequest(HttpServletRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(request.getInputStream(), UserRestDetailsImpl.class);
        } catch (IOException ex) {
            Logger.getLogger(UserRestDetailsImpl.class.getName()).log(Level.SEVERE, null, ex);
            return new UserRestDetailsImpl();
        }

    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
