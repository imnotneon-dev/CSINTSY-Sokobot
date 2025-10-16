package solver;
import java.util.*;

public class AstarAlgo {
    
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
    return "";
}