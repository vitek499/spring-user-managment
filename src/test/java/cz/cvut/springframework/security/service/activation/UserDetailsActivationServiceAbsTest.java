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
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class UserDetailsActivationServiceAbsTest {

    private UserDetailsActivationServiceAbsImpl testedClass;
    
    @Before
    public void setUp() {
        testedClass = new UserDetailsActivationServiceAbsImpl();
    }
    
    @Test
    public void testCanBeActivated() {
        UserDetailsActivationExpanded userDetail = mock(UserDetailsActivationExpanded.class);
        when(userDetail.isActive()).thenReturn(true);
        
        assertFalse(testedClass.canBeActivated(userDetail));
    }
    
    @Test
    public void testActivateUser() throws ActivateAccountException {
        UserDetailsActivationExpanded userDetail = mock(UserDetailsActivationExpanded.class);
        
        testedClass.activateUser(userDetail);
        
        verify(userDetail).activate();
    }
   
    
    private class UserDetailsActivationServiceAbsImpl extends UserDetailsActivationServiceAbs{

        @Override
        public void createUser(UserDetails ud) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void updateUser(UserDetails ud) {
            //throw new UnsupportedOperationException("Not supported yet.");
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
        public UserValidator getUserValidator() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public UserDetailsToken registerUser(UserDetailsActivationExpanded createdUser) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public PasswordValidator getPasswordValidator() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean exists(UserDetailsActivationExpanded createdUser) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
}
