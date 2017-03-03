
/**
 * 
 * @author Chris Bui
 *
 */
public class Tuple {
	
	private int key;
	private float value;
	
	/**
	 * Creates Tuple object with keyP as key and valueP as value.
	 * @param keyP
	 * @param valueP
	 */
	public Tuple(int keyP, float valueP) {
		this.key = keyP;
		this.value = valueP;
	}
	
	/**
	 * @return key
	 */
	public int getKey() {
		return key;
	}
	
	/**
	 * @return value
	 */
	public float getValue() {
		return value;
	}
	
	/**
	 * @param t
	 * @return true if this tuple equals t; otherwise returns false.
	 */
	public boolean equals(Tuple t) {
		if(this.key == t.key && this.value == t.value) return true;
		return false;
	}
}
