package main;

import org.encog.engine.network.activation.*;
import org.encog.ml.data.MLData;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.platformspecific.j2se.data.image.ImageMLData;
import org.encog.platformspecific.j2se.data.image.ImageMLDataSet;
import org.encog.util.downsample.RGBDownsample;


import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static Map<Character,Integer> dict = new HashMap<>(){{
        put('0', 0);
        put('1', 1);
        put('2', 2);
        put('3', 3);
        put('4', 4);
        put('5', 5);
        put('6', 6);
        put('7', 7);
        put('8', 8);
        put('9', 9);
        put('/', 10);
        put('-', 11);
    }};

    public static Integer telephoneNumberLength = 11;

    public static Integer numberOfTelephoneNumbers     = 8;
    public static Integer numberOfTelephoneNumbersTest = 2;

    public static Integer downSampleHeight = 14;
    public static Integer downSampleWidth  = 8;

    public static boolean epochOutput       = true;
    public static boolean phoneNumberOutput = true;

    public static double  sumofEpochs              = 0.0;
    public static double  sumOfErrorsOnTestSet     = 0.0;
    public static double  sumOfErrorsOnTrainingSet = 0.0;

    public static Integer nRuns = 1;

    public static void main(String[] args) {

        for (int i = 0; i < nRuns; i++) {
            System.out.print("Run #" + (i + 1));
            doARun();
        }

        System.out.println("Average epochs:                             " + sumofEpochs/nRuns);
        System.out.println("Average correct percentage on training set: " + (100.0 - (sumOfErrorsOnTrainingSet/nRuns)));
        System.out.println("Average correct percentage on test set:     " + (100.0 - (sumOfErrorsOnTestSet/nRuns)));

    }

    public static void doARun() {

        RGBDownsample downsample = new RGBDownsample();

        List<String> ideal = Stream.of(
                        "091/123-432",
                        "091-111/222",
                        "093-667-899",
                        "098/657/800",
                        "099-156/722",
                        "098/467-891",
                        "098-234-731",
                        "091-345-678",
                        "094-513/741",
                        "094-218/869"
                ).collect(Collectors.toList());

        double[][] output = getOutputFrom(ideal);

        ImageMLDataSet testSet = loadTestSet(downsample, output);
        testSet.downsample(downSampleHeight, downSampleWidth);

        ImageMLDataSet trainingSet = loadTrainingSet(downsample, output);
        trainingSet.downsample(downSampleHeight, downSampleWidth);

        Integer inputNodes = trainingSet.getInputSize();

        BasicNetwork network = new BasicNetwork();


        network.addLayer(new BasicLayer(null, false, trainingSet.getInputSize()));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 100));
        //network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 100));
        //network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 70));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, dict.size()));

        /*
        network.addLayer(new BasicLayer(new ActivationLinear() , true, trainingSet.getInputSize()));
        network.addLayer(new BasicLayer(new ActivationTANH(), false, 90));
        network.addLayer(new BasicLayer(new ActivationTANH(), false, 90));
        //network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 70));
        network.addLayer(new BasicLayer(null, true, dict.size()));
*/

        network.getStructure().finalizeStructure();
        network.reset();


        //final Backpropagation train = new Backpropagation(network, trainingSet);
        //final ManhattanPropagation train = new ManhattanPropagation(network, trainingSet, 0.01);
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
        train.addStrategy(new StopTrainingStrategy());

        int epoch = 1;
        do {
            train.iteration();
            if (epochOutput) System.out.println("Epoch #" + epoch + " Error:" + train.getError() );
            epoch++;
        } while (train.getError() > 0.001);
        train.finishTraining();

        System.out.println("\nTraining set ");
        printTrainingSet(trainingSet, ideal, network);

        System.out.println("\nTesting set  ");
        printTestSet(testSet, ideal, network);

        System.out.println("\nEpochs: " + epoch + "\n");
        sumofEpochs += epoch;

    }

    public static void printTrainingSet(ImageMLDataSet dataSet, List<String> ideal, BasicNetwork network) {

        sumOfErrorsOnTrainingSet += printSet(dataSet, ideal, network, 0);

    }

    public static void printTestSet(ImageMLDataSet dataSet, List<String> ideal, BasicNetwork network) {

        sumOfErrorsOnTestSet += printSet(dataSet, ideal, network, numberOfTelephoneNumbers * 11);

    }

    public static double printSet(ImageMLDataSet dataSet, List<String> ideal, BasicNetwork network, Integer inputI) {

        ArrayList<String> resultStrings = new ArrayList<>();
        ArrayList<String> idealStrings   = new ArrayList<>();

        int i = inputI;

        double nWrongChars = 0.0;
        double nChars      = 0.0;

        String resultString = "";
        for (MLDataPair pair : dataSet) {

            if (i % 11 == 0) {
                idealStrings.add(ideal.get(i/11));
                if (phoneNumberOutput) System.out.print("\nTraženo " + ideal.get(i/11) + "\n        ");
            }

            final MLData output2 = network.compute(pair.getInput());
            List<Integer> actualIndexList = Arrays
                    .stream(IntStream.range(0, output2.getData().length).toArray())
                    .boxed().collect(Collectors.toList())
                    .stream().sorted((o1, o2) -> {
                        if (output2.getData()[o1] - output2.getData()[o2] < 0)
                            return 1;
                        else if (output2.getData()[o1] - output2.getData()[o2] > 0)
                            return -1;
                        return 0;
                    }).collect(Collectors.toList());

            resultString += keys(dict, actualIndexList.get(0)).findFirst().get();
            if (phoneNumberOutput) System.out.print(keys(dict, actualIndexList.get(0)).findFirst().get());
            if (i % 11 == 10 && phoneNumberOutput) System.out.println();
            i++;

            if (i % 11 == 0) {
                resultStrings.add(resultString);
                resultString = "";
            }

        }

        double errorPercentage = calculateError(resultStrings, idealStrings);
        System.out.print("Percentage correct " + (100.0 - errorPercentage) + "");
        return errorPercentage;
    }


    public static double calculateError(ArrayList<String> resultStrings, ArrayList<String> ideal) {

        double nCharSum = 0.0;
        double nCharWrong = 0.0;
        for (int i = 0; i < resultStrings.size(); i++) {

            String idealString  = ideal.get(i);
            String resultString = resultStrings.get(i);

            for (int j = 0; j < idealString.length(); j++) {
                nCharSum++;
                if (idealString.charAt(j) != resultString.charAt(j)) {
                    nCharWrong++;
                }
            }

        }

        return (nCharWrong/nCharSum) * 100.0;
    }

    public static double[][] getOutputFrom(List<String> brojevi){
        double[][] output = new double[brojevi.size()*telephoneNumberLength][dict.size()];

        for (int i = 0; i < brojevi.size(); i++) {

            for (int j = 0; j < brojevi.get(i).length(); j++) {
                double[] broj = new double[dict.size()];
                broj[dict.get(brojevi.get(i).charAt(j))] = 1.0;
                output[i*brojevi.get(i).length() + j] = broj;
            }

        }
        return output;
    }

    public static ImageMLDataSet loadTrainingSet(RGBDownsample downsample, double[][] output) {

        ImageMLDataSet trainingSet = new ImageMLDataSet(downsample, false, 1.0, -1.0);

        Path dir = Path.of("brojevi");
        List<Path> direktoriji = new ArrayList<>();
        try {
            direktoriji = Files.list(dir).filter(dat -> dat.getFileName().toString().length() > 4).sorted((s1,s2) ->{
                        Integer val1 = null, val2 = null;
                        Pattern p = Pattern.compile("([0-9]+)");
                        Matcher m = p.matcher(s1.getFileName().toString());
                        if (m.find()) {
                            int len = m.group().length();
                            val1 = Integer.parseInt( m.group());
                            m = p.matcher(s2.getFileName().toString());
                            if (m.find()) {
                                len = m.group().length();
                                val2 = Integer.parseInt( m.group());
                            }
                        }

                        return val1.compareTo(val2);
                    })
                    .collect(Collectors.toList());
            for (int i = 1; i <= numberOfTelephoneNumbers; i++) {
                List<Path> datoteke = Files
                        .list(direktoriji.get(i-1))
                        .filter(dat -> dat.getFileName().toString().length() > 10)
                        .sorted((s1,s2) -> {
                            Integer val1 = null, val2 = null;
                            Pattern p = Pattern.compile("([0-9]+)\\.");
                            Matcher m = p.matcher(s1.getFileName().toString());
                            if (m.find()) {
                                int len = m.group().length();
                                val1 = Integer.parseInt( m.group().substring(0,len-1));
                                m = p.matcher(s2.getFileName().toString());
                                if (m.find()) {
                                    len = m.group().length();
                                    val2 = Integer.parseInt( m.group().substring(0,len-1));
                                }
                            }
                            return val1.compareTo(val2);
                        }).collect(Collectors.toList());

                for (int j = 1; j <= datoteke.size(); j++) {
                    if (datoteke.get(j-1).endsWith(String.format("broj%d-digit%d.PNG", i, j))){

                        Image img = ImageIO.read(datoteke.get(j-1).toFile());
                        ImageMLData data = new ImageMLData(img) ;
                        trainingSet.add(data, new BasicMLData(output[((i-1) * telephoneNumberLength) + j-1]));

                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return trainingSet;
    }

    public static ImageMLDataSet loadTestSet(RGBDownsample downsample, double[][] output) {
        Path dir = Path.of("brojevi");
        List<Path> direktoriji = new ArrayList<>();
        ImageMLDataSet testSet = new ImageMLDataSet(downsample, false, 1.0, -1.0);
        try {
            direktoriji = Files.list(dir).filter(dat -> dat.getFileName().toString().length() > 4).sorted((s1,s2) ->{
                        Integer val1 = null, val2 = null;
                        Pattern p = Pattern.compile("([0-9]+)");
                        Matcher m = p.matcher(s1.getFileName().toString());
                        if (m.find()) {
                            int len = m.group().length();
                            val1 = Integer.parseInt( m.group());
                            m = p.matcher(s2.getFileName().toString());
                            if (m.find()) {
                                len = m.group().length();
                                val2 = Integer.parseInt( m.group());
                            }
                        }

                        return val1.compareTo(val2);
                    })
                    .collect(Collectors.toList());
            for (int i = numberOfTelephoneNumbers + 1; i <= numberOfTelephoneNumbersTest + numberOfTelephoneNumbers; i++) {
                List<Path> datoteke = Files.list(direktoriji.get(i-1)).filter(dat -> dat.getFileName().toString().length() > 10).sorted((s1,s2) ->{
                    Integer val1 = null, val2 = null;
                    Pattern p = Pattern.compile("([0-9]+)\\.");
                    Matcher m = p.matcher(s1.getFileName().toString());
                    if (m.find()) {
                        int len = m.group().length();
                        val1 = Integer.parseInt( m.group().substring(0,len-1));
                        m = p.matcher(s2.getFileName().toString());
                        if (m.find()) {
                            len = m.group().length();
                            val2 = Integer.parseInt( m.group().substring(0,len-1));
                        }
                    }

                    return val1.compareTo(val2);

                }).collect(Collectors.toList());
                for (int j = 1; j <= datoteke.size(); j++) {
                    if (datoteke.get(j-1).endsWith(String.format("broj%d-digit%d.PNG", i, j))){

                        Image img = ImageIO.read(datoteke.get(j-1).toFile());
                        ImageMLData data = new ImageMLData(img);

                        testSet.add(data, new BasicMLData(output[((i-1) * telephoneNumberLength) + j-1]));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return testSet;
    }

    public static  <K, V> Stream<K> keys(Map<K, V> map, V value) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey);
    }

}
