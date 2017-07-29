package org.cytoscape.grncop2.controller.tasks;

import org.cytoscape.grncop2.controller.NetworkController;
import org.cytoscape.grncop2.controller.utils.CySwing;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ApplyVisualStyleTask extends AbstractTask {
    private final NetworkController network;
    
    public ApplyVisualStyleTask(NetworkController network) {
        this.network = network;
    }
    
    @Override
    public void run(TaskMonitor tm) {
        tm.setTitle("Applying visual style");
        tm.setStatusMessage("Applying visual style to the network.");
        
        try {
            network.applyVisualStyle();
        } catch (Exception ex) {
            String error = ex.getMessage();
            CySwing.displayPopUpMessage(error == null || error.isEmpty()
                ? "An unexpected error ocurred while styling your results."
                : error);
        }
    }
    
    @Override
    public void cancel() {
        // TODO Cancel
        super.cancel();
    }
}