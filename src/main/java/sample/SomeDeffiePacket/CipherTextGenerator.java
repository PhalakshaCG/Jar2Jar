package sample.SomeDeffiePacket;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CipherTextGenerator {
    public String encodeParams,decodeParams;
    public String getEncodedString(String message,byte[] secret){
        byte[][] array=null;
        try {
            array = encode(message,secret);
        } catch (IOException e) {
            e.printStackTrace();
        }
        encodeParams = bytetoString(array[1]);
        return new String(array[0],StandardCharsets.ISO_8859_1);

    }
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
        byte[] messageByte = message.getBytes(StandardCharsets.ISO_8859_1);
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
    public String decode(String cipherText,byte[] secret){
        byte[][] crypt = new byte[][]{cipherText.getBytes(StandardCharsets.ISO_8859_1),StringtoByte(decodeParams)};
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
            return new String(recovered,StandardCharsets.ISO_8859_1);
    }
    public String bytetoString(byte[] byteArray){

        String hex = "";
        // Iterating through each byte in the array
        for (byte i : byteArray) {
            hex += String.format("%02X", i);
        }
        return (hex);

    }
    public byte[] StringtoByte(String s){
        byte[] ans = new byte[s.length() / 2];
        //System.out.println("Hex String : "+s);
        for (int i = 0; i < ans.length; i++) {
            int index = i * 2;
            // Using parseInt() method of Integer class
            int val = Integer.parseInt(s.substring(index, index + 2), 16);
            ans[i] = (byte)val;
        }

        // Printing the required Byte Array
        System.out.print("Byte Array : ");
        for (int i = 0; i < ans.length; i++) {
            System.out.print(ans[i] + " ");
        }
        return ans;
    }

}
