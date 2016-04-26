/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naiveBayes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Aaron
 */
public class NaiveBayesTables {
    
    private ArrayList<String> actualResults;
    
    private ArrayList<ArrayList<String>> fullData;
    private ArrayList<ArrayList<String>> posData;
    private ArrayList<ArrayList<String>> negData;
    private HashMap<String, Double> negMap;
    private HashMap<String, Double> posMap;
    private Double negProb;
    private Double posProb;
    
    /**'
     * Classify data into posData and negData;
     * @param data 
     */
    public NaiveBayesTables(ArrayList<ArrayList<String>> data) {
        fullData = data;
        posData = new ArrayList<>();
        negData = new ArrayList<>();
        
        actualResults = new ArrayList<>();
        
        int lastCol = fullData.size() - 1;
        
        // Initialize posData and negData
        for (int i = 0; i < fullData.size(); i++) {
            ArrayList<String> dataColumn = new ArrayList<>();
            posData.add(dataColumn);
            
            dataColumn = new ArrayList<>();
            negData.add(dataColumn);
        }
        
        // Classify fullData into posData and negData
        ArrayList<String> lastColData = fullData.get(lastCol);
        
        for (int i = 0; i < lastColData.size(); i++) {
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
        
        double total = fullData.get(0).size();
        negProb = negData.get(0).size() / total;
        posProb = posData.get(0).size() / total;
    }
    
    public void createProbMaps() {
        posMap = createProbabilityMap(posData);
        negMap = createProbabilityMap(negData);
    }
    
    public HashMap createProbabilityMap(ArrayList<ArrayList<String>> data) {
        HashMap<String, Double> probabilityMap = new HashMap<>();
        
        ArrayList<String> classifiers = data.get(data.size()-1);
        int total = classifiers.size();
        
        for (int i = 0; i < data.size()-1; i++) {
            ArrayList<String> curAttribute = data.get(i);
            
            for (int j = 0; j < curAttribute.size(); j++) {
                String curValue = curAttribute.get(j);
                
                if (!probabilityMap.containsKey(curValue)) {
                    probabilityMap.put(curValue, 1.0);
                } else {
                    probabilityMap.put(curValue, probabilityMap.get(curValue)+1);
                }               
            }
        }
        
        for (Map.Entry<String, Double> entry : probabilityMap.entrySet()) {
            entry.setValue(entry.getValue() / total);
        }
        
        return probabilityMap;
    }
    
    public ArrayList<String> testData(ArrayList<ArrayList<String>> testData) {
        
        ArrayList<String> testResults = new ArrayList();
        
        for (int i = 0; i < testData.size(); i++) {
            StringBuilder sb = new StringBuilder();
            
            actualResults.add(testData.get(i).get(testData.get(0).size()-1));

            for (int j = 0; j < testData.get(0).size()-1; j++) {
                sb.append(testData.get(i).get(j) + ",");
            }
           
            testResults.add(testTable(sb.toString()));
        }
            
        return testResults;
    }
    
    private String testTable(String testData) {       
        String[] testDataSplit = testData.split(",");
        
        double positiveP = posProb;
        double negativeP = negProb;
        
        for (int i = 0; i < testDataSplit.length; i++) {
            if (posMap.containsKey(testDataSplit[i])) {
                positiveP *= posMap.get(testDataSplit[i]);
            } else {
                positiveP *= .000001;
            }
        }
        
        for (int i = 0; i < testDataSplit.length; i++) {
            if (negMap.containsKey(testDataSplit[i])) {
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
