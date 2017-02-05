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
package cz.cvut.springframework.security.common.jdbc;

import cz.cvut.springframework.security.common.token.ConfirmationToken;
import cz.cvut.springframework.security.common.token.ConfirmationTokenImpl;
import cz.cvut.springframework.security.common.validator.PasswordValidator;
import cz.cvut.springframework.security.common.validator.PasswordValidatorImpl;
import cz.cvut.springframework.security.common.validator.UserValidator;
import cz.cvut.springframework.security.common.validator.UserValidatorEmailBased;
import cz.cvut.springframework.security.service.UserDetailsToken;
import cz.cvut.springframework.security.service.activation.ActivateAccountException;
import cz.cvut.springframework.security.service.activation.UserDetailsActivationExpanded;
import cz.cvut.springframework.security.service.activation.UserDetailsActivationService;
import cz.cvut.springframework.security.service.password.AccountDisableException;
import cz.cvut.springframework.security.service.password.UserDetailsPasswordExpanded;
import cz.cvut.springframework.security.service.password.UserDetailsPasswordResetService;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.SerializationUtils;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class JdbcUserDetailsTokenValidityManager extends JdbcUserDetailsTokenManager implements UserDetailsPasswordResetService<UserDetailsPasswordExpanded>, UserDetailsActivationService<UserDetailsActivationExpanded> {

    public static final String DEF_USERS_BY_USERNAME_QUERY = "select username,password,enabled,token, token_create, active from users where username = ?";
    public static final String DEF_CREATE_USER_SQL = "insert into users (username, password, token, enabled, token_create, active) values (?,?,?,?,?,?)";
    public static final String DEF_UPDATE_USER_SQL = "update users set password = ?, enabled = ?, token = ?, token_create = ?, active = ? where username = ?";

    protected final Log logger = LogFactory.getLog(getClass());

    private String createUserSql = DEF_CREATE_USER_SQL;
    private String updateUserSql = DEF_UPDATE_USER_SQL;
    private String usersByUsernameQuery = DEF_USERS_BY_USERNAME_QUERY;

    private PasswordValidator passwordValidator;
    private UserValidator userValidator;

    public JdbcUserDetailsTokenValidityManager() {
        super();
        this.userValidator = new UserValidatorEmailBased();
    }

    public void createUser(final UserDetailsToken user) {
        validateUserDetails(user);
        getJdbcTemplate().update(createUserSql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, user.getUsername());
                ps.setString(2, getPasswordValidator().hashPassword(user.getPassword()));
                ps.setBytes(3, SerializationUtils.serialize(user.getToken()));
                ps.setBoolean(4, user.isEnabled());
                if (user instanceof UserDetailsPasswordExpanded) {
                    ps.setTimestamp(5, Timestamp.valueOf(((UserDetailsPasswordExpanded) user).getPasswordTokenValidity()));
                    ps.setBoolean(6, ((UserDetailsPasswordExpanded) user).isActive());
                } else {
                    ps.setTimestamp(5, null);
                    ps.setBoolean(6, false);
                }
            }

        });

        if (getEnableAuthorities()) {
            insertUserAuthorities(user);
        }
    }

    public void updateUser(final UserDetailsToken user) {
        validateUserDetails(user);
        getJdbcTemplate().update(updateUserSql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, user.getPassword());
                ps.setBoolean(2, user.isEnabled());
                ps.setBytes(3, SerializationUtils.serialize(user.getToken()));
                if (user instanceof UserDetailsPasswordExpanded) {
                    ps.setTimestamp(4, Timestamp.valueOf(((UserDetailsPasswordExpanded) user).getPasswordTokenValidity()));
                    ps.setBoolean(5, ((UserDetailsPasswordExpanded) user).isActive());
                } else {
                    ps.setTimestamp(4, null);
                    ps.setBoolean(5, false);
                }
                ps.setString(6, user.getUsername());
            }
        });

        if (getEnableAuthorities()) {
            deleteUserAuthorities(user.getUsername());
            insertUserAuthorities(user);
        }

        userCache.removeUserFromCache(user.getUsername());
    }

    /**
     * Executes the SQL <tt>usersByUsernameQuery</tt> and returns a list of
     * UserDetails objects. There should normally only be one matching user.
     *
     * @param username
     * @return
     */
    @Override
    protected List<UserDetails> loadUsersByUsername(String username) {
        return getJdbcTemplate().query(this.usersByUsernameQuery,
                new String[]{username}, new RowMapper<UserDetails>() {
            @Override
            public UserDetailsPasswordExpanded mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                String username = rs.getString(1);
                String password = rs.getString(2);
                boolean enabled = rs.getBoolean(3);
                byte[] tokenBytes = rs.getBytes(4);
                ConfirmationToken token = null;
                if (tokenBytes != null) {
                    token = (ConfirmationToken) SerializationUtils.deserialize(tokenBytes);
                }
                LocalDateTime tokenCreation = rs.getTimestamp(5).toLocalDateTime();
                boolean active = rs.getBoolean(6);
                return new UserEntity(username, password, token, tokenCreation, active, enabled, true, true, true,
                        AuthorityUtils.NO_AUTHORITIES);
            }

        });
    }

    @Override
    public boolean hasValidPasswordResetData(UserDetailsPasswordExpanded userDetails) {
        //check account state
        if (!userDetails.isEnabled()) {
            return false;
        }

        //check if has token
        if (!userDetails.hasToken() || userDetails.getToken().isEmpty()) {
            return false;
        }

        //time token validation
        if (userDetails.getPasswordTokenValidity().isAfter(LocalDateTime.now().minusDays(1))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void generatePasswordResetData(UserDetailsPasswordExpanded userDetails) throws AccountDisableException {
        if (!userDetails.isEnabled()) {
            throw new AccountDisableException(userDetails);
        }

        ConfirmationTokenImpl token = new ConfirmationTokenImpl();
        token.random();
        userDetails.setToken(token);
        userDetails.setPasswordTokenValidity(LocalDateTime.now());

        //save data
        this.updateUser(userDetails);
    }

    @Override
    public void changePassword(UserDetailsPasswordExpanded userDetails, String newPassword) throws AccountDisableException {
        if (!userDetails.isEnabled()) {
            throw new AccountDisableException(userDetails);
        }

        userDetails.setPassword(newPassword);
        userDetails.clearToken();

        //save data
        this.updateUser(userDetails);
    }

    @Override
    public boolean canBeActivated(UserDetailsActivationExpanded userDetails) {
        //check account state
        if (userDetails.isActive()) {
            return false;
        }

        //check if has token
        return userDetails.hasToken();
    }

    @Override
    public void generateActivationData(UserDetailsActivationExpanded userDetails) throws ActivateAccountException {
        if (userDetails.isActive()) {
            throw new ActivateAccountException(userDetails);
        }

        ConfirmationTokenImpl token = new ConfirmationTokenImpl();
        token.random();
        userDetails.setToken(token);

        //save data
        this.updateUser(userDetails);
    }

    @Override
    public void activateUser(UserDetailsActivationExpanded userDetails) throws ActivateAccountException {
        if (userDetails.isActive()) {
            throw new ActivateAccountException(userDetails);
        }

        //activate account
        userDetails.activate();
        userDetails.clearToken();

        //save data
        this.updateUser(userDetails);
    }

    @Override
    public PasswordValidator getPasswordValidator() {
        return this.passwordValidator;
    }

    @Override
    public UserValidator getUserValidator() {
        return this.userValidator;
    }

    @Override
    public UserDetailsToken registerUser(UserDetailsActivationExpanded createdUser) {
        this.createUser(createdUser);
        return this.loadUserByUsername(createdUser.getUsername());
    }

    /**
     * @param passwordValidator the passwordValidator to set
     */
    @Autowired
    public void setPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    /**
     * @param userValidator the userValidator to set
     */
    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    @Override
    public boolean exists(UserDetailsActivationExpanded createdUser) {
        if (this.loadUsersByUsername(createdUser.getUsername()).size() == 0) {
            return false;
        }
        return true;
    }

}
