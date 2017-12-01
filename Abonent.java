import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Abonent implements EncryptDecryptAlghorithm{
    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger b;

    private static int yakobiSymbol(BigInteger a, BigInteger b){
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

    private BigInteger prepareMessage(BigInteger message){
        int l = n.bitLength()/8;
        if (message.bitLength()/8+1 > l-10) throw new IllegalArgumentException();
        BigInteger r = new BigInteger(64,new Random());
        System.out.println("r " + r.toString(16));

        return BigInteger.valueOf(255).multiply(BigInteger.valueOf(2).pow(8*(l-2)))
                .add(message.multiply(BigInteger.valueOf(2).pow(64))).add(r);
    }
    public BigInteger getN(){
        return n;
    }

    public BigInteger getB(){
        return b;
    }

    public BigInteger generateNumber(){
        BigInteger number = BigInteger.ZERO;
        while(true){
            number = BigInteger.probablePrime(256, new Random());
            if ((number.subtract(BigInteger.valueOf(3))).mod(BigInteger.valueOf(4)).compareTo(BigInteger.ZERO) == 0) break;
        }
        return number;

    }
    @Override
    public void generateKeyPair() {
        p = generateNumber();
        q = generateNumber();
        b = new BigInteger(200, new Random());
        n = p.multiply(q);
    }
    @Override
    public String toString(){
        return "p="+this.p+" q="+ this.q + " b="+this.b+ " n="+this.n;
    }

    private BigInteger getC1(Abonent abonent, BigInteger message){
        return message.add(abonent.getB().multiply(BigInteger.valueOf(2).modInverse(abonent.getN()))).mod(abonent.getN()).mod(BigInteger.valueOf(2));
    }

    private BigInteger getC2(Abonent abonent, BigInteger message){
        return BigInteger.valueOf(yakobiSymbol(message.add(abonent.getB().multiply(BigInteger.valueOf(2).modInverse(abonent.getN()))),abonent.getN()));

    }

    @Override
    public Map<String, BigInteger> encrypt(Abonent destAbon, BigInteger message) {
        BigInteger preparedMessage = prepareMessage(message);
      //  BigInteger preparedMessage = message;
        System.out.println("preparedMessage"+preparedMessage);
        BigInteger y = (preparedMessage.multiply(preparedMessage.add(destAbon.getB()))).mod(destAbon.getN());
        BigInteger c1 = getC1(destAbon, preparedMessage);
        BigInteger c2 = getC2(destAbon, preparedMessage);
        return new HashMap<String,BigInteger>(){{
            put("cypherText",y);
            put("c1",c1);
            put("c2",c2);
        }};
    }

    @Override
    public BigInteger decrypt(Abonent fromAbon, Map<String, BigInteger> input) {

        BigInteger root = input.get("cypherText").add(b.multiply(b).multiply(BigInteger.valueOf(4).modInverse(fromAbon.getN()))).mod(n);

        BigInteger temp1 = root.modPow((p.add(BigInteger.ONE)).divide(BigInteger.valueOf(4)),p);

        BigInteger temp2 = root.negate().modPow((q.add(BigInteger.ONE)).divide(BigInteger.valueOf(4)),q);


        BigInteger[] uv = GCD(p,q);

        BigInteger u = uv[1];
        BigInteger v = uv[0];
      //  System.out.println("u*p+v*q="+u.multiply(p).add(v.multiply(q)))

            BigInteger x1 = u.multiply(p).multiply(temp2).add(v.multiply(q).multiply(temp1)).mod(n);
        BigInteger preparedMessage = (b.multiply(BigInteger.valueOf(2).modInverse(n)).negate()).add(x1).mod(n);
        if (input.get("c1") == getC1(this,preparedMessage) && input.get("c2")== getC2(this,preparedMessage)) return preparedMessage;
            BigInteger x2 = u.multiply(p).multiply(temp2).subtract(v.multiply(q).multiply(temp1)).mod(n);
        preparedMessage = (b.multiply(BigInteger.valueOf(2).modInverse(n)).negate()).add(x2).mod(n);
        if (input.get("c1") == getC1(this,preparedMessage) && input.get("c2")== getC2(this,preparedMessage)) return preparedMessage;
            BigInteger x3 = (u.multiply(p).multiply(temp2).negate()).add(v.multiply(q).multiply(temp1)).mod(n);
        preparedMessage = (b.multiply(BigInteger.valueOf(2).modInverse(n)).negate()).add(x3).mod(n);
        if (input.get("c1") == getC1(this,preparedMessage) && input.get("c2")== getC2(this,preparedMessage)) return preparedMessage;
            BigInteger x4 = (u.multiply(p).multiply(temp2).negate()).subtract(v.multiply(q).multiply(temp1)).mod(n);
        preparedMessage = (b.multiply(BigInteger.valueOf(2).modInverse(n)).negate()).add(x4).mod(n);
        if (input.get("c1") == getC1(this,preparedMessage) && input.get("c2")== getC2(this,preparedMessage)) return preparedMessage;

        return null;
    }

    private BigInteger[] GCD(BigInteger a, BigInteger b){
        boolean flag = false;
        if (a.compareTo(b)==-1){
            flag = true;
            BigInteger c = a;
            a = b;
            b = c;
        }
        if (b.compareTo(BigInteger.ZERO) == 0) {
            return new BigInteger[]{BigInteger.ONE,BigInteger.ZERO};
        }
        BigInteger x2 = BigInteger.ONE;
        BigInteger x1 = BigInteger.ZERO;
        BigInteger y2 = BigInteger.ZERO;
        BigInteger y1 = BigInteger.ONE;
        while (b.compareTo(BigInteger.ZERO) == 1){
            BigInteger q = a.divide(b);
            BigInteger r = a.subtract(q.multiply(b));
            BigInteger x = x2.subtract(q.multiply(x1));
            BigInteger y = y2.subtract(q.multiply(y1));

            a=b;
            b=r;
            x2 = x1;
            x1 = x;
            y2 = y1;
            y1 =y;
        }
        if (flag) return new BigInteger[]{x2,y2};
        return new BigInteger[]{y2,x2};
    }

    @Override
    public Map<String, BigInteger> sign(BigInteger message) {
        BigInteger preparedMessage = prepareMessage(message);
        while (yakobiSymbol(preparedMessage,p) != 1 && yakobiSymbol(preparedMessage,q) != 1){
            this.generateKeyPair();
            preparedMessage = prepareMessage(message);
        }
        BigInteger temp1 = preparedMessage.modPow(p.add(BigInteger.ONE).divide(BigInteger.valueOf(4)),p);
        BigInteger temp2 = preparedMessage.modPow(q.add(BigInteger.ONE).divide(BigInteger.valueOf(4)),q);

        BigInteger finalPreparedMessage = preparedMessage;
        return new HashMap<String,BigInteger>(){{
            put("sign",GCD(p,q)[0].multiply(p).multiply(temp1).mod(n));
            put("message", finalPreparedMessage);
        }};
    }

    @Override
    public boolean verify(Abonent fromAbon, Map<String, BigInteger> signedMessage) {
        return signedMessage.get("sign").pow(2).mod(fromAbon.getN()).compareTo(signedMessage.get("message")) == 0;
    }


    public static void main(String[] args) {
        Abonent a = new Abonent();

        a.generateKeyPair();
        System.out.println(a.decrypt(a,a.encrypt(a,BigInteger.valueOf(4410))));


    }
}
