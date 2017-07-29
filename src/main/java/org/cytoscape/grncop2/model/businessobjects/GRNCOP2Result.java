package org.cytoscape.grncop2.model.businessobjects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GRNCOP2Result  implements Serializable {
    private final String[] genes;
    private final Relationship[][][][] grns;
    
    public GRNCOP2Result(String[] genes, Relationship[][][][] grn) {
        this.genes = genes;
        this.grns = grn;
    }
    
    public String[] getGenes() {
        return genes;
    }
    
    public Relationship[][][][] getGRNs() {
       return grns; 
    }
    
    public List<Rule> getRules(float rca, float accuracy, float coverage)  {
        List<Rule> rules = new LinkedList<>();
        for (int w = 0; w < grns[0].length; w++) {
            for (int ind1 = 0; ind1 < genes.length; ind1++) {
                String gene1 = genes[ind1];
                for (int ind2 = 0; ind2 < genes.length; ind2++) {
                    String gene2 = genes[ind2];
                    int[] counter = new int[6];
                    Rule[] candidateRules = new Rule[6];
                    for (Relationship[][][] grn : grns) {
                        Relationship relationship = grn[w][ind1][ind2];
                        if (relationship == null) {
                            continue;
                        }
                        Rule rule = relationship.getRule(gene2, gene1, w, accuracy, coverage);
                        if (rule == null) {
                            continue;
                        }
                        int index = (rule.type > 0 ? rule.type - 1 : rule.type) + 3;
                        counter[index]++;
                        if (candidateRules[index] == null || rule.coverage < candidateRules[index].coverage) {
                            candidateRules[index] = rule;
                        }
                    }

                    int maxCount = 0;
                    Rule rule = null;
                    for (int i = 0; i < counter.length; i++) {
                        int ruleCount = counter[i];
                        Rule candidateRule = candidateRules[i];
                        if (ruleCount > rca && (ruleCount > maxCount || (rule != null && candidateRule != null && candidateRule.accuracy < rule.accuracy))) {
                            maxCount = ruleCount;
                            rule = candidateRule;
                        }
                    }

                    if (rule != null) {
                        rules.add(rule);
                    }
                }
            }
        }
        return rules;
    }
}
