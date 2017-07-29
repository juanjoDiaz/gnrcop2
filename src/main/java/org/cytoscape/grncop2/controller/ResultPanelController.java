package org.cytoscape.grncop2.controller;

import java.util.HashMap;
import java.util.Map;
import org.cytoscape.grncop2.controller.utils.CySwing;
import org.cytoscape.grncop2.model.businessobjects.GRNCOP2Result;
import org.cytoscape.grncop2.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.grncop2.view.resultPanel.MainResultsView;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ResultPanelController {
    private static TaskManager taskManager;
    public static final Map<CyNetwork, ResultPanelController> panels = new HashMap();
    
    private final NetworkController network;
    private GRNCOP2Result result;
    private MainResultsView rv;
    
    @SuppressWarnings("LeakingThisInConstructor")
    public ResultPanelController(ProgressMonitor pm, GRNCOP2Result result) {
        pm.setStatus("Creating network and view.");
        this.network = new NetworkController(result);
        this.result = result;
        
        pm.setStatus("Creating results panel.");
        rv = new MainResultsView(taskManager, this);
        CySwing.addPanel(rv);
        
        if(panels.containsKey(network.getCyNetwork())) {
            CySwing.removePanel(panels.get(network.getCyNetwork()).rv, false);
        }
        
        panels.put(network.getCyNetwork(), this);
    }
    
    public static void init(TaskManager taskManager) {
        ResultPanelController.taskManager = taskManager;
    } 
    
    public void dispose() {
        panels.remove(network.getCyNetwork());
        CySwing.removePanel(rv, panels.isEmpty());
        rv = null;
        result = null;
    }
    
    public TaskIterator getRefreshNetworkTasks() {
        return rv.getRefreshNetworkTasks();
    }
    
    public NetworkController getNetwork() {
        return network;
    }
    
    public GRNCOP2Result getResult() {
        return result;
    }
}