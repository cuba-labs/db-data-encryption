package com.company.demo.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Transient;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("demo_CreditCardEntityListener")
@NamePattern("%s|owner")
@Table(name = "DEMO_CREDIT_CARD")
@Entity(name = "demo$CreditCard")
public class CreditCard extends StandardEntity {
    private static final long serialVersionUID = -9143099673457840048L;

    @Lob
    @Column(name = "SECRET_VALUE", nullable = false)
    protected String secretValue;

    @Transient
    @MetaProperty(mandatory = true)
    protected String decryptedValue;

    @Column(name = "OWNER", nullable = false)
    protected String owner;

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setSecretValue(String secretValue) {
        this.secretValue = secretValue;
    }

    public String getSecretValue() {
        return secretValue;
    }

    public void setDecryptedValue(String decryptedValue) {
        this.decryptedValue = decryptedValue;
    }

    public String getDecryptedValue() {
        return decryptedValue;
    }
}