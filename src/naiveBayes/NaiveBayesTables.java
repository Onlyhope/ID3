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
    
    private ArrayList<ArrayList<String>> fullData;
    private ArrayList<ArrayList<String>> posData;
    private ArrayList<ArrayList<String>> negData;
    
    /**'
     * Classify data into posData and negData;
     * @param data 
     */
    public NaiveBayesTables(ArrayList<ArrayList<String>> data) {
        fullData = data;
        posData = new ArrayList<>();
        negData = new ArrayList<>();
        
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
    }
    
    public HashMap createProbabilityTable(ArrayList<ArrayList<String>> data) {
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
            System.out.println(entry.getKey() + "|" + entry.getValue());
            entry.setValue(entry.getValue() / total);
            System.out.println(entry.getKey() + "|" + entry.getValue());
        }
        
        return probabilityMap;
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
    
    
    
}
