import fitness.FitnessFunction;
import model.ChessBoard;
import mutation.Mutation;
import recombination.SinglePointRecombination;
import selection.KTournament;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class main  {

    public static void main (String args[]) {

        Integer BOARD_SIZE      = 6;
        Integer POPULATION_SIZE = 1000;
        Integer LIMIT           = 2000;

        averageGenerationsUntilSolution(LIMIT, POPULATION_SIZE, BOARD_SIZE);

    }

    public static void averageGenerationsUntilSolution(Integer LIMIT, Integer POPULATION_SIZE, Integer BOARD_SIZE) {
        Integer genSum          = 0;
        Integer numberOfSamples = 5;
        double timeSum = 0;
        for (int i = 0; i < numberOfSamples; i++) {
            Map<String, Integer> result = runUntilSolved(LIMIT, POPULATION_SIZE, BOARD_SIZE);
            genSum += result.get("generations");
            timeSum += (result.get("time") / 1000.0);
            System.out.println("FINISHED AFTER: " + result.get("generations"));
            System.out.println("TIME: " + result.get("time") / 1000.0);
            System.out.println();
        }
        System.out.println("\n\nTOTAL AVERAGE " + (genSum/numberOfSamples) + " GENERATIONS");
        System.out.println("TIME " + (timeSum/numberOfSamples));
    }

    public static Map<String, Integer> runUntilSolved(Integer LIMIT, Integer POPULATION_SIZE, Integer BOARD_SIZE) {
        ArrayList<ChessBoard> population = new ArrayList<>();
        generatePopulation(population, POPULATION_SIZE, BOARD_SIZE);
        Map<String, Integer> result = new HashMap<>();
        //Long startTime = System.nanoTime();
        Long startTime = System.currentTimeMillis();
        Integer generationNumber = 0;
        Boolean solutionFound = false;
        while (!solutionFound && generationNumber < LIMIT) {

            //System.out.println("STARTING GENERATION: " + (generationNumber));
            population = KTournament.getSubPopulation(population);
            population = SinglePointRecombination.execute(population);
            Mutation.mutate(population);
            evaluatePopulation(population);

            ChessBoard bestChessBoard = population.get(0);
            for (ChessBoard chessBoard : population) {
                if (chessBoard.getFitness() > bestChessBoard.getFitness()) {
                    bestChessBoard = chessBoard;
                }
            }

            //System.out.println("GENERATION " + generationNumber + " DONE");
            //System.out.println("BEST:");
            //bestChessBoard.printChessBoard();

            Integer numOfQueens = 0;
            for (ArrayList<Boolean> row : bestChessBoard.getBoardState()){
                for (Boolean value : row) {
                    if (value) numOfQueens++;
                }
            }
            if (FitnessFunction.areQueensAttacking(bestChessBoard) == 0 && numOfQueens == bestChessBoard.getSize()) {
                solutionFound = true;
                //bestChessBoard.printChessBoard();
            }

            generationNumber++;
        }
        //Long endtime = System.nanoTime();
        Long endtime = System.currentTimeMillis();
        result.put("generations", generationNumber);
        result.put("time", (int) (endtime - startTime));
        return result;
    }

    public static void generatePopulation(ArrayList<ChessBoard> population, Integer populationSize, Integer boardSize) {
        for (int i = 0; i < populationSize; i++) {
            population.add(ChessBoard.generateRandomChessBoard(boardSize));
        }
        evaluatePopulation(population);
    }

    public static void evaluatePopulation(ArrayList<ChessBoard> population) {
        for (ChessBoard chessBoard : population) {
            chessBoard.evaluateFitness();
        }
    }

}
