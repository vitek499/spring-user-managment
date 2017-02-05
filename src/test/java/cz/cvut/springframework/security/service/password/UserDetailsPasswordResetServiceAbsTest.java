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
import cz.cvut.springframework.security.common.validator.PasswordValidator;
import cz.cvut.springframework.security.service.UserDetailsToken;
import cz.cvut.springframework.security.service.activation.UserDetailsActivationExpanded;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.springframework.format.datetime.joda.LocalDateTimeParser;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class UserDetailsPasswordResetServiceAbsTest {

    private UserDetailsPasswordResetServiceAbsTestImpl testedClass;

    @Before
    public void setUp() {
        testedClass = new UserDetailsPasswordResetServiceAbsTestImpl();
    }

    @Test
    public void testHasValidPasswordResetData() {
        //create random token
        ConfirmationTokenImpl token = new ConfirmationTokenImpl();
        token.random();
        
        //create expired date
        LocalDateTime expiredDate = LocalDateTime.now().minusDays(1).minusSeconds(1);
        
        //define user class
        UserDetailsPasswordExpanded userDetail = mock(UserDetailsPasswordExpanded.class);
        when(userDetail.isEnabled()).thenReturn(true);
        when(userDetail.hasToken()).thenReturn(true);
        when(userDetail.getToken()).thenReturn(token);
        when(userDetail.getPasswordTokenValidity()).thenReturn(expiredDate);
        
        assertFalse(testedClass.hasValidPasswordResetData(userDetail));
    }

    @Test
    public void testChangePassword() {
        //define new pass
        String password = "test";
        
        //define user class
        UserDetailsPasswordExpanded userDetail = mock(UserDetailsPasswordExpanded.class);
        when(userDetail.isEnabled()).thenReturn(true);
        
        //call method
        testedClass.changePassword(userDetail,password);
        
        verify(userDetail).setPassword(password);
    }

    private class UserDetailsPasswordResetServiceAbsTestImpl extends UserDetailsPasswordResetServiceAbs {

        @Override
        public void createUser(UserDetails ud) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void updateUser(UserDetails ud) {
            
        }

        @Override
        public void deleteUser(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void changePassword(String string, String string1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean userExists(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UserDetailsToken loadUserByUsername(String username) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public PasswordValidator getPasswordValidator() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
