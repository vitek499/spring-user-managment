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
package cz.cvut.springframework.security.common.token;

import java.text.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 */
public class ConfirmationTokenImplTest {

    /**
     * Test of random method, of class ConfirmationTokenImpl.
     */
    @Test
    public void testRandom() {
        System.out.println("random");
        ConfirmationTokenImpl expResult = new ConfirmationTokenImpl();
        ConfirmationTokenImpl result = new ConfirmationTokenImpl();
        expResult.random();
        result.random();
        assertNotEquals(expResult, result);
    }

    @Test
    public void testParse() throws ParseException {
        System.out.println("parse");
        String expResult = "K820k2tlTZwWoBHniicKN58NefyCZkSIAU2G2auB";
        ConfirmationTokenImpl result = new ConfirmationTokenImpl();
        result.fromString(expResult);
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testEquals() throws ParseException {
        System.out.println("equals");
        String value = "K820k2tlTZwWoBHniicKN58NefyCZkSIAU2G2auB";
        ConfirmationTokenImpl expResult = new ConfirmationTokenImpl();
        ConfirmationTokenImpl result = new ConfirmationTokenImpl();
        result.fromString("K820k2tlTZwWoBHniicKN58NefyCZkSIAU2G2auB");
        expResult.fromString("K820k2tlTZwWoBHniicKN58NefyCZkSIAU2G2auB");
        assertTrue(result.equals(expResult));
    }

    @Test
    public void testIsEmpty() throws ParseException {
        ConfirmationTokenImpl token = new ConfirmationTokenImpl();
        token.fromString("K820k2tlTZwWoBHniicKN58NefyCZkSIAU2G2auB");
        assertFalse(token.isEmpty());
    }
}
