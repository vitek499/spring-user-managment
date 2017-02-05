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
package cz.cvut.springframework.security.endpoint;

import cz.cvut.springframework.security.common.jdbc.UserEntity;
import cz.cvut.springframework.security.common.messanger.PasswordMessangerImpl;
import cz.cvut.springframework.security.common.messanger.UserMessangerImpl;
import cz.cvut.springframework.security.common.token.ConfirmationTokenImpl;
import cz.cvut.springframework.security.common.validator.PasswordValidatorImpl;
import cz.cvut.springframework.security.service.password.InformationPasswordService;
import cz.cvut.springframework.security.service.password.UserDetailsPasswordResetService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class ResetPasswordEndpointTest {
    
    protected ResetPasswordEndpoint resetPasswordEndpoint;
    protected static ConfirmationTokenImpl token;
    protected UserDetailsPasswordResetService mockedUserDetailsService;
    protected UserEntity userEntity;

    @BeforeClass
    public static void setUpClass() {
        token = new ConfirmationTokenImpl();
    }

    @Before
    public void setUp() {
        resetPasswordEndpoint = new ResetPasswordEndpoint();
        resetPasswordEndpoint.setUserMessanger(new UserMessangerImpl());
        resetPasswordEndpoint.setInformationPasswordService(mock(InformationPasswordService.class));
        resetPasswordEndpoint.setPasswordMessanger(new PasswordMessangerImpl());
        resetPasswordEndpoint.setViewNameChangePasswordForm("password/change");
        resetPasswordEndpoint.setViewNameResetPasswordForm("password/resetForm");
        resetPasswordEndpoint.setViewNameResetPasswordRequest("password/resetRequest");

        mockedUserDetailsService = mock(UserDetailsPasswordResetService.class);
        userEntity = new UserEntity("test@test.com", "test", ResetPasswordEndpointTest.token, LocalDateTime.now(), true, true, true, true, true, AuthorityUtils.NO_AUTHORITIES);
        when(mockedUserDetailsService.loadUserByUsername(anyString())).thenReturn(userEntity);
        when(mockedUserDetailsService.getPasswordValidator()).thenReturn(spy(new PasswordValidatorImpl(Pattern.compile("^.*$"), "")));
        when(mockedUserDetailsService.hasValidPasswordResetData(userEntity)).thenReturn(true);

        resetPasswordEndpoint.setClientDetailsService(mockedUserDetailsService);
    }
    
    @Test
    public void testResetPassword() throws Exception
    {
        //set params
        Map<String, String> parameters = new HashMap<>();
        token.random();
        parameters.put("token", token.toString());
        parameters.put("email", "test@test.com");
        parameters.put("password", "pass");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("token")).thenReturn(parameters.get("token"));
        when(request.getParameter("email")).thenReturn(parameters.get("email"));
        when(request.getParameter("password")).thenReturn(parameters.get("password"));

        //reset password
        resetPasswordEndpoint.resetPassword(parameters, request);
        
        //verify callings
        verify(mockedUserDetailsService).hasValidPasswordResetData(userEntity);
        verify(mockedUserDetailsService).changePassword(userEntity, "pass");
    }
    
    @Test
    public void testResetPasswordRequest() throws Exception
    {
        //set params
        Map<String, String> parameters = new HashMap<>();
        token.random();
        parameters.put("token", token.toString());
        parameters.put("email", "test@test.com");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("token")).thenReturn(parameters.get("token"));
        when(request.getParameter("email")).thenReturn(parameters.get("email"));

        //create reset password request
        resetPasswordEndpoint.resetPasswordRequest(parameters, request);
        
        //verify callings
        verify(mockedUserDetailsService).generatePasswordResetData(userEntity);
    }
}
