package org.cytoscape.grncop2.controller.tasks;

import org.cytoscape.grncop2.controller.ResultPanelController;
import org.cytoscape.grncop2.controller.utils.CySwing;
import org.cytoscape.grncop2.controller.utils.CytoscapeTaskMonitor;
import org.cytoscape.grncop2.model.businessobjects.GRNCOP2Result;
import org.cytoscape.grncop2.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ShowResultsTask extends AbstractTask {
    private final GRNCOP2Result result;
    
    public ShowResultsTask(GRNCOP2Result result) {
        this.result = result;
    }
    
    @Override
    public void run(TaskMonitor tm) {
        tm.setTitle("Displaying the results");
            
        try {
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            ResultPanelController rpc = new ResultPanelController(pm, result);
            insertTasksAfterCurrentTask(rpc.getRefreshNetworkTasks());
        } catch (Exception ex) {
            String error = ex.getMessage();
            CySwing.displayPopUpMessage(error == null || error.isEmpty()
                ? "An unexpected error ocurred while displaying the results."
                : error);
        }
    }
    
    @Override
    public void cancel() {
        // TODO Cancel
        super.cancel();
    }
}