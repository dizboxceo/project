package kr.dizbox.util;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 코드유틸
 * @author dizbox
 *
 */
public class CodeUtil {
	
	private static final String KEY = "encryption!@0429";
	private static final String ENC_TYPE = "AES";
	private static final String IV_VAL = "0987654321654321";
	private static final String CIPHER_VAL = "AES/CBC/PKCS5Padding";
	private static final String CHAR_SET = "UTF-8";
	private static final String DATE_FORMAT = "yyyyMMddHHmmss";
	

	private static Key getAESKey() throws Exception {
        Key keySpec = null;
        byte[] keyBytes = new byte[16];
        byte[] b = KEY.getBytes(CHAR_SET);
        int len = b.length;
        if (len > keyBytes.length) {
           len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        keySpec = new SecretKeySpec(keyBytes, ENC_TYPE);
        return keySpec;
    }
 
    public static String encrypt(String str) throws Exception {
        Key keySpec = getAESKey();
        Cipher c = Cipher.getInstance(CIPHER_VAL);
        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(IV_VAL.getBytes(CHAR_SET)));
        byte[] encrypted = c.doFinal(str.getBytes(CHAR_SET));
        String enStr = new String(Base64.getEncoder().encode(encrypted));
        return enStr;
    }
 
    public static String decrypt(String enStr) throws Exception {
        Key keySpec = getAESKey();
        Cipher c = Cipher.getInstance(CIPHER_VAL);
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(IV_VAL.getBytes(CHAR_SET)));
        byte[] byteStr = Base64.getDecoder().decode(enStr.getBytes(CHAR_SET));
        String decStr = new String(c.doFinal(byteStr), CHAR_SET);
        return decStr;
    }
    
    public static String genUid() {
    	String uid = null;
    	int start = (int)(Math.random()*27);
    	String key = UUID.randomUUID().toString().replace("-", "").substring(start, start+6);
    	uid = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)).concat(key);
    	return uid;
    }

	
}
