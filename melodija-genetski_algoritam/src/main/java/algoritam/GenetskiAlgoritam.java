package algoritam;

import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.util.CharSeq;
import io.jenetics.util.Factory;
import org.jfugue.player.Player;

import javax.sound.midi.Sequence;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenetskiAlgoritam {
    private CharSequence  zeljenaMelodija;
    private List<Integer> asciiMelodija;
    private Player        player;

    public GenetskiAlgoritam(CharSequence zeljenaMelodija) {
        this.player = new Player();
        this.zeljenaMelodija = zeljenaMelodija;
        asciiMelodija = zeljenaMelodija.chars().boxed().collect(Collectors.toList());
    }
    public Phenotype<CharacterGene, Integer> solve(){

        //JEDINKA ( n kromosoma (n gena))
        //genotip n kromosom(1 gena))
        List<CharacterChromosome> chromosomeList = new ArrayList<>();
        chromosomeList.add(CharacterChromosome.of(CharSeq.of("ABCDEFG"), zeljenaMelodija.length()));
        Factory<Genotype<CharacterGene>> genotypeFactory = Genotype.of(chromosomeList);
        Engine<CharacterGene, Integer> engine = Engine.builder(this::calcFitness, genotypeFactory)
                .populationSize(5000)
                .survivorsSelector(new TournamentSelector<>(3))
                .offspringSelector(new TournamentSelector<>(8))
                .alterers(new SinglePointCrossover<>(.75), new Mutator<>(.01))//.2
                .offspringFraction(0.90)
                .build();

        Boolean[] found = {false};
        Phenotype<CharacterGene, Integer> rez = engine.stream()
                .limit(Limits.byFitnessThreshold(36 * zeljenaMelodija.length() + this.zeljenaMelodija.length() * 2 - 1))
                //.peek(generation -> {if (generation.generation() % 10 == 0) {play(generation.bestPhenotype());} } )
                .collect(EvolutionResult.toBestPhenotype());
        return rez;
    }

    public Integer calcFitness(Genotype<CharacterGene> genotype) {

        Integer fitnessBase = 36 * this.zeljenaMelodija.length();

        Integer fitnessRezult = fitnessBase;

        List<Integer> vrijednostiJedinke = genotype.chromosome().stream().map(characterGene -> {
            return ((int) characterGene.charValue());
        }).collect(Collectors.toList());

        for (int i = 0; i < vrijednostiJedinke.size(); i++) {
            Integer razlika = Math.abs(this.asciiMelodija.get(i) - vrijednostiJedinke.get(i));
            fitnessRezult -= (razlika * razlika);
        }

        if (fitnessRezult.equals(fitnessBase)) {
            fitnessRezult += this.zeljenaMelodija.length() * 2;
        }

        return fitnessRezult;
    }

    public void play(Phenotype<CharacterGene, Integer> characterGeneIntegerPhenotype) {
        this.player.play(characterGeneIntegerPhenotype.genotype().chromosome().toString());
    }
    
}
