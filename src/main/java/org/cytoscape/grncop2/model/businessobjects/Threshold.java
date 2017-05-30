package org.cytoscape.grncop2.model.businessobjects;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Cristian A. Gallo
 * @author Juan José Díaz Montaña
 */
public class Threshold {
    public float t = 0;
    public boolean active = true;

    public Threshold(float tr, boolean ac) {
        t = tr;
        active = ac;
    }

}
