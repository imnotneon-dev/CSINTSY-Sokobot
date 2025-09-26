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
    int playerX = -1, playerY = -1, found = 0;

    /* To find the player's starting index (x, y) 
       x = column
       y = row

          x
        y # 0 1 2 
          0 
          1  '@'
          2
    */
   
    for (int y = 0; y < height && found == 0; y++) { 
        for (int x = 0; x < width; x++) {
            if (itemsData[y][x] == '@') {
                playerX = x;
                playerY = y;
                found = 1;
            }
        }
    }

    moves.append('r');
    moves.append('r');
    moves.append('r');
    moves.append('d');
    moves.append('d');
    moves.append('l');
    moves.append('l');
    moves.append('l');
    moves.append('r');
    moves.append('r');
    moves.append('r');    
    moves.append('u');
    moves.append('u');
    moves.append('l');
    moves.append('l');
    moves.append('l');
    moves.append('d');
    moves.append('u');
    moves.append('r');
    moves.append('r');
    moves.append('r');
    moves.append('d');
    moves.append('d');
    moves.append('l');
    moves.append('l');
    moves.append('u');
    moves.append('d');
    moves.append('r');
    moves.append('r'); 
    moves.append('u');
    moves.append('u');
    moves.append('l');
    moves.append('d');
    moves.append('l');
    moves.append('l');
    moves.append('u');
    moves.append('r');

    return moves.toString(); // Returns the moves
  }
}
