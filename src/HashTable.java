import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * 
 * @author Chris Bui
 *
 */
public class HashTable {
	
	private ArrayList<LinkedList<Tuple>> table;
	public HashFunction hashFunction;
	private int numOfElements;
	/**
	 * Finds the smallest prime integer p whose value is at least size. Creates
	 * a hash table of size p where each cell initially is NULL. It will determine the hash function to be
     * used in the hash table by creating the object new HashFunction(p).
	 * @param size
	 */
	public HashTable(int size) {
	    numOfElements = 0;
		table = new ArrayList<LinkedList<Tuple>>();
		
		int p = getPrime(size);
		hashFunction = new HashFunction(p); 
		for(int i = 0; i < p; i++) {
			LinkedList<Tuple> tmp = new LinkedList<Tuple>();
			table.add(tmp);
		}
	}
	
	/**
	 * @return the maximum load of the hash table
	 */
	public int maxLoad() {
		int max = 0;
		for (int i = 0; i < table.size(); i++) {
			if (table.get(i) != null) {
				int numOfTuples = 0;
				for (ListIterator<Tuple> iter = table.get(i).listIterator(); iter.hasNext();) {
					iter.next();
					numOfTuples++;
				}
				if (numOfTuples > max) max = numOfTuples;
			}
		}
		return max;
	}
	
	/**
	 * @return the average load of the hash table
	 */
	public float averageLoad() {
		int nonEmptyBuckets = 0;
		for (int i = 0; i < table.size(); i++) {
			if (table.get(i) != null) {
				if (table.get(i).size() >= 1) {
					nonEmptyBuckets++;
				}
			}
		}
		return (nonEmptyBuckets == 0) ? 0 : (float) numOfElements / nonEmptyBuckets;
	}
	
	/**
	 * @return the current size of the hash table.
	 */
	public int size() {
		return table.size();
	}
	
	/**
	 * @return the number of Tuples that are currently stored in the hash table.
	 */
	public int numElements() {
		return numOfElements;
	}
	
	/**
	 * filled/length ex. total spots = 10, fill 7, loadFactor = 7/10 
	 * @return the load factor which is numElements()/size()
	 */
	public float loadFactor() {
		return (float) numElements() / size();
	}
	
	/**
	 * Adds the tuple t to the hash table; places t in the list pointed by the cell h(t.getKey())
	 * where h is the hash function that is being used. When the load factors becomes bigger than 0.7,2
	 * then it (approximately) doubles the size of the hash table and rehashes all the elements (tuples) to
 	 * the new hash table. The size of the new hash table must be: Smallest prime integer whose value is
	 * at least twice the current size.
	 * @param t tuple 
	 */
	public void add(Tuple t) {
		int hashIndex = hashFunction.hash(t.getKey());
		// Head of linked list at hash(index)
		LinkedList<Tuple> list = table.get(hashIndex);
		
		// traverse table and check each bucket  if the key exist return
		if(list != null) {
			for (ListIterator<Tuple> iter = list.listIterator(); iter.hasNext();) {
				Tuple tuple = iter.next();
				if(t.equals(tuple)) return;
			}
		}
		list = table.get(hashIndex);
		// else insert key in chain
		table.get(hashIndex).add(t);
		numOfElements++;
		
		// double size
		if (loadFactor() > 0.7) {
			int newSize = getPrime(size() * 2);
			hashFunction = new HashFunction(newSize);
			numOfElements = 0;
			
			ArrayList<LinkedList<Tuple>> tmp = table;
			table = new ArrayList<>();
			
			for(int i = 0; i < newSize; i++) {
				LinkedList<Tuple> tmpList = new LinkedList<Tuple>();
				table.add(tmpList);
			}

			for (LinkedList<Tuple> oldLinkedList : tmp) {
				for (ListIterator<Tuple> iter = oldLinkedList.listIterator(); iter.hasNext();) {
					Tuple tuple = iter.next();
					add(tuple);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param k
	 * @return an array list of Tuples (in the hash table) whose key equals k. If no such
	 * Tuples exist, returns an empty list.
	 */
	public ArrayList<Tuple> search(int k) {
		ArrayList<Tuple> tupleList = new ArrayList<Tuple>();

		int index = hashFunction.hash(k);
		if (table.get(index) != null) {
			for (ListIterator<Tuple> iter = table.get(index).listIterator(); iter.hasNext();) {
				Tuple tuple = iter.next();
				if (tuple.getKey() == k) tupleList.add(tuple);
			}
		}
		return tupleList;
	}
	
	/**
	 * Removes the Tuple t from the hash table
	 * @param t
	 */
	public boolean remove(Tuple t) {
		int hashIndex = hashFunction.hash(t.getKey());
		
		if (table.get(hashIndex) != null) {
			for (ListIterator<Tuple> iter = table.get(hashIndex).listIterator(); iter.hasNext();) {
				Tuple tuple = iter.next();
				if (tuple.equals(t)) {
					iter.remove();
					numOfElements--;
					return true;
				}
			}
		}
		return false;
	}
	
	// Helper Methods
	public static void printTable(HashTable hashTable) {
		ArrayList<LinkedList<Tuple>> table = hashTable.table;
		for (int i = 0; i < table.size(); i++) {
			if(table.get(i) != null) {
				System.out.println("Bucket " + i);
				for (ListIterator<Tuple> iter = table.get(i).listIterator(); iter.hasNext();) {
					Tuple tuple = iter.next();
					System.out.println("key: "+tuple.getKey()+" value: "+tuple.getValue() + " | ");
				}
				System.out.println("End Bucket "+ i);
				System.out.println();
			}
		}
	}
	
	private static int getPrime(int n) {
		int found = 1;
		while (found == 1) {
			found = isPrime(n);
			if(found == 1) n++;
		}
		return n;
	}
	
	private static int isPrime(int n) {
		if (n % 2 == 0) return 1;
		
		for (int i = 3; i * i <= n; i += 2) {
			if (n % i == 0) return 1;
		}
		return 0;
	}
	
}
