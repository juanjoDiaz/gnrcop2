package org.cytoscape.grncop2.model.businessobjects;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Cristian A. Gallo
 * @author Juan José Díaz Montaña
 */
public class Relationship {
    public final Metric downReg;
    public final Metric upReg;

    public Relationship(Metric upReg, Metric downReg){
        this.upReg = upReg;
        this.downReg = downReg;
    }
    
    public Rule getRule(String regulator, String target, int lag, float accuracy, float coverage) {
        if ((downReg.PR * downReg.NR >= accuracy)) {
            return new Rule(regulator, target, -1, lag, downReg.PR * downReg.NR, 1);
        } else if (upReg.PR * upReg.NR >= accuracy)  {
            return new Rule(regulator, target, 1, lag,  upReg.PR * upReg.NR, 1);
        } else if ((downReg.PR >= accuracy) && (downReg.TP > coverage)) {
            return new Rule(regulator, target, -2, lag,  downReg.PR, downReg.TP);
        } else if ((downReg.NR >= accuracy) && (downReg.TN > coverage)) {
            return new Rule(regulator, target, -3, lag,  downReg.NR, downReg.TN);
        } else if ((upReg.PR >= accuracy) && (upReg.TP > coverage))  {
            return new Rule(regulator, target, 2, lag,  upReg.PR, upReg.TP);
        } else if ((upReg.NR >= accuracy) && (upReg.TN > coverage)) {
            return new Rule(regulator, target, 3, lag,  upReg.NR, upReg.TN);
        } else {
            return null;
        }
    }
}
