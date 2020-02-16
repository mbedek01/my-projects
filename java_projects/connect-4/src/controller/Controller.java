package controller;
import model.Model;
import view.WindowView;

/**
 * This class represents the controller which contains the application logic for the Connect4 game. The controller
 * communicates with the model and the view. Running the controller begins execution of the game.
 */
public class Controller implements Runnable {

    // an instance of the model object
    protected Model model;      // made protected for JUnit testing
    //private Model model;

    // an instance of the view object
    protected WindowView refToView;     // made protected for JUnit testing
    //private WindowView refToView;

    private boolean stopRunningLoop;

    private int playerTurn;

    // minimum consecutive slots of same color required to win
    private int MIN_TO_WIN = 4;


    /**
     * The constructor for the controller.
     * The constructor instantiates an object of the Model class and the WindowView class
     */
    public Controller(){
        this.model = new Model();
        this.refToView = new WindowView(this);
        this.stopRunningLoop = false;
        this.playerTurn = 1;        // set Player 1 to play first
    }


    /**
     * This method is called after consuming the x and y coordinates of the grid calculated based on mouseclick.
     * This method does the following:
     *  1. Returns the next available slot in the column that can be filled based on data in the model
     *  2. Updates the model grid
     *  3. Updates the view grid
     *  4. Checks if the game is over by accessing model's data
     * @param x the row of the grid
     * @param y the column of the grid
     * @param color the color of the player, 1: Red, 2: Yellow
     *              return type is void
     */

    public void updateModelsGrid(int x, int y, int color){

        // If player clicks anywhere else except on the top row, do nothing. This includes:
        // 1. outside the grid boundary & 2. anywhere else but the top row on the grid

        if(x !=0 || y < 0 || y > model.MAX_COLUMN){
            ;
        }

        // If player clicks on the top row
        else {
            // call to the upgradeColorGrid method returns the next available row which can be filled up
            int rowToBeUpdated = this.model.updateColorGrid(y, color);

            // if the grid is filled & next available row is the top row, dont update model's & view's grids
            if(rowToBeUpdated == -1){
                ;
            }
            else{
                this.updateViewsGrid(rowToBeUpdated, y, color);
                if (this.checkIfGameIsOver(rowToBeUpdated, y, color, MIN_TO_WIN)) {
                    this.refToView.gameOverFlag = true;
                }
                // Logic to alternate player turns
                if(this.playerTurn==1){
                    this.playerTurn = 2;
                }
                else{
                    this.playerTurn = 1;
                }
                // update view about which player played their turn
                this.refToView.setColourToBeFlickeredNextTime(this.playerTurn);
            }

        }

    }


    /**
     * This method updates the view's grid
     * @param rowToBeUpdated    the next available empty slot in the column
     * @param columnToBeUpdated the column clicked by the player
     * @param color the player color, 1: Red 2: Yellow
     */
    public void updateViewsGrid(int rowToBeUpdated, int columnToBeUpdated, int color){

        this.refToView.updateColorGrid(rowToBeUpdated, columnToBeUpdated, color);
    }

    /**
     * This method accesses the state of model's grid and check is the game is over. It checks for the following:
     *      1. Vertical win
     *      2. Horizontal win
     *      3. Right down diagonal win
     *      4. Left down diagonal win
     * @param row   the most recently filled empty slot in the column
     * @param column    the column clicked by the player
     * @param color     the player color, 1: Red 2: Yellow
     * @param MIN_TO_WIN    minimum consecutive slots of same color to win
     * @return  true if game is over, false otherwise
     */
    public boolean checkIfGameIsOver(int row, int column, int color, int MIN_TO_WIN){

        boolean result;     // default value

        //Is game over row wise
        boolean rowWon = model.checkIfConsecutiveRows(row, color, MIN_TO_WIN);

        // Is game over column wise
        boolean columnWon = model.checkIfConsecutiveColumns(row, column, color, MIN_TO_WIN);

        // Is game over right down diagonal wise
        boolean rightDownDiagonalWon = model.checkIfRightDownDiagonal(row, column, color, MIN_TO_WIN);

        // Is game over left down diagonal wise
        boolean leftDownDiagonalWon = model.checkIfLeftDownDiagonal(row, column, color, MIN_TO_WIN);

        // If row is won, updates color of that sequence to blue
        if (rowWon) {
            for (int winColor = model.startingWinPoint; winColor >= model.endingWinPoint+1; winColor--) {
                this.refToView.updateColorGrid(model.otherStartingPoint, winColor, 7);
            }
            System.out.println("Game over!!!");

        }

        // If column is won, updates color of that sequence to blue
        else if (columnWon){

            for (int rowNumber = model.startingWinPoint; rowNumber >= model.endingWinPoint; rowNumber--) {
                this.refToView.updateColorGrid(rowNumber, model.otherStartingPoint, 7);
            }
                System.out.println("Game over!!!");
        }


        else if(rightDownDiagonalWon) {
            System.out.println("Game over!!!");
        }
        else if(leftDownDiagonalWon){
            System.out.println("Game over!!!");
        }

        // Game is over if there is a win in either directions
        result = (rowWon || columnWon || rightDownDiagonalWon || leftDownDiagonalWon);
        return result;      // returns true if there is a win

    }

    public void quitTheGame(){
        this.stopRunningLoop = true;

    }

    public void run(){
        while (true) {
            this.refToView.repaint();
            if (stopRunningLoop) {
                int playerWon = 1;
                if (this.playerTurn == 1)
                    playerWon = 2;
                else
                    playerWon = 1;
                this.refToView.showDiagAndQuit(playerWon);
            }
         }
    }

    // The program executes from here, the main method
    public static void main(String[] args) {
        Thread thread = new Thread(new Controller());
        thread.start();
    }


}
