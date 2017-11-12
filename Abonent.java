import java.math.BigInteger;
import java.util.Map;

public class Abonent implements EncryptDecryptAlghorithm{
    public BigInteger n;


    @Override
    public void generateKeyPair() {

    }

    @Override
    public Map<String, BigInteger> encrypt(BigInteger message) {
        return null;
    }

    @Override
    public BigInteger decrypt(Map<String, BigInteger> input) {
        return null;
    }

    @Override
    public Map<String, BigInteger> sign(BigInteger message) {
        return null;
    }

    @Override
    public boolean verify(Map<String, BigInteger> signedMessage) {
        return false;
    }
}
