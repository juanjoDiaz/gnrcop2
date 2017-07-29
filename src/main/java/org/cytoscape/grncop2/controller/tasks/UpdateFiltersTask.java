package org.cytoscape.grncop2.controller.tasks;

import org.cytoscape.grncop2.controller.NetworkController;
import org.cytoscape.grncop2.controller.utils.CySwing;
import org.cytoscape.grncop2.controller.utils.CytoscapeTaskMonitor;
import org.cytoscape.grncop2.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class UpdateFiltersTask extends AbstractTask {
    private final NetworkController network;
    private final float rca;
    private final float accuracy;
    private final float coverage;
    
    public UpdateFiltersTask(NetworkController network, float rca, float accuracy, float coverage) {
        this.network = network;
        this.rca = rca;
        this.accuracy = accuracy;
        this.coverage = coverage;
    }

    @Override
    public void run(TaskMonitor tm) {
        tm.setTitle("Filtering by rca, accuracy and coverage");
        
        try {
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            network.updateFilters(pm, rca, accuracy, coverage);
        } catch (Exception ex) {
            String error = ex.getMessage();
            CySwing.displayPopUpMessage(error == null || error.isEmpty()
                ? "An unexpected error ocurred applying the rules filters."
                : error);
        }
    }
    
    @Override
    public void cancel() {
        // TODO Cancel
        super.cancel();
    }
}