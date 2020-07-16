package de.pauerbanane.core.addons.votifier.crypto;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAIO {
    public static void save(File directory, KeyPair keyPair) throws Exception {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey.getEncoded());
        PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        FileOutputStream publicOut = new FileOutputStream(directory + "/public.key");
        try {
            FileOutputStream privateOut = new FileOutputStream(directory + "/private.key");
            try {
                publicOut.write(Base64.getEncoder().encode(publicSpec.getEncoded()));
                privateOut.write(Base64.getEncoder().encode(privateSpec.getEncoded()));
                privateOut.close();
            } catch (Throwable throwable) {
                try {
                    privateOut.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            }
            publicOut.close();
        } catch (Throwable throwable) {
            try {
                publicOut.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }
            throw throwable;
        }
    }

    public static KeyPair load(File directory) throws Exception {
        File publicKeyFile = new File(directory + "/public.key");
        byte[] encodedPublicKey = Files.readAllBytes(publicKeyFile.toPath());
        encodedPublicKey = Base64.getDecoder().decode(new String(encodedPublicKey, StandardCharsets.UTF_8));
        File privateKeyFile = new File(directory + "/private.key");
        byte[] encodedPrivateKey = Files.readAllBytes(privateKeyFile.toPath());
        encodedPrivateKey = Base64.getDecoder().decode(new String(encodedPrivateKey, StandardCharsets.UTF_8));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return new KeyPair(publicKey, privateKey);
    }
}