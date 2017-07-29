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
public class UpdateTimeLagFilterTask extends AbstractTask {
    private final NetworkController network;
    private final Integer lag;
    
    public UpdateTimeLagFilterTask(NetworkController network, Integer lag) {
        this.network = network;
        this.lag = lag;
    }
    
    @Override
    public void run(TaskMonitor tm) {
        tm.setTitle("Filtering by time lag");
            
        try {
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            network.updateTimeLagFilter(pm, lag);
        } catch (Exception ex) {
            String error = ex.getMessage();
            CySwing.displayPopUpMessage(error == null || error.isEmpty()
                ? "An unexpected error ocurred filtering by time lag."
                : error);
        }
    }
    
    @Override
    public void cancel() {
        // TODO Cancel
        super.cancel();
    }
}