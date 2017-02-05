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

import cz.cvut.springframework.security.common.exception.TokenExpired;
import cz.cvut.springframework.security.common.jdbc.UserEntity;
import cz.cvut.springframework.security.common.messanger.AccountActivationMessangerImpl;
import cz.cvut.springframework.security.common.messanger.UserMessangerImpl;
import cz.cvut.springframework.security.common.token.ConfirmationTokenImpl;
import cz.cvut.springframework.security.common.validator.PasswordValidatorImpl;
import cz.cvut.springframework.security.common.validator.UserValidatorEmailBased;
import cz.cvut.springframework.security.service.activation.InformationActivationService;
import cz.cvut.springframework.security.service.activation.UserDetailsActivationService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class ActivationAccountEndpointTest {

    protected ActivationAccountEndpoint activationAccountEndpoint;
    protected static ConfirmationTokenImpl token;
    protected UserDetailsActivationService mockedUserDetailsService;
    protected UserEntity userEntity;

    @BeforeClass
    public static void setUpClass() {
        token = new ConfirmationTokenImpl();
    }

    @Before
    public void setUp() {
        activationAccountEndpoint = new ActivationAccountEndpoint();
        activationAccountEndpoint.setUserMessanger(new UserMessangerImpl());
        activationAccountEndpoint.setInformationActivationService(mock(InformationActivationService.class));
        activationAccountEndpoint.setAccountActivationMessanger(new AccountActivationMessangerImpl());
        activationAccountEndpoint.setViewNameUserActivation("activate/userActivation");

        mockedUserDetailsService = mock(UserDetailsActivationService.class);
        userEntity = new UserEntity("test@test.com", "test", ActivationAccountEndpointTest.token, LocalDateTime.now(), true, true, true, true, true, AuthorityUtils.NO_AUTHORITIES);
        when(mockedUserDetailsService.loadUserByUsername(anyString())).thenReturn(userEntity);
        when(mockedUserDetailsService.canBeActivated(userEntity)).thenReturn(true);

        activationAccountEndpoint.setClientDetailsService(mockedUserDetailsService);
    }

    @Test(expected = TokenExpired.class)
    public void testCanBeActivated() throws Exception {
        when(mockedUserDetailsService.canBeActivated(userEntity)).thenReturn(false);

        //set params
        Map<String, String> parameters = new HashMap<>();
        token.random();
        parameters.put("token", token.toString());
        parameters.put("email", "test@test.com");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("token")).thenReturn(parameters.get("token"));
        when(request.getParameter("email")).thenReturn(parameters.get("email"));

        //activate
        activationAccountEndpoint.activate(parameters, request);
    }

    @Test
    public void testActivation() throws Exception {
        //set params
        Map<String, String> parameters = new HashMap<>();
        token.random();
        parameters.put("token", token.toString());
        parameters.put("email", "test@test.com");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("token")).thenReturn(parameters.get("token"));
        when(request.getParameter("email")).thenReturn(parameters.get("email"));

        //activate
        activationAccountEndpoint.activate(parameters, request);

        verify(mockedUserDetailsService).activateUser(userEntity);
    }
}
