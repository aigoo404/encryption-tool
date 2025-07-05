package model;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class DSAUtil {
    
    private static final String ALGORITHM = "DSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withDSA";
    
    public static KeyPair genKey(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    public static String keyToBase64(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey base64ToPublicKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    public static PrivateKey base64ToPrivateKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }
 
    public static PublicKey loadPublicKey(String publicKeyString) throws Exception {
        String cleanKey = publicKeyString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }
    
    public static PrivateKey loadPrivateKey(String privateKeyString) throws Exception {
        String cleanKey = privateKeyString
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }
    
    public static String sign(String message, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
    
    public static String sign(String message, String privateKeyString) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyString);
        return sign(message, privateKey);
    }
    
    public static boolean verify(String message, String signatureString, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        
        byte[] signatureBytes = Base64.getDecoder().decode(signatureString);
        return signature.verify(signatureBytes);
    }
    
    public static boolean verify(String message, String signatureString, String publicKeyString) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyString);
        return verify(message, signatureString, publicKey);
    }
 
    public static String signFile(String filePath, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        signature.update(fileBytes);
        
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    public static String signFile(String filePath, String privateKeyString) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyString);
        return signFile(filePath, privateKey);
    }
    
    public static boolean verifyFile(String filePath, String signatureString, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        signature.update(fileBytes);
        
        byte[] signatureBytes = Base64.getDecoder().decode(signatureString);
        return signature.verify(signatureBytes);
    }

    public static boolean verifyFile(String filePath, String signatureString, String publicKeyString) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyString);
        return verifyFile(filePath, signatureString, publicKey);
    }

    public static String signRawHash(byte[] hashedMessage, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("NONEwithDSA");
        signature.initSign(privateKey);
        signature.update(hashedMessage);
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
    
    public static String signRawHash(byte[] hashedMessage, String privateKeyString) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyString);
        return signRawHash(hashedMessage, privateKey);
    }
    
    public static boolean verifyRawHash(byte[] hashedMessage, String signatureString, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("NONEwithDSA");
        signature.initVerify(publicKey);
        signature.update(hashedMessage);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureString);
        return signature.verify(signatureBytes);
    }
    
    public static boolean verifyRawHash(byte[] hashedMessage, String signatureString, String publicKeyString) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyString);
        return verifyRawHash(hashedMessage, signatureString, publicKey);
    }

    public static void savePublicKeyToFile(PublicKey publicKey, String filePath) throws IOException {
        String encodedKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("-----BEGIN PUBLIC KEY-----\n");
            for (int i = 0; i < encodedKey.length(); i += 64) {
                int end = Math.min(i + 64, encodedKey.length());
                writer.write(encodedKey.substring(i, end) + "\n");
            }
            writer.write("-----END PUBLIC KEY-----\n");
        }
    }
    
    public static void savePrivateKeyToFile(PrivateKey privateKey, String filePath) throws IOException {
        String encodedKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("-----BEGIN PRIVATE KEY-----\n");
            for (int i = 0; i < encodedKey.length(); i += 64) {
                int end = Math.min(i + 64, encodedKey.length());
                writer.write(encodedKey.substring(i, end) + "\n");
            }
            writer.write("-----END PRIVATE KEY-----\n");
        }
    }
    
    public static PublicKey loadPublicKeyFromFile(String filePath) throws Exception {
        String keyContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        return loadPublicKey(keyContent);
    }

    public static PrivateKey loadPrivateKeyFromFile(String filePath) throws Exception {
        String keyContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        return loadPrivateKey(keyContent);
    }
    
    public static void saveSignatureToFile(String signature, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(signature);
        }
    }
    
    public static String loadSignatureFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8).trim();
    }
}