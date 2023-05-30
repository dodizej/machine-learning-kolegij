package mutation;

import model.ChessBoard;

import java.util.ArrayList;
import java.util.Random;

public class Mutation {

    public static final float mutationProbability = 0.35f;

    public static void mutate(ArrayList<ChessBoard> population) {

        Random rand = new Random();
        Integer randBound = (int) (1.0 / mutationProbability);

        for (ChessBoard chessBoard : population) {
            Integer randNumber = rand.nextInt(randBound);
            if (randNumber.equals(randBound / 2)) {
                mutate(chessBoard);
            }
        }
    }

    private static void mutate(ChessBoard chessBoard) {

        Integer boardSize = chessBoard.getSize();
        Random rand = new Random();

        ArrayList<Boolean> rowToFlip = chessBoard.getBoardState().get(rand.nextInt(boardSize));
        for (int i = 0; i < rowToFlip.size(); i++) {
            rowToFlip.set(i, false);
        }

        rowToFlip.set(rand.nextInt(boardSize), true);

    }

}
