package model;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class threeDESUtil {

    private static final int TRIPLE_DES_BLOCK_SIZE = 8;
    private static final String ALGORITHM = "DESede";

    public static String genKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(168);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static String genIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[TRIPLE_DES_BLOCK_SIZE];
        random.nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }

    public static String loadKey(String filePath) throws IOException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filePath));
        String content = new String(keyBytes, StandardCharsets.UTF_8).trim();

        if (content.contains("-----BEGIN") && content.contains("-----END")) {
            String[] lines = content.split("\n");
            StringBuilder keyContent = new StringBuilder();
            boolean inKeySection = false;

            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("-----BEGIN")) {
                    inKeySection = true;
                    continue;
                }
                if (line.startsWith("-----END")) {
                    break;
                }
                if (inKeySection) {
                    keyContent.append(line);
                }
            }
            return keyContent.toString();
        }

        return content;
    }

    public static String loadIV(String filePath) throws IOException {
        byte[] ivBytes = Files.readAllBytes(Paths.get(filePath));
        return new String(ivBytes, StandardCharsets.UTF_8).trim();
    }

    public static String encrypt(String plaintext, String secretKey, String algorithm,
            String mode, String padding, String iv) throws Exception {

        String transformation = algorithm;
        if (mode != null && !mode.isEmpty() && !"none".equalsIgnoreCase(mode)) {
            transformation += "/" + mode;
            if (padding != null && !padding.isEmpty()) {
                transformation += "/" + padding;
            }
        }

        Cipher cipher = Cipher.getInstance(transformation);

        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

        if (iv != null && !iv.isEmpty()) {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        }

        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String ciphertext, String secretKey, String algorithm,
            String mode, String padding, String iv) throws Exception {

        String transformation = algorithm;
        if (mode != null && !mode.isEmpty() && !"none".equalsIgnoreCase(mode)) {
            transformation += "/" + mode;
            if (padding != null && !padding.isEmpty()) {
                transformation += "/" + padding;
            }
        }

        Cipher cipher = Cipher.getInstance(transformation);

        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

        if (iv != null && !iv.isEmpty()) {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
        }

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void encryptFile(String inputFilePath, String outputFilePath, String secretKey,
            String algorithm, String mode, String padding, String iv) throws Exception {

        String transformation = algorithm;
        if (mode != null && !mode.isEmpty() && !"none".equalsIgnoreCase(mode)) {
            transformation += "/" + mode;
            if (padding != null && !padding.isEmpty()) {
                transformation += "/" + padding;
            }
        }

        Cipher cipher = Cipher.getInstance(transformation);

        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

        if (iv != null && !iv.isEmpty()) {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        }

        try (FileInputStream fis = new FileInputStream(inputFilePath);
                FileOutputStream fos = new FileOutputStream(outputFilePath);
                CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void decryptFile(String inputFilePath, String outputFilePath, String secretKey,
            String algorithm, String mode, String padding, String iv) throws Exception {

        String transformation = algorithm;
        if (mode != null && !mode.isEmpty() && !"none".equalsIgnoreCase(mode)) {
            transformation += "/" + mode;
            if (padding != null && !padding.isEmpty()) {
                transformation += "/" + padding;
            }
        }

        Cipher cipher = Cipher.getInstance(transformation);

        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

        if (iv != null && !iv.isEmpty()) {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
        }

        try (FileInputStream fis = new FileInputStream(inputFilePath);
                CipherInputStream cis = new CipherInputStream(fis, cipher);
                FileOutputStream fos = new FileOutputStream(outputFilePath)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void saveKey(String key, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("-----BEGIN 3DES KEY-----\n");
            writer.write(key);
            writer.write("\n-----END 3DES KEY-----\n");
        }
    }

    public static void saveIV(String iv, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(iv);
        }
    }

    public static void encryptFileInPlace(String filePath, String secretKey, String algorithm,
            String mode, String padding, String iv) throws Exception {

        String tempFilePath = filePath + ".tmp";
        encryptFile(filePath, tempFilePath, secretKey, algorithm, mode, padding, iv);

        File originalFile = new File(filePath);
        File tempFile = new File(tempFilePath);

        if (!originalFile.delete()) {
            tempFile.delete();
            throw new IOException("Failed to delete original file");
        }

        if (!tempFile.renameTo(originalFile)) {
            throw new IOException("Failed to rename temporary file");
        }
    }

    public static void decryptFileInPlace(String filePath, String secretKey, String algorithm,
            String mode, String padding, String iv) throws Exception {

        String tempFilePath = filePath + ".tmp";
        decryptFile(filePath, tempFilePath, secretKey, algorithm, mode, padding, iv);

        File originalFile = new File(filePath);
        File tempFile = new File(tempFilePath);

        if (!originalFile.delete()) {
            tempFile.delete();
            throw new IOException("Failed to delete original file");
        }

        if (!tempFile.renameTo(originalFile)) {
            throw new IOException("Failed to rename temporary file");
        }
    }
}