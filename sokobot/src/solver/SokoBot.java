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


  
//I ADDED THE A* QUEUE ALGO BUT IDK IF THIS IS RIGHT 

  PriorityQueue<State> openSet = new PriorityQueue<>();
Map<String, Integer> bestCost = new HashMap<>();

// X and Y coordinates of player
State initialState = new State(playerX, playerY, boxes, "", 0);
initialState.hCost = calculateHeuristic(boxes, goals);
openSet.add(initialState);
bestCost.put(initialState.getKey(), initialState.fCost());

int[][] directions = { {0, -1}, {0, 1}, {-1, 0}, {1, 0} }; // up, down, left, right coor
char[] dirChars = { 'u', 'd', 'l', 'r' };

int exploredStates = 0;
int maxStates = 100000; // cap search so doesnt do inifinite?

while (!openSet.isEmpty() && exploredStates < maxStates) {
    State current = openSet.poll();
    exploredStates++;

    if (current.boxes.equals(goals)) {
        System.out.println("Solution Found! YAY! Explored " + exploredStates + " states");
        return current.path;
    }

    for (int i = 0; i < 4; i++) {
        int newX = current.playerX + directions[i][0];
        int newY = current.playerY + directions[i][1];

        // Check bounds and walls
        if (newX < 0 || newX >= width || newY < 0 || newY >= height) continue;
        if (mapData[newY][newX] == '#') continue;

        String newPos = newX + "," + newY;

        if (current.boxes.contains(newPos)) {
            // Trying to push a box
            int boxNewX = newX + directions[i][0];
            int boxNewY = newY + directions[i][1];

            // Check if box can be pushed
            if (boxNewX < 0 || boxNewX >= width || boxNewY < 0 || boxNewY >= height) continue;
            if (mapData[boxNewY][boxNewX] == '#') continue;

            String boxNewPos = boxNewX + "," + boxNewY;
            if (current.boxes.contains(boxNewPos)) continue; // Another box blocking

            // Deadlock detection
            if (isDeadlock(boxNewX, boxNewY, mapData, goals, width, height)) continue;

            // Create new state with box pushed
            Set<String> newBoxes = new HashSet<>(current.boxes);
            newBoxes.remove(newPos);
            newBoxes.add(boxNewPos);

            State newState = new State(newX, newY, newBoxes, current.path + dirChars[i], current.gCost + 1);
            newState.hCost = calculateHeuristic(newBoxes, goals);

            String key = newState.getKey();
            int newFCost = newState.fCost();

            if (!bestCost.containsKey(key) || newFCost < bestCost.get(key)) {
                bestCost.put(key, newFCost);
                openSet.add(newState);
            }
        } else {
            // Just moving, no box
            State newState = new State(newX, newY, current.boxes, current.path + dirChars[i], current.gCost + 1);
            newState.hCost = calculateHeuristic(current.boxes, goals);

            String key = newState.getKey();
            int newFCost = newState.fCost();

            if (!bestCost.containsKey(key) || newFCost < bestCost.get(key)) {
                bestCost.put(key, newFCost);
                openSet.add(newState);
            }
        }
    }
}

// No solution found
System.out.println("No solution found after exploring " + exploredStates + " states");
return "";


//END OF PRIORITY QUEUE 


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
          index++;
        }
      }
    }

    return coords;
  }

  // We can keep changing the heuristic to see what works best
  public static double heuristic (int x1, int y1, int x2, int y2) {
    return Math.abs(x1 - x2) + Math.abs(y1 - y2);
  }

  public static double totalHeuristic (int[][] boxes, int[][] goals) {
    double total = 0;
    for (int[] b : boxes) {
      double best = Double.MAX_VALUE;
      for (int[] g : goals) {
        double dist = heuristic(b[0], b[1], g[0], g[1]);
        if (dist < best) best = dist;
      }
      total += best;
    }
    return total;
  }

}
