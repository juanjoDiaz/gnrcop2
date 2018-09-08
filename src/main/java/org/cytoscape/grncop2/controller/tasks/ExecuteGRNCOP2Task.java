package org.cytoscape.grncop2.controller.tasks;

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
    private GRNCOP2 grncop2;
    private final int window;
    private final String genesFilePath;
    private final String[] datasetsPaths;
    private final char csvSeparator;
    
    public ExecuteGRNCOP2Task(String genesFilePath, String[] datasetsPaths, int window, char csvSeparator) {
        this.window = window;
        this.genesFilePath = genesFilePath;
        this.datasetsPaths = datasetsPaths;
        this.csvSeparator = csvSeparator;
    }
    
    @Override
    public void run(TaskMonitor tm) {
        try {
            tm.setTitle("Running GNRCOP2 analysis");
            ProgressMonitor pm = new CytoscapeTaskMonitor(tm);
            grncop2 = new GRNCOP2(pm);
            pm.setStatus("Loading input files");
            GRNCOP2Result result = grncop2.search(genesFilePath, datasetsPaths, csvSeparator, window);
            insertTasksAfterCurrentTask(new ShowResultsTask(result));
        } catch (Exception ex) {
            String error = ex.getMessage();
            CySwing.displayPopUpMessage(error == null || error.isEmpty()
                ? "An unexpected error ocurred during the analysis."
                : error);
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