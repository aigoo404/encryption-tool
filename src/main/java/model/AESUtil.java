package model;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtil {

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int AES_BLOCK_SIZE = 16;

    public static String genKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(keySize);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static String genIV(String mode) {
        SecureRandom random = new SecureRandom();
        byte[] iv;

        if ("GCM".equalsIgnoreCase(mode)) {
            iv = new byte[GCM_IV_LENGTH];
        } else {
            iv = new byte[AES_BLOCK_SIZE];
        }

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
                if (inKeySection && !line.isEmpty()) {
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
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        if ("ECB".equalsIgnoreCase(mode) || mode == null || mode.isEmpty() || "none".equalsIgnoreCase(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        } else if ("GCM".equalsIgnoreCase(mode)) {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
        } else {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
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
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        if ("ECB".equalsIgnoreCase(mode) || mode == null || mode.isEmpty() || "none".equalsIgnoreCase(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        } else if ("GCM".equalsIgnoreCase(mode)) {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);
        } else {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
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
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        if ("ECB".equalsIgnoreCase(mode) || mode == null || mode.isEmpty() || "none".equalsIgnoreCase(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        } else if ("GCM".equalsIgnoreCase(mode)) {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
        } else {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        }

        try (FileInputStream fis = new FileInputStream(inputFilePath);
                FileOutputStream fos = new FileOutputStream(outputFilePath);
                CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

            byte[] buffer = new byte[8192];
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
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        if ("ECB".equalsIgnoreCase(mode) || mode == null || mode.isEmpty() || "none".equalsIgnoreCase(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        } else if ("GCM".equalsIgnoreCase(mode)) {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);
        } else {
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        }

        try (FileInputStream fis = new FileInputStream(inputFilePath);
                CipherInputStream cis = new CipherInputStream(fis, cipher);
                FileOutputStream fos = new FileOutputStream(outputFilePath)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void saveKey(String key, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("-----BEGIN AES KEY-----\n");

            String keyContent = key.replaceAll("\\s+", "");

            for (int i = 0; i < keyContent.length(); i += 64) {
                int endIndex = Math.min(i + 64, keyContent.length());
                writer.write(keyContent.substring(i, endIndex) + "\n");
            }

            writer.write("-----END AES KEY-----\n");
        }
    }

    public static void saveKeyToFile(String key, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("-----BEGIN AES KEY-----\n");

            String keyContent = key.replaceAll("\\s+", "");

            for (int i = 0; i < keyContent.length(); i += 64) {
                int endIndex = Math.min(i + 64, keyContent.length());
                writer.write(keyContent.substring(i, endIndex) + "\n");
            }

            writer.write("-----END AES KEY-----\n");
        }
    }

    public static void saveIV(String iv, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(iv);
        }
    }

    public static void saveIVToFile(String iv, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write(iv);
        }
    }

    public static void encryptFileInPlace(String filePath, String secretKey, String algorithm, String mode,
            String padding, String iv) throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKey), algorithm);
        String transformation = algorithm + "/" + mode + "/" + padding;
        Cipher cipher = Cipher.getInstance(transformation);

        if ("ECB".equals(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        } else {
            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        }
        byte[] encryptedContent = cipher.doFinal(fileContent);
        Files.write(Paths.get(filePath), encryptedContent);
    }

    public static void decryptFileInPlace(String filePath, String secretKey, String algorithm, String mode,
            String padding, String iv) throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKey), algorithm);
        String transformation = algorithm + "/" + mode + "/" + padding;
        Cipher cipher = Cipher.getInstance(transformation);

        if ("ECB".equals(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
        } else {
            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        }
        byte[] decryptedContent = cipher.doFinal(fileContent);
        Files.write(Paths.get(filePath), decryptedContent);
    }
}