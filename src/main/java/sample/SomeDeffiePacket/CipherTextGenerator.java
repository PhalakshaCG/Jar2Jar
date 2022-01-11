package sample.SomeDeffiePacket;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CipherTextGenerator {
    public byte[][] encode(String message,byte[] secret) throws IOException {
        SecretKeySpec keySpec =new SecretKeySpec(secret, 0, 16, "AES");
        Cipher cipher=null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        }  catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] messageByte = message.getBytes();
        byte[] ciphertext = new byte[0];
        try {
            ciphertext = cipher.doFinal(messageByte);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        byte[][] ret = {ciphertext,cipher.getParameters().getEncoded()};
        return ret;
    }
    public byte[] decode(byte[][] crypt,byte[] secret){
        SecretKeySpec aesKey = new SecretKeySpec(secret, 0, 16, "AES");
        byte[] encodedParams = crypt[1];
        AlgorithmParameters aesParams = null;
        try {
            aesParams = AlgorithmParameters.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            aesParams.init(encodedParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Cipher aliceCipher = null;
        try {
            aliceCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            aliceCipher.init(Cipher.DECRYPT_MODE, aesKey,aesParams);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] recovered=null;
        try {
            recovered = aliceCipher.doFinal(crypt[0]);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return recovered;
    }

}
