import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author nkarasch
 */
public class JUnitTest {
    // CONFIGURE TESTS HERE
    private static final String TEST_DATA_FILENAME = "matrix.txt";
    private static final String TEST_RESULTS_FILENAME = "test_results.txt";
    // If you get failed tests, reduce these numbers to 25 and 5. That should make it easier
    // to check where things went wrong.
    private static final int NUM_USERS = 1000;
    private static final int NUM_MOVIES = 100;
    // This is some small float, like 0.00001f
    private static final float EPSILON = NearestPoints.EPSILON;
    // If your tests fail, note the randomizer seed that was printed.
    // Put that seed here and set SHOULD_SEED to true.
    private static final long RANDOMIZER_SEED = 8828630490038314326l;
    private static final boolean SHOULD_SEED = true;

    @Test
    public void ratingOf() throws Exception {
        // Set randomizer seed
        Random rand = new Random();
        @SuppressWarnings("ConstantConditions")
        long seed = (SHOULD_SEED) ? RANDOMIZER_SEED : rand.nextLong();
        System.out.println("\n_RecSysTest\n  Randomizer Seed: " + seed);
        rand = new Random(seed);

        // Output other configuration details
        System.out.format("  Filename for generated test data: %s\n" +
                        "  Filename for generated results:   %s\n" +
                        "  Number of users generated: %d\n" +
                        "  Number of movies per user: %d\n",
                TEST_DATA_FILENAME, TEST_RESULTS_FILENAME, NUM_USERS, NUM_MOVIES);

        // Create the testing oracle
        RecSysOracle oracle = new RecSysOracle(NUM_USERS, NUM_MOVIES, rand);

        // Create the RecSys
        RecSys rs = new RecSys(TEST_DATA_FILENAME);

        // Run tests
        float actual;
        float expected;
        for (int uIndex = 1; uIndex < NUM_USERS; uIndex++) {
            for (int mIndex = 1; mIndex < NUM_MOVIES; mIndex++) {
                actual = rs.ratingOf(uIndex, mIndex);
                expected = oracle.results[uIndex - 1][mIndex];
                String str = String.format("At User.point %.1f\nExpected: %f\nActual:   %f",
                        oracle.results[uIndex - 1][0], expected, actual);
                assertTrue(str, nearlyEqual(expected, actual));
            }
        }
    }

    // Used for generating test data and expected results to verify against.
    private class RecSysOracle {
        float[][] testData;
        float[][] results;
        int numUsers;
        int numMovies;
        Random rand;

        RecSysOracle(int numUsers, int numMovies, Random rand) {
            this.rand = rand;
            this.numUsers = numUsers;
            this.numMovies = numMovies;

            testData = new float[numUsers][numMovies + 1];
            results = new float[numUsers][numMovies + 1];

            init();

            writeFile(TEST_DATA_FILENAME, testData, true);
            writeFile(TEST_RESULTS_FILENAME, results, false);
        }

        private void init() {
            // These two arrays are incremented/decremented as it goes through
            // the test data, creating averages for prediction results.
            float[] sums = new float[numMovies + 1];
            int[] numEls = new int[numMovies + 1];

            // Go up through all the users
            for (int i = 0; i < numUsers; i++) {
                // User points will each be 0.1 apart
                testData[i][0] = i * 0.1f;
                results[i][0] = testData[i][0];

                // Go through all movies
                for (int j = 1; j <= numMovies; j++) {
                    // Generate rating
                    testData[i][j] = rand.nextInt(6);

                    // Ratings not equal to zero are added to results and to sums array
                    if (!nearlyEqual(testData[i][j], 0)) {
                        results[i][j] = testData[i][j];
                        sums[j] += testData[i][j];
                        numEls[j]++;
                    }

                    // Once we've passed the first 9 indices, we can start making
                    // predictions for results 10 indices back that are equal to 0
                    // (10 indices back at 0.1 point increments are "close")
                    if (i > 9 && nearlyEqual(results[i - 10][j], 0)) {
                        // Assign the average (using sums and numEls array) to the result space
                        results[i - 10][j] = (numEls[j] > 0) ? sums[j] / numEls[j] : 0f;
                    }
                    // Once we've passed the first 19 indices, we can remove any
                    // ratings that got added to the sums array 20 indices back (they're
                    // not "close" enough to use in the average anymore)
                    if (i > 19 && !nearlyEqual(testData[i - 20][j], 0)) {
                        sums[j] -= testData[i - 20][j];
                        numEls[j]--;
                    }
                }
            }

            // Continue moving past the end of the user set to finish getting
            // results predictions.
            for (int i = numUsers; i < numUsers + 10; i++) {
                for (int j = 1; j <= numMovies; j++) {
                    if (i > 9 && nearlyEqual(results[i - 10][j], 0)) {
                        results[i - 10][j] = (numEls[j] > 0) ? sums[j] / numEls[j] : 0f;
                    }
                    if (i > 19 && !nearlyEqual(testData[i - 20][j], 0)) {
                        sums[j] -= testData[i - 20][j];
                        numEls[j]--;
                    }
                }
            }
        }

        // Open and write a file, filling it with the elements of 'content' with
        // newline delimiters.
        private void writeFile(String filename, float[][] content, boolean shouldRound) {
            BufferedWriter bw = null;
            FileWriter fw = null;

            try {
                fw = new FileWriter(filename);
                bw = new BufferedWriter(fw);
                bw.write(content.length + " " + (content[0].length - 1) + "\n");
                for (float[] line : content) {
                    String str = String.format("%.1f", line[0]);
                    bw.write(str);
                    for (int i = 1; i < line.length; i++) {
                        if (shouldRound) {
                            // For writing test data as ints
                            bw.write(" " + (int) Math.floor(line[i]));
                        } else {
                            // For writing results data as floats
                            str = String.format(" %.5f", line[i]);
                            bw.write(str);
                        }
                    }
                    bw.write("\n");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } finally {
                //noinspection Duplicates
                try {
                    if (bw != null) {
                        bw.close();
                    }
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // http://floating-point-gui.de/errors/comparison/
    private static boolean nearlyEqual(float a, float b) {
        final float absA = Math.abs(a);
        final float absB = Math.abs(b);
        final float diff = Math.abs(a - b);

        if (a == b) { // shortcut, handles infinities
            return true;
        } else if (a == 0 || b == 0 || diff < Float.MIN_NORMAL) {
            // a or b is zero or both are extremely close to it
            // relative error is less meaningful here
            return diff < (EPSILON * Float.MIN_NORMAL);
        } else { // use relative error
            return diff / Math.min((absA + absB), Float.MAX_VALUE) < EPSILON;
        }
    }
}