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

import cz.cvut.springframework.security.common.messanger.UserMessanger;
import org.springframework.beans.factory.InitializingBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
abstract public class AbstractEndpoint implements InitializingBean {

    protected final Log logger = LogFactory.getLog(getClass());

    private UserDetailsService clientDetailsService;
    private UserMessanger userMessanger;

    @Override
    public void afterPropertiesSet() {
        Assert.state(clientDetailsService != null, "ClientDetailsService must be provided");
        Assert.state(userMessanger != null, "UserMessanger must be provided");
    }

    public UserDetailsService getClientDetailsService() {
        return clientDetailsService;
    }

    public void setClientDetailsService(UserDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
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
    public void setUserMessanger(UserMessanger userMessanger) {
        this.userMessanger = userMessanger;
    }
}
