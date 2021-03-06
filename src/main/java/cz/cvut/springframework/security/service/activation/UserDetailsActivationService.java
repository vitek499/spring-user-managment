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
package cz.cvut.springframework.security.service.activation;

import cz.cvut.springframework.security.common.validator.PasswordValidator;
import cz.cvut.springframework.security.common.validator.UserValidator;
import cz.cvut.springframework.security.service.UserDetailsToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
@Service
public interface UserDetailsActivationService<UD extends UserDetailsToken> extends UserDetailsService {

    @Override
    UserDetailsToken loadUserByUsername(String username);
    
    boolean canBeActivated(UD userDetails);

    void generateActivationData(UD userDetails) throws Exception;

    void activateUser(UD userDetails) throws Exception;
    
    UserValidator getUserValidator();
    
    UserDetailsToken registerUser(UD createdUser);

    PasswordValidator getPasswordValidator();
    
    boolean exists(UD createdUser);
}
