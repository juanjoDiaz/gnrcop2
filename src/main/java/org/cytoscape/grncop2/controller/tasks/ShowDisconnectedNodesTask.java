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
public class ShowDisconnectedNodesTask extends AbstractTask {
    private final NetworkController network;
    private final boolean show;
    
    public ShowDisconnectedNodesTask(NetworkController network, boolean show) {
        this.network = network;
        this.show = show;
    }
    
    @Override
    public void run(TaskMonitor tm) {
        tm.setTitle((show ? "Showing" : "Hiding") + " disconnected nodes");
        
        try {
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            network.showDisconnectedNodes(pm, show);
        } catch (Exception ex) {
            String error = ex.getMessage();
            CySwing.displayPopUpMessage(error == null || error.isEmpty()
                ? "An unexpected error ocurred displaying the disconnected nodes."
                : error);
        }
    }
    
    @Override
    public void cancel() {
        // TODO Cancel
        super.cancel();
    }
}