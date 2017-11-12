import java.math.BigInteger;
import java.util.Map;

public class Abonent implements EncryptDecryptAlghorithm{
    public BigInteger n;

    private int yakobiSymbol(BigInteger a, BigInteger b){
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
            return r.multiply(BigInteger.valueOf(yakobiSymbol(a,b))).intValue();
        }
        return r.intValue();
    }

    private int iversonSymbol(int yakoby){
        if (yakoby == 1) return 1;
        else return 0;
    }

    public BigInteger getN(){
        return n;
    }

    @Override
    public void generateKeyPair() {

    }

    @Override
    public Map<String, BigInteger> encrypt(Abonent destAbon, BigInteger message) {
        return null;
    }

    @Override
    public BigInteger decrypt(Abonent fromAbon, Map<String, BigInteger> input) {
        return null;
    }

    @Override
    public Map<String, BigInteger> sign(BigInteger message) {
        return null;
    }

    @Override
    public boolean verify(Abonent fromAbon, Map<String, BigInteger> signedMessage) {
        return signedMessage.get("sign").pow(2).mod(fromAbon.getN()).compareTo(signedMessage.get("message")) == 0;
    }

}
