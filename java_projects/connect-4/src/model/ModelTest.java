package model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelTest {

    private Model testModel;
    private int MAX_ROW = 7;
    private int MAX_COLUMN = 7;

    @Before
    public void setUp() throws Exception {
        testModel = new Model();
    }

    @Test
    public void initializeColorGrid() {

        int[][] expectedGrid = new int[MAX_ROW][MAX_COLUMN];
        for(int i=0; i<MAX_ROW; i++){
            for(int j=0; j<MAX_COLUMN; j++){
                expectedGrid[i][j] = 0;
            }
        }
        assertArrayEquals(testModel.getModelGrid(), expectedGrid);
    }

    @Test
    public void updateColorGrid() {

        // User clicks on column 0
        assertEquals(6, testModel.updateColorGrid(0,1));
        // User clicks on column 0 again
        assertEquals(5, testModel.updateColorGrid(0,2));
        // User clicks on column 2
        assertEquals(6, testModel.updateColorGrid(2,2));

        // the grid that we expect
        int[][] expectedGrid = new int[MAX_ROW][MAX_COLUMN];

        for(int i=0; i<MAX_ROW; i++) {
            for (int j = 0; j < MAX_COLUMN; j++) {
                expectedGrid[i][j] = 0;
            }
        }
        expectedGrid[6][0] = 1;
        expectedGrid[5][0] = 2;
        expectedGrid[6][2] = 2;

        assertArrayEquals(testModel.getModelGrid(), expectedGrid);

    }

    @Test
    public void checkIfConsecutiveRows() {

        testModel.updateColorGrid(0,1);
        testModel.updateColorGrid(1,1);
        testModel.updateColorGrid(2,1);
        int n = testModel.updateColorGrid(3,1);
        System.out.print(n);

        assertEquals(true, testModel.checkIfConsecutiveRows(6,1,4));
    }

    @Test
    public void checkIfConsecutiveColumns() {

        testModel.updateColorGrid(2,2);
        testModel.updateColorGrid(2,2);
        testModel.updateColorGrid(2,2);
        int n = testModel.updateColorGrid(2,2);
        System.out.print(n);

        assertEquals(true, testModel.checkIfConsecutiveColumns(3, 2,2,
                4));
    }

    @Test
    public void checkIfRightDownDiagonal() {

        // Checks for right diagonal win. User 1 & 2 click alternately.

        testModel.updateColorGrid(1,2);
        testModel.updateColorGrid(2,1);
        testModel.updateColorGrid(3,2);
        testModel.updateColorGrid(4,1);
        testModel.updateColorGrid(2,2);
        testModel.updateColorGrid(3,1);
        testModel.updateColorGrid(3,2);
        testModel.updateColorGrid(4,1);
        testModel.updateColorGrid(2,2);
        testModel.updateColorGrid(4,1);
        testModel.updateColorGrid(4,2);

        assertEquals(true, testModel.checkIfRightDownDiagonal(3,4,2,4));

    }

    @Test
    public void checkIfLeftDownDiagonal() {

        // checks for left diagonal win. User 1 & 2 click alternately

        testModel.updateColorGrid(1,2);
        testModel.updateColorGrid(2,1);
        testModel.updateColorGrid(3,2);
        testModel.updateColorGrid(4,1);
        testModel.updateColorGrid(2,2);
        testModel.updateColorGrid(1,1);
        testModel.updateColorGrid(0,2);
        testModel.updateColorGrid(0,1);
        testModel.updateColorGrid(0,2);
        testModel.updateColorGrid(3,1);
        testModel.updateColorGrid(0,2);
        testModel.updateColorGrid(2,1);
        testModel.updateColorGrid(1,2);

        assertEquals(true, testModel.checkIfLeftDownDiagonal(4,1,2,4));


    }
}