package model;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class digitalSignature {

    private static final String DEFAULT_SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String RAW_SIGNATURE_ALGORITHM = "NONEwithRSA";

    public static String signRawHash(byte[] hashedMessage, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(RAW_SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(hashedMessage);
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    public static String signRawHash(byte[] hashedMessage, String privateKeyString) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyString);
        return signRawHash(hashedMessage, privateKey);
    }

    public static String sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(DEFAULT_SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    public static String sign(byte[] data, String privateKeyString) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyString);
        return sign(data, privateKey);
    }

    public static boolean verifyRawHash(byte[] hashedMessage, String signatureString, PublicKey publicKey)
            throws Exception {
        Signature signature = Signature.getInstance(RAW_SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(hashedMessage);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureString);
        return signature.verify(signatureBytes);
    }

    public static boolean verifyRawHash(byte[] hashedMessage, String signatureString, String publicKeyString)
            throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyString);
        return verifyRawHash(hashedMessage, signatureString, publicKey);
    }

    public static boolean verify(byte[] data, String signatureString, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(DEFAULT_SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureString);
        return signature.verify(signatureBytes);
    }

    public static boolean verify(byte[] data, String signatureString, String publicKeyString) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyString);
        return verify(data, signatureString, publicKey);
    }

    public static String signFile(String filePath, String privateKeyString, boolean isAlreadyHashed) throws Exception {
        byte[] fileData = Files.readAllBytes(Paths.get(filePath));
        if (isAlreadyHashed) {

            return signRawHash(fileData, privateKeyString);
        } else {
            return sign(fileData, privateKeyString);
        }
    }

    public static boolean verifyFile(String filePath, String signatureString, String publicKeyString,
            boolean isAlreadyHashed) throws Exception {
        byte[] fileData = Files.readAllBytes(Paths.get(filePath));
        if (isAlreadyHashed) {
            return verifyRawHash(fileData, signatureString, publicKeyString);
        } else {
            return verify(fileData, signatureString, publicKeyString);
        }
    }

    public static String signFile(String hashedFilePath, PrivateKey privateKey) throws Exception {
        byte[] hashedData = Files.readAllBytes(Paths.get(hashedFilePath));
        return signRawHash(hashedData, privateKey);
    }

    public static String signFile(String hashedFilePath, String privateKeyString) throws Exception {
        byte[] hashedData = Files.readAllBytes(Paths.get(hashedFilePath));
        return signRawHash(hashedData, privateKeyString);
    }

    public static boolean verifyFile(String hashedFilePath, String signatureString, PublicKey publicKey)
            throws Exception {
        byte[] hashedData = Files.readAllBytes(Paths.get(hashedFilePath));
        return verifyRawHash(hashedData, signatureString, publicKey);
    }

    public static boolean verifyFile(String hashedFilePath, String signatureString, String publicKeyString)
            throws Exception {
        byte[] hashedData = Files.readAllBytes(Paths.get(hashedFilePath));
        return verifyRawHash(hashedData, signatureString, publicKeyString);
    }

    public static void saveSignatureToFile(String signature, String filePath) throws Exception {
        Files.write(Paths.get(filePath), signature.getBytes(StandardCharsets.UTF_8));
    }

    public static String loadSignatureFromFile(String filePath) throws Exception {
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8).trim();
    }

    private static PrivateKey loadPrivateKey(String keyString) throws Exception {
        String cleanKey = keyString.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private static PublicKey loadPublicKey(String keyString) throws Exception {
        String cleanKey = keyString.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}