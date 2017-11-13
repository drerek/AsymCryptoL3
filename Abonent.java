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

    private int iversonSymbol(int yakoby){
        if (yakoby == 1) return 1;
        else return 0;
    }

    private BigInteger prepareMessage(BigInteger message){
        int l = n.bitLength()/8;
        if (message.bitLength()/8+1 > l-10) throw new IllegalArgumentException();
        BigInteger r = new BigInteger(64,new Random());

        return BigInteger.valueOf(255).multiply(BigInteger.valueOf(2).pow(8*(l-8)))
                .add(message.multiply(BigInteger.valueOf(2).pow(64))).add(r);
    }
    public BigInteger getN(){
        return n;
    }

    public BigInteger getB(){
        return b;
    }

    @Override
    public void generateKeyPair() {
        p = BigInteger.valueOf(4).multiply(new BigInteger(256,new Random())).add(BigInteger.valueOf(3));
        q = BigInteger.valueOf(4).multiply(new BigInteger(256,new Random())).add(BigInteger.valueOf(3));
        b = (new BigInteger(256,new Random()));
     //   p = BigInteger.valueOf(4).multiply(BigInteger.valueOf(Integer.MAX_VALUE)).add(BigInteger.valueOf(3));
      //  q = BigInteger.valueOf(4).multiply(BigInteger.valueOf(Integer.MAX_VALUE-100)).add(BigInteger.valueOf(3));
        n = p.multiply(q);
    }

    private BigInteger getC1(Abonent abonent, BigInteger message){
        return message.add(abonent.getB().divide(BigInteger.valueOf(2))).mod(abonent.getN()).mod(BigInteger.valueOf(2));
    }

    private BigInteger getC2(Abonent abonent, BigInteger message){
        return BigInteger.valueOf(yakobiSymbol(message.add(abonent.getB().divide(BigInteger.valueOf(2))),abonent.getN()));

    }

    @Override
    public Map<String, BigInteger> encrypt(Abonent destAbon, BigInteger message) {
        BigInteger preparedMessage = prepareMessage(message);
        BigInteger y = preparedMessage.multiply(message.add(destAbon.getB())).mod(destAbon.getN());
        BigInteger c1 = getC1(destAbon,preparedMessage);
        BigInteger c2 = getC2(destAbon, preparedMessage);
        return new HashMap<String,BigInteger>(){{
            put("cypherText",y);
            put("c1",c1);
            put("c2",c2);
        }};
    }

    @Override
    public BigInteger decrypt(Abonent fromAbon, Map<String, BigInteger> input) {
        BigInteger root = input.get("cypherText").add(b.pow(2).divide(BigInteger.valueOf(4)));

        BigInteger temp1 = root.modPow(p.add(BigInteger.ONE).divide(BigInteger.valueOf(4)),p);
        BigInteger temp2 = root.modPow(q.add(BigInteger.ONE).divide(BigInteger.valueOf(4)),q);
        BigInteger[] uv = GCD(p,q);

        BigInteger u = uv[0];
        BigInteger v = uv[1];
        System.out.println("u*q+v*p="+u.multiply(q).add(v.multiply(p)));


        BigInteger preparedMessage = (b.divide(BigInteger.valueOf(2)).negate()).add(u.multiply(p).multiply(temp1)).mod(n);
        if (getC1(this,preparedMessage).compareTo(input.get("c1"))==0
                && getC2(this,preparedMessage).compareTo(input.get("c2"))==0)
            return preparedMessage;

        preparedMessage=(b.divide(BigInteger.valueOf(2)).negate()).add(n.subtract(u.multiply(p).multiply(temp1))).mod(n);
        if (getC1(this,preparedMessage).compareTo(input.get("c1"))==0
                && getC2(this,preparedMessage).compareTo(input.get("c2"))==0)
            return preparedMessage;

        preparedMessage = (b.divide(BigInteger.valueOf(2)).negate()).add(v.multiply(q).multiply(temp2)).mod(n);
        if (getC1(this,preparedMessage).compareTo(input.get("c1"))==0
                && getC2(this,preparedMessage).compareTo(input.get("c2"))==0)
            return preparedMessage;

        preparedMessage = (b.divide(BigInteger.valueOf(2)).negate()).add(n.subtract(v.multiply(q).multiply(temp2))).mod(n);
        if (getC1(this,preparedMessage).compareTo(input.get("c1"))==0
                && getC2(this,preparedMessage).compareTo(input.get("c2"))==0)
            return preparedMessage;

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
        while (yakobiSymbol(preparedMessage,p) != 1 || yakobiSymbol(preparedMessage,q) != 1){
            preparedMessage = prepareMessage(message);
        }
        BigInteger temp1 = preparedMessage.modPow(p.add(BigInteger.ONE).divide(BigInteger.valueOf(4)),p);
        BigInteger temp2 = preparedMessage.modPow(q.add(BigInteger.ONE).divide(BigInteger.valueOf(4)),q);

        BigInteger finalPreparedMessage = preparedMessage;
        return new HashMap<String,BigInteger>(){{
            put("sign",GCD(p,q)[0].multiply(p).multiply(temp1));
            put("message", finalPreparedMessage);
        }};
    }

    @Override
    public boolean verify(Abonent fromAbon, Map<String, BigInteger> signedMessage) {
        return signedMessage.get("sign").pow(2).mod(fromAbon.getN()).compareTo(signedMessage.get("message")) == 0;
    }


    public static void main(String[] args) {
    Abonent a = new Abonent();
        Abonent b = new Abonent();
        a.generateKeyPair();
        b.generateKeyPair();
        System.out.println(b.decrypt(b,a.encrypt(b,BigInteger.valueOf(128))));
    }
}
