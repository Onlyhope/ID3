/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id3;

import java.io.File;
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
    public void buildTree(ArrayList<ArrayList<String>> newData) {
        data = newData;
        createRoot(data);
    }

    private void createRoot(ArrayList<ArrayList<String>> dataToUse) {
        root = new Id3Node();
        root.setNodeData(dataToUse);

        root.bestSplit();
    }

    public ArrayList<String> testData(ArrayList<ArrayList<String>> testData) {
        if (root == null) {
            System.out.println("Tree not built");
            return new ArrayList();
        }

        ArrayList<String> testResults = new ArrayList();
        int counter = 0;

        for (int i = 0; i < testData.size(); i++) {
            StringBuilder sb = new StringBuilder();

            for (String word : testData.get(i)) {
                sb.append(word + "|");
            }

            testResults.add(testTree(sb.toString(), root));
            counter++;
        }

        System.out.println(counter);
        System.out.println(testResults.size());

        return testResults;
    }

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

    public void traverseTree() {
        if (root == null) {
            System.out.println("Tree not built");
            return;
        }

        traverseTree(root);
    }

    public void postOrderTraversal(Id3Node curNode) {
        if (curNode == null) {
            return;
        } else {

            ArrayList<Id3Node> children = curNode.getChildren();

            for (Id3Node child : children) {
                postOrderTraversal(child);
            }

        }
    }

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
