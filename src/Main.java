
/**
 * Used as creating the instance and testing runtime 
 */
public class Main {

	public static void main(String[] args) {
		RecSys rs = new RecSys("matrix.txt");
		System.out.println(rs.ratingOf(3, 5));
	}

}
