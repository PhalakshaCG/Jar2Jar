package sample.SomeDeffiePacket;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class DHHandler {
    public DHHandler(){}
    KeyAgreement AkeyAgreement = null;
    public byte[] PublicKeyGenerator(){
        KeyPairGenerator AkeyPairGenerator = null;
        try {
            AkeyPairGenerator = KeyPairGenerator.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert AkeyPairGenerator != null;
        AkeyPairGenerator.initialize(512);
        KeyPair AkeyPair = AkeyPairGenerator.generateKeyPair();

        try {
            AkeyAgreement = KeyAgreement.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try{
            assert AkeyAgreement != null;
            AkeyAgreement.init(AkeyPair.getPrivate());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return AkeyPair.getPublic().getEncoded();
    }
    public byte[][] PublishGenPubKey(byte[] crypt){
        X509EncodedKeySpec x509EncodedKeySpec =new X509EncodedKeySpec(crypt);
        KeyFactory BKeyFactory = null;
        try {
            BKeyFactory = KeyFactory.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        PublicKey APublicKey = null;
        try {
            APublicKey = BKeyFactory.generatePublic(x509EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        DHParameterSpec dhParamFromAPubKey = ((DHPublicKey) APublicKey).getParams();
        KeyPairGenerator BKeyPairGen = null;
        try {
            BKeyPairGen = KeyPairGenerator.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            BKeyPairGen.initialize(dhParamFromAPubKey);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        KeyPair BKeyPair = BKeyPairGen.generateKeyPair();
        KeyAgreement BKeyAgree=null;
        try {
            BKeyAgree = KeyAgreement.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            BKeyAgree.init(BKeyPair.getPrivate());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            BKeyAgree.doPhase(APublicKey,true);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[][] ret = {BKeyPair.getPublic().getEncoded(),BKeyAgree.generateSecret()};
        return ret;
    }
    public byte[] GenerateSecretKey(byte[] PublicKeyEncrypted){
        KeyFactory AkeyFactory = null;
        try {
            AkeyFactory = KeyFactory.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(PublicKeyEncrypted);
        PublicKey BPublicKey = null;
        try {
            assert AkeyFactory != null;
            BPublicKey = AkeyFactory.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        try {
            AkeyAgreement.doPhase(BPublicKey, true);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] sharedSecret = AkeyAgreement.generateSecret();
        System.out.println(sharedSecret.toString());
        return sharedSecret;
    }
}
