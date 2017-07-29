package org.cytoscape.grncop2.model.businessobjects;

import java.io.Serializable;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Cristian A. Gallo
 * @author Juan José Díaz Montaña
 */
public class Metric implements Serializable {
   public final float TP,FP,TN,FN,PR,NR;

   public Metric(int TP, int FP, int TN, int FN){
       int total = TP + FP + TN +  FN;
       this.TP = (float)TP / total;
       this.FP = (float)FP / total;
       this.TN = (float)TN / total;
       this.FN = (float)FN / total;
       
        PR = TP + FP == 0 ? 0 : (float)TP / (TP + FP);
        NR = TN + FN == 0 ? 0 : (float)TN / (FN + TN);
   }
}

