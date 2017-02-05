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
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class ActivationAccountEndpoint extends AbstractEndpoint {

    private InformationActivationService informationActivationService;
    private String viewNameUserActivation;
    private AccountActivationMessanger accountActivationMessanger;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.state(informationActivationService != null, "InformationActivationService must be provided");
        Assert.state(accountActivationMessanger != null, "AccountActivationMessanger must be provided");
        Assert.state(viewNameUserActivation != null, "ViewNameUserActivation must be provided");
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

    @RequestMapping(value = "/account/activate")
    @PreAuthorize("isAnonymous()")
    public ModelAndView activate(@RequestParam Map<String, String> parameters, HttpServletRequest request) throws Exception {
        Map<String, Object> resultModel = new HashMap<>();

        //load data from request
        AccountActivationMessanger data = this.getAccountActivationMessanger().fromRequest(request);
        resultModel.put("AccountActivationMessanger", data);

        //try to get user
        UserDetailsToken userDetails = this.getUserDetailsActivationService().loadUserByUsername(data.getUsername());
        resultModel.put("UserMessanger", this.getUserMessanger().fromUserDetails(userDetails));

        //ceck for correct token
        if (!this.getUserDetailsActivationService().canBeActivated(userDetails)) {
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

        //activate user
        this.getUserDetailsActivationService().activateUser(userDetails);

        //send information
        this.getInformationActivationService().sendSuccessfulInformation(userDetails);

        //return response
        return new ModelAndView(this.getViewNameUserActivation(), resultModel);
    }
    
    private String activationLink;

    public void setActivationLink(String activationLink) {
        this.activationLink = activationLink;
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
    public void setViewNameUserActivation(String viewNameUserActivation) {
        this.viewNameUserActivation = viewNameUserActivation;
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
