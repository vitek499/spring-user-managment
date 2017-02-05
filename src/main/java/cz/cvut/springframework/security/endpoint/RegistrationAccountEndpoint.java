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
import cz.cvut.springframework.security.common.exception.TokenNotValid;
import cz.cvut.springframework.security.common.exception.UserExists;
import cz.cvut.springframework.security.common.messanger.AccountActivationMessanger;
import cz.cvut.springframework.security.common.messanger.UserMessanger;
import cz.cvut.springframework.security.service.UserDetailsToken;
import cz.cvut.springframework.security.service.activation.InformationActivationService;
import cz.cvut.springframework.security.service.activation.UserDetailsActivationService;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
@FrameworkEndpoint
public class RegistrationAccountEndpoint extends AbstractEndpoint {

    private InformationActivationService informationActivationService;
    private String viewNameUserRegistration;
    private AccountActivationMessanger accountActivationMessanger;
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.state(informationActivationService != null, "InformationActivationService must be provided");
        Assert.state(getAccountActivationMessanger() != null, "AccountActivationMessanger must be provided");
        Assert.state(viewNameUserRegistration != null, "ViewNameUserRegistration must be provided");
        Assert.state(this.getClientDetailsService() instanceof UserDetailsActivationService, "ClientDetailsService must be UserDetailsActivationService instance ");
    }

    protected InformationActivationService getInformationActivationService() {
        return informationActivationService;
    }

    public void setInformationActivationService(InformationActivationService informationActivationService) {
        this.informationActivationService = informationActivationService;
    }

    protected UserDetailsActivationService getUserDetailsActivationService() {
        return (UserDetailsActivationService) this.getClientDetailsService();
    }

    @RequestMapping(value = "/account/registration")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registration(@RequestParam Map<String, String> parameters, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultModel = new HashMap<>();

        //load data from request
        UserMessanger data = this.getUserMessanger().fromRequest(request);
        resultModel.put("UserMessangerInput", data);

        //register user
        if (!data.isUserDetailsEmpty()) {
            UserDetailsToken createdUser = data.getUserDetails();

            //check for userentity validation
            if (!this.getUserDetailsActivationService().getUserValidator().isValid(createdUser)) {
                throw this.getUserDetailsActivationService().getUserValidator().getValidtyException(createdUser);
            }

            //validate password
            if (!this.getUserDetailsActivationService().getPasswordValidator().isValid(createdUser.getPassword())) {
                throw this.getUserDetailsActivationService().getPasswordValidator().getValidtyException(createdUser.getPassword());
            }

            //check if user isnt exists
            if (this.getUserDetailsActivationService().exists(createdUser)) {
                throw new UserExists("Created user exists");
            }

            UserDetailsToken registredUser = this.getUserDetailsActivationService().registerUser(createdUser);

            //send activation information
            this.getUserDetailsActivationService().generateActivationData(registredUser);
            this.getInformationActivationService().sendConfirmationInformation(registredUser);

            //put registred user to view
            resultModel.put("UserMessangerRegistred", this.getUserMessanger().fromUserDetails(registredUser));

        }

        //new MappingJackson2HttpMessageConverter().write(resultModel, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
        //return null;
        //return response
        return new ModelAndView(this.getViewNameUserRegistration(), resultModel);
    }

    private String registerLink;

    public void setRegistrationLink(String registerLink) {
        this.registerLink = registerLink;
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
    public void setViewNameUserRegistration(String viewNameUserRegistration) {
        this.viewNameUserRegistration = viewNameUserRegistration;
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
    public void setAccountActivationMessanger(AccountActivationMessanger accountActivationMessanger) {
        this.accountActivationMessanger = accountActivationMessanger;
    }
}
