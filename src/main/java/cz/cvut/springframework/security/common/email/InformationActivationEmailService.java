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
package cz.cvut.springframework.security.common.email;

import cz.cvut.springframework.security.common.messanger.AccountActivationMessanger;
import cz.cvut.springframework.security.endpoint.ActivationAccountEndpoint;
import cz.cvut.springframework.security.service.activation.InformationActivationService;
import cz.cvut.springframework.security.service.activation.UserDetailsActivationExpanded;
import java.lang.reflect.Method;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class InformationActivationEmailService implements InformationActivationService<UserDetailsActivationExpanded> {

    private JavaMailSender mailSender;

    private String fromEmailAdress;
    private AccountActivationMessanger accountActivationMessanger;

    public InformationActivationEmailService(JavaMailSender mailSender, AccountActivationMessanger accountActivationMessanger) {
        this(mailSender, accountActivationMessanger, null);
    }

    public InformationActivationEmailService(JavaMailSender mailSender, AccountActivationMessanger accountActivationMessanger, String fromEmailAdress) {
        this.fromEmailAdress = fromEmailAdress;
        this.accountActivationMessanger = accountActivationMessanger;
        this.mailSender = mailSender;
    }

    @Override
    public void sendConfirmationInformation(UserDetailsActivationExpanded userDetails) throws Exception {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(userDetails.getUsername());
        mailMessage.setSubject("Activation email");
        if (this.fromEmailAdress != null) {
            mailMessage.setFrom(this.fromEmailAdress);
        }
        
        Link link = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ActivationAccountEndpoint.class).activate(this.accountActivationMessanger.createActivationLinkMap(userDetails), null)).withSelfRel();
                        
        mailMessage.setText("Hello,\n\n"
                + "To finish activating your account - please visit " + link.getHref() + " \n\n"
                + "Regards,\nthe Team.");
        this.mailSender.send(mailMessage);
    }

    @Override
    public void sendSuccessfulInformation(UserDetailsActivationExpanded userDetails) {

    }
}
