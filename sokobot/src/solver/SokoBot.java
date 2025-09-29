package solver;

public class SokoBot {

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly.
     */

    // COMMENTED OUT (IDK IF WE NEED THIS)
    // try {
    //     Thread.sleep(3000);
    // } catch (Exception ex) {
    //     ex.printStackTrace();
    // }

    StringBuilder moves = new StringBuilder(); // To add in the move list, just do -- moves.append('Letter'); --
                                               // NOTE: MOVES MUST BE IN small letters [u, d, l, r]
    int[] startingPoint = SokoBot.getCoordinates(width, height, mapData, '@');
    int[][] goalPoints = SokoBot.getGoalStates(width, height, mapData, '.');
    int[][] boxStartPoint = SokoBot.getBoxPoints(width, height, itemsData, '$', goalPoints.length);
    

    return moves.toString(); // Returns the moves
  }

  public static int[] getCoordinates (int width, int height, char[][] itemsData, char bot) {
    boolean found = false;
    int[] coords = new int[2];
    for (int y = 0; y < itemsData.length && !found; y++) {
      for (int x = 0; x < itemsData[0].length && !found; x++) {
        if (itemsData[y][x] == bot) {
          coords[0] = x;
          coords[1] = y;
          found = true;
        }
      }
    }
    if (found)
      return coords;
    return null;
  }

  public static int[][] getGoalStates (int width, int height, char[][] mapData, char goal) {
    int count = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (mapData[y][x] == goal) {
          count++;
        }
      }
    }

    int[][] coords = new int[count][2];

    int index = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (mapData[y][x] == goal) {
          coords[index][0] = x;
          coords[index][1] = y; 
          index++;
        }
      }
    }

    return coords;
  }

  public static int[][] getBoxPoints (int width, int height, char[][] itemsData, char box, int number) {
    int index = 0;
    int[][] coords = new int[number][2];
    for (int y = 0; y < height && index < number; y++) {
      for (int x = 0; x < width && index < number; x++) {
        if (itemsData[y][x] == box) {
          coords[index][0] = x;
          coords[index][1] = y;
        }
      }
    }

    return coords;
  }

  // We can keep changing the heuristic to see what works best
  public static double heuristic (int x1, int y1, int x2, int y2) {
    return Math.abs(x1 - x2) + Math.abs(y1 - y2);
  }

}
