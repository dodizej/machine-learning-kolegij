package recombination;


import model.ChessBoard;

import java.util.ArrayList;
import java.util.Random;

public class SinglePointRecombination {

    public static final float crossoverRate = 0.2f;

    public static ArrayList<ChessBoard> execute(ArrayList<ChessBoard> oldPopulation) {
        Random rand = new Random();

        Integer unitsToCrossover = (int) (oldPopulation.size() * crossoverRate);
        Integer boardSize        = oldPopulation.get(0).getSize();
        Integer numberOfSquares  = boardSize * boardSize;

        ArrayList<ChessBoard> newPopulation = new ArrayList<>();

        while (unitsToCrossover > 1 && oldPopulation.size() == 0) {
            ChessBoard first = oldPopulation.get(rand.nextInt(oldPopulation.size()));
            oldPopulation.remove(first);

            ChessBoard second = oldPopulation.get(rand.nextInt(oldPopulation.size()));
            oldPopulation.remove(second);

            Integer crossOverPoint = rand.nextInt(numberOfSquares);

            ChessBoard newFirst  = first.returnCopy();
            ChessBoard newSecond = first.returnCopy();

            for (int i = 0; i < crossOverPoint; i++) {
                Integer row    = i / boardSize;
                Integer column = i % boardSize;
                newFirst.setSquareValue(row, column, first.getSquareValue(row, column));
                newSecond.setSquareValue(row, column, second.getSquareValue(row, column));
            }

            newPopulation.add(newFirst);
            newPopulation.add(newSecond);
            unitsToCrossover -= 2;
        }

        while (oldPopulation.size() > 0) {
            newPopulation.add(oldPopulation.get(0).returnCopy());
            oldPopulation.remove(0);
        }

        return newPopulation;
    }

}
