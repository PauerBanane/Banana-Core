package de.pauerbanane.core.addons.votifier.crypto;

import de.pauerbanane.core.addons.votifier.Votifier;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;

public class RSAKeygen {

    public static KeyPair generate(int bits) throws Exception {
        Votifier.LOG.info("Votifier is generating an RSA key pair...");
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(bits, RSAKeyGenParameterSpec.F4);
        keygen.initialize(spec);
        return keygen.generateKeyPair();
    }

}
