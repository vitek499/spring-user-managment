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

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Vit Stekly <stekly.vit@vs-point.cz>
 * @link http://www.vs-point.cz
 */
public class ConfirmationTokenImpl implements ConfirmationToken{

    private static final long serialVersionUID = -9223372036854775808L;
    
    private static final int KEY_BYTE_SIZE = 30;
    
    private static volatile SecureRandom numberGenerator = null;
    
    private byte[] data;
    
    public ConfirmationTokenImpl()
    {
        this.data = null;
    }
    
    public void fromByteArray(byte[] data)
    {
        assert data.length == KEY_BYTE_SIZE;
        this.data=data;
    }
    
    public void random() {
        SecureRandom ng = numberGenerator;
        if (ng == null) {
            numberGenerator = ng = new SecureRandom();
        }

        this.data = new byte[KEY_BYTE_SIZE];
        ng.nextBytes(this.data);
    }
    
    public void fromString(String value) throws ParseException {
        byte[] parsedValue = Base64.decodeBase64(value);
        if(parsedValue.length!=KEY_BYTE_SIZE)
        {
            throw new ParseException(value, ((int)( KEY_BYTE_SIZE / 0.75f )) +1 );
        }
        this.fromByteArray(parsedValue);
    }
    
    @Override
    public String toString()
    {
        return Base64.encodeBase64URLSafeString(this.data);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Arrays.hashCode(this.data);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConfirmationTokenImpl other = (ConfirmationTokenImpl) obj;
        if (!Arrays.equals(this.data, other.data)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        if(this.data==null) {
            return true;
        }
        return false;
    }

}
