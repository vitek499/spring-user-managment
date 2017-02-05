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
package cz.cvut.springframework.security;

import cz.cvut.springframework.security.common.messanger.AccountActivationMessanger;
import cz.cvut.springframework.security.common.messanger.PasswordMessanger;
import cz.cvut.springframework.security.common.messanger.UserMessanger;
import cz.cvut.springframework.security.endpoint.FrameworkEndpointHandlerMapping;
import cz.cvut.springframework.security.service.activation.InformationActivationService;
import cz.cvut.springframework.security.service.password.InformationPasswordService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 * @link http://www.vs-point.cz
 */
public class UserManagmentEndpointsConfigurer {

    private Map<String, String> patternMap = new HashMap<String, String>();

    private UserDetailsService userDetailsService;

    private boolean userDetailsServiceOverride = false;

    public UserManagmentEndpointsConfigurer pathMapping(String defaultPath, String customPath) {
        this.patternMap.put(defaultPath, customPath);
        return this;
    }

    public boolean isUserDetailsServiceOverride() {
        return userDetailsServiceOverride;
    }

    public UserManagmentEndpointsConfigurer setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.userDetailsServiceOverride = true;
        return this;
    }

    public UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

    private InformationActivationService informationActivationService;
    private AccountActivationMessanger accountActivationMessanger;
    private String viewNameUserActivation;
    private String viewNameUserRegistration;
    private UserMessanger userMessanger;
    private InformationPasswordService informationPasswordService;
    private PasswordMessanger passwordMessanger;
    private String viewNameResetPasswordForm;
    private String viewNameChangePasswordForm;
    private String viewNameResetPasswordRequest;

    private boolean informationActivationServiceOverride;
    private boolean accountActivationMessangerOverride;
    private boolean viewNameUserActivationOverride;
    private boolean viewNameUserRegistrationOverride;
    private boolean userMessangerOverride;
    private boolean informationPasswordServiceOverride;
    private boolean passwordMessangerOverride;
    private boolean viewNameResetPasswordFormOverride;
    private boolean viewNameChangePasswordFormOverride;
    private boolean viewNameResetPasswordRequestOverride;

    /**
     * @return the informationActivationService
     */
    public InformationActivationService getInformationActivationService() {
        return informationActivationService;
    }

    /**
     * @param informationActivationService the informationActivationService to
     * set
     */
    public UserManagmentEndpointsConfigurer setInformationActivationService(InformationActivationService informationActivationService) {
        this.informationActivationService = informationActivationService;
        this.informationActivationServiceOverride = true;
        return this;
    }

    /**
     * @return the accountActivationMessanger
     */
    public AccountActivationMessanger getAccountActivationMessanger() {
        return accountActivationMessanger;
    }

    /**
     * @param accountActivationMessanger the accountActivationMessanger to set
     */
    public UserManagmentEndpointsConfigurer setAccountActivationMessanger(AccountActivationMessanger accountActivationMessanger) {
        this.accountActivationMessanger = accountActivationMessanger;
        this.accountActivationMessangerOverride = true;
        return this;
    }

    /**
     * @return the viewNameUserActivation
     */
    public String getViewNameUserActivation() {
        return viewNameUserActivation;
    }

    /**
     * @param viewNameUserActivation the viewNameUserActivation to set
     */
    public UserManagmentEndpointsConfigurer setViewNameUserActivation(String viewNameUserActivation) {
        this.viewNameUserActivation = viewNameUserActivation;
        this.viewNameUserActivationOverride = true;
        return this;
    }

    /**
     * @return the viewNameUserRegistration
     */
    public String getViewNameUserRegistration() {
        return viewNameUserRegistration;
    }

    /**
     * @param viewNameUserRegistration the viewNameUserRegistration to set
     */
    public UserManagmentEndpointsConfigurer setViewNameUserRegistration(String viewNameUserRegistration) {
        this.viewNameUserRegistration = viewNameUserRegistration;
        this.viewNameUserRegistrationOverride = true;
        return this;
    }

    /**
     * @return the userMessanger
     */
    public UserMessanger getUserMessanger() {
        return userMessanger;
    }

    /**
     * @param userMessanger the userMessanger to set
     */
    public UserManagmentEndpointsConfigurer setUserMessanger(UserMessanger userMessanger) {
        this.userMessanger = userMessanger;
        this.userMessangerOverride = true;
        return this;
    }

    /**
     * @return the informationPasswordService
     */
    public InformationPasswordService getInformationPasswordService() {
        return informationPasswordService;
    }

    /**
     * @param informationPasswordService the informationPasswordService to set
     */
    public UserManagmentEndpointsConfigurer setInformationPasswordService(InformationPasswordService informationPasswordService) {
        this.informationPasswordService = informationPasswordService;
        this.informationPasswordServiceOverride = true;
        return this;
    }

    /**
     * @return the passwordMessanger
     */
    public PasswordMessanger getPasswordMessanger() {
        return passwordMessanger;
    }

    /**
     * @param passwordMessanger the passwordMessanger to set
     */
    public UserManagmentEndpointsConfigurer setPasswordMessanger(PasswordMessanger passwordMessanger) {
        this.passwordMessanger = passwordMessanger;
        this.passwordMessangerOverride = true;
        return this;
    }

    /**
     * @return the viewNameResetPasswordForm
     */
    public String getViewNameResetPasswordForm() {
        return viewNameResetPasswordForm;
    }

    /**
     * @param viewNameResetPasswordForm the viewNameResetPasswordForm to set
     */
    public UserManagmentEndpointsConfigurer setViewNameResetPasswordForm(String viewNameResetPasswordForm) {
        this.viewNameResetPasswordForm = viewNameResetPasswordForm;
        this.viewNameResetPasswordFormOverride = true;
        return this;
    }

    /**
     * @return the viewNameChangePasswordForm
     */
    public String getViewNameChangePasswordForm() {
        return viewNameChangePasswordForm;
    }

    /**
     * @param viewNameChangePasswordForm the viewNameChangePasswordForm to set
     */
    public UserManagmentEndpointsConfigurer setViewNameChangePasswordForm(String viewNameChangePasswordForm) {
        this.viewNameChangePasswordForm = viewNameChangePasswordForm;
        this.viewNameChangePasswordFormOverride = true;
        return this;
    }

    /**
     * @return the viewNameResetPasswordRequest
     */
    public String getViewNameResetPasswordRequest() {
        return viewNameResetPasswordRequest;
    }

    /**
     * @param viewNameResetPasswordRequest the viewNameResetPasswordRequest to
     * set
     */
    public UserManagmentEndpointsConfigurer setViewNameResetPasswordRequest(String viewNameResetPasswordRequest) {
        this.viewNameResetPasswordRequest = viewNameResetPasswordRequest;
        this.viewNameResetPasswordRequestOverride = true;
        return this;
    }

    /**
     * @return the informationActivationServiceOverride
     */
    public boolean isInformationActivationServiceOverride() {
        return informationActivationServiceOverride;
    }

    /**
     * @return the accountActivationMessangerOverride
     */
    public boolean isAccountActivationMessangerOverride() {
        return accountActivationMessangerOverride;
    }

    /**
     * @return the viewNameUserActivationOverride
     */
    public boolean isViewNameUserActivationOverride() {
        return viewNameUserActivationOverride;
    }

    /**
     * @return the viewNameUserRegistrationOverride
     */
    public boolean isViewNameUserRegistrationOverride() {
        return viewNameUserRegistrationOverride;
    }

    /**
     * @return the userMessangerOverride
     */
    public boolean isUserMessangerOverride() {
        return userMessangerOverride;
    }

    /**
     * @return the informationPasswordServiceOverride
     */
    public boolean isInformationPasswordServiceOverride() {
        return informationPasswordServiceOverride;
    }

    /**
     * @return the passwordMessangerOverride
     */
    public boolean isPasswordMessangerOverride() {
        return passwordMessangerOverride;
    }

    /**
     * @return the viewNameResetPasswordFormOverride
     */
    public boolean isViewNameResetPasswordFormOverride() {
        return viewNameResetPasswordFormOverride;
    }

    /**
     * @return the viewNameChangePasswordFormOverride
     */
    public boolean isViewNameChangePasswordFormOverride() {
        return viewNameChangePasswordFormOverride;
    }

    /**
     * @return the viewNameResetPasswordRequestOverride
     */
    public boolean isViewNameResetPasswordRequestOverride() {
        return viewNameResetPasswordRequestOverride;
    }

    private FrameworkEndpointHandlerMapping frameworkEndpointHandlerMapping;

    private String prefix;

    public UserManagmentEndpointsConfigurer prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public FrameworkEndpointHandlerMapping getFrameworkEndpointHandlerMapping() {
        return frameworkEndpointHandlerMapping();
    }

    public UserManagmentEndpointsConfigurer addInterceptor(HandlerInterceptor interceptor) {
        this.interceptors.add(interceptor);
        return this;
    }

    private List<Object> interceptors = new ArrayList<Object>();

    public UserManagmentEndpointsConfigurer addInterceptor(WebRequestInterceptor interceptor) {
        this.interceptors.add(interceptor);
        return this;
    }

    private FrameworkEndpointHandlerMapping frameworkEndpointHandlerMapping() {
        if (frameworkEndpointHandlerMapping == null) {
            frameworkEndpointHandlerMapping = new FrameworkEndpointHandlerMapping();
            frameworkEndpointHandlerMapping.setMappings(patternMap);
            frameworkEndpointHandlerMapping.setPrefix(prefix);
            frameworkEndpointHandlerMapping.setInterceptors(interceptors.toArray());
        }
        return frameworkEndpointHandlerMapping;
    }
}
