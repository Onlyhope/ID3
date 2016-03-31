/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id3;

import java.util.ArrayList;

/**
 *
 * @author Aaron
 */
public class Id3Node {
    
    private ArrayList<ArrayList<String>> nodeData;
    private ArrayList<Id3Node> children;
    private double entropy;
    private int posCount;
    private int negCount;
    private String attribute;
    
    
    
    public Id3Node() {
        nodeData = new ArrayList();
        children = new ArrayList();
        entropy = -1;
    }
    
    // Class Methods
    
    
    public boolean determineEntropy() {
        boolean success = processData();
        
        if (success) {
            entropy = calcEntropy(posCount, negCount);
            return true;
        } else {
            return false;
        }     
    }
    
    private boolean processData() {
        int lastIndex = nodeData.size() - 1;
        
        for (String mData : nodeData.get(lastIndex)) {
            if (mData.charAt(0) == '<') {
                negCount++;
            } else if (mData.charAt(0) == '>') {
                posCount++;
            }
        }
        
        if (posCount == 0 && negCount == 0)
            return false;
        else
            return true;
    }
    
    private boolean processData2() {
        int lastIndex = nodeData.get(0).size() - 1;
        
        for(ArrayList<String> strArr : nodeData) {
            if (strArr.get(lastIndex).charAt(0) == '<') {
                negCount++;
            } else if (strArr.get(lastIndex).charAt(0) == '>') {
                posCount++;
            }
        }
        
        if (posCount == 0 && negCount == 0)
            return false;
        else
            return true;
    }
    /**
     * Calculates the entropy
     * @param positive
     * @param negative
     * @return 
     */
    public double calcEntropy(int positive, int negative) {      
        int total = positive + negative;
        double p = (double) positive / total;
        double n = (double) negative / total;
        
        double log1, log2;
        
        if (p > 0) {
            log1 = Math.log(p) / Math.log(2);
        } else {
            log1 = 0;
        }
        if (n > 0) {
            log2 = Math.log(n) / Math.log(2);
        } else {
            log2 = 0;
        }
        
        double result = -(p * log1) - (n * log2); 
        
        return result;
    }
    /**
     * Calculates pseudo info gain.
     * @param nodeList 
     * @return 
     */
    private double calcInfoGain(ArrayList<Id3Node> nodeList) {
       double sum = 0;
       
       for (Id3Node curNode : nodeList) {
           sum = sum + curNode.getEntropy() * curNode.getNodeData().size();
       }
         
       return sum;
    }
    
    private void addNodeData(ArrayList<String> newData) {
        nodeData.add(newData);        
    }
    
    // Getters
    public ArrayList<ArrayList<String>> getNodeData() {
        return nodeData;
    }
    
    public ArrayList getChildren() {
        return children;
    }
    
    public String getAttribute() {
        return attribute;
    }
    
    public double getEntropy() {
        return entropy;
    }
    
    // Setters
    public void setChildren(ArrayList<Id3Node> newChildren) {
        children = newChildren;
    }
    public void setNodeData(ArrayList<ArrayList<String>> newData) {
        nodeData = newData;
    }
    
    public void setAttribute(String newAttribute) {
        attribute = newAttribute;
    }
    
    
}
