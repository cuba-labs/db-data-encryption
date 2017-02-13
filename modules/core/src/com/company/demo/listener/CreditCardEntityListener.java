package com.company.demo.listener;

import com.company.demo.entity.CreditCard;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.listener.BeforeAttachEntityListener;
import com.haulmont.cuba.core.listener.BeforeDetachEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Component("demo_CreditCardEntityListener")
public class CreditCardEntityListener implements BeforeDetachEntityListener<CreditCard>,
        BeforeInsertEntityListener<CreditCard>,
        BeforeAttachEntityListener<CreditCard> {

    private final SecretKey key;

    public CreditCardEntityListener() {
        DESKeySpec dks;
        try {
            // you need to keep this value in secret, do not store it in a database, do not use constant
            dks = new DESKeySpec("squirrel123".getBytes());
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            key = skf.generateSecret(dks);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Unable to init key");
        }
    }

    @Override
    public void onBeforeInsert(CreditCard entity, EntityManager entityManager) {
        encryptSensitiveData(entity);
    }

    @Override
    public void onBeforeDetach(CreditCard entity, EntityManager entityManager) {
        decryptSensitiveData(entity);
    }

    @Override
    public void onBeforeAttach(CreditCard entity) {
        encryptSensitiveData(entity);
    }

    private void decryptSensitiveData(CreditCard entity) {
        Cipher desCipher = getCipher();
        try {
            desCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Unable to init key");
        }

        if (PersistenceHelper.isLoaded(entity, "secretValue") && entity.getSecretValue() != null) {
            try {
                byte[] textEncrypted = Base64Utils.decodeFromString(entity.getSecretValue());
                entity.setDecryptedValue(new String(desCipher.doFinal(textEncrypted), StandardCharsets.UTF_8));
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("Unable to perform encryption");

            }
        }
    }

    private void encryptSensitiveData(CreditCard entity) {
        Cipher desCipher = getCipher();
        try {
            desCipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Unable to init key");
        }

        if (PersistenceHelper.isLoaded(entity, "secretValue") || PersistenceHelper.isNew(entity)) {
            try {
                byte[] textEncrypted = desCipher.doFinal(entity.getDecryptedValue().getBytes(StandardCharsets.UTF_8));
                entity.setSecretValue(Base64Utils.encodeToString(textEncrypted));
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("Unable to perform encryption");
            }
        }
    }

    private Cipher getCipher() {
        Cipher desCipher;
        try {
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Unable to init encryption");
        }
        return desCipher;
    }
}