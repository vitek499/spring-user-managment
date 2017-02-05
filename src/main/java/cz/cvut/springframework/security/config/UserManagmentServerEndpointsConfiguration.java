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
package cz.cvut.springframework.security.config;

import cz.cvut.springframework.security.UserManagmentConfigurer;
import cz.cvut.springframework.security.UserManagmentEndpointsConfigurer;
import cz.cvut.springframework.security.common.email.InformationActivationEmailService;
import cz.cvut.springframework.security.common.email.InformationPasswordEmailService;
import cz.cvut.springframework.security.common.messanger.AccountActivationMessanger;
import cz.cvut.springframework.security.common.messanger.AccountActivationMessangerImpl;
import cz.cvut.springframework.security.common.messanger.PasswordMessangerImpl;
import cz.cvut.springframework.security.common.messanger.UserMessangerImpl;
import cz.cvut.springframework.security.endpoint.ActivationAccountEndpoint;
import cz.cvut.springframework.security.endpoint.FrameworkEndpointHandlerMapping;
import cz.cvut.springframework.security.endpoint.RegistrationAccountEndpoint;
import cz.cvut.springframework.security.endpoint.ResetPasswordEndpoint;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 * @link http://www.vs-point.cz
 */
@Configuration
//@Import(TokenKeyEndpointRegistrar.class)
public class UserManagmentServerEndpointsConfiguration {

    private UserManagmentEndpointsConfigurer endpoints = new UserManagmentEndpointsConfigurer();

    @Autowired
    private List<UserManagmentConfigurer> configurers = Collections.emptyList();

    @Autowired(required = false)
    private UserDetailsService clientDetailsService;

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    @PostConstruct
    public void init() {
        for (UserManagmentConfigurer configurer : configurers) {
            try {
                configurer.configure(endpoints);
            } catch (Exception e) {
                throw new IllegalStateException("Cannot configure enpdoints", e);
            }
        }
        //endpoints.userDetailsService(clientDetailsService);
    }

    @Bean
    public ActivationAccountEndpoint activationAccountEndpoint() throws Exception {
        ActivationAccountEndpoint activationAccountEndpoint = new ActivationAccountEndpoint();

        FrameworkEndpointHandlerMapping mapping = endpoints.getFrameworkEndpointHandlerMapping();
        activationAccountEndpoint.setActivationLink(extractPath(mapping, "/account/activate"));

        if (endpoints.isUserDetailsServiceOverride()) {
            activationAccountEndpoint.setClientDetailsService(endpoints.getUserDetailsService());
        } else {
            activationAccountEndpoint.setClientDetailsService(this.clientDetailsService);
        }

        if (endpoints.isAccountActivationMessangerOverride()) {
            activationAccountEndpoint.setAccountActivationMessanger(endpoints.getAccountActivationMessanger());
        } else {
            activationAccountEndpoint.setAccountActivationMessanger(new AccountActivationMessangerImpl());
        }

        if (endpoints.isInformationActivationServiceOverride()) {
            activationAccountEndpoint.setInformationActivationService(endpoints.getInformationActivationService());
        } else {
            activationAccountEndpoint.setInformationActivationService(new InformationActivationEmailService(javaMailSender, activationAccountEndpoint.getAccountActivationMessanger()));
        }

        if (endpoints.isViewNameUserActivationOverride()) {
            activationAccountEndpoint.setViewNameUserActivation(endpoints.getViewNameUserActivation());
        } else {
            activationAccountEndpoint.setViewNameUserActivation("activate/userActivation");
        }

        if (endpoints.isUserMessangerOverride()) {
            activationAccountEndpoint.setUserMessanger(endpoints.getUserMessanger());
        } else {
            activationAccountEndpoint.setUserMessanger(new UserMessangerImpl());
        }

        return activationAccountEndpoint;
    }
    
    @Bean
    public RegistrationAccountEndpoint registrationAccountEndpoint() throws Exception {
        RegistrationAccountEndpoint registrationAccountEndpoint = new RegistrationAccountEndpoint();

        FrameworkEndpointHandlerMapping mapping = endpoints.getFrameworkEndpointHandlerMapping();
        registrationAccountEndpoint.setRegistrationLink(extractPath(mapping, "/account/registration"));

        if (endpoints.isUserDetailsServiceOverride()) {
            registrationAccountEndpoint.setClientDetailsService(endpoints.getUserDetailsService());
        } else {
            registrationAccountEndpoint.setClientDetailsService(this.clientDetailsService);
        }

        if (endpoints.isAccountActivationMessangerOverride()) {
            registrationAccountEndpoint.setAccountActivationMessanger(endpoints.getAccountActivationMessanger());
        } else {
            registrationAccountEndpoint.setAccountActivationMessanger(new AccountActivationMessangerImpl());
        }

        if (endpoints.isInformationActivationServiceOverride()) {
            registrationAccountEndpoint.setInformationActivationService(endpoints.getInformationActivationService());
        } else {
            registrationAccountEndpoint.setInformationActivationService(new InformationActivationEmailService(javaMailSender, registrationAccountEndpoint.getAccountActivationMessanger()));
        }

        if (endpoints.isViewNameUserRegistrationOverride()) {
            registrationAccountEndpoint.setViewNameUserRegistration(endpoints.getViewNameUserRegistration());
        } else {
            registrationAccountEndpoint.setViewNameUserRegistration("activate/userRegistration");
        }

        if (endpoints.isUserMessangerOverride()) {
            registrationAccountEndpoint.setUserMessanger(endpoints.getUserMessanger());
        } else {
            registrationAccountEndpoint.setUserMessanger(new UserMessangerImpl());
        }

        return registrationAccountEndpoint;
    }

    @Bean
    public FrameworkEndpointHandlerMapping endpointHandlerMapping() throws Exception {
        return endpoints.getFrameworkEndpointHandlerMapping();
    }

    @Bean
    public ResetPasswordEndpoint resetPasswordEndpoint() throws Exception {
        ResetPasswordEndpoint resetPasswordEndpoint = new ResetPasswordEndpoint();

        FrameworkEndpointHandlerMapping mapping = endpoints.getFrameworkEndpointHandlerMapping();
        resetPasswordEndpoint.setPasswordRequestResetLink(extractPath(mapping, "/account/reset"));
        resetPasswordEndpoint.setPasswordResetLink(extractPath(mapping, "/account/password/reset"));
        resetPasswordEndpoint.setPasswordChangeLink(extractPath(mapping, "/account/password/change"));

        if (endpoints.isUserDetailsServiceOverride()) {
            resetPasswordEndpoint.setClientDetailsService(endpoints.getUserDetailsService());
        } else {
            resetPasswordEndpoint.setClientDetailsService(this.clientDetailsService);
        }

        if (endpoints.isPasswordMessangerOverride()) {
            resetPasswordEndpoint.setPasswordMessanger(endpoints.getPasswordMessanger());
        } else {
            resetPasswordEndpoint.setPasswordMessanger(new PasswordMessangerImpl());
        }

        if (endpoints.isUserMessangerOverride()) {
            resetPasswordEndpoint.setUserMessanger(endpoints.getUserMessanger());
        } else {
            resetPasswordEndpoint.setUserMessanger(new UserMessangerImpl());
        }

        if (endpoints.isInformationPasswordServiceOverride()) {
            resetPasswordEndpoint.setInformationPasswordService(endpoints.getInformationPasswordService());
        } else {
            resetPasswordEndpoint.setInformationPasswordService(new InformationPasswordEmailService(javaMailSender, resetPasswordEndpoint.getPasswordMessanger()));
        }

        if (endpoints.isViewNameResetPasswordFormOverride()) {
            resetPasswordEndpoint.setViewNameResetPasswordForm(endpoints.getViewNameResetPasswordForm());
        } else {
            resetPasswordEndpoint.setViewNameResetPasswordForm("password/resetForm");
        }

        if (endpoints.isViewNameChangePasswordFormOverride()) {
            resetPasswordEndpoint.setViewNameChangePasswordForm(endpoints.getViewNameChangePasswordForm());
        } else {
            resetPasswordEndpoint.setViewNameChangePasswordForm("password/change");
        }

        if (endpoints.isViewNameResetPasswordRequestOverride()) {
            resetPasswordEndpoint.setViewNameResetPasswordRequest(endpoints.getViewNameResetPasswordRequest());
        } else {
            resetPasswordEndpoint.setViewNameResetPasswordRequest("password/resetRequest");
        }

        return resetPasswordEndpoint;
    }

    private String extractPath(FrameworkEndpointHandlerMapping mapping, String page) {
        String path = mapping.getPath(page);
        if (path.contains(":")) {
            return path;
        }
        return "forward:" + path;
    }
}
