import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 
 * @author Chris Bui
 *
 */
public class RecSys {
	private int numOfMovies;
	private NearestPoints np;
	private ArrayList<Float> users;
	private HashMap<Float, ArrayList<Integer>> map;
	/**
	 * 
	 * @param mrMatrix contains the absolute path name of the file that
	 * contains the mapped ratings matrix
	 * @throws FileNotFoundException 
	 */
	public RecSys(String mrMatrix) {
		map = new HashMap<Float, ArrayList<Integer>>();
		File file = new File(mrMatrix);
		Scanner scanLine;
		try {
			Scanner scanner = new Scanner(file);
			
			if (scanner.hasNextLine()) {
				scanLine = new Scanner(scanner.nextLine());
				scanLine.nextInt();
				numOfMovies = scanLine.nextInt();
				
				users = new ArrayList<Float>();
				
				while (scanner.hasNextLine()) {
					scanLine = new Scanner(scanner.nextLine());
					ArrayList<Integer> userRatings = new ArrayList<Integer>();
					float user = scanLine.nextFloat();
					users.add(user);
					
					for (int i = 0; i < numOfMovies; i++) {
						userRatings.add(scanLine.nextInt());	
					}
					map.put(user,userRatings);
				}
				np = new NearestPoints(users);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}

	}
	
	/**
	 * If the user u has rated movie m, then it returns that rating; otherwise
	 * it will predict the rating based on the approach described above, and returns the predicted rating.
	 * The type of this method must be float.
	 * @param u user
	 * @param m rated movie
	 * @return rating or predicted rating
	 */
	public float ratingOf(int u, int m) {
		float userValue = users.get(u - 1);
		if (map.get(userValue).get(m - 1) != 0) return (float) map.get(userValue).get(m - 1);
		
		/* Ex returns user map of {2.1, 3.0, 2.8} */
		ArrayList<Float> ratings = np.npHashNearestPoints(userValue);
		float predictedRating = 0;
		int rated = 0;
		for (int i = 0; i < ratings.size(); i++) {
			int movieRating = map.get(ratings.get(i)).get(m - 1);
			if (movieRating > 0) {
				predictedRating += movieRating;
				rated++;
			}
		}
		return (rated == 0) ? 0 : predictedRating / rated;
	}
}
