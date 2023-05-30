package com.company;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    public static int MAX_WORD_LENGTH   = 10;

    public static int NUMBER_OF_LETTERS = 26;

    public static String convertInputToWord(double[] input) {

        String result = "";
        double[] tempList = new double[NUMBER_OF_LETTERS];

        for (int i = 0; i < input.length; i += 26) {

            for (int j = 0; j < 26; j++) {
                if (input[i+j] != 0.0) {
                    result += Character.toString((char) j + 'a');
                    break;
                }
            }
        }
        return result;
    }

    public static double[][] convertList(List<List<Double>> input) {

        double[][] output = new double[input.size()][input.get(0).size()];
        for (int i = 0; i < input.size(); i++) {
            for (int j = 0; j < input.get(0).size(); j++) {
                output[i][j] = input.get(i).get(j);
            }
        }
        return output;
    }

    public static double[] convertSingleList(List<Double> input) {

        double[] output = new double[input.size()];
        for (int i = 0; i < input.size(); i++) {
                output[i] = input.get(i);
        }
        return output;
    }

    public static List<Double> convertWordToInput(String inputString) {

        List<Double> oneHotResult = new ArrayList<>();

        for (int i = 0; i < MAX_WORD_LENGTH; i++) {
            if (i < inputString.length())
                oneHotResult.addAll(convertChar(inputString.charAt(i), i, inputString.length()));
            else
                oneHotResult.addAll(createEmptyHotCode());
        }

        return oneHotResult;
    }

    public static List<Double> convertChar(Character inputCharacter, int index, int strLen) {

        inputCharacter = Character.toLowerCase(inputCharacter);
        List<Double> oneHotCode = createEmptyHotCode();

        int charAscii = (int) inputCharacter - 'a';
        if (index == 0 || index == strLen - 1) {
            oneHotCode.set(charAscii, 1.0);
        } else {
            oneHotCode.set(charAscii, 1.0);
        }
        return oneHotCode;
    }

    public static List<Double> createEmptyHotCode() {
        List<Double> oneHotCode = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            oneHotCode.add(0.0);
        }
        return oneHotCode;
    }

    public static void printWord(List<Double> input) {

        for (int i = 0; i < input.size(); i++) {
            if (i % NUMBER_OF_LETTERS == 0) System.out.println();
            System.out.print(input.get(i) + " ");
        }
        System.out.println();
    }

    public static void printWord(Double input[][]) {

        for (Double[]                                                                                                                                                                                                                                                                                                                                                          letter : input) {
            for (Double bit : letter) {
                System.out.print(bit + "  ");
            }
            System.out.println();
        }

    }

    public static List<Double> convertWordToOutput(String s, long size) {
        return null;
    }

}
