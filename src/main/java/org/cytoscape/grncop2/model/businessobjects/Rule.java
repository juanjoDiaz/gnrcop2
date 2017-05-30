package org.cytoscape.grncop2.model.businessobjects;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class Rule {
    public final String regulator;
    public final String target;
    public final int type;
    public final int lag;
    public final double accuracy;
    public final double coverage;

    public Rule(String regulator, String target, int type, int lag, double accuracy, double coverage){
        this.regulator = regulator;
        this.target = target;
        this.type = type;
        this.lag = lag;
        this.accuracy = accuracy;
        this.coverage = coverage;
    }
    
    public String getInteraction() {
        switch(type) {
            case 3:
                return "- " + "=> -";
            case 2:
                return "+ " + "=> +";
            case 1:
                return "+/- " + "=> +/-";
            case -1:
                return "+/- " + "=> -/+";
            case -2:
                return "- " + "=> +";
            case -3:
                return "+ " + "=> -";
        }
        return null;
    }
    
    @Override
    public String toString() {
        switch(type) {
            case 3:
                return "- " + regulator + " " + lag + "=> - " + target + " (" + accuracy + ")";
            case 2:
                return "+ " + regulator + " " + lag + "=> + " + target + " (" + accuracy + ")";
            case 1:
                return "+/- " + regulator + " " + lag + "=> +/- " + target + " (" + accuracy + ")";
            case -1:
                return "+/- " + regulator + " " + lag + "=> -/+ " + target + " (" + accuracy + ")";
            case -2:
                return "- " + regulator + " " + lag + "=> + " + target + " (" + accuracy + ")";
            case -3:
                return "+ " + regulator + " " + lag + "=> - " + target + " (" + accuracy + ")";
        }
        return null;
    }
}
