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
    
    private String attribute;
    
    private ArrayList<Id3Node> children;
    private ArrayList<ArrayList<String>> nodeData;
    private double entropy;
    int posCount;
    int negCount;
    

    public Id3Node() {
        nodeData = new ArrayList();
        children = new ArrayList();
        entropy = -1;
    }

    public Id3Node(int dataSize) {
        nodeData = new ArrayList();
        children = new ArrayList();
        entropy = -1;

        ArrayList<String> newEmptyData;
        // Intializine new node data
        for (int i = 0; i < dataSize; i++) {
            newEmptyData = new ArrayList<>();
            nodeData.add(newEmptyData);
        }

    }

    // Class Methods
    public void split(int target) {

    }

    public void bestSplit() {
        int numAttributes = nodeData.size() - 1; // Subtract 1, Last row is results
        if (entropy == -1) determineEntropy();
//        System.out.println(entropy);
        
        if (entropy > 0 && numAttributes > 0) {
            double bestGain = 1;
            double curGain = 0;
            int bestIndex = 0;
            ArrayList<Id3Node> bestSplitChildren = new ArrayList<>();

            // Finding best split
            for (int i = 0; i < numAttributes; i++) {
                children.clear();
                curGain = getGainOfAttribute(nodeData.get(i));

                if (curGain < bestGain) {
                    bestGain = curGain;
                    bestIndex = i;
                    bestSplitChildren.clear();
                    bestSplitChildren.addAll(children);
                }

            }
            children.clear();
            children.addAll(bestSplitChildren);
            cleanChildrenData(bestIndex);
            for (Id3Node child : children) {
                child.bestSplit();
            }
        } else if (entropy == 0) {
//            System.out.println("Pure set found!");
            Id3Node answerNode = new Id3Node();
            int lastIndex = nodeData.size() - 1;
            answerNode.setAttribute(nodeData.get(lastIndex).get(0));
            children.add(answerNode);
            return;
        } else {
//            System.out.println("No more classfiers or entropy not above 0");
            return;
        }
    }

    private double getGainOfAttribute(ArrayList<String> curAttribute) {

        String curData;

        for (int i = 0; i < curAttribute.size(); i++) {
            curData = curAttribute.get(i);

            // Find children with correct attribute name
            createChildren(children, curData, i);
        }

        for (Id3Node curChildren : children) {
            curChildren.determineEntropy();
        }

        return calcInfoGain(children);
    }

    private boolean createChildren(ArrayList<Id3Node> childrenNodes, String target, int curIndex) {
        for (Id3Node node : childrenNodes) {
            if (node.getAttribute().equals(target)) {
                // Then get data from at mData's index from parent nodeData
                for (int k = 0; k < nodeData.size(); k++) {
                    node.getNodeData().get(k).add(nodeData.get(k).get(curIndex));
                }
                return true;
            }
        }
        // Node was not found, create new node
        Id3Node newNode = new Id3Node(getNodeData().size());
        newNode.setAttribute(target);
        children.add(newNode);

        for (int k = 0; k < nodeData.size(); k++) {
            newNode.getNodeData().get(k).add(nodeData.get(k).get(curIndex));
        }

        if (children.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Calculates pseudo info gain.
     * The lower it is the better.
     *
     * @param nodeList
     * @return the weighted total entropy of children nodes
     */
    public double calcInfoGain(ArrayList<Id3Node> nodeList) {
        double sum = 0;

        for (Id3Node curNode : nodeList) {
            sum = sum + curNode.getEntropy() * curNode.getNodeData().get(0).size();
        }
        sum /= getNodeData().get(0).size();

        return sum;
    }
    
    private void cleanChildrenData(int indexToRemove) {
        for (Id3Node node : children) {
            node.getNodeData().remove(indexToRemove);
        }
    }

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
            if (mData.equals("less")) {
                negCount++;
            } else if (mData.equals("more")) {
                posCount++;
            }
        }

        if (posCount == 0 && negCount == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Calculates the entropy
     *
     * @param positive
     * @param negative
     * @return
     */
    public double calcEntropy(int positive, int negative) {
        int total = positive + negative;
        double p = (double) positive / total;
        double n = (double) negative / total;

        double logP, logN;

        if (p > 0) {
            logP = Math.log(p) / Math.log(2);
        } else {
            logP = 0;
        }
        if (n > 0) {
            logN = Math.log(n) / Math.log(2);
        } else {
            logN = 0;
        }

        double result = -(p * logP) - (n * logN);

        return result;
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
