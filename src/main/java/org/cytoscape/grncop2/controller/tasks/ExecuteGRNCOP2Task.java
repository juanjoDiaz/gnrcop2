package org.cytoscape.grncop2.controller.tasks;

import org.cytoscape.grncop2.controller.ResultPanelController;
import org.cytoscape.grncop2.controller.utils.CySwing;
import org.cytoscape.grncop2.controller.utils.CytoscapeTaskMonitor;
import org.cytoscape.grncop2.model.businessobjects.GRNCOP2Result;
import org.cytoscape.grncop2.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.grncop2.model.logic.GRNCOP2;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ExecuteGRNCOP2Task extends AbstractTask {
    private final GRNCOP2 grncop2;
    private final String genesFilePath;
    private final String[] datasetsPaths;
    
    public ExecuteGRNCOP2Task(String genesFilePath, String[] datasetsPaths, int window) {
        grncop2 = new GRNCOP2();
        grncop2.SetWindow(window);
        this.genesFilePath = genesFilePath;
        this.datasetsPaths = datasetsPaths;
    }
    
    @Override
    public void run(TaskMonitor tm) {
        try {
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            grncop2.setProgressMonitor(pm);
            tm.setTitle("Loading input files");
            grncop2.load(genesFilePath, datasetsPaths);
            tm.setTitle("Running GNRCOP2 analysis");
            GRNCOP2Result result = grncop2.search();
            pm.setStatus("Displaying the results.");
            new ResultPanelController(result);
            CySwing.displayPopUpMessage("GRNCOP2 anlysis succesfully completed!");
        } catch (Exception ex) {
            CySwing.displayPopUpMessage(ex.getMessage());
        }
    }
    
    @Override
    public void cancel() {
        if (grncop2 != null) {
            grncop2.interrupt();
        }
        super.cancel();
    }
}