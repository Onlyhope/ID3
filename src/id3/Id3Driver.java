/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id3;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Aaron
 */
public class Id3Driver {
    public static void main(String[] args) {
        
        
        File file = new File("");
        String filePath = file.getAbsolutePath();
        filePath = filePath + "\\cse353-hw2-data.tsv"; // Real file: cse353-hw2-data.tsv, test file: test-file.txt
        
        Id3DataManager dm = new Id3DataManager();
        dm.loadDataFile(filePath);
        
        
        dm.processContinuousData(0, 1);
        dm.processContinuousData(12, 1);      
        dm.processContinuousData(10, 100);        
        dm.processContinuousData(11, 100);       
        dm.processContinuousData(4, 1);
        dm.removeDataSet(4);
        dm.removeDataSet(2);
        
        Id3Tree id3Tree = new Id3Tree();
        id3Tree.buildTree(dm.getData());
                
        id3Tree.traverseTree();
    }
}
