package org.cytoscape.grncop2.model.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.cytoscape.grncop2.model.businessobjects.Relationship;
import org.cytoscape.grncop2.model.businessobjects.GRNCOP2Result;
import org.cytoscape.grncop2.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.grncop2.model.logic.utils.CSVFileReader;

public class GRNCOP2 {
    private final int maxThreads = Runtime.getRuntime().availableProcessors(); 
    private final  ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);
    private final CompletionService completionService = new ExecutorCompletionService(executorService);
    
    private final ProgressMonitor pm;

    public void interrupt() {
        executorService.shutdownNow();
    }

    public GRNCOP2(ProgressMonitor pm) {
        this.pm = pm;
    }
    
    private String[] parseGenesFile(String genesFilePath) throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(genesFilePath))) {
            List<String> geneList = new LinkedList<>();
            String line;
            while ((line = br.readLine()) != null) {
                geneList.add(line);
            }
            return geneList.toArray(new String[geneList.size()]);
        }
    }
    public float[][][] parseExpressionDataFiles(String datasetsPaths[], char csvSeparator,int expectedGenes) throws FileNotFoundException, IOException, NumberFormatException {
        float[][][] expressionData = new float[datasetsPaths.length][expectedGenes][];
        
        for (int k = 0; k < expressionData.length; k++){
            CSVFileReader CSVreader = new CSVFileReader(datasetsPaths[k], csvSeparator);

            int i = 0;
            int samples = 0;
            List<String> row;
            while ((row = CSVreader.readFields()) != null) {
                if (i == 0) {
                    samples = row.size();
                    expressionData[k] = new float[expectedGenes][samples];
                } else {
                    if (row.size() != samples) {
                        throw new IllegalArgumentException("The dataset " + datasetsPaths[k] + "is invalid. It should contain the same number of data point for each gene (" + samples + ")");
                    }
                }

                for (int j = 0; j < samples; j++){
                    expressionData[k][i][j] = Float.parseFloat(row.get(j));
                }
                i++;
            }

            if (i != expectedGenes) {
                throw new IllegalArgumentException("The dataset " + datasetsPaths[k] + "is invalid. It should contain a row for each gene (" + expectedGenes + ")");
            }
        }
        return expressionData;
    }

    public GRNCOP2Result search(String genesFilePath, String datasetsPaths[], char csvSeparator, int window) throws InterruptedException, FileNotFoundException, IOException, NumberFormatException {
        String[] genes = parseGenesFile(genesFilePath);
        float[][][] expressionData = parseExpressionDataFiles(datasetsPaths, csvSeparator, genes.length);
        Relationship[][][][] GRN = new Relationship[expressionData.length][window + 1][genes.length][genes.length];

        for (int gene = 0; gene < genes.length; gene++) {
            completionService.submit(new GRNCOP2Thread(gene, genes, expressionData, window, GRN));
        }
            
        executorService.shutdown();
        for (int gene = 0; gene < genes.length; gene++) {
            completionService.take();
            pm.setProgress((float)gene / genes.length);
        }
        
        return new GRNCOP2Result(genes, GRN);
    }
}
