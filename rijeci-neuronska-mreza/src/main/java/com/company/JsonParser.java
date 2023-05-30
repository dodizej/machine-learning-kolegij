package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonParser {

    private static List<JSONArray> rijeci;

    private static Path            pathToDict;

    private static Long            dictSize;

    private static JSONParser      parser;

    private static List<Path>      datoteke;

    private static List<String>    languagesToExclude  =  List.of(new String[]{"Croatian", "Ukrainian"});

    private static List<String>    unikRijeci;

    private static List<String>    testRijeci;

    private static String          testJezik  =  "Ukrainian";

    private static double[][]      testData   =  new double[27][26];

    public static void init(Path path) {
        pathToDict = path;

        try {
            datoteke = Files.list(pathToDict).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        parser = new JSONParser();
        dictSize = datoteke.stream().count();
        rijeci = datoteke.stream().map(path1 -> {
            try (FileReader reader = new FileReader(String.valueOf(path1))){
                return (JSONArray) parser.parse(reader);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        JsonParser.prevediNaLatinicu();
        initTestData();
    }

    public static List<String> getResultRijeci() {
        List<String> hrRijeci = new ArrayList<>();

        for (JSONArray array : rijeci) {
            for (Object obj : array) {
                JSONObject jsonObject = (JSONObject) obj;
                if (jsonObject.get("jezik").equals("Croatian")) {
                    hrRijeci.add((String) jsonObject.get("rijec"));
                }
            }
        }

        return hrRijeci;
    }

    public static double[][] getTestData() {
        return testData;
    }

    public static List<String> getTestRijeci() { return testRijeci; }

    public static List<String> getUnikRijeci() {
        return unikRijeci;
    }

    private static void parsirajPrijevode(JSONObject prijevod) {
        prijevod.replace("rijec", transliterate((String) prijevod.get("rijec")));
    }


    static char[] cirilica = {1112, 1110, ' ', 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с',
            'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    static String[] latinica = {"j", "i", "", "a", "b", "v", "g", "d", "e", "e", "z", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s"
            , "t", "u", "f", "h", "c", "c", "s", "s", "", "i", "", "e", "d", "d", "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "C", "C", "S", "S", "", "I", "", "E", "D", "D", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static String transliterate(String message) {
        StringBuilder builder = new StringBuilder();
        message = Normalizer.normalize(message, Normalizer.Form.NFD);
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < cirilica.length; x++) {
                if (message.charAt(i) == cirilica[x]) {
                    builder.append(latinica[x]);
                    break;
                }
            }
        }
        return builder.toString();
    }

    public static void prevediNaLatinicu(){
        try {
            Files.list(Path.of("rijeci")).forEach(path -> {
                JSONArray noveRijeci = new JSONArray();
                try (FileReader reader = new FileReader(String.valueOf(path)))
                {
                    //Read JSON file
                    Object obj = parser.parse(reader);

                    JSONArray rijeci = (JSONArray) obj;

                    rijeci.forEach( prijevod -> {
                                parsirajPrijevode( (JSONObject) prijevod );
                                noveRijeci.add(prijevod);
                            }
                    );

                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                try(FileWriter writer = new FileWriter(String.valueOf(path))){
                    writer.write(noveRijeci.toJSONString());
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static double[][] getTestOutputData() {
        double[][] result = new double[27][27];
        for (int i = 0; i < 27; i++) {
            double[] tmp = new double[27];
            for (int j = 0; j < 27; j++) {
                if (j == i) tmp[j] = 1.0;
                else tmp[j] = 0.0;
            }
            result[i] = tmp;
        }
        return result;
    }

    public static double[][] getInput() {
        List<List<Double>> rez;
        rez = rijeci.stream().flatMap(rijeciJSON -> {
                List<String> rijeciString = new ArrayList<>();
                rijeciJSON.stream().filter(obj ->
                        !languagesToExclude.contains(((JSONObject) obj)
                                            .get("jezik").toString()))
                                            .forEach(obj ->
                                                    rijeciString.add(((JSONObject) obj).get("rijec").toString().toLowerCase()));
                return rijeciString.stream().map(Converter::convertWordToInput);

        }).collect(Collectors.toList());
        return Converter.convertList(rez);
    }

    public static void getTestInputWords() {
        testRijeci = new ArrayList<>();
        for (JSONArray array : rijeci) {
            for (Object obj : array) {
                JSONObject jsonObject = (JSONObject) obj;
                if (jsonObject.get("jezik").equals(testJezik)) {
                    testRijeci.add((String) jsonObject.get("rijec"));
                }
            }
        }
    }

    public static void initTestData() {
        getTestInputWords();
        for (int i = 0; i < testRijeci.size(); i++) {
            testData[i] = Converter.convertSingleList(Converter.convertWordToInput(testRijeci.get(i)));
        }
    }

    public static double[][] getOutput() {
        List<List<Double>> rez = new ArrayList<>();
        List<String> sveRijeci;
        sveRijeci = rijeci.stream().flatMap(rijeciJSON -> {

            List<String> rijeciString = new ArrayList<>();
            List<JSONObject> rijeciArray = (List<JSONObject>) rijeciJSON.stream()
                    .filter(obj -> languagesToExclude.get(0).equals(((JSONObject) obj).get("jezik").toString()))
                    .collect(Collectors.toList());
            String hrvRijec = (rijeciArray.get(0)).get("rijec").toString();
            for (int i = 0; i < rijeciJSON.size() - languagesToExclude.size(); i++) {
                rijeciString.add(hrvRijec);
            }
          /*          .forEach(obj ->
                            rijeciString.add(((JSONObject) obj).get("rijec").toString().toLowerCase()));*/
            return rijeciString.stream();

        }).collect(Collectors.toList());
        unikRijeci = rijeci.stream().flatMap(rijeciJSON -> {

            List<String> rijeciString = new ArrayList<>();
            rijeciJSON.stream()
                    .filter(obj -> languagesToExclude.get(0).equals(((JSONObject) obj).get("jezik").toString()))
                    .forEach(obj ->
                            rijeciString.add(((JSONObject) obj).get("rijec").toString().toLowerCase()));
            return rijeciString.stream();

        }).collect(Collectors.toList());
        for (int i = 0; i < sveRijeci.size(); i++) {
            rez.add(new ArrayList<>());
            for (int j = 0; j < dictSize; j++) {
                if(sveRijeci.get(i).equals(unikRijeci.get(j))){
                    rez.get(i).add(1.0);
                }else{
                    rez.get(i).add(0.0);
                }
            }

        }
        return Converter.convertList(rez);
        /*
        //List<List<Double>> rez = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            List<String> hrvRijeci = rijeci.stream()
                    .map(sveRijeci ->{
                        return (List<String>) sveRijeci.stream()
                                .filter(rijec -> ((JSONObject) rijec).get("jezik").toString().equals("Croatian"))
                                .map(o -> {return ((JSONObject) o).get("rijec").toString();})
                                .collect(Collectors.toList());})
                    .collect(Collectors.toList()).stream().flatMap(Collection::stream).collect(Collectors.toList());

        }
        double[][] output = new double[input.length][rijeci.size()];
        for (int i = 0; i < rijeci.size(); i++) {

        }
        rez = rijeci.stream().flatMap(rijeciJSON -> {

            List<String> rijeciString = new ArrayList<>();
            rijeciJSON.forEach(obj -> rijeciString.add(((JSONObject) obj).get("rijec").toString().toLowerCase()));

            return rijeciString.stream().map((String s) -> Converter.convertWordToOutput(s, dictSize));

        }).collect(Collectors.toList());
        return Converter.convertList(rez);
    */
    }

    public static void CollectWords() {
        JSONArray result = new JSONArray();
        JSONParser jsonParser = new JSONParser();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\tmp\\out.txt", true));
            Files.list(Path.of("C:\\Users\\Dodig\\Documents\\nekonvencionalni\\rijeci\\rijeci")).forEach(path -> {
                try (FileReader reader = new FileReader(String.valueOf(path)))
                {
                    //Read JSON file
                    Object obj = jsonParser.parse(reader);

                    JSONArray rijeci = (JSONArray) obj;
                    JSONArray rijeciZaSpremit = new JSONArray();

                    for ( Object o : rijeci) {
                        JSONObject ob = (JSONObject) o;
                        writer.write((ob.get("rijec") + ", "));
                    }

                    writer.write("\n");

                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            });

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
