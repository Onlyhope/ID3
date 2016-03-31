/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id3;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Aaron
 */
public class Id3Tree {
    
    private ArrayList<ArrayList<String>> data;
    
    private Id3Node root;
    
    /**
     * Constructor
     */
    public Id3Tree() {
        data = new ArrayList();
    }
    
    // Class Methods
    public void buildTree() {
        File file = new File("");
        String filePath = file.getAbsolutePath();
        filePath = filePath + "\\test-file2.txt"; // Real file: cse353-hw2-data.tsv
        
        Id3DataManager dm = new Id3DataManager();
        dm.loadDataFile(filePath);
        data = dm.getData();
        
        createRoot();
    }
    
    private void createRoot() {
        root = new Id3Node();
        root.setNodeData(data);
        
        
        root.bestSplit();       
        
    }
    






    
    // Getters
    
    public Id3Node getRoot() {
        return root;
    }
    
    // Setters
    
    public void setData(ArrayList<ArrayList<String>> newData) {
        data = newData;
    }

   

    
    
}
