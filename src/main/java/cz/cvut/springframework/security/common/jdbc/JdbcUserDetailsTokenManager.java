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
import cz.cvut.springframework.security.service.UserDetailsToken;
import cz.cvut.springframework.security.service.password.UserDetailsPasswordExpanded;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class JdbcUserDetailsTokenManager extends JdbcUserDetailsManager implements UserDetailsManager {

    public static final String DEF_USERS_BY_USERNAME_QUERY = "select username,password,enabled,token from users where username = ?";
    public static final String DEF_CREATE_USER_SQL = "insert into users (username, password, token, enabled) values (?,?,?,?)";
    public static final String DEF_UPDATE_USER_SQL = "update users set password = ?, enabled = ?, token = ? where username = ?";
    public static final String DEF_INSERT_AUTHORITY_SQL = "insert into authorities (username, authority) values (?,?)";
    public static final String DEF_DELETE_USER_AUTHORITIES_SQL = "delete from authorities where username = ?";

    protected final Log logger = LogFactory.getLog(getClass());

    private String createUserSql = DEF_CREATE_USER_SQL;
    private String updateUserSql = DEF_UPDATE_USER_SQL;
    private String createAuthoritySql = DEF_INSERT_AUTHORITY_SQL;
    private String deleteUserAuthoritiesSql = DEF_DELETE_USER_AUTHORITIES_SQL;
    private String usersByUsernameQuery = DEF_USERS_BY_USERNAME_QUERY;

    protected UserCache userCache = new NullUserCache();

    public void createUser(final UserDetailsToken user) {
        validateUserDetails(user);
        getJdbcTemplate().update(createUserSql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setBytes(3, SerializationUtils.serialize(user.getToken()));
                ps.setBoolean(4, user.isEnabled());
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
                ps.setString(4, user.getUsername());
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
            public UserDetailsToken mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                String username = rs.getString(1);
                String password = rs.getString(2);
                boolean enabled = rs.getBoolean(3);
                byte[] tokenBytes = rs.getBytes(4);
                ConfirmationToken token = null;
                if (tokenBytes != null) {
                    token = (ConfirmationToken) SerializationUtils.deserialize(tokenBytes);
                }
                return new UserEntity(username, password, token, null, true, enabled, true, true, true,
                        AuthorityUtils.NO_AUTHORITIES);
            }

        });
    }

    public UserDetailsToken loadUserByUsername(String username) {
        if (this.loadUsersByUsername(username).size() == 0) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return (UserDetailsToken) this.loadUsersByUsername(username).get(0);
    }

    protected void validateUserDetails(UserDetailsToken user) {
        Assert.hasText(user.getUsername(), "Username may not be empty or null");
        validateAuthorities(user.getAuthorities());
    }

    protected void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Authorities list must not be null");

        for (GrantedAuthority authority : authorities) {
            Assert.notNull(authority, "Authorities list contains a null entry");
            Assert.hasText(authority.getAuthority(),
                    "getAuthority() method must return a non-empty string");
        }
    }

    protected void insertUserAuthorities(UserDetailsToken user) {
        for (GrantedAuthority auth : user.getAuthorities()) {
            getJdbcTemplate().update(createAuthoritySql, user.getUsername(),
                    auth.getAuthority());
        }
    }

    protected void deleteUserAuthorities(String username) {
        getJdbcTemplate().update(deleteUserAuthoritiesSql, username);
    }
}
