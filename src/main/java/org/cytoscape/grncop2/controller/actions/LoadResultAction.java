package org.cytoscape.grncop2.controller.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.grncop2.controller.tasks.ImportResultsTask;
import org.cytoscape.grncop2.controller.utils.CySwing;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;


/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class LoadResultAction extends AbstractCyAction {
    private final TaskManager taskManager;
    public static File currentFolder;
    
    public LoadResultAction(TaskManager taskManager) {
        super("Load result");
        setPreferredMenu("Apps.GRNCOP2");
        this.taskManager = taskManager;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load GRNCOP2 results");
        fileChooser.setSelectedFile(new File("grncop2results.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("GRNCOP2 file", "grncop2"));
        fileChooser.setCurrentDirectory(currentFolder);
        int returnValue = fileChooser.showOpenDialog(CySwing.getDesktopJFrame());
        if (returnValue != 0) {
            return;
        }
        
        currentFolder = new File(fileChooser.getSelectedFile().getParent());
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        taskManager.execute(new TaskIterator(new ImportResultsTask(filePath)));
    }
}