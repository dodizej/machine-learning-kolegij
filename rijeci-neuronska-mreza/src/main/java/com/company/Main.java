package com.company;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationStep;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static Path pathToDict = Path.of("rijeci");

    public static double ukupanBrojEpoha              = 0.0;

    public static double ukupanBrojTocnihRjesenja     = 0.0;

    public static double ukupniPostotakTocnihRjesenja = 0.0;

    public static void main(String[] args) {

        JsonParser.init(pathToDict);

        int nRuns = 1;

        for (int i = 0; i < nRuns; i++) {
            doARun(i);
        }

        /*
        System.out.println("Prosjecan broj tocnih rjesenja:     " + ukupanBrojTocnihRjesenja     / nRuns);
        System.out.println("Prosjecan postotak tocnih rjesenja: " + ukupniPostotakTocnihRjesenja / nRuns);
        System.out.println("Prosjecan broj epoha:               " + ukupanBrojEpoha              / nRuns);
        */

        Encog.getInstance().shutdown();
    }


    public static void doARun(int nRun) {

        //System.out.println("Run: #" + (nRun+1));

        double[][] input = JsonParser.getInput();
        double[][] output = JsonParser.getOutput();

        MLDataSet trainingSet = new BasicMLDataSet(input, output);
        BasicNetwork network = new BasicNetwork();

        //ULAZNI SLOJ
        network.addLayer(new BasicLayer(null,false,260));

        //SKRIVENI SLOJ
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,260));

        //IZLAZNI SLOJ
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,27));

        network.getStructure().finalizeStructure();
        network.reset();

        //final Backpropagation     train    = new Backpropagation(network, trainingSet);
        final ResilientPropagation  train    = new ResilientPropagation(network, trainingSet);

        int epoch = 1;
        do {
            train.iteration();
            //System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while(train.getError() > 0.005);
        train.finishTraining();

        // test
        int brojTocnih = 0;
        MLDataSet testingSet = new BasicMLDataSet(JsonParser.getTestData(), JsonParser.getTestOutputData());

        System.out.println("Neural Network Results:");
        for(MLDataPair pair: testingSet) {
        //for(MLDataPair pair: trainingSet) {
            final MLData output2 = network.compute(pair.getInput());

            Integer indexIdeal = IntStream.range(0, pair.getIdeal().size())
                    .filter(index -> pair.getIdeal().getData(index) == 1.0)
                    .findFirst()
                    .getAsInt();

            List<Integer> actualIndexList = Arrays.stream(IntStream.range(0, output2.getData().length)
                            .toArray()).boxed().collect(Collectors.toList())
                    .stream().sorted((o1, o2) -> {
                        if (output2.getData()[o1] - output2.getData()[o2] < 0)
                            return 1;
                        else if (output2.getData()[o1] - output2.getData()[o2] > 0)
                            return -1;
                        return 0;
                    }).collect(Collectors.toList());

            if (JsonParser.getUnikRijeci().get(actualIndexList.get(0)).equals(JsonParser.getResultRijeci().get(indexIdeal))) {
                brojTocnih++;
            }

            System.out.printf("ulaz: %-10s izlaz: %-10s ideal: %-10s output at node: %1.3f\n"
                    , Converter.convertInputToWord(pair.getInput().getData())
                    , JsonParser.getUnikRijeci().get(actualIndexList.get(0))
                    , JsonParser.getResultRijeci().get(indexIdeal)
                    , output2.getData()[actualIndexList.get(0)] );

        }

        /*
        ukupanBrojEpoha += epoch;
        ukupanBrojTocnihRjesenja += brojTocnih;
        ukupniPostotakTocnihRjesenja += (brojTocnih/27.0)*100;

        System.out.println("Broj epoha:               " + epoch);
        System.out.println("Broj tocnih:              " + brojTocnih);
        System.out.println("Postotak tocnih rjesenja: " + (brojTocnih/27.0)*100);
        System.out.println();
         */

        Encog.getInstance().shutdown();

    }

}
