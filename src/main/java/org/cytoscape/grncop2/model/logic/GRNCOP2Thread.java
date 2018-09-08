package org.cytoscape.grncop2.model.logic;

import java.util.Arrays;
import java.util.concurrent.Callable;
import org.cytoscape.grncop2.model.businessobjects.Relationship;
import org.cytoscape.grncop2.model.businessobjects.Metric;
import org.cytoscape.grncop2.model.businessobjects.Threshold;

public class GRNCOP2Thread implements Callable {
    private final int pGene;
    private final String[] genes;
    private final float[][][] expressionData;
    private final int window;
    private final Relationship[][][][] GRN;

    public GRNCOP2Thread(int pGene, String[] genes, float[][][] expressionData,
            int window, Relationship[][][][] GRN){
        this.pGene = pGene;
        this.genes = genes;
        this.expressionData = expressionData;
        this.window = window;
        this.GRN = GRN;
    }

    /***
     * Rearranges the gene expression values of each gene from lower to higher value.
     * 
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
     * Calculates the mean gene expression value for each gene.
     * As a side effect, it also set in activeGenes a boolean indicating
     * whether the gene is active or not.
     * 
     * @param gene 
     * @param ordGEDMDiscrete
     * @param activeGenes
     * @return The mean gene expression value for a given gene
     */
    private float MeanExpressionDataByGene(int gene, float[] ordGEDMDiscrete, boolean[] activeGenes) {
        int numOfSamples = ordGEDMDiscrete.length;
        if (numOfSamples == 1) {
            activeGenes[gene] = false;
            return ordGEDMDiscrete[0];
        }
        
        if (numOfSamples == 2) {
            activeGenes[gene] = true;
            return (ordGEDMDiscrete[0] + ordGEDMDiscrete[1]) / 2F;
        }
        
        float m1 = ordGEDMDiscrete[0];
        float m2 = 0;
        for (int sample = 1; sample < numOfSamples; sample++) {
          m2 += ordGEDMDiscrete[sample];
        }
        float prom1;
        float prom2 = m2 / (numOfSamples - 1);
        float obj = 0;
        for (int sample = 1; sample < numOfSamples; sample++) {
           obj += Math.pow(ordGEDMDiscrete[sample] - prom2, 2);
        }
        obj /= (numOfSamples - 1);
        int pos = 0;
        for (int sample = 1; sample < numOfSamples - 1; sample++) {
            m2 -= ordGEDMDiscrete[sample];
            m1 += ordGEDMDiscrete[sample];
            prom1 = m1 / (sample + 1);
            prom2 = m2 / (numOfSamples - (sample + 1));
            float var1 = 0;
            float var2 = 0;
            for (int vi = 0; vi< numOfSamples; vi++){
                if (vi <= sample) {
                    var1 += Math.pow(ordGEDMDiscrete[vi] - prom1, 2);
                } else {
                    var2 += Math.pow(ordGEDMDiscrete[vi] - prom2, 2);
                }
            }
            var1 /= sample + 1;
            var2 /= numOfSamples - (sample + 1);

            if (var1 + var2 < obj) {
                obj = var1 + var2;
                pos = sample;
            }
        }

        activeGenes[gene] = pos != 0 && pos != numOfSamples - 2;
        return (ordGEDMDiscrete[pos] + ordGEDMDiscrete[pos + 1]) / 2;
    }
    
    @Override
    public Void call() throws Exception {
        for (int k = 0; k < expressionData.length; k++) {
            float[][] ged = expressionData[k];
            for (int w = 0; w < window + 1; w++) {
                int samplesToAnalyze = ged[0].length - w;

                float[] medg = new float[genes.length];
                boolean[] active = new boolean[genes.length];
                for (int kGene = 0; kGene < genes.length; kGene++) {
                    float[] ordGEDMDiscrete = OrdExpressionDataByGeneforDiscretization(ged[kGene], samplesToAnalyze, w);
                    medg[kGene] = MeanExpressionDataByGene(kGene, ordGEDMDiscrete, active);
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
     * Calculates all gene thresholds
     * 
     * @param ged
     * @param MEDG
     * @param numOfSamples
     * @param w
     */
    private Threshold[] ThresholdMatrixComputation( float[][] ged, float[] MEDG, int numOfSamples, int w) {
        Threshold[] WTHRM = new Threshold[genes.length];
        for (int kGene = 0; kGene < genes.length; kGene++) {
            float[] exp = OrdExpressionDataByGeneforDiscretization(ged[kGene], numOfSamples, 0);
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
     * @param pGeneMean
     * @param numOfSamples
     * @param w
     * @param threshold
     * @return Partition Entropy
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

    private Relationship[] ComputeSolution(float[][] ged, Threshold[] WTHRM, float[] MEDG, int numOfSamples, int w) {
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
        return i != pGene
            ? ged[j] < WTHRM[i].t ? -1 : 1
            : ged[j + w] < MEDG[pGene] ? -1 : 1;
    }
}
