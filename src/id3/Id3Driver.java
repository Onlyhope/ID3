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
    
    private static Id3DataManager dm;
    
    public static void main(String[] args) {
        
        
        File file = new File("");
        String filePath = file.getAbsolutePath();
        filePath = filePath + "\\cse353-hw2-data.tsv"; // Real file: cse353-hw2-data.tsv, test file: test-file.txt
        
        dm = new Id3DataManager();
        dm.loadDataFile(filePath);
           
        dm.processContinuousData(0, 1);
        dm.processContinuousData(12, 1);      
        dm.processContinuousData(10, 100);        
        dm.processContinuousData(11, 100);       
        dm.processContinuousData(4, 1);
        dm.removeDataSet(2);
        dm.removeDataSet(4);
        
        int fold = 5;
        double sum = 0;
        
        for (int i = 1; i <= fold; i++) {
            double accuracy = validationTest(fold, i);
            sum += accuracy;
        }
        
        sum /= 5;

        System.out.println(sum);
    }
    
    public static double compareResults(ArrayList<String> results, ArrayList<ArrayList<String>> data) {
        double accuracy = 0;
        
        double posCount = 0;
        double counter = 0;
        int index = data.get(0).size() - 1;
        
        for (int i = 0; i < results.size() && i < data.size(); i++) {            
            if (results.get(i).equals(data.get(i).get(index))) {
                posCount++;
            }
            counter++;
        }
        
        accuracy = (posCount / counter);
        
        return accuracy;
    }
    
    /**
     * Removes the part/split of the main array data.
     * @param split
     * @param part
     * @return 
     */
    public static double validationTest(int split, int part) {
        int n = dm.getData().get(0).size();
        
        n /= split;
        part--;
        
        int beg = n * part;
        
        ArrayList<ArrayList<String>> modifiedDataArr = dm.dataSplit(beg, n, part);
        
        Id3Tree id3Tree = new Id3Tree();
        id3Tree.buildTree(modifiedDataArr);
        ArrayList<ArrayList<String>> splitData = dm.getDataSplit();
        ArrayList<String> results = id3Tree.testData(splitData);
        double accuracy = compareResults(results, splitData);
        
        return accuracy;
    }
        
}
