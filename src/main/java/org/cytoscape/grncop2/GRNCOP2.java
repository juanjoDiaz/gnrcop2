package org.cytoscape.grncop2;

import java.util.Properties;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.grncop2.controller.NetworkController;
import org.cytoscape.grncop2.controller.ResultPanelController;
import org.cytoscape.grncop2.controller.actions.LoadResultAction;
import org.cytoscape.grncop2.controller.actions.RunAnalysisAction;
import org.cytoscape.grncop2.controller.listener.NetworkClosedListener;
import org.cytoscape.grncop2.controller.utils.CySwing;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class GRNCOP2 extends AbstractCyActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        CySwingApplication swingApplication = getService(context, CySwingApplication.class);
        CyServiceRegistrar serviceRegistrar = getService(context, CyServiceRegistrar.class);
        CyNetworkFactory networkFactory = getService(context, CyNetworkFactory.class);
        CyNetworkManager networkManager = getService(context, CyNetworkManager.class);
        CyNetworkViewFactory networkViewFactory = getService(context, CyNetworkViewFactory.class);
        CyNetworkViewManager networkViewManager = getService(context, CyNetworkViewManager.class);
        CyEventHelper eventHelper = getService(context, CyEventHelper.class);
        VisualMappingManager visualMappingManager = getService(context, VisualMappingManager.class);
        VisualStyleFactory visualStyleFactory = getService(context, VisualStyleFactory.class);
        VisualMappingFunctionFactory continuousMappingFactoryServiceRef = getService(context, VisualMappingFunctionFactory.class, "(mapping.type=continuous)");
        VisualMappingFunctionFactory discreteMappingFactoryServiceRef = getService(context, VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
        CyLayoutAlgorithmManager layoutAlgorithmManager = getService(context, CyLayoutAlgorithmManager.class);
        TaskManager taskManager = getService(context, TaskManager.class);
        
        CySwing.init(swingApplication, serviceRegistrar);
        ResultPanelController.init(taskManager);
        NetworkController.init(networkFactory, networkManager,
                networkViewFactory, networkViewManager, eventHelper,
                visualStyleFactory, visualMappingManager,
                continuousMappingFactoryServiceRef, discreteMappingFactoryServiceRef,
                layoutAlgorithmManager);
        
        // UI controls
        RunAnalysisAction runAnalysisAction = new RunAnalysisAction(taskManager);
        registerService(context, runAnalysisAction, CyAction.class, new Properties());
        LoadResultAction loadResultAction = new LoadResultAction(taskManager);
        registerService(context, loadResultAction, CyAction.class, new Properties());
        
        serviceRegistrar.registerService(new NetworkClosedListener(), NetworkAboutToBeDestroyedListener.class, new Properties());
    }
}