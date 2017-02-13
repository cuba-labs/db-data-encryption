Sensitive data encryption demo
==============================

Key files:

1. com.company.demo.entity.CreditCard - entity that contains two properties: secretValue and decryptedValue. The first is stored to DB, the second is used on UI.
2. com.company.demo.listener.CreditCardEntityListener - implements automatic encryption/decryption of data


Note: CreditCardEntityListener uses constant secret key, but in the real world you have to keep your secret key in a reliable source, not in your DB and it cannot be constant.