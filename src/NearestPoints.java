import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Chris Bui
 *
 */
public class NearestPoints {

	public static final float EPSILON = 0.00001f;
	private ArrayList<Float> set;
	private HashTable table;
	/**
	 * 
	 * @param dataFile s the absolute path of the file 	that contains the set of points S
	 * @throws FileNotFoundException 
	 */
	public NearestPoints(String dataFile){
		
		set = new ArrayList<Float>();
		
		File file = new File(dataFile);
		Scanner scan;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			return ;
		}
		
		while (scan.hasNextLine()) {
			float tmp = Float.parseFloat(scan.nextLine()); //convert line in file to float value
			set.add(tmp); //add to Array List
		}
		
		scan.close();
		
		buildDataStructure();
	}
	
	/**
	 * 
	 * @param pointSet contains the set of points S
	 */
	public NearestPoints(ArrayList<Float> pointSet) {	
		set = pointSet;
		buildDataStructure();
	}
	
	/**
	 * This method must implement the naive approach. Note that the type of this method must be ArrayList<float>
	 * 
	 * We say that two points/numbers p and q are close if abs(p - q) <= 1
	 * @param p
	 * @return array list of points (from the set S) that are close to p
	 */
	public ArrayList<Float> naiveNearestPoints(float p) {
		ArrayList<Float> nearest = new ArrayList<Float>();
		
		for(int i=0;i<set.size();i++) {
			
			if(Math.abs(set.get(i)- p) <= 1) { //check for nearest points
				nearest.add(set.get(i));
			}
		}
		
		return nearest;
	}
	
	/**
	 * Builds the data structure that enables to quickly answer nearest point
	 * queries. Your data structure must use the notion of neighbor preserving hashing and along with
	 * the class HashTable.
	 */
	public void buildDataStructure() {
		table = new HashTable( (int) (set.size() * 1.5));
		
		// add tuple (g(p), p) at h(g(p))
		for (int i = 0; i < set.size(); i++) {	
			float f = set.get(i);
			Tuple tuple = new Tuple( (int) Math.floor(f), f);
			table.add(tuple);
		}
		
		//table.printTable(table);
	}
	
	/**
	 * This method must use the data structure that was built. The expected run time of this method
	 * must be O(N(p));
	 * @param p
	 * @return  array list of points (from the S) that are close to p.
	 */
	public ArrayList<Float> npHashNearestPoints(float p) {
		int pointLowerBound = (((int) Math.floor(p) - 1) < 0) ? 0 : (int) Math.floor(p) - 1; // O(1)
		int pointUpperBound = (int) Math.floor(p) + 1; // O(1)

		ArrayList<Tuple> tupleList = table.search(pointLowerBound); // O(1)
		tupleList.addAll(table.search(pointUpperBound)); // O(1)
		// Since lower bound can be 0, if floor(p) is zero as well we don't want to search twice
		if(p >= 1) {
			tupleList.addAll(table.search((int) Math.floor(p))); // O(1)
		}
		ArrayList<Float> nearestPoints = new ArrayList<Float>();  // O(1)
		for (int i = 0; i < tupleList.size(); i++) { // O(N(p))
			float value = Math.abs(tupleList.get(i).getValue() - p);
			if(value <= 1) { // O(1)
				nearestPoints.add((float) tupleList.get(i).getValue()); // O(1)
			} else if (nearlyEqual(value, 1, EPSILON)) {
				nearestPoints.add((float) tupleList.get(i).getValue()); // O(1)
			}
		}
		return nearestPoints; // Total => O(1 + N(p)) => O(N(p))
	}
	
	/**
	 * For every point p e S, compute the list of all points from S that
	 * are close to p by calling the method NaiveNearestPoints(p). Write the results to a file named
	 * NaiveSolution.txt
	 * @throws IOException 

	 */
	public void allNearestPointsNaive(){
		String filename = "NaiveSolution.txt";
		FileWriter fw;
		try {
			fw = new FileWriter(filename, true);
			
			BufferedWriter bw = new BufferedWriter(fw);
			ArrayList<ArrayList<Float>> nearestPoints = new ArrayList<>();   
			
			for (int i = 0; i < set.size(); i++) {
				nearestPoints.add(naiveNearestPoints(set.get(i)));
			}
			
			for (int j = 0; j < nearestPoints.size(); j++) {
				bw.write(nearestPoints.get(j).get(0) + " ");
				
				for (int k = 0; k < nearestPoints.get(j).size(); k++) {
					bw.write(nearestPoints.get(j).get(k) + " ");
				}
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("File Not Found");
			return ;
		}
		
	}
	
	/**
	 * For every point p e S, compute the list of all points from S that
	 * are close to p by calling the method NPHashNearestPoints(p). Write the results to a file named
	 * HashSolution.txt. The expected time of this method must be O(n+EpeS N(p));
	 * @throws IOException 
	 */
	public void allNearestPointsHash(){
		ArrayList<ArrayList<Float>> list = new ArrayList<>();
		FileWriter fw;
		try {
			fw = new FileWriter("HashSolution.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
		
			/* O(Sum p in s * N(p)) */
			for (int i = 0; i < set.size(); i++) {
				/* Loop through each point time: O(N(p)) */
				list.add(npHashNearestPoints(set.get(i)));
			}
			
			/* O(Sum p in s * N(p)) */
			for (int j = 0; j < list.size(); j++) {
				bw.write(list.get(j).get(0) + " ");
			    for (int k = 0; k < list.get(j).size(); k++) {
			    	bw.write(list.get(j).get(k) + " ");
			    }
				bw.newLine();
			}
			bw.close();
			/* (O(Sum p in s * N(p)) + O(Sum p in s * N(p)) => O(Sum p in s * N(p)) */
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("File Not Found");
			return ;
		}
		
	}
	
	// http://floating-point-gui.de/errors/comparison/
	private static boolean nearlyEqual(float a, float b, float epsilon) {
		final float absA = Math.abs(a);
		final float absB = Math.abs(b);
		final float diff = Math.abs(a - b);

		if (a == b) { // shortcut, handles infinities
			return true;
		} else if (a == 0 || b == 0 || diff < Float.MIN_NORMAL) {
			// a or b is zero or both are extremely close to it
			// relative error is less meaningful here
			return diff < (epsilon * Float.MIN_NORMAL);
		} else { // use relative error
			return diff / Math.min((absA + absB), Float.MAX_VALUE) < epsilon;
		}
	}
	public static void main(String[] args) throws IOException {
		NearestPoints snp = new NearestPoints("points.txt");

		long start = System.currentTimeMillis();
		snp.allNearestPointsHash();
		long finals = System.currentTimeMillis();
		System.out.println("ALL HASH: " + (finals - start) / 1000f);
		
		start = System.currentTimeMillis();
		snp.allNearestPointsNaive();
		finals = System.currentTimeMillis();
		System.out.println("ALL NAIVE: " + (finals - start) / 1000f);
		
		//snp.allNearestPointsHash();
	}
	
}
