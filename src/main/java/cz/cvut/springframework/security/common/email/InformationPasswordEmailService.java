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

import cz.cvut.springframework.security.common.messanger.PasswordMessanger;
import cz.cvut.springframework.security.endpoint.ActivationAccountEndpoint;
import cz.cvut.springframework.security.endpoint.ResetPasswordEndpoint;
import cz.cvut.springframework.security.service.password.InformationPasswordService;
import cz.cvut.springframework.security.service.password.UserDetailsPasswordExpanded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class InformationPasswordEmailService implements InformationPasswordService<UserDetailsPasswordExpanded> {

    private JavaMailSender mailSender;

    private String fromEmailAdress;
    private PasswordMessanger passwordMessanger;

    public InformationPasswordEmailService(JavaMailSender mailSender, PasswordMessanger passwordMessanger) {
        this(mailSender, passwordMessanger, null);
    }

    public InformationPasswordEmailService(JavaMailSender mailSender, PasswordMessanger passwordMessanger, String fromEmailAdress) {
        this.fromEmailAdress = fromEmailAdress;
        this.passwordMessanger = passwordMessanger;
        this.mailSender = mailSender;
    }

    @Override
    public void sendResetPasswordData(UserDetailsPasswordExpanded userDetails) throws Exception {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(userDetails.getUsername());
        mailMessage.setSubject("Reset Password");
        if (this.fromEmailAdress != null) {
            mailMessage.setFrom(this.fromEmailAdress);
        }

        Link link = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ResetPasswordEndpoint.class).resetPassword(this.passwordMessanger.createResetPasswordLinkMap(userDetails), null)).withSelfRel();

        mailMessage.setText(
                "Hello,\n\n"
                + "To reset your password - please visit " + link.getHref()
                + " \n\n"
                + "Regards,\nthe Team.");

        this.mailSender.send(mailMessage);
    }

}
