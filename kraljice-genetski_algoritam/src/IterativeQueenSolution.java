import model.ChessBoard;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class IterativeQueenSolution {

    public static Boolean solve(ChessBoard chessBoard) {

        ArrayList<ArrayList<Boolean>> chessBoardState = chessBoard.getBoardState();
        final Integer boardSize = chessBoard.getSize();
        Integer startingPosition = 0;
        Integer numQueensPlaced;

        while (startingPosition < boardSize) {
            chessBoard.clearBoard();
            numQueensPlaced = 1;
            chessBoardState.get(0).set(startingPosition, true);
            for (int rowNum = 1; rowNum < chessBoard.getSize(); rowNum++) {
                for (int squareNum = 0; squareNum < boardSize; squareNum++) {
                    chessBoardState.get(rowNum).set(squareNum, true);
                    if (areQueensAttacking(chessBoard)) {
                        chessBoardState.get(rowNum).set(squareNum, false);
                    } else {
                        numQueensPlaced++;
                        break;
                    }
                }
            }
            if (numQueensPlaced.equals(boardSize)) return true;
            startingPosition++;
        }

        return false;
    }

    public static Boolean areQueensAttacking(ChessBoard chessBoard) {

        //check rows
        for (ArrayList<Boolean> row : chessBoard.getBoardState()) {
            Integer queensInRow = 0;
            for (Boolean squareValue : row) {
                if (squareValue) queensInRow++;
            }
            if (queensInRow > 1) {
                return true;
            }
        }

        //check columns
        for (int columnNum = 0; columnNum < chessBoard.getSize(); columnNum++) {
            Integer queensInColumn = 0;
            for (ArrayList<Boolean> row : chessBoard.getBoardState()) {
                if (row.get(columnNum)) queensInColumn++;
            }
            if (queensInColumn > 1) {
                return true;
            }
        }

        //check diagonals
        for (int rowNum = 0; rowNum < chessBoard.getSize(); rowNum++) {
            for (int colNum = 0; colNum < chessBoard.getSize(); colNum++) {
                if (chessBoard.getSquareValue(rowNum, colNum)) {

                    Integer tempRowNum = rowNum - 1;
                    Integer tempColNum = colNum - 1;
                    while (tempRowNum >= 0 && tempColNum >= 0) {
                        if (chessBoard.getSquareValue(tempRowNum, tempColNum)) {
                            return true;
                        }
                        tempRowNum--;
                        tempColNum--;
                    }

                    tempRowNum = rowNum + 1;
                    tempColNum = colNum + 1;
                    while (tempRowNum < chessBoard.getSize() && tempColNum < chessBoard.getSize()) {
                        if (chessBoard.getSquareValue(tempRowNum, tempColNum)) {
                            return true;
                        }
                        tempRowNum++;
                        tempColNum++;
                    }

                    tempRowNum = rowNum + 1;
                    tempColNum = colNum - 1;
                    while (tempRowNum < chessBoard.getSize() && tempColNum >= 0) {
                        if (chessBoard.getSquareValue(tempRowNum, tempColNum)) {
                            return true;
                        }
                        tempRowNum++;
                        tempColNum--;
                    }

                    tempRowNum = rowNum - 1;
                    tempColNum = colNum + 1;
                    while (tempRowNum >= 0 && tempColNum < chessBoard.getSize()) {
                        if (chessBoard.getSquareValue(tempRowNum, tempColNum)) {
                            return true;
                        }
                        tempRowNum--;
                        tempColNum++;
                    }

                }
            }
        }

        return false;
    }

}
