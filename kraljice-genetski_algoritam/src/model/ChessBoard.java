package model;

import fitness.FitnessFunction;

import java.util.ArrayList;
import java.util.Random;

public class ChessBoard {

    private ArrayList<ArrayList<Boolean>> boardState;

    private Integer size;
    private Integer fitness     = null;

    public ChessBoard(Integer n) {

        if (n < 1) {
            throw new RuntimeException("\nInvalid board size argument. Value inputted = " + n.toString());
        }

        this.size = n;
        this.boardState = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            ArrayList<Boolean> row = new ArrayList();
            for (int j = 0; j < n; j++) {
                row.add(false);
            }
            this.boardState.add(row);
        }
    }

    public void evaluateFitness() {
        this.setFitness(FitnessFunction.evaluate(this));
    }

    public static ChessBoard generateRandomChessBoard(Integer n) {
        ChessBoard result = new ChessBoard(n);
        Random rand = new Random();

        for (ArrayList<Boolean> row : result.getBoardState()) {
            Integer queenSquare = rand.nextInt(4);
            row.set(queenSquare, true);
        }
        return result;
    }

    public void clearBoard() {
        for (ArrayList<Boolean> row : this.getBoardState()) {
            for (int colNum = 0; colNum < this.getSize(); colNum++) {
                row.set(colNum, false);
            }
        }
    }

    public void printChessBoard() {
        for (ArrayList<Boolean> row : this.boardState) {
            for (Boolean squareState : row) {
                if (squareState) {
                    System.out.print("Q  ");
                } else {
                    System.out.print("_  ");
                }
            }
            System.out.println();
        }
        if (fitness != null) System.out.println("Fitness:    " + this.fitness);
        else System.out.println("Fitness not evaluated!");
        System.out.println();
    }

    public ChessBoard returnCopy() {
        ChessBoard chessBoardCopy = new ChessBoard(this.size);
        chessBoardCopy.setFitness(this.fitness);
        chessBoardCopy.setSize(this.size);
        chessBoardCopy.setBoardState(this.getBoardState());
        return chessBoardCopy;
    }

    public void     setSize(Integer size) { this.size = size; }

    public void     setFitness(Integer fitness) { this.fitness = fitness; }

    public void     setBoardState(ArrayList<ArrayList<Boolean>> boardState) { this.boardState = new ArrayList<>(boardState); }

    public void     setSquareValue(Integer x, Integer y, Boolean value) { this.boardState.get(x).set(y, value); }

    public Integer  getSize() { return this.size; }

    public Integer  getFitness() { return this.fitness; }

    public ArrayList<ArrayList<Boolean>> getBoardState() { return this.boardState; }

    public Boolean  getSquareValue(Integer x, Integer y) { return this.boardState.get(x).get(y); }

}
