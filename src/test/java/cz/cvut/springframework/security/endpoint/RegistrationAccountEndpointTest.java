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
import cz.cvut.springframework.security.common.messanger.AccountActivationMessangerImpl;
import cz.cvut.springframework.security.common.messanger.UserMessangerImpl;
import cz.cvut.springframework.security.common.token.ConfirmationTokenImpl;
import cz.cvut.springframework.security.common.validator.PasswordValidatorImpl;
import cz.cvut.springframework.security.common.validator.UserValidatorEmailBased;
import static cz.cvut.springframework.security.endpoint.ActivationAccountEndpointTest.token;
import cz.cvut.springframework.security.service.UserDetailsToken;
import cz.cvut.springframework.security.service.activation.InformationActivationService;
import cz.cvut.springframework.security.service.activation.UserDetailsActivationService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class RegistrationAccountEndpointTest {

    protected RegistrationAccountEndpoint registrationAccountEndpoint;
    protected UserDetailsActivationService mockedUserDetailsService;
    protected UserEntity userEntity;

    @Before
    public void setUp() {
        registrationAccountEndpoint = new RegistrationAccountEndpoint();
        registrationAccountEndpoint.setUserMessanger(new UserMessangerImpl());
        registrationAccountEndpoint.setInformationActivationService(mock(InformationActivationService.class));
        registrationAccountEndpoint.setAccountActivationMessanger(new AccountActivationMessangerImpl());
        registrationAccountEndpoint.setViewNameUserRegistration("activate/userRegistration");

        mockedUserDetailsService = mock(UserDetailsActivationService.class);
        userEntity = new UserEntity("test@test.com", "test", new ConfirmationTokenImpl(), LocalDateTime.now(), true, true, true, true, true, AuthorityUtils.NO_AUTHORITIES);
        when(mockedUserDetailsService.loadUserByUsername(anyString())).thenReturn(userEntity);
        when(mockedUserDetailsService.registerUser(any(UserDetailsToken.class))).thenReturn(userEntity);
        when(mockedUserDetailsService.canBeActivated(userEntity)).thenReturn(true);
        when(mockedUserDetailsService.getPasswordValidator()).thenReturn(spy(new PasswordValidatorImpl(Pattern.compile("^.*$"), "")));
        when(mockedUserDetailsService.getUserValidator()).thenReturn(spy(new UserValidatorEmailBased()));

        registrationAccountEndpoint.setClientDetailsService(mockedUserDetailsService);
    }

    @Test
    public void testValidationCall() throws Exception {
        //set params
        Map<String, String> parameters = new HashMap<>();
        parameters.put("password", "test");
        parameters.put("email", "test@test.com");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("password")).thenReturn(parameters.get("password"));
        when(request.getParameter("email")).thenReturn(parameters.get("email"));

        //registration
        registrationAccountEndpoint.registration(parameters, request, mock(HttpServletResponse.class));

        //verify validation
        verify(mockedUserDetailsService.getPasswordValidator()).isValid(anyString());
        verify(mockedUserDetailsService.getUserValidator()).isValid(userEntity);
    }

    @Test
    public void testRegisterUser() throws Exception {
        //set params
        Map<String, String> parameters = new HashMap<>();
        parameters.put("password", "test");
        parameters.put("email", "test@test.com");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("password")).thenReturn(parameters.get("password"));
        when(request.getParameter("email")).thenReturn(parameters.get("email"));

        //registration
        registrationAccountEndpoint.registration(parameters, request, mock(HttpServletResponse.class));

        //validation of register user
        verify(mockedUserDetailsService).registerUser(userEntity);
    }

}
