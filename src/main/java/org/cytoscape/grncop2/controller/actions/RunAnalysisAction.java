package org.cytoscape.grncop2.controller.actions;

import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.grncop2.view.configurationDialogs.ConfigurationDialog;
import org.cytoscape.work.TaskManager;


/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class RunAnalysisAction extends AbstractCyAction {
    private final ConfigurationDialog dialog;
    
    public RunAnalysisAction(TaskManager taskManager) {
        super("Run analysis");
        setPreferredMenu("Apps.GRNCOP2");
        dialog = new ConfigurationDialog(taskManager);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.setVisible(true);
    }
}