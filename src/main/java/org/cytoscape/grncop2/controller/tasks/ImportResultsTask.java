package org.cytoscape.grncop2.controller.tasks;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.cytoscape.grncop2.controller.utils.CySwing;
import org.cytoscape.grncop2.model.businessobjects.GRNCOP2Result;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ImportResultsTask extends AbstractTask {
    private final String path;
    
    public ImportResultsTask(String path) {
        this.path = path;
    }
    
    @Override
    public void run(TaskMonitor tm) {
        tm.setTitle("Applying visual style");
        tm.setStatusMessage("Applying visual style to the network.");
        
        try (ObjectInputStream save = new ObjectInputStream(new FileInputStream(path))) {
            GRNCOP2Result result = (GRNCOP2Result) save.readObject();
            insertTasksAfterCurrentTask(new ShowResultsTask(result));
        } catch (FileNotFoundException ex) {
            CySwing.displayPopUpMessage("The results' file couldn't be read.");
        } catch (IOException ex) {
            CySwing.displayPopUpMessage("The results' file couldn't be read.");
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