This project is based on Model View Controller Pattern & implements the Connect4 game with a user interface enabled by mouse clicks.

Game description:

Grid: 6 x 7 grid
** Note : An additional top row is for user to click. This row is not utilized for disc insertion. User is expected to click only on top row to indicate the column in which the player wishes to place their disc. Clicks other than the top area of grid are invalid.

Player 1 & 2 play alternately to select the column where they want to insert their discs.
The game checks for the following:
  - 4 consecutive discs in a row of the same color
  - 4 consecutive discs in a column of the same color
  - 4 consecutive discs in a diagonal pattern of the same color
  
If either of the above are satisfied, the game is over & exits. The user is notified of the game outcome indicating which player won.
