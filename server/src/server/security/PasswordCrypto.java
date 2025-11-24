package server.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

public final class PasswordCrypto {
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final int KEY_LENGTH_BYTES = 32; // 256 bits

    private PasswordCrypto() {}

    public static String encryptPassword(String password) throws GeneralSecurityException {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Senha vazia não pode ser criptografada.");
        }

        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = buildKey(passwordBytes);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(passwordBytes);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private static byte[] buildKey(byte[] source) {
        byte[] key = new byte[KEY_LENGTH_BYTES];
        for (int i = 0; i < key.length; i++) {
            key[i] = source[i % source.length];
        }
        return key;
    }
}
