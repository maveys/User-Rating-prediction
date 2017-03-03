import java.util.Random;

/**
 * 
 * @author Chris Bui
 *
 */
public class HashFunction {

	private int a;
	private int b;
	private int p;
	
	/**
	 * Picks the first (positive) prime integer whose value is at least range,
	 * and sets the value of p to that prime integer. Then picks two random integers x and y from
	 * {0, 1, 砷 砬 p - 1} and sets a as x and b as y.
	 * @param range
	 */
	public HashFunction(int range) {
		// set p as the first prime integer whose value is at least range
		this.p = getPrime(range);
		// a & b are random between {0,1,...,p-1}
		Random rand = new Random();
		// (max - min + 1) + min => ((p - 1) - 0 + 1) + 0 => p
		int x = rand.nextInt(p);
		int y = rand.nextInt(p);
		
		setA(x);
		setB(y);
	}
	
	/**
	 * 
	 * @param x
	 * @return s the value of the hash function on x; i.e, returns (ax + b)%p.
	 */
	public int hash(int x) {
		long tmp =  ( ((long) a * (long) x) + b ) % p;
		
		return (int) tmp;
	}
	
	/**
	 * @return a
	 */
	public int getA() {
		return a;
	}
	
	/**
	 * @return b
	 */
	public int getB() {
		return b;
	}
	
	/**
	 * @return p
	 */
	public int getP() {
		return p;
	}
	
	/**
	 * change the value of a to x%p.
	 * @param x
	 */
	public void setA(int x) {
		a = x % p;
	}
	
	/**
	 * change the value of b to y%p.
	 * @param y
	 */
	public void setB(int y) {
		b = y % p;
	}
	
	/**
	 * This method will pick the first (positive)
	 * prime whose value is at least x and sets the value of p to that integer.
	 * @param x
	 */
	public void setP(int x) {
		this.p = getPrime(x);
		Random rand = new Random();
		int x1 = rand.nextInt(p);
		int y = rand.nextInt(p);
		
		setA(x1);
		setB(y);
	}
	
	/**
	 * TODO
	 * @param n
	 * @return 
	 */
	private static int getPrime(int n) {
		int found = 1;
		while (found == 1) {
			found = isPrime(n);
			if(found == 1) n++;
		}
		return n;
	}
	
	/**
	 * @param n
	 * @return 1 if prime 0 if not prime
	 */
	private static int isPrime(int n) {
		if (n == 2) return 0;
		if (n % 2 == 0) return 1;
		
		for (int i = 3; i * i <= n; i += 2) {
			if (n % i == 0) return 1;
		}
		return 0;
	}
}
