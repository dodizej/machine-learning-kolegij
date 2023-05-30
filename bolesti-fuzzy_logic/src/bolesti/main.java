package bolesti;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.LinguisticTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;


public class main {
    public static void main(String[] args) throws Exception {
        // Load from 'FCL' file
        String fileName = "src/myfcl.fcl";
        FIS fis = FIS.load(fileName,true);

        // Error while loading?
        if( fis == null ) { 
            System.err.println("Can't load file: '" + fileName + "'");
            return;
        }
        
        // Show 
        JFuzzyChart.get().chart(fis);

        // Set inputs
        fis.setVariable("temperature",  37);
        fis.setVariable("headache"   ,  5);
        fis.setVariable("fatigue"    ,  6);
        fis.setVariable("nose"  	 ,  2);
        fis.setVariable("sneezing"   ,  2);
        fis.setVariable("soreThroat" ,  8);
        fis.setVariable("cough"      ,  2);
        fis.setVariable("chills"     ,  5);

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable decision = fis.getVariable("decision");
        JFuzzyChart.get().chart(decision, decision.getDefuzzifier(), true);
        
        Entry<String, LinguisticTerm> result =  
        	decision.getLinguisticTerms().entrySet()
	        .stream()
	        .max((e1, e2) -> 
		        Double.compare(fis.getVariable("decision").getMembership(e2.getValue().getTermName()), 
		        		       fis.getVariable("decision").getMembership(e1.getValue().getTermName())))
	        .get();
         
        // Print ruleSet
        System.out.println(fis);
        System.out.println("Most likely disease: " + result.getValue().getTermName());
        
    }
}



