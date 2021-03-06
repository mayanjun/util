/*
 * Copyright 2016-2018 mayanjun.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mayanjun.util;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;

/**
 * AES utility.
 * Note that this utility using AES/CBC/PKCS7Padding
 * @since 2019-08-20
 * @author mayanjun
 */
public class AES {

    private static final Logger LOG = LoggerFactory.getLogger(AES.class);

    private AES() {
    }


    /**
     * Encrypt data
     * @param content data
     * @param iv init vector
     * @param key secret key
     * @return encrypted data encoded in base64
     */
    public static byte[] encrypt(byte content[], byte iv[], byte key[]) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(content);
        } catch (Exception e) {
            LOG.error("AES encrypt error:", e);
        }
        return null;
    }

    /**
     * Decrypt data
     * @param content data
     * @param iv init vector
     * @param key secret key
     * @return decrypted data
     */
    public static byte[] decrypt(byte content[], byte iv[], byte key[]) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);// 初始化解密器
            return cipher.doFinal(content);
        } catch (Exception e) {
            LOG.error("AES decrypt error:", e);
        }
        return null;
    }

    /**
     * Encrypt data
     * @param content data string in base64
     * @param iv init vector in base64
     * @param key encoded key in base64
     * @return encrypted data encoded in base64
     */
    public static String encrypt(String content, String iv, String key) {
        return Base64.encodeBase64String(
                encrypt(Base64.decodeBase64(content), Base64.decodeBase64(iv), Base64.decodeBase64(key))
        );
    }

    /**
     * Decrypt data
     * @param content data string in base64
     * @param iv init vector in base64
     * @param key encoded key in base64
     * @return decrypted data bytes
     */
    public static byte[] decrypt(String content, String iv, String key) {
        return decrypt(Base64.decodeBase64(content), Base64.decodeBase64(iv), Base64.decodeBase64(key));
    }

    /**
     * Encrypt data
     * @param content data string in base64
     * @param iv init vector in base64
     * @param key secret key
     * @return encrypted data encoded in base64
     */
    public static String encrypt(String content, String iv, SecretKey key) {
        return Base64.encodeBase64String(
                encrypt(Base64.decodeBase64(content), Base64.decodeBase64(iv), key.getEncoded())
        );
    }

    /**
     * Decrypt data
     * @param content data string in base64
     * @param iv init vector in base64
     * @param key secret key
     * @return decrypted data bytes
     */
    public static byte[] decrypt(String content, String iv, SecretKey key) {
        return decrypt(Base64.decodeBase64(content), Base64.decodeBase64(iv), key.getEncoded());
    }

    /**
     * Encrypt data
     * @param content string data(plain)
     * @param iv init vector
     * @param key secret key
     * @return encrypted data encoded in base64
     */
    public static String encryptString(String content, byte iv[], SecretKey key) {
        try {
            return Base64.encodeBase64String(encrypt(content.getBytes("UTF-8"), iv, key.getEncoded()));
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    /**
     * Encrypt data
     * @param content string data in base64
     * @param iv init vector
     * @param key secret key
     * @return plain data
     */
    public static String decryptString(String content, byte iv[], SecretKey key) {
        try {
            byte bs[] = decrypt(Base64.decodeBase64(content), iv, key.getEncoded());
            if (bs == null) return null;

            return new String(bs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    /**
     * Encrypt data
     * @param content string data(plain)
     * @param iv init vector
     * @param key secret key
     * @return encrypted data encoded in base64
     */
    public static String encryptString(String content, byte iv[], byte key[]) {
        try {
            return Base64.encodeBase64String(encrypt(content.getBytes("UTF-8"), iv, key));
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    /**
     * Encrypt data
     * @param content string data in base64
     * @param iv init vector
     * @param key secret key
     * @return plain data
     */
    public static String decryptString(String content, byte iv[], byte key[]) {
        try {
            byte bs[] = decrypt(Base64.decodeBase64(content), iv, key);
            if (bs == null) return null;
            return new String(bs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    /**
     * Generate secret key
     * @param secretKey key string
     * @return secret key
     * @throws Exception throws internal exception
     */
    public static SecretKey generateKey(String secretKey) throws Exception {
        //防止linux下 随机生成key
        Provider p = Security.getProvider("SUN");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", p);
        secureRandom.setSeed(secretKey.getBytes());
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256, secureRandom);
        return kg.generateKey();
    }


    /**
     * Generate init vector
     * @return iv bytes
     */
    public static byte[] generateIV() {
        return Strings.random(16).getBytes();
    }
}
