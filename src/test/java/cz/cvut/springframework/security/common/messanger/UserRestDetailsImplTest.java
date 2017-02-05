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
package cz.cvut.springframework.security.common.messanger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.springframework.security.common.jdbc.UserEntity;
import cz.cvut.springframework.security.common.token.ConfirmationTokenImpl;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class UserRestDetailsImplTest {

    protected static final String TEST_USER = "{\"email\":\"test@test.com\"}";
    protected static final String TEST_PASS_USER = "{\"email\":\"test@test.com\",\"password\":\"pass\"}";
    protected UserEntity testUserEntity;

    protected ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        testUserEntity = new UserEntity("test@test.com", "xx", new ConfirmationTokenImpl(), LocalDateTime.now(), true, true, true, true, true, AuthorityUtils.NO_AUTHORITIES);
    }

    @Test
    public void testJsonDeserializer() throws JsonGenerationException, JsonMappingException, IOException {
        UserRestDetailsImpl parsed = mapper.readValue(TEST_USER, UserRestDetailsImpl.class);
        UserRestDetailsImpl created = new UserRestDetailsImpl();
        created = (UserRestDetailsImpl) created.fromUserDetails(testUserEntity);
        assertEquals(parsed.getEmail(), created.getEmail());
    }

    @Test
    public void testJsonSerializer() throws JsonGenerationException, JsonMappingException, IOException {
        UserRestDetailsImpl forSerialization = new UserRestDetailsImpl();
        forSerialization = (UserRestDetailsImpl) forSerialization.fromUserDetails(testUserEntity);

        String serializedJsonString = mapper.writeValueAsString(forSerialization);

        assertEquals(TEST_USER, serializedJsonString);
    }
    
    @Test
    public void testJsonPasswordSerializer() throws JsonGenerationException, JsonMappingException, IOException {
        UserEntity spyUserEntity = Mockito.spy(testUserEntity);
        UserRestDetailsImpl forSerialization = new UserRestDetailsImpl();
        forSerialization = (UserRestDetailsImpl) forSerialization.fromUserDetails(spyUserEntity);

        String serializedJsonString = mapper.writeValueAsString(forSerialization);

        Mockito.verify(spyUserEntity,Mockito.times(0)).getPassword();
    }
}
