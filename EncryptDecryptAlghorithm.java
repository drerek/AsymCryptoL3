import java.math.BigInteger;
import java.util.Map;

interface EncryptDecryptAlghorithm {
    BigInteger openKey = null;
    void generateKeyPair();

    Map<String, BigInteger> encrypt(Abonent destAbon, BigInteger message);

    BigInteger decrypt(Abonent fromAbon, Map<String, BigInteger> input);

    Map<String, BigInteger> sign(BigInteger message);

    boolean verify(Abonent fromAbon, Map<String, BigInteger> signedMessage);
}
