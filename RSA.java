import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSA {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES";

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] encryptWithAES(byte[] data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptWithAES(byte[] encryptedData, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }

    public static byte[] encryptKeyWithRSA(SecretKey secretKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(secretKey.getEncoded());
    }

    public static SecretKey decryptKeyWithRSA(byte[] encryptedKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] keyBytes = cipher.doFinal(encryptedKey);
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    public static void saveToFile(String filename, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(data);
        }
    }

    public static byte[] readFromFile(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            return fis.readAllBytes();
        }
    }

    public void encrypt() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            KeyPair keyPair = generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            saveToFile("data/publicKey.key", publicKey.getEncoded());
            saveToFile("data/privateKey.key", privateKey.getEncoded());

            String dbFilename = "data/planets.db";
            byte[] dbData = readFromFile(dbFilename);

            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();

            byte[] encryptedData = encryptWithAES(dbData, secretKey);
            byte[] encryptedKey = encryptKeyWithRSA(secretKey, publicKey);

            saveToFile("data/planets_encrypted_RSAAES.db", encryptedData);
            saveToFile("data/secretKey_encrypted_RSAAES.key", encryptedKey);

            System.out.println("Processo concluído: dados criptografados com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decrypt() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            byte[] privateKeyBytes = readFromFile("data/privateKey.key");
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            byte[] encryptedKeyFromFile = readFromFile("data/secretKey_encrypted_RSAAES.key");
            SecretKey decryptedKey = decryptKeyWithRSA(encryptedKeyFromFile, privateKey);

            byte[] encryptedData = readFromFile("data/planets_encrypted_RSAAES.db");
            byte[] decryptedData = decryptWithAES(encryptedData, decryptedKey);

            saveToFile("data/planets_decrypted_RSAAES.db", decryptedData);

            System.out.println("Processo concluído: dados descriptografados com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
