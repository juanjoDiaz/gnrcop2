package org.cytoscape.grncop2.controller;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.grncop2.model.businessobjects.GRNCOP2Result;
import org.cytoscape.grncop2.model.businessobjects.Rule;
import org.cytoscape.grncop2.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.work.TaskIterator;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public final class NetworkController {
    private static CyNetworkFactory networkFactory;
    private static CyNetworkManager networkManager;
    private static CyNetworkViewFactory networkViewFactory;
    private static CyNetworkViewManager networkViewManager;
    private static VisualMappingManager visualMappingManager;
    private static CyEventHelper eventHelper;
    private static VisualStyleFactory visualStyleFactory;
    private static VisualMappingFunctionFactory continuousMappingFactoryServiceRef;
    private static VisualMappingFunctionFactory discreteMappingFactoryServiceRef;
    private static CyLayoutAlgorithmManager layoutAlgorithmManager;

    public static final String LagColumn = "Time-lag";
    public static final String TypeColumn = "Type";
    public static final String AccuracyColumn = "Accuracy";
    public static final String CoverageColumn = "Coverage";

    private final GRNCOP2Result result;
    private CyNetwork network;
    private CyNetworkView networkView;
    private Map<String, CyNode> nodes;
    private List<View<CyNode>> disconnectedNodes;
    private float accuracy;
    private float coverage;
    
    private VisualStyle style;
    private DiscreteMapping edgeColorMapping;
    private ContinuousMapping edgeTransparencyMapping;
    private ContinuousMapping edgeWidthMapping;
    
    public static void init(CyNetworkFactory networkFactory, CyNetworkManager networkManager,
            CyNetworkViewFactory networkViewFactory, CyNetworkViewManager networkViewManager,
            CyEventHelper eventHelper,
            VisualStyleFactory visualStyleFactory, VisualMappingManager visualMappingManager,
            VisualMappingFunctionFactory continuousMappingFactoryServiceRef,
            VisualMappingFunctionFactory discreteMappingFactoryServiceRef,
            CyLayoutAlgorithmManager layoutAlgorithmManager) {
        NetworkController.networkFactory = networkFactory;
        NetworkController.networkManager = networkManager;
        NetworkController.networkViewFactory = networkViewFactory;
        NetworkController.networkViewManager = networkViewManager;
        NetworkController.eventHelper = eventHelper;
        NetworkController.visualStyleFactory = visualStyleFactory;
        NetworkController.visualMappingManager = visualMappingManager;
        NetworkController.continuousMappingFactoryServiceRef = continuousMappingFactoryServiceRef;
        NetworkController.discreteMappingFactoryServiceRef = discreteMappingFactoryServiceRef;
        NetworkController.layoutAlgorithmManager = layoutAlgorithmManager;
    }
    
    public NetworkController(GRNCOP2Result result) {
        this.result = result;
        createCyNetwork();
    }
    
    public void createCyNetwork() {
        network = networkFactory.createNetwork();
        network.getRow(network).set(CyNetwork.NAME, "GRNCOP2 network");

        String[] genes = result.getGenes();
        nodes = new HashMap<>(genes.length);
        for (String gene : genes) {
            CyNode node = network.addNode();
            CyRow row = network.getRow(node);
            row.set(CyNetwork.NAME, gene);
            nodes.put(gene, node);
        }
        disconnectedNodes = new LinkedList<>();

        networkManager.addNetwork(network);
        networkView = networkViewFactory.createNetworkView(network);
        networkViewManager.addNetworkView(networkView);
        
        eventHelper.flushPayloadEvents();
    }
    
    public void updateFilters(ProgressMonitor pm, float rca, float accuracy, float coverage) {
        pm.setStatus("Filtering rules by rca, accuracy and coverage.");
        this.accuracy = accuracy;
        this.coverage = coverage;
        List<Rule> rules = result.getRules(rca, accuracy, coverage);
        network.removeEdges(network.getEdgeList());
        
        CyTable edgeTable = network.getDefaultEdgeTable();
        List<Long> ids = edgeTable.getAllRows().stream()
            .map(row -> row.get(CyEdge.SUID, Long.class))
            .collect(Collectors.toList());

        try {
            edgeTable.deleteRows(ids);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            edgeTable.createColumn(LagColumn, Integer.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        try {
            edgeTable.createColumn(TypeColumn, Integer.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        try {
            edgeTable.createColumn(AccuracyColumn, Double.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }
        try {
            edgeTable.createColumn(CoverageColumn, Double.class, false);
        } catch (IllegalArgumentException ex) {
            // Do nothing and just override the values in the collumn
        }

        AtomicInteger i = new AtomicInteger();
        int steps = rules.size() + disconnectedNodes.size() + network.getNodeList().size();
        rules.stream().forEach(rule -> {
            CyNode node1 = nodes.get(rule.regulator);
            CyNode node2 = nodes.get(rule.target);
            CyEdge edge = network.addEdge(node1, node2, true);
            CyRow row = network.getRow(edge);
            row.set(CyNetwork.NAME, rule.toString());
            row.set(CyEdge.INTERACTION, rule.getInteraction());
            row.set(LagColumn, rule.lag);
            row.set(TypeColumn, rule.type);
            row.set(AccuracyColumn, rule.accuracy);
            row.set(CoverageColumn, rule.coverage);
            pm.setProgress((float)i.incrementAndGet() / steps);
        });
        
        disconnectedNodes.stream().forEach(nodeView -> {
            nodeView.clearValueLock(BasicVisualLexicon.NODE_VISIBLE);
            pm.setProgress((float)i.incrementAndGet() / steps);
        });
        
        disconnectedNodes = network.getNodeList().stream()
            .filter(node -> network.getAdjacentEdgeList(node, CyEdge.Type.ANY).isEmpty())
            .map(node -> networkView.getNodeView(node))
            .peek(node -> pm.setProgress((float)i.incrementAndGet() / steps))
            .collect(Collectors.toList());
        
        eventHelper.flushPayloadEvents();
    }
    
    public void updateTimeLagFilter(ProgressMonitor pm, Integer lag) {
        pm.setStatus(lag == null
            ? "Showing all rules."
            : "Showing rules lagged by " + lag + ".");
        AtomicInteger i = new AtomicInteger();
        int edgeViewCount = networkView.getEdgeViews().size();
        networkView.getEdgeViews().stream().forEach(edgeView -> {
            CyRow row = network.getRow(edgeView.getModel());
            boolean isVisible = lag == null || Objects.equals(lag, row.get(LagColumn, Integer.class));
            if (isVisible) {
                edgeView.clearValueLock(BasicVisualLexicon.EDGE_VISIBLE);
            } else {
                row.set(CyNetwork.SELECTED, false);
                edgeView.setLockedValue(BasicVisualLexicon.EDGE_VISIBLE, false);
            }
            pm.setProgress((float)i.incrementAndGet() / edgeViewCount);
        });
        
        eventHelper.flushPayloadEvents();
    }
    
    public void showDisconnectedNodes(ProgressMonitor pm, boolean show) {
        pm.setStatus((show ? "Showing" : "Hiding") + " disconnected nodes.");
        AtomicInteger i = new AtomicInteger();
        int disconnecteNodesCount = disconnectedNodes.size();
        disconnectedNodes.stream().forEach(nodeView -> {
            if (show) {
                nodeView.clearValueLock(BasicVisualLexicon.NODE_VISIBLE);
            } else {
                network.getRow(nodeView.getModel()).set(CyNetwork.SELECTED, false);
                nodeView.setLockedValue(BasicVisualLexicon.NODE_VISIBLE, false);
            }
            pm.setProgress((float)i.incrementAndGet() / disconnecteNodesCount);
        });
        
        eventHelper.flushPayloadEvents();
    }

    public void dispose() {
    }
    
    public CyNetwork getCyNetwork() {
        return network;
    }

    public void applyVisualStyle() {
        if (style == null) {
            style = visualStyleFactory.createVisualStyle(visualMappingManager.getDefaultVisualStyle());
            style.setTitle("GRNCOP2");
            style.setDefaultValue(BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE, ArrowShapeVisualProperty.ARROW);
            visualMappingManager.addVisualStyle(style);
        }

        if (edgeColorMapping != null) {
            style.removeVisualMappingFunction(edgeColorMapping.getVisualProperty());
        }
        edgeColorMapping = (DiscreteMapping)discreteMappingFactoryServiceRef
           .createVisualMappingFunction(TypeColumn, Integer.class, BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT);
        edgeColorMapping.putMapValue(3, new Color(0x000066));
        edgeColorMapping.putMapValue(2, new Color(0x006666));
        edgeColorMapping.putMapValue(1, new Color(0x00CCCC));
        edgeColorMapping.putMapValue(-1, new Color(0xFF00CC));
        edgeColorMapping.putMapValue(-2, new Color(0xFF3300));
        edgeColorMapping.putMapValue(-3, new Color(0xCC0000));
        style.addVisualMappingFunction(edgeColorMapping);

        if (edgeTransparencyMapping != null) {
            style.removeVisualMappingFunction(edgeTransparencyMapping.getVisualProperty());
        }
        edgeTransparencyMapping = (ContinuousMapping)continuousMappingFactoryServiceRef
           .createVisualMappingFunction(AccuracyColumn, Double.class, BasicVisualLexicon.EDGE_TRANSPARENCY);
        edgeTransparencyMapping.addPoint(accuracy, new BoundaryRangeValues<>(10, 10, 10));
        edgeTransparencyMapping.addPoint(1d, new BoundaryRangeValues<>(255, 255,  255));
        style.addVisualMappingFunction(edgeTransparencyMapping);
        
        if (edgeWidthMapping != null) {
            style.removeVisualMappingFunction(edgeWidthMapping.getVisualProperty());
        }
        double minWidth = 0.1;
        double maxWidth = 3 * style.getDefaultValue(BasicVisualLexicon.EDGE_WIDTH);
        edgeWidthMapping = (ContinuousMapping)continuousMappingFactoryServiceRef
           .createVisualMappingFunction(CoverageColumn, Double.class, BasicVisualLexicon.EDGE_WIDTH);
        edgeWidthMapping.addPoint(coverage, new BoundaryRangeValues<>(minWidth, minWidth, minWidth));
        edgeWidthMapping.addPoint(1d, new BoundaryRangeValues<>(maxWidth, maxWidth, maxWidth));
        style.addVisualMappingFunction(edgeWidthMapping);

        style.apply(networkView);
        visualMappingManager.setVisualStyle(style, networkView);
        
        eventHelper.flushPayloadEvents();
    }
    
    public TaskIterator getApplyLayoutTask() {
        CyLayoutAlgorithm layout = layoutAlgorithmManager.getDefaultLayout();
        return layout.createTaskIterator(networkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, null);
    }
}