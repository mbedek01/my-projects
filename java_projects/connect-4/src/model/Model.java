package model;

/**
 * This class represents the Model of the MVC based Connect4 game. The model holds the data of the game.
 * This game is a 6 x 7 grid based game. The top(7th) additional row represents the space where the player clicks.
 */
public class Model {

    // The game's actual grid has 6 rows & 7 columns. However, the first row is created only for player to click
    // on the intended column where disc is to be dropped.
    public int MAX_ROW = 7;         // additional row where user will click
    public int MAX_COLUMN = 7;
    int WHITE_COLOR = 0;            // indicates blank space
    int RED_COLOR = 1;              // For player with red disc
    int YELLOW_COLOR = 2;           // For player with yellow disc
    int MIN_WIN_CNT = 4;            // minimum number of consecutive discs for win

    // declared variables to be used while calculating diagonal win below
    private int startingRowForDiags;
    private int endingRowForDiags;
    private int startingColumnForDiags;
    private int endingColumnForDiags;

    public int startingWinPoint;
    public int endingWinPoint;
    public int otherStartingPoint;

    /** modelsColorGrid is an instance of the 2D array/grid object that holds model's data */
    private int[][] modelsColorGrid = new int[MAX_ROW][MAX_COLUMN];


    /**
     * Constructor for the model class. The constructor initializes the game grid to 0 = white spaces.
     */
    public Model(){
        this.initializeColorGrid();
    }

    /**
     * This is a helper method to initialize the model's grid to WHITE_COLOR indicating empty grid
     */
    private void initializeColorGrid(){

        for(int i = 0; i < MAX_ROW; i++){
            for (int j=0; j < MAX_COLUMN; j++){
                modelsColorGrid[i][j] = WHITE_COLOR;
            }
        }
    }

    /**
     * Getter method to access model's grid
     * @return model's grid
     */
    public int[][] getModelGrid(){
        return this.modelsColorGrid;
    }


    /**
     * This method is called by the constructor and updates the model's grid based on the click coordinates
     * received by the controller from the WindowView
     * @param column the column on which player clicks
     * @param color the player's color, 1: Red or 2: Yellow
     * @return returns the next available row in the column that user clicks
     */
    public int updateColorGrid(int column, int color) {

        int nextAvailableRow = 0;
        for (nextAvailableRow = MAX_ROW - 1; nextAvailableRow > 0; nextAvailableRow--) {
            if (modelsColorGrid[nextAvailableRow][column] == WHITE_COLOR) {
                // if the calculated next available row is the top row, grid will not be updated.
                // nextAvailableRow will be returned as -1 to the controller
                modelsColorGrid[nextAvailableRow][column] = color;
                break;
            }
        }
        if(nextAvailableRow ==0)
            nextAvailableRow = -1;
        return nextAvailableRow;
    }


    /**
     * This method is called from the constructor to check if there is a horizontal win
     * @param startingRow   the row where disc is inserted after click
     * @param color         the color of the player, 1: Red, 2: Yellow
     * @param minConsecutiveRow the minimum consecutive discs of the same color required to win
     * @return  returns true, if there is a winning row. False otherwise.
     */
    public boolean checkIfConsecutiveRows(int startingRow, int color, int minConsecutiveRow)
    {
        int winCount = 0;
        boolean gameOver = false;

        //Logic to check horizontally if player has won.
        //Better solution is to check to left and right of column number. Better is just parse the whole row.

        for (int cnt = 0; cnt < this.MAX_COLUMN; cnt++) {
            // the player color does not match the slot color
            if (this.modelsColorGrid[startingRow][cnt] != color) {
                winCount = 0;
                // the player color matches the slot color
            } else {
                winCount++;
            }
            // if there are 4 or more consecutive slots in the column filled with same color, game is over
            if (winCount >= minConsecutiveRow) {
                gameOver = true;

                //Set the color to blue for fun when game is over
                this.startingWinPoint = cnt;
                this.endingWinPoint = cnt - minConsecutiveRow;
                this.otherStartingPoint = startingRow;
                break;

            }
        }
        return gameOver;        // returns true if row is completed
    }

    /**
     * This method is called by the constructor to check if there is a vertical win
     * @param startingRow   the row where disc is inserted after click
     * @param startingColumn the column where new disc is inserted after click
     * @param color the color of the player
     * @param minConsecutiveCol the minimum consecutive discs of the same color required to win
     * @return  returns true, if there is a winning column. False otherwise.
     */

    public boolean checkIfConsecutiveColumns(int startingRow, int startingColumn, int color, int minConsecutiveCol)
    {
        //Logic to check vertically if player has won
        //Set WinCount to = 0 before starting the process.
        int winCount = 0;
        boolean gameOver = false;       // default value of gameOver is false
            int cnt;
            for (cnt = startingRow; cnt < MAX_ROW; cnt++) {
                // if player color does not match slot color
                if (modelsColorGrid[cnt][startingColumn] != color) {
                    break;
                    // player color matches slot color
                } else {
                    winCount++;
                }
            }

        // if there are 4 or more consecutive slots in the column filled with same color, game is over
            if (winCount >= minConsecutiveCol) {
                gameOver = true;

                //Set the color to blue for fun after a win
                this.startingWinPoint = cnt-1;
                this.endingWinPoint = cnt - minConsecutiveCol;
                this.otherStartingPoint = startingColumn;
            }
            return gameOver;        // returns true if column is completed
    }


    /**
     * This method is called by the constructor to check if a right diagonal sequence of 4 consecutive slots of same
     * color are completed
     * @param startingRow  the row number where new disc is going to get placed
     * @param startingColumn the column number where new disc is going to get place.
     * @param color     player
     * @param minConsecutiveDiag    minimum no. of consecutive slots of same color required for win
     * @return true if right diagonal is complete, false otherwise
     */
    public boolean checkIfRightDownDiagonal(int startingRow, int startingColumn, int color, int minConsecutiveDiag)
    {
        //We find minimum and maximum ends of a diagonal formed by including the newly created disc.
        //After finding the ends, find out if there are minConsecutiveDiagonal elements
        //in this diagonal which constitutes a win.
        int winCount = 0;
        boolean gameOver = false;       // default value
        int lowRowNumber = startingRow;
        int lowColumnNumber = startingColumn;
        int largeRowNumber = startingRow;
        int largeColumnNumber = startingColumn;

        //Find out top right end.
        for (int cnt = 0; cnt < (MAX_ROW * MAX_COLUMN); cnt++) {
            if (lowRowNumber ==0 || largeColumnNumber == MAX_COLUMN-1)
                break;
            else {
                if (lowRowNumber > 0)
                    lowRowNumber--;
                if (largeColumnNumber < MAX_COLUMN-1)
                    largeColumnNumber++;
            }
        }
        //Find out bottom right end.
        for (int cnt = 0; cnt < (MAX_ROW * MAX_COLUMN); cnt++) {
            if (largeRowNumber ==MAX_ROW-1 || lowColumnNumber == 0)
                break;
            else {
                if (largeRowNumber < (MAX_ROW-1))
                    largeRowNumber++;
                if (lowColumnNumber > 0)
                    lowColumnNumber--;
            }
        }

        //Logic to check if min consecutive slots are present in the ends.
        for (int cnt = 0; cnt <= (largeRowNumber-lowRowNumber); cnt++) {
            if(modelsColorGrid[largeRowNumber-cnt][lowColumnNumber+cnt]!=color){
                winCount = 0;
            } else {
                if(winCount ==0) {
                    startingRowForDiags = largeRowNumber-cnt;
                    startingColumnForDiags = lowColumnNumber+cnt;
                }
                winCount++;
                if(winCount >= minConsecutiveDiag) {
                    endingRowForDiags = largeRowNumber-cnt;
                    endingColumnForDiags = lowColumnNumber+cnt;
                    gameOver = true;
                    break;
                }
            }
        }
        return gameOver;    // returns true if right diagonal is complete
    }


    /**
     * This method is called by the constructor to check if a left diagonal sequence of 4 consecutive slots of same
     * color are completed
     * @param startingRow  the row number where new disc is going to get placed
     * @param startingColumn the column number where new disc is going to get place.
     * @param color         player
     * @param minConsecutiveElem    minimum no. of consecutive slots of same color required for win
     * @return true if right diagonal is complete, false otherwise
     */

    public boolean checkIfLeftDownDiagonal(int startingRow, int startingColumn, int color, int minConsecutiveElem)
    {

        //We find minimum and maximum ends of a diagonal formed by including the newly created disc.
        //After finding the end, we find out if there are minConsecutiveDiagonal elements
        //in this diagonal which constitutes a win.

        int winCount = 0;
        boolean gameOver = false;       // default value
        int lowRowNumber = startingRow;
        int lowColumnNumber = startingColumn;
        int largeRowNumber = startingRow;
        int largeColumnNumber = startingColumn;


        // Find out top left end
        for (int cnt = 0; cnt < (MAX_ROW * MAX_COLUMN); cnt++) {
            if (lowRowNumber ==0 || lowColumnNumber == 0)
                break;
            else {
                if (lowRowNumber > 0)
                    lowRowNumber--;
                if (lowColumnNumber > 0)
                    lowColumnNumber--;
            }
        }

        // Find out bottom left end.
        for (int cnt = 0; cnt < (MAX_ROW * MAX_COLUMN); cnt++) {
            if (largeRowNumber ==MAX_ROW-1 || largeColumnNumber == MAX_COLUMN-2)
                break;
            else {
                if (largeRowNumber < (MAX_ROW-1))
                    largeRowNumber++;
                if (largeColumnNumber < MAX_COLUMN-2)
                    largeColumnNumber++;
            }
        }

        // Logic to check if min consecutive slots are present in the end.
        for (int cnt = 0; cnt <= (largeColumnNumber-lowColumnNumber); cnt++) {
            if(modelsColorGrid[lowRowNumber+cnt][lowColumnNumber+cnt]!=color){
                winCount = 0;
            } else {
                if(winCount ==0) {
                    startingRowForDiags = lowRowNumber+cnt;
                    startingColumnForDiags = lowColumnNumber+cnt;
                }
                winCount++;
                if(winCount >= minConsecutiveElem) {
                    endingRowForDiags = lowRowNumber+cnt;
                    endingColumnForDiags = lowColumnNumber+cnt;
                    gameOver = true;
                    break;
                }
            }
        }
        return gameOver;        // returns true if left diagonal is complete
    }

}
