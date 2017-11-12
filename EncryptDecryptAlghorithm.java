import java.math.BigInteger;
import java.util.Map;

interface EncryptDecryptAlghorithm {
    BigInteger openKey = null;
    void generateKeyPair();

    Map<String, BigInteger> encrypt(BigInteger message);

    BigInteger decrypt(Map<String, BigInteger> input);

    Map<String, BigInteger> sign(BigInteger message);

    boolean verify(Map<String, BigInteger> signedMessage);
}
