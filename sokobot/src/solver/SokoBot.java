package solver;
import java.util.*;

public class SokoBot {

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {

        // get coordinates of player
        int[] startingPoint = SokoBot.getCoordinates(width, height, mapData, itemsData, '@');
        // get coordinatess of goal points for the boxes
        int[][] goalPoints = SokoBot.getGoalStates(width, height, mapData);
        // get coordinates of boxes
        int[][] boxStartPoint = SokoBot.getBoxPoints(width, height, itemsData, '$', goalPoints.length);

        // this converts the goalPoints to String for easier comparison
        Set<String> goalSet = new HashSet<>();
        for (int[] g : goalPoints) {
            goalSet.add(g[0] + "," + g[1]);
        }

        // this converts the boxStartPoint to String for easier comparison
        Set<String> boxPositions = new HashSet<>();
        for (int[] b : boxStartPoint) {
            boxPositions.add(b[0] + "," + b[1]);
        }

        AstarAlgo astar = new AstarAlgo();
        String aStarAlgo = astar.aStarAlgo(width, height, mapData, startingPoint, goalSet, boxPositions, goalPoints);

        return aStarAlgo;
    }
  
    /*
     *   HELPER FUNCTIONS
     */

    public static int[] getCoordinates(int width, int height, char[][] mapData, char[][] itemsData, char bot) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((itemsData[y][x] == bot) || (mapData[y][x] == bot)) {
                    return new int[]{x, y};
                }
            }
        }
        return null;
    }

    public static int[][] getGoalStates(int width, int height, char[][] mapData) {
        List<int[]> goals = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (mapData[y][x] == '.' || mapData[y][x] == '*') {
                    goals.add(new int[]{x, y});
                }
            }
        }

        return goals.toArray(new int[goals.size()][]);
    }

    public static int[][] getBoxPoints (int width, int height, char[][] itemsData, char box, int number) {
        int index = 0;
        int[][] coords = new int[number][2];
        for (int y = 0; y < height && index < number; y++) {
            for (int x = 0; x < width && index < number; x++) {
                if (itemsData[y][x] == box) {
                    coords[index][0] = x;
                    coords[index][1] = y;
                    index++;
                }
            }
        }

        return coords;
    }

    public static boolean isGoalState(Set<String> boxes, Set<String> goals) {
        return goals.containsAll(boxes);
    }

    // We can keep changing the heuristic to see what works best
    public static double heuristic (int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public static double totalHeuristic (Set<String> boxes, int[][] goals) {
        double total = 0;
      // loop thru all boxes
        for (String box : boxes) {
            String[] parts = box.split(",");
          // extract the boxes' coordinates
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            double best = Double.MAX_VALUE;
          // loop comparing distances from goals
            for (int[] g : goals) {
                double dist = heuristic(x, y, g[0], g[1]);
                if (dist < best)
                    best = dist;
            }
            total += best;
        }
        return total;
    }
}
