package org.cytoscape.grncop2.model.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.cytoscape.grncop2.model.businessobjects.Relationship;
import org.cytoscape.grncop2.model.businessobjects.GRNCOP2Result;
import org.cytoscape.grncop2.model.businessobjects.Metric;
import org.cytoscape.grncop2.model.businessobjects.Threshold;
import org.cytoscape.grncop2.model.businessobjects.utils.ProgressMonitor;
import org.cytoscape.grncop2.model.logic.utils.CSVFileReader;

public class GRNCOP2 implements Callable {
    private static ProgressMonitor pm;
    
    private static char csvSeparator = ';';
    private static int window = 0;

    private final int maxThreads = Runtime.getRuntime().availableProcessors(); 
    private final  ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);
    private final CompletionService completionService = new ExecutorCompletionService(executorService);
    
    private static String[] genes;
    private static int[] numOfSamples;
    private static float[][][] D_ged;
    
    private static Relationship[][][][] GRN;

    private int pGene;
    private Threshold[][][] D_WTHRM;

    public void SetWindow(int window){
        GRNCOP2.window = window;
    }

    public void setProgressMonitor(ProgressMonitor pm){
        GRNCOP2.pm = pm;
    }

    public void interrupt() {
        executorService.shutdownNow();
    }

    public String[] getGenes(){
        return genes;
    }

    public Relationship[][][][] getRules(){
        return GRN;
    }

    public GRNCOP2() {
        genes = null;
        D_ged = null;
        GRN = null;
    }

    public GRNCOP2(int pGene){
        this.pGene = pGene;     
    }
    
    public void load(String genesFilePath, String datasetsPaths[]) throws FileNotFoundException, IOException, NumberFormatException {
        try (BufferedReader br = new BufferedReader(new FileReader(genesFilePath))) {
            List<String> geneList = new LinkedList<>();
            String line;
            while ((line = br.readLine()) != null) {
                geneList.add(line);
            }
            genes = geneList.toArray(new String[geneList.size()]);
        }

        numOfSamples = new int[datasetsPaths.length];
        D_ged = new float[numOfSamples.length][genes.length][];
        
        for (int k = 0; k < numOfSamples.length; k++){
            CSVFileReader CSVreader = new CSVFileReader(datasetsPaths[k], csvSeparator);

            int i = 0;
            int samples = 0;
            List<String> row;
            while ((row = CSVreader.readFields()) != null) {
                if (i == 0) {
                    samples = row.size();
                    numOfSamples[k] = samples;
                    D_ged[k] = new float[genes.length][samples];
                } else {
                    if (row.size() != samples) {
                        throw new IllegalArgumentException("The dataset " + datasetsPaths[k] + "is invalid. It should contain the same number of data point for each gene (" + samples + ")");
                    }
                }

                for (int j = 0; j < samples; j++){
                    D_ged[k][i][j] = Float.parseFloat(row.get(j));
                }
                i++;
            }

            if (i != genes.length) {
                throw new IllegalArgumentException("The dataset " + datasetsPaths[k] + "is invalid. It should contain a row for each gene (" + genes.length + ")");
            }
        }
    }

    public GRNCOP2Result search() throws InterruptedException{
        GRN = new Relationship[numOfSamples.length][window + 1][genes.length][genes.length];

        for (int gene = 0; gene < genes.length; gene++) {
            completionService.submit(new GRNCOP2(gene));
        }
            
        executorService.shutdown();
        for (int gene = 0; gene < genes.length; gene++) {
            completionService.take();
            pm.setProgress(gene / genes.length);
        }
        
        return new GRNCOP2Result(genes, GRN);
    }
    
    /***
     * This procedure rearranges the gene expression values of each gene from lower to higher value. 
     * The reordering is saved in the matrix OrdGEDM.
     * @param OrdGEDM
     * @param ged
     * @param numOfSamples 
     */
    private float[] OrdExpressionDataByGene(float[] ged, int numOfSamples) {
        float[] sortedSamples = new float[numOfSamples];
        System.arraycopy(ged, 0, sortedSamples, 0, numOfSamples);
        Arrays.sort(sortedSamples);
        return sortedSamples;
    }

    /***
     * This procedure rearranges the gene expression values of each gene from lower to higher value.
     * The reordering is saved in the matrix OrdGEDMDiscrete.
     * 
     * @param OrdGEDMDiscrete
     * @param ged
     * @param numOfSamples
     * @param w
     */
    private float[] OrdExpressionDataByGeneforDiscretization(float[] ged, int numOfSamples, int w) {
        float[] sortedSamples = new float[numOfSamples];      
        System.arraycopy(ged, w, sortedSamples, 0, numOfSamples);            
        Arrays.sort(sortedSamples);
        return sortedSamples;
    }

    /***
     * This procedure calculates the mean gene expression value for each gene.
     * The information is stored in the vector MEDGM.
     * 
     * @param OrdGEDMDiscrete
     * @param MEDG
     * @param w
     * @param numOfSamples
     * @param activegenes 
     */
    private float MeanExpressionDataByGene(float[] OrdGEDMDiscrete, int numOfSamples, boolean[] activegenes, int gene) {
        if (numOfSamples == 1) {
            activegenes[gene] = false;
            return OrdGEDMDiscrete[0];
        }
        
        if (numOfSamples == 2) {
            activegenes[gene] = true;
            return (OrdGEDMDiscrete[0] + OrdGEDMDiscrete[1]) / 2F;
        }
        
        float m1 = OrdGEDMDiscrete[0];
        float m2 = 0;
        for (int sample = 1; sample < numOfSamples; sample++) {
          m2 += OrdGEDMDiscrete[sample];
        }
        float prom1;
        float prom2 = m2 / (numOfSamples - 1);
        float obj = 0;
        for (int sample = 1; sample < numOfSamples; sample++) {
           obj += Math.pow(OrdGEDMDiscrete[sample] - prom2, 2);
        }
        obj /= (numOfSamples - 1);
        int pos = 0;
        for (int sample = 1; sample < numOfSamples - 1; sample++) {
            m2 -= OrdGEDMDiscrete[sample];
            m1 += OrdGEDMDiscrete[sample];
            prom1 = m1 / (sample + 1);
            prom2 = m2 / (numOfSamples - (sample + 1));
            float var1 = 0;
            float var2 = 0;
            for (int vi = 0; vi< numOfSamples; vi++){
                if (vi <= sample) {
                    var1 += Math.pow(OrdGEDMDiscrete[vi] - prom1, 2);
                } else {
                    var2 += Math.pow(OrdGEDMDiscrete[vi] - prom2, 2);
                }
            }
            var1 /= sample + 1;
            var2 /= numOfSamples - (sample + 1);

            if (var1 + var2 < obj) {
                obj = var1 + var2;
                pos = sample;
            }
        }

        activegenes[gene] = pos != 0 && pos != numOfSamples - 2;
        return (OrdGEDMDiscrete[pos] + OrdGEDMDiscrete[pos + 1]) / 2;
    }
    
    @Override
    public Void call() throws Exception {
        for (int k = 0; k < numOfSamples.length; k++) {
            float[][] ged = D_ged[k];
            for (int w = 0; w < window + 1; w++) {
                int samplesToAnalyze = numOfSamples[k] - w;
                float[] medg = new float[genes.length];
                boolean[] active = new boolean[genes.length];
                for (int kGene = 0; kGene < genes.length; kGene++) {
                    float[] D_OrdGEDMDiscrete = OrdExpressionDataByGeneforDiscretization(ged[kGene], samplesToAnalyze, w);
                    medg[kGene] = MeanExpressionDataByGene(D_OrdGEDMDiscrete, samplesToAnalyze, active, kGene);
                }

                if (!active[pGene]) {
                    continue;
                }
                
                Threshold[] thresholds = ThresholdMatrixComputation(ged, medg, samplesToAnalyze, w);
                GRN[k][w][pGene] = ComputeSolution(ged, thresholds, medg, samplesToAnalyze, w);
            }
        }

        return null;
    }

    /***
     * This procedure calculates all gene thresholds and this data is saved in the matrix THRM
     * 
     * @param ged
     * @param MEDG
     * @param columnas
     * @param numOfSamples
     * @param w
     */
    private Threshold[] ThresholdMatrixComputation( float[][] ged, float[] MEDG, int numOfSamples, int w) {
        Threshold[] WTHRM = new Threshold[genes.length];
        for (int kGene = 0; kGene < genes.length; kGene++) {
            float[] exp = OrdExpressionDataByGene(ged[kGene], numOfSamples);
            WTHRM[kGene] = BestThresholdCalculation(ged[pGene], ged[kGene], exp, MEDG[pGene], numOfSamples, w);
        }
        return WTHRM;
    }

    private Threshold BestThresholdCalculation(float[] pGeneSamples, float[] kGeneSamples, float[] OrdGEDM, float pGeneMean, int numOfSamples, int w) {
        float t = Float.NEGATIVE_INFINITY;
        float bestThreshold = Float.NEGATIVE_INFINITY;
        float bestEntropy = Float.POSITIVE_INFINITY;

        for (float threshold : OrdGEDM) {
            if (threshold > t) {
                t = threshold;
                float entropy = PartitionEntropy(pGeneSamples, kGeneSamples, pGeneMean, numOfSamples, w, t);
                if (entropy < bestEntropy) {
                    bestEntropy = entropy;
                    bestThreshold = t;
                }
            }
        }
        boolean active = bestThreshold !=0 && bestThreshold !=1 && bestThreshold != numOfSamples - 1;
        return new Threshold(bestThreshold, active);
    }

    /***
     * This procedure calculates the entropy of a sample set partition
     * 
     * @param pGeneSamples
     * @param kGeneSamples
     * @param mean
     * @param numOfSamples
     * @param w
     * @param t
     * @return 
     */
    private float PartitionEntropy(float[] pGeneSamples, float[] kGeneSamples, float pGeneMean, int numOfSamples, int w, float threshold) {
        int posS1 = 0; 
        int negS1 = 0;
        int cardS1 = 0;
        int posS2 = 0;
        int negS2 = 0;
        int cardS2 = 0;

        for (int sample = 0; sample < numOfSamples; sample++) {              
            if (kGeneSamples[sample] < threshold) { /* the sample belong to S1 */
                cardS1++;
                if (pGeneSamples[sample + w] < pGeneMean) { /* pGene is upregulated*/
                    posS1++;
                } else { /* pGene is downregulated */
                    negS1++;
                }
            } else { /* then, the sample belong to S2 */
                cardS2++;
                if (pGeneSamples[sample + w] >= pGeneMean) { /* pGene is upregulated*/
                    posS2++;
                } else {/* pGene is downregulated */
                    negS2++;
                }
            }
        }

        float pEntropy = 0;
        /* Entropy calculated using equation (3.1), page 55, Mitchell's Machine Learning Book */
        if (cardS1 == 0)  {
            if (posS2 == 0) {
                pEntropy = (float) (-(negS2 / (float) numOfSamples) * (Math.log(negS2 / (float) numOfSamples) / Math.log(2)));
            } else if (negS2 == 0) {
                pEntropy = (float) (-(posS2 / (float) numOfSamples) * (Math.log(posS2 / (float) numOfSamples) / Math.log(2)));
            } else {
                pEntropy = (float) (-(posS2 / (float) numOfSamples) * (Math.log(posS2 / (float) numOfSamples) / Math.log(2)) - (negS2 / (float) numOfSamples) * (Math.log(negS2 / (float) numOfSamples) / Math.log(2)));
            }
        }

        if (cardS2 == 0) {
            if (posS1 == 0) {
                pEntropy = (float) (-(negS1 / (float) numOfSamples) * (Math.log(negS1 / (float) numOfSamples) / Math.log(2)));
            } else if (negS1 == 0) {
                pEntropy = (float) (-(posS1 / (float) numOfSamples) * (Math.log(posS1 / (float) numOfSamples) / Math.log(2)));
            } else {
                pEntropy = (float) (-(posS1 / (float) numOfSamples) * (Math.log(posS1 / (float) numOfSamples) / Math.log(2)) - (negS1 / (float) numOfSamples) * (Math.log(negS1 / (float) numOfSamples) / Math.log(2)));
            }
        }
        
        float entS1 = 0;
        float entS2 = 0;
        if (cardS1 > 0 && cardS2 > 0) {
            if (posS1 == 0) {
                entS1 = (float) (-(negS1 / (float) numOfSamples) * (Math.log(negS1 / (float) numOfSamples) / Math.log(2)));
            } else if (negS1 == 0) {
                entS1 = (float) (-(posS1 / (float) numOfSamples) * (Math.log(posS1 / (float) numOfSamples) / Math.log(2)));
            } else if (posS1 > 0 && negS1 > 0) {
                entS1 = (float) (-(posS1 / (float) numOfSamples) * (Math.log(posS1 / (float) numOfSamples) / Math.log(2)) - (negS1 / (float) numOfSamples) * (Math.log(negS1 / (float) numOfSamples) / Math.log(2)));
            }
            
            if (posS2 == 0) {
                entS2 = (float) (-(negS2 / (float) numOfSamples) * (Math.log(negS2 / (float) numOfSamples) / Math.log(2)));
            } else if (negS2 == 0) {
                entS2 = (float) (-(posS2 / (float) numOfSamples) * (Math.log(posS2 / (float) numOfSamples) / Math.log(2)));
            } else if (posS2 > 0 && negS2 > 0) {
                entS2 = (float) (-(posS2 / (float) numOfSamples) * (Math.log(posS2 / (float) numOfSamples) / Math.log(2)) - (negS2 / (float) numOfSamples) * (Math.log(negS2 / (float) numOfSamples) / Math.log(2)));
            }
            /* The next equation is based on page 23 of Kohani's Thesis */
            pEntropy = (cardS1 * entS1) / (float)numOfSamples + (cardS2 * entS2) / (float) numOfSamples;
        }

        return pEntropy;
    }

    public Relationship[] ComputeSolution(float[][] ged, Threshold[] WTHRM, float[] MEDG, int numOfSamples, int w) {
        Relationship[] relationships = new Relationship[genes.length];
        for (int gene = 0; gene < genes.length; gene++) {
            if (gene == pGene || !WTHRM[gene].active) {
                continue;
            }

            int downRegTP = 0;
            int downRegFP = 0;
            int downRegTN = 0;
            int downRegFN = 0;

            int upRegTP = 0;
            int upRegFP = 0;
            int upRegTN = 0;
            int upRegFN = 0;
                
            for (int sample = 0; sample < numOfSamples; sample++) {
                float valpGene = DGEDM(ged[pGene], WTHRM, MEDG, w, pGene, sample);
                float calculatedDGEDM = DGEDM(ged[gene], WTHRM, MEDG, w, gene, sample);
                if (calculatedDGEDM == 0) {
                    continue;
                }
                
                if (valpGene == 1) {
                    if (calculatedDGEDM == 1) {
                        downRegFN++;
                        upRegTP++;
                    } else { /* gene = -1 */
                        downRegTP++;
                        upRegFN++;
                    }
                } else if (valpGene == -1) {
                    if (calculatedDGEDM == 1) {
                        downRegTN++;
                        upRegFP++;
                    } else { /* gene = -1 */
                        downRegFP++;
                        upRegTN++;
                    }
                } else { /* valpGene = 0 */
                    if (calculatedDGEDM == 1)  {
                        downRegFN++;
                        upRegFP++;
                    } else { /* gene = -1 */
                        downRegFP++;
                        upRegFN++;
                    }
                }
            }

            Metric downReg = new Metric(downRegTP, downRegFP, downRegTN, downRegFN);
            Metric upReg = new Metric(upRegTP, upRegFP, upRegTN, upRegFN);

            relationships[gene] = new Relationship(upReg, downReg);
        }
        return relationships;
    }

    private float DGEDM(float[] ged, Threshold[] WTHRM, float[] MEDG, int w, int i, int j){
        if (i != pGene){
            return ged[j] < WTHRM[i].t ? -1 : 1;
        } else {
            return ged[j + w] < MEDG[pGene] ? -1 : 1;
        }
    }
}
