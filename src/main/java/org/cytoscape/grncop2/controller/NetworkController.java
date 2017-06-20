package org.cytoscape.grncop2.controller;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.cytoscape.grncop2.model.businessobjects.GRNCOP2Result;
import org.cytoscape.grncop2.model.businessobjects.Rule;
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
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public final class NetworkController {
    private static TaskManager taskManager;
    private static CyNetworkFactory networkFactory;
    private static CyNetworkManager networkManager;
    private static CyNetworkViewFactory networkViewFactory;
    private static CyNetworkViewManager networkViewManager;
    private static VisualMappingManager visualMappingManager;
    private static VisualStyleFactory visualStyleFactory;
    private static VisualMappingFunctionFactory continuousMappingFactoryServiceRef;
    private static CyLayoutAlgorithmManager layoutAlgorithmManager;

    public static final String LagColumn = "Time-lag";
    public static final String TypeColumn = "Type";
    public static final String AccuracyColumn = "Accuracy";
    public static final String CoverageColumn = "Coverage";

    private final CyNetwork network;
    private final CyNetworkView networkView;
    private final GRNCOP2Result result;
    private final Map<String, CyNode> nodes;
    private final List<View<CyNode>> disconnectedNodes;
    
    private VisualStyle style;
    private ContinuousMapping edgeColorMapping;
    private ContinuousMapping edgeWidthMapping;
    
    public static void init(TaskManager taskManager, CyNetworkFactory networkFactory, CyNetworkManager networkManager,
            CyNetworkViewFactory networkViewFactory, CyNetworkViewManager networkViewManager,
            VisualStyleFactory visualStyleFactory, VisualMappingManager visualMappingManager,
            VisualMappingFunctionFactory continuousMappingFactoryServiceRef, CyLayoutAlgorithmManager layoutAlgorithmManager) {
        NetworkController.taskManager = taskManager;
        NetworkController.networkFactory = networkFactory;
        NetworkController.networkManager = networkManager;
        NetworkController.networkViewFactory = networkViewFactory;
        NetworkController.networkViewManager = networkViewManager;
        NetworkController.visualStyleFactory = visualStyleFactory;
        NetworkController.visualMappingManager = visualMappingManager;
        NetworkController.continuousMappingFactoryServiceRef = continuousMappingFactoryServiceRef;
        NetworkController.layoutAlgorithmManager = layoutAlgorithmManager;
    }
    
    public NetworkController(GRNCOP2Result result) {
        this.result = result;
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
    }
    
    public List<Rule> updateFilters(float rca, float accuracy, float coverage) {
        List<Rule> rules = result.getRules(rca, accuracy, coverage);
        network.removeEdges(network.getEdgeList());
        
        CyTable edgeTable = network.getDefaultEdgeTable();
        List<Long> ids = edgeTable.getAllRows().stream()
            .map((row) -> row.get(CyEdge.SUID, Long.class))
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

        rules.stream().forEach((rule) -> {
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
        });
        
        network.getNodeList().stream().forEach((node) -> {
            if (network.getAdjacentEdgeList(node, CyEdge.Type.ANY).isEmpty()) {
                View<CyNode> nodeView = networkView.getNodeView(node);
                disconnectedNodes.add(nodeView);
                nodeView.setVisualProperty(BasicVisualLexicon.NODE_VISIBLE, false);
            }
        });

        applyVisualStyle(accuracy, coverage);
        applyLayout();
        
        return rules;
    }
    
    public void filterEdges(Integer lag) {
        networkView.getEdgeViews().stream().forEach((edgeView) -> {
            CyRow row = network.getRow(edgeView.getModel());
            boolean isVisible = lag == null || Objects.equals(lag, row.get(LagColumn, Integer.class));
            edgeView.setVisualProperty(BasicVisualLexicon.EDGE_VISIBLE, isVisible);
        });
    }
    
    public void showDisconnectedNodes(boolean show) {
        disconnectedNodes.stream().forEach((nodeView) -> {
            nodeView.setVisualProperty(BasicVisualLexicon.NODE_VISIBLE, show);
        });
    }

    public void dispose() {
    }
    
    public CyNetwork getCyNetwork() {
        return network;
    }

    public void applyVisualStyle(double accuracy, double coverage) {
        if (style == null) {
            style = visualStyleFactory.createVisualStyle(visualMappingManager.getDefaultVisualStyle());
            style.setTitle("GRNCOP2");
            style.setDefaultValue(BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE, ArrowShapeVisualProperty.ARROW);
            visualMappingManager.addVisualStyle(style);
        }

        if (edgeColorMapping != null) {
            style.removeVisualMappingFunction(edgeColorMapping.getVisualProperty());
        }
        edgeColorMapping = (ContinuousMapping)continuousMappingFactoryServiceRef
           .createVisualMappingFunction(AccuracyColumn, Double.class, BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT);
        edgeColorMapping.addPoint(accuracy, new BoundaryRangeValues<>(new Color(0xE5E5E5), new Color(0xE5E5E5), new Color(0xE5E5E5)));
        edgeColorMapping.addPoint(1d, new BoundaryRangeValues<>(new Color(0x333333), new Color(0x333333), new Color(0x333333)));
        style.addVisualMappingFunction(edgeColorMapping);
        
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
    }
    
    public void applyLayout() {
        CyLayoutAlgorithm layout = layoutAlgorithmManager.getDefaultLayout();
        TaskIterator iterator = layout.createTaskIterator(networkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, null);
        taskManager.execute(iterator);
    }
}