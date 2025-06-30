package model;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class RSAUtil {
    
    private static final String ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    
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
    
    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    public static String encrypt(String plainText, String publicKeyString) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyString);
        return encrypt(plainText, publicKey);
    }
    
    public static String decrypt(String encryptedText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    public static String decrypt(String encryptedText, String privateKeyString) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyString);
        return decrypt(encryptedText, privateKey);
    }
 
    public static void encryptFile(String inputFilePath, String outputFilePath, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        byte[] inputBytes = Files.readAllBytes(Paths.get(inputFilePath));
        
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
        int keySize = rsaPublicKey.getModulus().bitLength();
        int maxChunkSize = (keySize / 8) - 11;
        
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            int offset = 0;
            while (offset < inputBytes.length) {
                int chunkSize = Math.min(maxChunkSize, inputBytes.length - offset);
                byte[] chunk = new byte[chunkSize];
                System.arraycopy(inputBytes, offset, chunk, 0, chunkSize);
                
                byte[] encryptedChunk = cipher.doFinal(chunk);
                fos.write(encryptedChunk);
                offset += chunkSize;
            }
        }
    }

    public static void encryptFile(String inputFilePath, String outputFilePath, String publicKeyString) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyString);
        encryptFile(inputFilePath, outputFilePath, publicKey);
    }
    
    public static void decryptFile(String inputFilePath, String outputFilePath, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        byte[] encryptedBytes = Files.readAllBytes(Paths.get(inputFilePath));
        
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
        int keySize = rsaPrivateKey.getModulus().bitLength();
        int chunkSize = keySize / 8; 
        
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            int offset = 0;
            while (offset < encryptedBytes.length) {
                byte[] chunk = new byte[chunkSize];
                System.arraycopy(encryptedBytes, offset, chunk, 0, chunkSize);
                
                byte[] decryptedChunk = cipher.doFinal(chunk);
                fos.write(decryptedChunk);
                offset += chunkSize;
            }
        }
    }

    public static void decryptFile(String inputFilePath, String outputFilePath, String privateKeyString) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyString);
        decryptFile(inputFilePath, outputFilePath, privateKey);
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

    public static void encryptFileInPlace(String filePath, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        byte[] inputBytes = Files.readAllBytes(Paths.get(filePath));
        
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
        int keySize = rsaPublicKey.getModulus().bitLength();
        int maxChunkSize = (keySize / 8) - 11;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int offset = 0;
        while (offset < inputBytes.length) {
            int chunkSize = Math.min(maxChunkSize, inputBytes.length - offset);
            byte[] chunk = new byte[chunkSize];
            System.arraycopy(inputBytes, offset, chunk, 0, chunkSize);
            
            byte[] encryptedChunk = cipher.doFinal(chunk);
            baos.write(encryptedChunk);
            offset += chunkSize;
        }
        
        Files.write(Paths.get(filePath), baos.toByteArray());
    }

    public static void encryptFileInPlace(String filePath, String publicKeyString) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyString);
        encryptFileInPlace(filePath, publicKey);
    }
    
    public static void decryptFileInPlace(String filePath, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        byte[] encryptedBytes = Files.readAllBytes(Paths.get(filePath));
        
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
        int keySize = rsaPrivateKey.getModulus().bitLength();
        int chunkSize = keySize / 8;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int offset = 0;
        while (offset < encryptedBytes.length) {
            byte[] chunk = new byte[chunkSize];
            System.arraycopy(encryptedBytes, offset, chunk, 0, chunkSize);
            
            byte[] decryptedChunk = cipher.doFinal(chunk);
            baos.write(decryptedChunk);
            offset += chunkSize;
        }
        
        Files.write(Paths.get(filePath), baos.toByteArray());
    }

    public static void decryptFileInPlace(String filePath, String privateKeyString) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyString);
        decryptFileInPlace(filePath, privateKey);
    }
}