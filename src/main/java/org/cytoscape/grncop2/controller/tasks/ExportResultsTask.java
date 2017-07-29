package org.cytoscape.grncop2.controller.tasks;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import org.cytoscape.grncop2.controller.utils.CySwing;
import org.cytoscape.grncop2.model.businessobjects.GRNCOP2Result;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ExportResultsTask extends AbstractTask {
    private final GRNCOP2Result result;
    private final String path;
    
    public ExportResultsTask(GRNCOP2Result result, String path) {
        this.result = result;
        this.path = path;
    }
    
    @Override
    public void run(TaskMonitor tm) {
        tm.setTitle("Applying visual style");
        tm.setStatusMessage("Applying visual style to the network.");
        
        try (ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream(path))) {
            save.writeObject(result);
        } catch (Exception ex) {
            String error = ex.getMessage();
            CySwing.displayPopUpMessage(error == null || error.isEmpty()
                ? "An unexpected error ocurred while saving your results."
                : error);
        }
    }
    
    @Override
    public void cancel() {
        // TODO Cancel
        super.cancel();
    }
}