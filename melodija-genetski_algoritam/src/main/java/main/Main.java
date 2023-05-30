package main;

import algoritam.GenetskiAlgoritam;
import io.jenetics.CharacterChromosome;
import io.jenetics.CharacterGene;
import io.jenetics.Phenotype;
import org.jfugue.player.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        Player player = new Player();

        Double ukupnoVrijeme = 0.0;
        Long brojGeneracija = 0l;
        Integer brojUzoraka = 7;

        //String zeljenaMelodija = "EDCDEEEDDDEGG";
        String zeljenaMelodija = "EDCDEEEDDDEGGAGBDEFADEGAC";
        //String zeljenaMelodija = "EDCDEEEDDDEGGAGBDEFADEGACDEGGAGBDEF";
        //player.play(dodajRazmake(zeljenaMelodija));

        for (int i = 0; i < brojUzoraka + 1; i++) {

            GenetskiAlgoritam prvi = new GenetskiAlgoritam((zeljenaMelodija.subSequence(0, zeljenaMelodija.length())));
            Long startTime = System.currentTimeMillis();
            Phenotype<CharacterGene, Integer> rez = prvi.solve();
            Long endTime = System.currentTimeMillis();

            if (i == 0) continue;

            System.out.println("Test " + i);
            Double vrijeme = ((endTime-startTime))/1000.0;
            System.out.println("Vrijeme izvrsavanja: " + vrijeme + " s");
            System.out.println("Broj generacija:     " + rez.generation() + "\n");
            ukupnoVrijeme += vrijeme;
            brojGeneracija += rez.generation();
        }

        System.out.println("Prosjecno vrijeme izvrsavanja:         " + ukupnoVrijeme/brojUzoraka);
        System.out.println("Prosjecan broj generacija do rjesenja: " + brojGeneracija/brojUzoraka);

    }
    private static String dodajRazmake(String bezRazmaka){
        return String.join(" ", bezRazmaka.split(""));
    }
}
