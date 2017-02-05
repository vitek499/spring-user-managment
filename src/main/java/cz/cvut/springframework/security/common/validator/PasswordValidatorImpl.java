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
package cz.cvut.springframework.security.common.validator;

import cz.cvut.springframework.security.common.exception.PasswordNotValid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
@Configuration
public class PasswordValidatorImpl implements PasswordValidator {

    private PasswordEncoder passwordEncoder;

    private Pattern pattern;
    private String errorMessage;

    public PasswordValidatorImpl() {
        this.pattern = Pattern.compile("^.*(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).*$");
        this.errorMessage = "Password Must be contain at least 8 characters, including at least 1 number and includes both lower and uppercase letters";
    }

    public PasswordValidatorImpl(Pattern pattern, String errorMessage) {
        this.pattern = pattern;
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean isValid(final String password) {
        return this.pattern.matcher(password).matches();
    }

    @Override
    public PasswordNotValid getValidtyException(final String password) {
        throw new PasswordNotValid(errorMessage);
    }

    @Override
    public String hashPassword(String plainPassword) {
        if (this.passwordEncoder == null) {
            return plainPassword;
        }
        return this.passwordEncoder.encode(plainPassword);
    }

    /**
     * @return the passwordEncoder
     */
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    /**
     * @param passwordEncoder the passwordEncoder to set
     */
    @Autowired(required = false)
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

}
