package solver;
import java.util.*;

public class AstarAlgo {

    public String aStarAlgo(int width, int height, char[][] mapData, int[] startingPoint, Set<String> goalSet, Set<String> boxPositions, int[][] goalPoints){

        PriorityQueue<State> openSet = new PriorityQueue<>();
        Map<String, Double> bestCost = new HashMap<>();

        // starting coordinates
        State initialState = new State(startingPoint[0], startingPoint[1], boxPositions, "", 0);
        initialState.h = SokoBot.totalHeuristic(boxPositions, goalPoints);

        // add initial state to queue
        openSet.add(initialState);
        bestCost.put(initialState.getKey(), initialState.fTotal());

        // player moves
        int[][] directions = { {0, -1}, {0, 1}, {-1, 0}, {1, 0} }; // up, down, left, right coor
        char[] dirChars = { 'u', 'd', 'l', 'r' };

        int exploredStates = 0;
        int maxStates = 100000000; // cap search so doesnt do inifinite?

        // main loop to find solution
        while (!openSet.isEmpty() && exploredStates < maxStates) {
            State current = openSet.poll();
            exploredStates++;

            if (SokoBot.isGoalState(current.boxes, goalSet)) {
                System.out.println("Solution Found! YAY! Explored " + exploredStates + " states");
                return current.path;
            }

            // explore all directions
            for (int i = 0; i < 4; i++) {
                int newX = current.playerX + directions[i][0];
                int newY = current.playerY + directions[i][1];

                // Check bounds and walls
                if (newX < 0 || newX >= width || newY < 0 || newY >= height)
                    continue;
                if (mapData[newY][newX] == '#')
                    continue;

                String newPos = newX + "," + newY;

                if (current.boxes.contains(newPos)) {
                    // Trying to push a box
                    int boxNewX = newX + directions[i][0];
                    int boxNewY = newY + directions[i][1];

                    // Check if box can be pushed
                    if (boxNewX < 0 || boxNewX >= width || boxNewY < 0 || boxNewY >= height)
                        continue;
                    if (mapData[boxNewY][boxNewX] == '#')
                        continue;

                    String boxNewPos = boxNewX + "," + boxNewY;
                    if (current.boxes.contains(boxNewPos))
                        continue; // Another box blocking

                    
                    if (isDeadlock(boxNewX, boxNewY, mapData, goalSet, width, height)) continue; // if pushed box lands on deadlock cell skip it

                    
                    // Create new state with box pushed
                    Set<String> newBoxes = new HashSet<>(current.boxes);
                    newBoxes.remove(newPos);
                    newBoxes.add(boxNewPos);

                    State newState = new State(newX, newY, newBoxes, current.path + dirChars[i], current.g + 1);
                    newState.h = SokoBot.totalHeuristic(newBoxes, goalPoints);

                    String key = newState.getKey();
                    double newfTotal = newState.fTotal();

                    if (!bestCost.containsKey(key) || newfTotal < bestCost.get(key)) {
                        bestCost.put(key, newfTotal);
                        openSet.add(newState);
                    }
                } else {
                    // Just moving, no box
                    Set<String> newBoxes = new HashSet<>(current.boxes);
                    newBoxes.remove(newPos);
                    State newState = new State(newX, newY, current.boxes, current.path + dirChars[i], current.g + 1);
                    newState.h = SokoBot.totalHeuristic(newBoxes, goalPoints);

                    String key = newState.getKey();
                    double newfTotal = newState.fTotal();

                    if (!bestCost.containsKey(key) || newfTotal < bestCost.get(key)) {
                        bestCost.put(key, newfTotal);
                        openSet.add(newState);
                    }
                }
            }
        }

        // No solution found
        System.out.println("No solution found after exploring " + exploredStates + " states");
        return "";
    }

    // STATE CLASS -- compare one state to another
    // this stores the current state of player, boxes, cost, and path
    static class State implements Comparable<State> {
        int playerX, playerY; // this is the current coordinates of the player
        Set<String> boxes; // current positions of the boxes
        String path; // sequence of moves
        int g; // moves
        double h; // heuristic estimate

        public State(int playerX, int playerY, Set<String> boxes, String path, int g) {
            this.playerX = playerX;
            this.playerY = playerY;
            this.boxes = boxes;
            this.path = path;
            this.g = g;
        }

        // total cost
        public double fTotal() {
            return g + h;
        }

        // this returns a String (the state) which was explored to avoid revisiting of states
        public String getKey() {
            List<String> sorted = new ArrayList<>(boxes);
            Collections.sort(sorted);
            return playerX + "," + playerY + "|" + String.join(";", sorted);
        }

        // override
        // compares two states with total cost (which fTotal is lower)
        public int compareTo(State other) {

            return Double.compare(this.fTotal(), other.fTotal());
        }
    }
    
    //DEADLOCK DETECTIONN ADDTION
    
    private boolean inBounds(int x, int y, int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private boolean isWall(int x, int y, char[][] map) {
        return map[y][x] == '#';
    }

    /**
     * Some notes: (so it makes more sense hehe)
     * Corner deadlock: two perpendicular walls
     * Corridor deadlock: On a wall, and the wall-bounded segment in that row/col has no goal.
     */


    private boolean isDeadlock(int x, int y,
                           char[][] map,
                           Set<String> goals,
                           int width,
                           int height) {
    String pos = x + "," + y;
    if (goals.contains(pos)) return false;

    // helpers
    boolean inB  = (x>=0 && x<width && y>=0 && y<height);
    boolean upW    = (y-1<0)           || map[y-1][x] == '#';
    boolean downW  = (y+1>=height)     || map[y+1][x] == '#';
    boolean leftW  = (x-1<0)           || map[y][x-1] == '#';
    boolean rightW = (x+1>=width)      || map[y][x+1] == '#';

    // 1) Corner 
    if ((upW && leftW) || (upW && rightW) || (downW && leftW) || (downW && rightW)) return true;

    // 2) Horizontal corridor deadlock Box is tight to a vertical wall AND cannot move perpendicular?? (both up and down are walls),
    if ((leftW || rightW) && (upW && downW)) {
        int L=x; while (L-1>=0     && map[y][L-1] != '#') L--;
        int R=x; while (R+1<width  && map[y][R+1] != '#') R++;
        boolean hasGoal=false;
        for (int cx=L; cx<=R; cx++) if (goals.contains(cx + "," + y)) { hasGoal=true; break; }
        if (!hasGoal) return true;
    }

    // 3) Vertical corridor deadlock Box is tight to a hrizontal wall AND cannot move perpendicular (both left and right are walls),
    if ((upW || downW) && (leftW && rightW)) {
        int U=y; while (U-1>=0      && map[U-1][x] != '#') U--;
        int D=y; while (D+1<height  && map[D+1][x] != '#') D++;
        boolean hasGoal=false;
        for (int cy=U; cy<=D; cy++) if (goals.contains(x + "," + cy)) { hasGoal=true; break; }
        if (!hasGoal) return true;
    }

    return false;
}





    
}
