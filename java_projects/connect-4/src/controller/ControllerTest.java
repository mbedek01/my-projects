package controller;

import model.Model;
import org.junit.Test;
import view.WindowView;

import static org.junit.Assert.*;

public class ControllerTest {

    private Controller testController;
    int MAX_ROW = 7;
    int MAX_COLUMN = 7;

    @org.junit.Before
    public void setUp() throws Exception {

        testController = new Controller();
    }


    @org.junit.Test
    public void updateModelsGrid() {

        // create expected grid initialized to 0 (WHITE_COLOR)
        int[][]expectedGrid = new int[MAX_ROW][MAX_COLUMN];
        for(int i=0; i<MAX_ROW; i++) {
            for (int j = 0; j < MAX_COLUMN; j++) {
                expectedGrid[i][j] = 0;
            }
        }

        // Test 1: If user clicks outside of grid, grid is unchanged
        testController.updateModelsGrid(-2,9,1);
        assertArrayEquals(expectedGrid, testController.model.getModelGrid());

        // Test2: If player clicks inside the grid, but not on top row - Grid is unchanged
        testController.updateModelsGrid(4, 4,2);
        assertArrayEquals(expectedGrid, testController.model.getModelGrid());

    }

    @org.junit.Test
    public void updateViewsGrid() {

        // Test 1: Clicks in column 0
        testController.updateViewsGrid(6,0,1);
        testController.updateViewsGrid(5,0,2);
        int[][] expectedGrid = new int[MAX_ROW][MAX_COLUMN];
        expectedGrid[6][0] = 1;
        expectedGrid[5][0] = 2;
        assertArrayEquals(testController.refToView.getViewColorGrid(), expectedGrid);
    }

    @org.junit.Test
    public void checkIfGameIsOver() {

        // Since, this method is called from the updateModelsGrid() method of the controller, isolated calls to
        // checkIfGameIsOver will not work. This is because the updateModelsGrid() calls 2 other methods in addition to
        // checkIfGameIsOver():
        //      1. updateModelsGrid
        //      2. updateViewsGrid
        /** With isolated call to checkIfGameIsOver method, the model & view's grid will not get updated & therefore,
         * we might get wrong results resulting in test to fail.
         * Note: x is always 0 as only clicks on the top row are valid clicks
         */

        /* Test1: Check if game is over after vertical win */
        testController.updateModelsGrid(0,2,1);
        // also calls testController.checkIfGameIsOver(6,0,1, 4));

        testController.updateModelsGrid(0,2,1);
        // also calls testController.checkIfGameIsOver(5,0,1, 4));

        assertEquals(false, testController.refToView.gameOverFlag);     // checked after 2 clicks

        testController.updateModelsGrid(0,2,1);
        // also calls testController.checkIfGameIsOver(4,0,1, 4));

        testController.updateModelsGrid(0,2,1);
        // also calls testController.checkIfGameIsOver(3,0,1, 4));

        assertEquals(true, testController.refToView.gameOverFlag);      // checked after 4 clicks


        //--------------------------------------------------------------------------------------------------//

        // Test2: Check if game is over after horizontal win
        testController.updateModelsGrid(0,1,2);
        // also calls testController.checkIfGameIsOver(6,1,2, 4));

        testController.updateModelsGrid(0,2,2);
        // also calls testController.checkIfGameIsOver(6,2,2, 4));

        testController.updateModelsGrid(0,3,2);
        // also calls testController.checkIfGameIsOver(6,3,2, 4));

        testController.updateModelsGrid(0,4,2);
        // also calls testController.checkIfGameIsOver(6,4,2, 4));

        assertEquals(true, testController.refToView.gameOverFlag);      // checked after 4 clicks

        // ------------------------------------------------------------------------------------------------ //

        // Test3: Check if game is over after right diagonal win

        testController.updateModelsGrid(0,1,2);
        // also calls testController.checkIfGameIsOver(6,1,2, 4));

        testController.updateModelsGrid(0,2,1);
        // also calls testController.checkIfGameIsOver(6,2,1, 4));

        testController.updateModelsGrid(0,3,2);
        // also calls testController.checkIfGameIsOver(6,3,2, 4));

        testController.updateModelsGrid(0,4,1);
        // also calls testController.checkIfGameIsOver(6,4,1, 4));

        testController.updateModelsGrid(0,2,2);
        // also calls testController.checkIfGameIsOver(5,2,2, 4));

        testController.updateModelsGrid(0,1,1);
        // also calls testController.checkIfGameIsOver(5,1,1, 4));

        testController.updateModelsGrid(0,0,2);
        // also calls testController.checkIfGameIsOver(6,0,2, 4));

        testController.updateModelsGrid(0,0,1);
        // also calls testController.checkIfGameIsOver(5,0,1, 4));

        testController.updateModelsGrid(0,0,2);
        // also calls testController.checkIfGameIsOver(4,0,2, 4));

        testController.updateModelsGrid(0,3,1);
        // also calls testController.checkIfGameIsOver(5,3,1, 4));

        testController.updateModelsGrid(0,0,2);
        // also calls testController.checkIfGameIsOver(3,3,2, 4));

        testController.updateModelsGrid(0,2,1);
        // also calls testController.checkIfGameIsOver(4,2,1, 4));

        testController.updateModelsGrid(0,1,2);
        // also calls testController.checkIfGameIsOver(4,1,2, 4));

        assertEquals(true, testController.refToView.gameOverFlag);      // after diagonal is formed

        // ------------------------------------------------------------------------------------------------ //

        // Test4: Check if game is over after left diagonal win

        testController.updateModelsGrid(0,1,2);
        // also calls testController.checkIfGameIsOver(6,1,2, 4));

        testController.updateModelsGrid(0,2,1);
        // also calls testController.checkIfGameIsOver(6,2,1, 4));

        testController.updateModelsGrid(0,3,2);
        // also calls testController.checkIfGameIsOver(6,3,2, 4));

        testController.updateModelsGrid(0,4,1);
        // also calls testController.checkIfGameIsOver(6,4,1, 4));

        testController.updateModelsGrid(0,2,2);
        // also calls testController.checkIfGameIsOver(5,2,2, 4));

        testController.updateModelsGrid(0,3,1);
        // also calls testController.checkIfGameIsOver(5,3,1, 4));

        //assertEquals(false, testController.refToView.gameOverFlag);      // checked before diagonal complete

        testController.updateModelsGrid(0,3,2);
        // also calls testController.checkIfGameIsOver(4,3,2, 4));

        testController.updateModelsGrid(0,4,1);
        // also calls testController.checkIfGameIsOver(5,4,1, 4));

        testController.updateModelsGrid(0,2,2);
        // also calls testController.checkIfGameIsOver(4,2,2, 4));

        testController.updateModelsGrid(0,4,1);
        // also calls testController.checkIfGameIsOver(4,4,1, 4));

        testController.updateModelsGrid(0,4,2);
        // also calls testController.checkIfGameIsOver(3,4,2, 4));

        assertEquals(true, testController.refToView.gameOverFlag);      // checked after diagonal complete
    }
}