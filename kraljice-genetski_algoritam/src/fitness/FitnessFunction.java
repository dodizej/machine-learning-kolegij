package fitness;


import model.ChessBoard;

import java.util.ArrayList;

public class FitnessFunction {

    public static final int SOLUTION_REWARD_KOEF  = 200;

    public static final int QUEENS_ATTACKING_KOEF = 10;

    public static Integer evaluate(ChessBoard chessBoard) {

        Integer fitness = 1000;

        Integer numberOfAttacks = areQueensAttacking(chessBoard);
        //System.out.println("NUMBER OF ATTACKS: " + numberOfAttacks);
        if (numberOfAttacks > 0) {
            fitness = fitness - (numberOfAttacks * numberOfAttacks * QUEENS_ATTACKING_KOEF);
        } else {
            fitness = fitness + SOLUTION_REWARD_KOEF;
        }

        if (fitness < 0) {
            fitness = 0;
        }

        return fitness;
    }

    public static Integer areQueensAttacking(ChessBoard chessBoard) {

        Integer numberOfAttacks = 0;
        //check rows
        for (ArrayList<Boolean> row : chessBoard.getBoardState()) {
            Integer queensInRow = 0;
            for (Boolean squareValue : row) {
                if (squareValue) queensInRow++;
            }
            if (queensInRow > 1) {
                numberOfAttacks = numberOfAttacks + queensInRow - 1;
            }
        }

        //check columns
        for (int columnNum = 0; columnNum < chessBoard.getSize(); columnNum++) {
            Integer queensInColumn = 0;
            for (ArrayList<Boolean> row : chessBoard.getBoardState()) {
                if (row.get(columnNum)) queensInColumn++;
            }
            if (queensInColumn > 1) {
                numberOfAttacks = numberOfAttacks + queensInColumn - 1;
            }
        }

        Integer diagonalAttacks = 0;
        //check diagonals
        for (int rowNum = 0; rowNum < chessBoard.getSize(); rowNum++) {
            for (int colNum = 0; colNum < chessBoard.getSize(); colNum++) {
                if (chessBoard.getSquareValue(rowNum, colNum)) {

                    Integer tempRowNum = rowNum - 1;
                    Integer tempColNum = colNum - 1;
                    while (tempRowNum >= 0 && tempColNum >= 0) {
                        if (chessBoard.getSquareValue(tempRowNum, tempColNum)) {
                            diagonalAttacks++;
                            break;
                        }
                        tempRowNum--;
                        tempColNum--;
                    }

                    tempRowNum = rowNum + 1;
                    tempColNum = colNum + 1;
                    while (tempRowNum < chessBoard.getSize() && tempColNum < chessBoard.getSize()) {
                        if (chessBoard.getSquareValue(tempRowNum, tempColNum)) {
                            diagonalAttacks++;
                            break;
                        }
                        tempRowNum++;
                        tempColNum++;
                    }

                    tempRowNum = rowNum + 1;
                    tempColNum = colNum - 1;
                    while (tempRowNum < chessBoard.getSize() && tempColNum >= 0) {
                        if (chessBoard.getSquareValue(tempRowNum, tempColNum)) {
                            diagonalAttacks++;
                            break;
                        }
                        tempRowNum++;
                        tempColNum--;
                    }

                    tempRowNum = rowNum - 1;
                    tempColNum = colNum + 1;
                    while (tempRowNum >= 0 && tempColNum < chessBoard.getSize()) {
                        if (chessBoard.getSquareValue(tempRowNum, tempColNum)) {
                            diagonalAttacks++;
                            break;
                        }
                        tempRowNum--;
                        tempColNum++;
                    }

                }
            }
        }

        return (diagonalAttacks / 2) + numberOfAttacks;
    }

}
