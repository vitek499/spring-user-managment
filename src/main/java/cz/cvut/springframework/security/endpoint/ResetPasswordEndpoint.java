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

import cz.cvut.springframework.security.common.exception.OldPasswordNotEquals;
import cz.cvut.springframework.security.common.exception.TokenExpired;
import cz.cvut.springframework.security.common.exception.TokenNotValid;
import cz.cvut.springframework.security.common.messanger.PasswordMessanger;
import cz.cvut.springframework.security.common.validator.PasswordValidator;
import cz.cvut.springframework.security.service.UserDetailsToken;
import cz.cvut.springframework.security.service.password.AccountDisableException;
import cz.cvut.springframework.security.service.password.InformationPasswordService;
import cz.cvut.springframework.security.service.password.UserDetailsPasswordResetService;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 * @link http://www.vs-point.cz
 */
@FrameworkEndpoint
public class ResetPasswordEndpoint extends AbstractEndpoint {

    private InformationPasswordService informationPasswordService;
    private PasswordMessanger passwordMessanger;
    private String viewNameResetPasswordForm;
    private String viewNameChangePasswordForm;
    private String viewNameResetPasswordRequest;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.state(informationPasswordService != null, "InformationPasswordService must be provided");
        Assert.state(passwordMessanger != null, "PasswordMessanger must be provided");
        Assert.state(viewNameResetPasswordForm != null, "ViewNameResetPasswordForm must be provided");
        Assert.state(viewNameChangePasswordForm != null, "ViewNameChangePasswordForm must be provided");
        Assert.state(viewNameResetPasswordRequest != null, "ViewNameResetPasswordRequest must be provided");
        Assert.state(this.getClientDetailsService() instanceof UserDetailsPasswordResetService, "ClientDetailsService must be UserDetailsPasswordResetService instance");
    }

    public PasswordMessanger getPasswordMessanger() {
        return passwordMessanger;
    }

    public void setPasswordMessanger(PasswordMessanger passwordMessanger) {
        this.passwordMessanger = passwordMessanger;
    }

    protected InformationPasswordService getInformationPasswordService() {
        return informationPasswordService;
    }

    protected UserDetailsPasswordResetService getUserDetailsPasswordService() {
        return (UserDetailsPasswordResetService) this.getClientDetailsService();
    }

    protected PasswordValidator getPasswordValidator() {
        return this.getUserDetailsPasswordService().getPasswordValidator();
    }

    public void setInformationPasswordService(InformationPasswordService informationPasswordService) {
        this.informationPasswordService = informationPasswordService;
    }

    @RequestMapping(value = "/account/reset")
    @PreAuthorize("isAnonymous()")
    public ModelAndView resetPasswordRequest(@RequestParam Map<String, String> parameters, HttpServletRequest request) throws Exception, AccountDisableException, UsernameNotFoundException, TokenExpired {
        Map<String, Object> resultModel = new HashMap<>();

        //load password from request
        PasswordMessanger data = passwordMessanger.fromRequest(request);
        resultModel.put("PasswordMessanger", data);

        //try to get user
        if (!data.isUsernameEmpty()) {
            UserDetailsToken userDetails = this.getUserDetailsPasswordService().loadUserByUsername(data.getUsername());
            resultModel.put("UserMessanger", this.getUserMessanger().fromUserDetails(userDetails));
            this.getUserDetailsPasswordService().generatePasswordResetData(userDetails);
            this.informationPasswordService.sendResetPasswordData(userDetails);
        }

        //return response
        return new ModelAndView(this.getViewNameResetPasswordRequest(), resultModel);
    }

    @RequestMapping(value = "/account/password/reset")
    @PreAuthorize("isAnonymous()")
    public ModelAndView resetPassword(@RequestParam Map<String, String> parameters, HttpServletRequest request) throws Exception, AccountDisableException, UsernameNotFoundException, TokenExpired {
        Map<String, Object> resultModel = new HashMap<>();

        //load password from request
        PasswordMessanger data = passwordMessanger.fromRequest(request);
        resultModel.put("PasswordMessanger", data);

        //try to get user
        UserDetailsToken userDetails = this.getUserDetailsPasswordService().loadUserByUsername(data.getUsername());
        resultModel.put("UserMessanger", this.getUserMessanger().fromUserDetails(userDetails));

        //ceck for correct token
        if (!this.getUserDetailsPasswordService().hasValidPasswordResetData(userDetails)) {
            throw new TokenExpired("Sended token has been expired");
        }

        //check token
        if (userDetails.getToken().isEmpty()) {
            throw new TokenExpired("User hasn't any token");
        }

        //validate token
        if (!data.hasToken() || data.getToken().isEmpty() || !userDetails.getToken().equals(data.getToken())) {
            throw new TokenNotValid("Sended token is not valid");
        }

        //if password is send
        if (!data.isPasswordEmpty()) {
            //validate password
            if (!this.getPasswordValidator().isValid(data.getPassword())) {
                throw this.getPasswordValidator().getValidtyException(data.getPassword());
            }

            //change pass
            this.getUserDetailsPasswordService().changePassword(userDetails, hashPassword(data.getPassword()));
        }

        //return response
        return new ModelAndView(this.getViewNameResetPasswordForm(), resultModel);
    }

    @RequestMapping(value = "/account/password/change")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView changePassword(@RequestParam Map<String, String> parameters, HttpServletRequest request, Principal principal) throws Exception, AccountDisableException, UsernameNotFoundException, TokenExpired {
        Map<String, Object> resultModel = new HashMap<>();

        //load password from request
        PasswordMessanger data = passwordMessanger.fromRequest(request);
        resultModel.put("PasswordMessanger", data);

        //get user
        UserDetailsToken userDetails = (UserDetailsToken) ((Authentication) principal).getPrincipal();
        resultModel.put("UserMessanger", this.getUserMessanger().fromUserDetails(userDetails));

        //check if data was send
        if (data.getOldPassword() != null && data.getPassword() != null) {
            //check old password
            if (data.isOldPasswordEmpty() || !hashPassword(data.getOldPassword()).equals(userDetails.getPassword())) {
                throw new OldPasswordNotEquals("Sended old password not equals");
            }

            //if password is send
            if (!data.isPasswordEmpty() && this.getPasswordValidator().isValid(data.getPassword())) {
                //change pass
                this.getUserDetailsPasswordService().changePassword(userDetails, hashPassword(data.getPassword()));
            } else {
                throw this.getPasswordValidator().getValidtyException(data.getPassword());
            }
        }

        //return response
        return new ModelAndView(this.getViewNameChangePasswordForm(), resultModel);
    }

    private String rasswordRequestResetLink;

    public void setPasswordRequestResetLink(String rasswordRequestResetLink) {
        this.rasswordRequestResetLink = rasswordRequestResetLink;
    }

    private String rasswordResetLink;

    public void setPasswordResetLink(String rasswordResetLink) {
        this.rasswordResetLink = rasswordResetLink;
    }

    private String passwordChangeLink;

    public void setPasswordChangeLink(String passwordChangeLink) {
        this.passwordChangeLink = passwordChangeLink;
    }

    private String hashPassword(String plainPass) {
        return this.getPasswordValidator().hashPassword(plainPass);
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
    public void setViewNameResetPasswordForm(String viewNameResetPasswordForm) {
        this.viewNameResetPasswordForm = viewNameResetPasswordForm;
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
    public void setViewNameChangePasswordForm(String viewNameChangePasswordForm) {
        this.viewNameChangePasswordForm = viewNameChangePasswordForm;
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
    public void setViewNameResetPasswordRequest(String viewNameResetPasswordRequest) {
        this.viewNameResetPasswordRequest = viewNameResetPasswordRequest;
    }

}
