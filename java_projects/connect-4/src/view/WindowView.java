package view;

import controller.Controller;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.*;
import javax.swing.JOptionPane;
import java.util.concurrent.TimeUnit;

/**
 * This class represents the View of the Model-View-Controller design pattern. This class allows for interactivity
 * with the user via mouse clicks. It extends the interface JFrame.
 */
public class WindowView extends JFrame {

    private int spacing = 5;
    private int clickedX = -100;
    private int clickedY = -100;

    // instance of the controller
    private Controller controller;
    // the view's grid
    private int[][] ViewColorGrid;


    private int turn = 1;
    boolean sleep = false;

    private int MAX_ROW = 7;    // no. of rows in our grid. The extra(top) row is only for user to click
    private int MAX_COLUMN = 7; // no. of columns in our grid

    private int RED_COLOR = 1;      // Player 1
    private int YELLOW_COLOR = 2;   // Player 2
    private int WHITE_COLOR = 0;    // Empty slot
    // Optionally the program could create an enum class with RED_COLOR, YELLOW_COLOR and WHITE_COLOR as enum types

    public boolean gameOverFlag = false;    // default value: game is not over
    boolean quitGame = false;               // default value: game is on
    private int colorToFlicker;


    /**
     * Getter method for view's color grid
     * @return the view's grid
     */
    public int[][] getViewColorGrid(){
        return this.ViewColorGrid;
    }

    /**
     * This method updates the color grid maintained by the view class. This method is called by the constructor.
     * @param rowNumber row in the grid
     * @param columnNumber  column in the grid
     * @param color color to be filled at grid[rowNumber][columnNumber]
     */
    public void updateColorGrid(int rowNumber, int columnNumber, int color) {

        this.ViewColorGrid[rowNumber][columnNumber] = color;
    }

    /**
     * Constructor for the WindowView class. The constructor:
     *      1. instantiates an instance of the controller class
     *      2. initializes the color grid of the view.
     *      3. creates the display window/frame for the Connect4 game
     *
     * @param controller    an instance of the controller class
     */
    public WindowView(Controller controller){

        this.controller = controller;
        this.ViewColorGrid = new int[MAX_ROW][MAX_COLUMN];
        this.colorToFlicker = 1;
        initializeViewColorGrid();
        makeWindow();

    }

    /**
     * A helper method to set the user interface of the Connect4 game.
     */
    private void makeWindow(){

        this.setTitle("Connect4");                      // title of the frame
        this.setSize(600, 700);        // dimensions of the frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        Board board = new Board();
        Click click = new Click();
        this.add(board);
        this.addMouseListener(click);       // set action listener for mouse click
        this.setVisible(true);
    }

    /**
     * This is a helper method called in the constructor to initialize the view's grid to White color
     */
    private void initializeViewColorGrid() {
        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COLUMN; j++) {
                ViewColorGrid[i][j] = 0;
            }
        }
    }

    public void showDiagAndQuit(int player){
        JFrame parent = new JFrame();

        String winner;
        if(player==1){
            winner = "Yellow";
        }
        else{
            winner = "Red";
        }
        String message = "Game over!!! Player " + player + ": " + winner + " won";
        JOptionPane.showMessageDialog(parent, message);

        System.exit(0);
    }

    public void setColourToBeFlickeredNextTime(int colorToFlicker){
        this.colorToFlicker = colorToFlicker;
    }

    /**
     * This class extends the JPanel interface. It colors the frame and the grid and updates the colors of the slots
     * based on input from the controller.
     */
    public class Board extends JPanel {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (quitGame) {
                controller.quitTheGame();

            }

            if (sleep) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {

                }
                sleep = false;
            }

            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, 600, 700);
            // set color of the rectangles in grid as gray

            for (int row = 0; row < MAX_ROW; row++) {
                for (int column = 0; column < MAX_COLUMN; column++) {
                    g.setColor(Color.gray);
                    // set color of first row as white. User will only click on this row to choose their column.
                    // Clicks anywhere else are invalid.
                    if (row == 0) {
                        g.setColor(Color.white);
                    }

                    //This case is to flag top square & flash user color to show user selection
                    if (clickedX == row && clickedY == column) {
                        sleep = true;
                        if (colorToFlicker == 1) {
                            g.setColor(Color.red);

                        } else {
                            //g.drawString("Yellow's turn", 800, 50);
                            g.setColor(Color.yellow);

                        }
                    }

                    if (ViewColorGrid[row][column] != WHITE_COLOR) {
                        if (ViewColorGrid[row][column] == RED_COLOR)
                            g.setColor(Color.red);
                        if (ViewColorGrid[row][column] == YELLOW_COLOR)
                            g.setColor(Color.yellow);
                        if (ViewColorGrid[row][column] == 7) {
                            g.setColor(Color.blue);
                            gameOverFlag = true;
                        }
                    }
                    //Each rectangle is of width 70 and height 70.
                    // It's x-coordinate(starting point) = 5,85,165, y-coordinate(starting point) = 85,165
                    g.fillRect(10+spacing + column * 80, spacing + row * 80 + 80,
                            80 - 2 * spacing, 80 - 2 * spacing);
                }
            }
            //Reset value of clicked after it is consumed.
            clickedX = -100;
            clickedY = -100;

            if (gameOverFlag)
                quitGame = true;
        }

    }

    public class Click implements MouseListener {


        @Override
        public void mouseClicked(MouseEvent e) {
           this.handleMouseEvent(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        public void handleMouseEvent(MouseEvent e){

            int xcordinate = e.getX();      // returns x coordinate from mouse click
            int ycoordinate = e.getY();     // returns y coordinate from mouse click

            clickedY = (xcordinate - spacing) / 80;             // to calculate column number
            clickedX = (ycoordinate - (spacing + 80)) / 80;     // to calculate row number


            if (clickedX != 0){
                //invalid case. Someone clicked somewhere other than 0th row.
                clickedX = -100;
                clickedY = -100;
                return;
            } else {
                if (turn ==1)
                    turn = 2;
                else turn = 1;

                controller.updateModelsGrid(clickedX, clickedY, turn);

            }

        }

    }
}
