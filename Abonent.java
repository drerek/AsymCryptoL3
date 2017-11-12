import java.math.BigInteger;
import java.util.Map;

public class Abonent implements EncryptDecryptAlghorithm{
    public BigInteger n;

    private int yakobi(BigInteger a, BigInteger b){
        if (b.compareTo(BigInteger.ONE) <= 0 || b.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) == 0 )
            throw new IllegalArgumentException();
        //1. Verification of mutual simplicity
        if (a.gcd(b).compareTo(BigInteger.ONE) != 0) {
            return 0;
        }
        //2. Initialization
        BigInteger r = BigInteger.ONE;
        //3. go to positive numbers
        if (a.compareTo(BigInteger.ZERO) == -1) {
            a=a.negate();
            if (b.mod(BigInteger.valueOf(4)).compareTo(BigInteger.valueOf(3))== 0) r=r.negate();
        }
        //4. (getting rid of parity). t: = 0
        BigInteger t = BigInteger.ZERO;
        while (a.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) == 0){
            t = t.add(BigInteger.ONE);
            a = a.divide(BigInteger.valueOf(2));
        }
        if (t.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) != 0){
            if (b.mod(BigInteger.valueOf(8)).compareTo(BigInteger.valueOf(3)) == 0 ||
                    b.mod(BigInteger.valueOf(8)).compareTo(BigInteger.valueOf(5)) == 0) r=r.negate();
        }
        //5. quadratic reciprocity law
        if (a.mod(BigInteger.valueOf(4)).compareTo(BigInteger.valueOf(3)) == 0 &&
                b.mod(BigInteger.valueOf(4)).compareTo(BigInteger.valueOf(3)) == 0) {
            r = r.negate();
        }
        BigInteger c = a;
        a = b.mod(c);
        b = c;

        //6. Exit from algorithm
        if (a.compareTo(BigInteger.ZERO) != 0) {
            return r.multiply(BigInteger.valueOf(yakobi(a,b))).intValue();
        }
        return r.intValue();
    }

    public static void main(String[] args) {
        Abonent a = new Abonent();
        System.out.println(a.yakobi(BigInteger.valueOf(219), BigInteger.valueOf(383)));
    }
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
