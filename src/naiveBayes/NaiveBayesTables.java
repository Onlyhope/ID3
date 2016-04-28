/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naiveBayes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aaron
 */
public class NaiveBayesTables {

    private ArrayList<String> actualResults;

    private ArrayList<ArrayList<String>> fullData;
    private ArrayList<ArrayList<String>> conFullData;
    private ArrayList<ArrayList<String>> posData;
    private ArrayList<ArrayList<String>> conPosData;
    private ArrayList<ArrayList<String>> negData;
    private ArrayList<ArrayList<String>> conNegData;
    private HashMap<String, Double> negMap;
    private HashMap<Integer, Double> conAverageNegMap;
    private HashMap<Integer, Double> conVarianceNegMap;
    private HashMap<String, Double> posMap;
    private HashMap<Integer, Double> conAveragePosMap;
    private HashMap<Integer, Double> conVariancePosMap;
    private Double negProb;
    private Double posProb;

    /**
     * '
     * Classify data into posData and negData;
     *
     * @param data
     */
    public NaiveBayesTables(ArrayList<ArrayList<String>> data) {
        fullData = data;
        posData = new ArrayList<>();
        negData = new ArrayList<>();

        actualResults = new ArrayList<>();

        conFullData = new ArrayList<>();
        conPosData = new ArrayList<>();
        conNegData = new ArrayList<>();
        
        int lastCol = fullData.size() - 1;
        ArrayList<String> lastColData = fullData.get(lastCol);

        separateContinuous(fullData, 0, 8, 9, 10);

        // Initialize posData and negData
        for (int i = 0; i < fullData.size(); i++) {
            ArrayList<String> dataColumn = new ArrayList<>();
            posData.add(dataColumn);

            dataColumn = new ArrayList<>();
            negData.add(dataColumn);
        }
        
        for (int i = 0; i < conFullData.size(); i++) {
            ArrayList<String> dataColumn = new ArrayList<>();
            conPosData.add(dataColumn);

            dataColumn = new ArrayList<>();
            conNegData.add(dataColumn);
        }

        // Classify fullData into posData and negData
        

        for (int i = 0; i < fullData.get(0).size(); i++) {
            if (lastColData.get(i).equals("more")) {
                // Add the positive data to each column of posData
                addDataRow(fullData, posData, i);
            } else if (lastColData.get(i).equals("less")) {
                // Add the negative data to each column of negData
                addDataRow(fullData, negData, i);
            } else {
                // Leftover Conditions for debugging purposes
            }
        }
        
        for (int i = 0; i < conFullData.get(0).size(); i++) {
            if (lastColData.get(i).equals("more")) {
                // Add the positive data to each column of posData
                addDataRow(conFullData, conPosData, i);
            } else if (lastColData.get(i).equals("less")) {
                // Add the negative data to each column of negData
                addDataRow(conFullData, conNegData, i);
            } else {
                // Leftover Conditions for debugging purposes
            }
        }

        double total = fullData.get(0).size();
        negProb = negData.get(0).size() / total;
        posProb = posData.get(0).size() / total;
    }

    /**
     * Creating probability map for posData and negData. Initialize posMap and
     * negMap
     */
    public void createProbMaps() {

        try {
            posMap = createProbabilityMap(posData);
            negMap = createProbabilityMap(negData);
            conAveragePosMap = createConAverageMap(conPosData);
            conAverageNegMap = createConAverageMap(conNegData);
            conVariancePosMap = createConVarianceMap(conPosData);
            conVarianceNegMap = createConVarianceMap(conNegData);
        } catch (Exception ex) {
            Logger.getLogger(NaiveBayesTables.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Create probabilityMap (Hash tables) of the data given
     *
     * @param data
     * @return a probability map (Hash table)
     */
    public HashMap createProbabilityMap(ArrayList<ArrayList<String>> data) {
        HashMap<String, Double> probabilityMap = new HashMap<>();

        ArrayList<String> classifiers = data.get(data.size() - 1);
        int total = classifiers.size();

        for (int i = 0; i < data.size() - 1; i++) {
            ArrayList<String> curAttribute = data.get(i);

            for (int j = 0; j < curAttribute.size(); j++) {
                String curValue = curAttribute.get(j);

                if (!probabilityMap.containsKey(curValue)) {
                    probabilityMap.put(curValue, 1.0);
                } else {
                    probabilityMap.put(curValue, probabilityMap.get(curValue) + 1);
                }
            }
        }

        for (Map.Entry<String, Double> entry : probabilityMap.entrySet()) {
            entry.setValue(entry.getValue() / total);
        }

        return probabilityMap;
    }

    /**
     * Create the average continuous
     *
     * @param data
     * @return
     * @throws Exception
     */
    public HashMap createConAverageMap(ArrayList<ArrayList<String>> data) throws Exception {
        HashMap<Integer, Double> probabilityMap = new HashMap<>();

        for (int i = 0; i < data.size(); i++) {
            double average = getAverage(data.get(i));

            probabilityMap.put(i, average);
        }

        return probabilityMap;
    }

    public HashMap createConVarianceMap(ArrayList<ArrayList<String>> data) throws Exception {
        HashMap<Integer, Double> probabilityMap = new HashMap<>();

        for (int i = 0; i < data.size(); i++) {
            double variance = getVariance(data.get(i));

            probabilityMap.put(i, variance);
        }

        return probabilityMap;
    }

    /**
     * Get results from testData
     *
     * @param testData
     * @param k list of continuous data
     * @return
     */
    public ArrayList<String> testData(ArrayList<ArrayList<String>> testData, int... k) {

        ArrayList<String> testResults = new ArrayList();
        ArrayList<Integer> conIndices = new ArrayList<>();

        // Transform i into ArrayList of indicies
        for (int index : k) {
            conIndices.add(index);
//            System.out.println(index);
        }

        for (int i = 0; i < testData.size(); i++) {
            StringBuilder sb = new StringBuilder();
            StringBuilder con = new StringBuilder();

            actualResults.add(testData.get(i).get(testData.get(0).size() - 1));

            for (int j = 0; j < testData.get(0).size() - 1; j++) {
                if (conIndices.contains(j)) {
                    con.append(testData.get(i).get(j) + ",");
                } else {
                    sb.append(testData.get(i).get(j) + ",");
                }
            }
            
            String data = con.toString() + sb.toString();
            testResults.add(testTable(data, conIndices));
        }

        return testResults;
    }

    private String testTable(String testData, ArrayList<Integer> indicies) {
        String[] testDataSplit = testData.split(",");

        double positiveP = posProb;
        double negativeP = negProb;

        for (int i = 0; i < testDataSplit.length; i++) {
            if (i < 4) {
                double average = conAveragePosMap.get(i);
                double variance = conVariancePosMap.get(i);
                positiveP *= getContinuousP(testDataSplit[i], average, variance);
                
            } else if (posMap.containsKey(testDataSplit[i])) {
                positiveP *= posMap.get(testDataSplit[i]);
            } else {
                positiveP *= .000001;
            }
        }

        for (int i = 0; i < testDataSplit.length; i++) {
            if (i < 4) {
                double average = conAverageNegMap.get(i);
                double variance = conVarianceNegMap.get(i);
                negativeP *= getContinuousP(testDataSplit[i], average, variance);
            } else if (negMap.containsKey(testDataSplit[i])) {
                negativeP *= negMap.get(testDataSplit[i]);
            } else {
                negativeP *= .000001;
            }
        }

        if (posProb > negProb) {
            return "more";
        } else {
            return "less";
        }
    }

    /**
     * Transfers a row of data from arrSource to arrTarget
     *
     * @param arrSource list of data, source
     * @param arrTarget list of data, target
     * @param row the index of the row to be transferred
     */
    private void addDataRow(ArrayList<ArrayList<String>> arrSource, ArrayList<ArrayList<String>> arrTarget, int row) {
        for (int i = 0; i < arrSource.size(); i++) {
            String dataToAdd = arrSource.get(i).get(row);
            arrTarget.get(i).add(dataToAdd);
        }
    }

    // Continuous Data Processing
    public void separateContinuous(ArrayList<ArrayList<String>> dataToSeparate, int... i) {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int index : i) {
            ArrayList<String> curConData = dataToSeparate.get(index);
            conFullData.add(curConData);
            indices.add(index);
        }

        Collections.sort(indices, Collections.reverseOrder());

        for (int index : indices) {
            dataToSeparate.remove(index);
        }

    }

    public double getContinuousP(String value, double average, double variance) {
        double x = Double.parseDouble(value);

        double term1 = Math.sqrt(2 * Math.PI * variance);
        term1 = 1 / term1;

        double term2 = (x - average);
        term2 = Math.pow(term2, 2);
        term2 = (term2 / variance) * (-.5);
        term2 = Math.exp(term2);

        return term1 * term2;
    }

    public double getAverage(ArrayList<String> targetData) throws Exception {
        double total = 0;
        double count = 0;

        for (String x : targetData) {
            try {
                total += Double.parseDouble(x);
                count++;
            } catch (Exception e) {
                System.out.println("Error in parsing double (Average). " + x);
            }
        }

        return total / count;
    }

    public double getVariance(ArrayList<String> targetData) throws Exception {
        double average = getAverage(targetData);

        double total = 0;
        double count = 0;

        for (String x : targetData) {
            try {
                double curVal = Double.parseDouble(x) - average;
                curVal = Math.pow(curVal, 2.0);
                total += curVal;
                count++;
            } catch (Exception e) {
                System.out.println("Error in parsing double (Variance). " + x);
            }
        }

        return total / count;
    }

    // Getters
    public ArrayList<ArrayList<String>> getPosData() {
        return posData;
    }

    public ArrayList<ArrayList<String>> getNegData() {
        return negData;
    }

    public HashMap getNegMap() {
        return negMap;
    }

    public HashMap getPosMap() {
        return posMap;
    }

    public double getPosProb() {
        return posProb;
    }

    public double getNegProb() {
        return negProb;
    }

    public ArrayList<String> getActualResults() {
        return actualResults;
    }
}
