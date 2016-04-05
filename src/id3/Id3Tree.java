/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id3;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Aaron
 */
public class Id3Tree {

    private ArrayList<ArrayList<String>> data;
    private ArrayList<String> results;

    private Id3Node root;

    /**
     * Constructor
     */
    public Id3Tree() {
        data = new ArrayList();
    }

    // Class Methods
    
    /**
     * Begin building the tree. Set the data of tree to the data pass through.
     * Then creates the new root and pass the newData to it.
     * 
     * @param newData 
     */
    public void buildTree(ArrayList<ArrayList<String>> newData) {
        data = newData;
        createRoot(data);
    }
    
    /**
     * Creates the root and set the nodeData to dataToUse. 
     * Then split the root using a recursive Id3 Algorithm.
     * 
     * @param dataToUse 
     */
    private void createRoot(ArrayList<ArrayList<String>> dataToUse) {
        root = new Id3Node();
        root.setNodeData(dataToUse);

        root.bestSplit();
    }
    
    /**
     * Tests the data by turning each sample into a String.
     * Test each sample using the private helper method testTree,
     * which runs the sample down the decision tree.
     * Saves the result in an array that is to be returned.
     * 
     * @param testData's row represent each sample and its columns
     * represent an attribute of the sample.
     * @return the array, testResults
     */
    public ArrayList<String> testData(ArrayList<ArrayList<String>> testData) {
        if (root == null) {
            System.out.println("Tree not built");
            return new ArrayList();
        }

        ArrayList<String> testResults = new ArrayList();

        for (int i = 0; i < testData.size(); i++) {
            StringBuilder sb = new StringBuilder();

            for (String word : testData.get(i)) {
                sb.append(word + "|");
            }
            testResults.add(testTree(sb.toString(), root));
        }

//        System.out.println(counter);
//        System.out.println(testResults.size());

        return testResults;
    }
    
    /**
     * Runs the sample down the decision tree, at the start of curNode.
     * @param sample to be tested.
     * @param curNode is usually the root.
     * @return results
     */
    public String testTree(String sample, Id3Node curNode) {
        ArrayList<Id3Node> children = curNode.getChildren();

        if (curNode == null) {
            return "";
        }
        
        // Cases where there is no more data left to split the node. 
        if (children.isEmpty()) {
            if (curNode.getAttribute().equals("less") || curNode.getAttribute().equals("more")) {
                return curNode.getAttribute();
            } else {
                if (curNode.posCount > curNode.negCount) {
                    return "more";
                } else if (curNode.negCount > curNode.posCount) {
                    return "less";
                } else {
                    return "Undetermined";
                }
            }
        }

        for (int i = 0; i < children.size(); i++) {
            Id3Node curChild = children.get(i);
            if (sample.contains(curChild.getAttribute())) {
                sample = sample.replace(curChild.getAttribute(), "");
                return testTree(sample, curChild);
            }
        }
        
        // Cases where the decision tree has not yet encounter
        if (curNode.posCount > curNode.negCount) {
            return "more";
        } else if (curNode.negCount > curNode.posCount) {
            return "less";
        } else {
            return "Undetermined";
        }
    }
    
    /**
     * Allows user controller traversal of tree. Used for debugging.
     */
    public void traverseTree() {
        if (root == null) {
            System.out.println("Tree not built");
            return;
        }

        traverseTree(root);
    }
    
    /**
     * Helper method of traverseTree()
     * 
     * @param curNode 
     */
    private void traverseTree(Id3Node curNode) {
        if (curNode.getChildren().isEmpty()) {
            System.out.println(curNode.getAttribute());
            return;
        } else {

            System.out.println("Which child node do you want to visit? ");

            ArrayList<Id3Node> children = curNode.getChildren();
            int i = 0;

            for (Id3Node child : children) {
                System.out.println(i++ + ". " + child.getAttribute() + " | Entropy: " + child.getEntropy());
            }

            Scanner in = new Scanner(System.in);
            int childIndex = Integer.parseInt(in.nextLine());

            traverseTree((Id3Node) curNode.getChildren().get(childIndex));
            return;
        }
    }

    // Getters
    public Id3Node getRoot() {
        return root;
    }

    public ArrayList<String> getResults() {
        return results;
    }

    // Setters
    public void setData(ArrayList<ArrayList<String>> newData) {
        data = newData;
    }
}
