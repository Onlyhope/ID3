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
    
    /**
     * Uses the Id3 algorithm to split the nodes depending on the highest
     * information gain or gain ratio.
     */
    public void bestSplit() {
        int numAttributes = nodeData.size() - 1; // Subtract 1, Last row is results
        if (entropy == -1) {
            determineEntropy();
        }
//        System.out.println(entropy);

        if (entropy > 0 && numAttributes > 0) {
            double infoGain = 0;
            double splitEntropy = 0;
            double gainRatio = 0;
            double bestGain = 0;
            double curGain = 0;
            int bestIndex = 0;
            ArrayList<Id3Node> bestSplitChildren = new ArrayList<>();

            // Finding best split
            for (int i = 0; i < numAttributes; i++) {
                children.clear();
                curGain = getGainOfAttribute(nodeData.get(i));
                infoGain = entropy - curGain;
                splitEntropy = calcSplitEntropy(children, i);
                gainRatio = (infoGain / splitEntropy);

                if (children.size() > 2) {
                    if (gainRatio > bestGain) {
                        bestGain = gainRatio;
                        bestIndex = i;
                        bestSplitChildren.clear();
                        bestSplitChildren.addAll(children);
                    }
                } else {
                    if (infoGain > bestGain) {
                        bestGain = infoGain;
                        bestIndex = i;
                        bestSplitChildren.clear();
                        bestSplitChildren.addAll(children);
                    }
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

    /**
     * Gets the pseudoInfoGain from splitting with the target attribute /
     * classifier. Transfer current node data to the corresponding child node,
     * by iterating over the targeted attribute data and matching it with the
     * child node with same attribute. Uses the private helper method
     * createChildren to accomplish this. Then pseudoInfoGain is calculated
     * using the children nodes.
     *
     * @param curAttribute is the target attribute or classifier
     * @return the pseudoInfoGain
     */
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

    /**
     * Data is transfered over to the correct child node. If no child nodes
     * matches with target or current data, a new node is created and added on
     * to childrenNodes. Data are also transferred.
     *
     * @param childrenNodes list of child nodes
     * @param target is the current attribute value at the current index
     * @param curIndex is the current index of the data being transferred
     * @return
     */
    private void createChildren(ArrayList<Id3Node> childrenNodes, String target, int curIndex) {
        for (Id3Node node : childrenNodes) {
            if (node.getAttribute().equals(target)) {
                // Then get data from at mData's index from parent nodeData
                for (int k = 0; k < nodeData.size(); k++) {
                    node.getNodeData().get(k).add(nodeData.get(k).get(curIndex));
                }
                return;
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
            System.out.println("No child node was created");
        }
    }

    /**
     * Calculates pseudo info gain, using the list of children. The lower it is
     * the better.
     *
     * @param nodeList is the children nodes
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

    /**
     * Calculates the splitInformation using the children nodes, which each has
     * its own subset of values over the targeted attributed.
     *
     * @param childrenNodes to be used.
     * @param index of targeted attribute
     * @return splitInformation of attribute and the node that called the method
     */
    private double calcSplitEntropy(ArrayList<Id3Node> childrenNodes, int index) {
        double splitEntropy = 0;

        double setTotal = getNodeData().get(index).size();
        double subsetRatio;
        double log2Ratio;

        for (Id3Node child : childrenNodes) {
            subsetRatio = (child.getNodeData().get(0).size() / setTotal);
            log2Ratio = Math.log(subsetRatio) / Math.log(2);
            splitEntropy = splitEntropy - (subsetRatio * log2Ratio);
        }

        return splitEntropy;
    }

    /**
     * Deletes the attribute's data once it is used.
     *
     * @param indexToRemove index of targeted attribute
     */
    private void cleanChildrenData(int indexToRemove) {
        for (Id3Node node : children) {
            node.getNodeData().remove(indexToRemove);
        }
    }

    /**
     * Processes data and sets posCount and negCount. Then it sets the entropy
     * value to the calculated entropy value.
     *
     * @return
     */
    public void determineEntropy() {
        boolean success = processData();

        if (success) {
            entropy = calcEntropy(posCount, negCount);
            return;
        } else {
            System.out.println("Entropy is not set.");
        }

        return;
    }

    /**
     * Counts the occurrences positive and negative in the data, using the
     * targeted attribute.
     *
     * @return
     */
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
     * Calculates the entropy, using the node posCount and negCount.
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
