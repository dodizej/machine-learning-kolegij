package selection;

import fitness.FitnessFunction;
import model.ChessBoard;

import java.util.ArrayList;
import java.util.Random;

public class KTournament {

    public  static final Float   K_KOEF = 0.2f;

    private static       Integer K;

    public static ArrayList<ChessBoard> getSubPopulation(ArrayList<ChessBoard> population) {

        K = (int) (population.size() * K_KOEF);

        ArrayList<ChessBoard> subPopulation = new ArrayList<>();
        Random rand = new Random();

        while (subPopulation.size() < population.size()) {
            ArrayList<ChessBoard> tournamentEntries = new ArrayList<>();
            for (int i = 0; i < K; i++) {
                Integer randomIndex = rand.nextInt(population.size());
                tournamentEntries.add(population.get(randomIndex));
            }

            ChessBoard tournamentWinner = tournament(tournamentEntries);
            subPopulation.add(tournamentWinner);
        }

        return subPopulation;
    }

    public static ChessBoard tournament(ArrayList<ChessBoard> tournamentEntries) {
        while (tournamentEntries.size() > 1) {
            for (int i = 0; i < tournamentEntries.size(); i += 2) {
                if (i + 1 >= tournamentEntries.size()) break;
                if (tournamentEntries.get(i).getFitness() >
                        tournamentEntries.get(i + 1).getFitness()) {
                    tournamentEntries.remove(tournamentEntries.get(i + 1));
                } else {
                    tournamentEntries.remove(tournamentEntries.get(i));
                }
            }
        }
        return tournamentEntries.get(0);
    }

}
