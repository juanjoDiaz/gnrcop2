package org.cytoscape.grncop2.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cytoscape.grncop2.controller.utils.CySwing;
import org.cytoscape.grncop2.model.businessobjects.GRNCOP2Result;
import org.cytoscape.grncop2.model.businessobjects.Rule;
import org.cytoscape.grncop2.view.resultPanel.MainResultsView;
import org.cytoscape.model.CyNetwork;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ResultPanelController {
    public static final Map<CyNetwork, ResultPanelController> panels = new HashMap();
    
    private final NetworkController network;
    private GRNCOP2Result result;
    private MainResultsView rv;
    
    @SuppressWarnings("LeakingThisInConstructor")
    public ResultPanelController(GRNCOP2Result result) {
        this.network = new NetworkController(result);
        this.result = result;
        rv = new MainResultsView(this);
        CySwing.addPanel(rv);
        
        if(panels.containsKey(network.getCyNetwork())) {
            CySwing.removePanel(panels.get(network.getCyNetwork()).rv, false);
        }
        
        panels.put(network.getCyNetwork(), this);
    }
    
    public void dispose() {
        panels.remove(network.getCyNetwork());
        CySwing.removePanel(rv, panels.isEmpty());
        rv = null;
        result = null;
    }
    
    public NetworkController getNetwork() {
        return network;
    }
    
    public GRNCOP2Result getResult() {
        return result;
    }
}