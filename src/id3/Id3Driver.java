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
public class Id3Driver {
    
    private static DataManager dm;
    
    public static void main(String[] args) {
        
        
        File file = new File("");
        String filePath = file.getAbsolutePath();
        filePath = filePath + "\\cse353-hw2-data.tsv"; // Real file: cse353-hw2-data.tsv, test file: test-file.txt
        
        dm = new DataManager();
        dm.loadDataFile(filePath);
           
        dm.processContinuousData(0, 1); // Turns it into a binary attribute, using a threshold
        dm.processContinuousData(12, 1); // Turns it into a binary attribute, using a threshold     
        dm.processContinuousData(10, 100); // Turns it into a binary attribute, using a threshold  
        dm.processContinuousData(11, 100); // Turns it into a binary attribute, using a threshold
        dm.processContinuousData(4, 1); // Turns it into a binary attribute, using a threshold
        dm.removeDataSet(2); // Removes un-needed data fnlwgt
        dm.removeDataSet(4); // Removes continous version of education
        
        int fold = 5; // Test fold
        double sum = 0;
        
        for (int i = 1; i <= fold; i++) {
            double accuracy = validationTest(fold, i);
            System.out.println("Accuracy: " + accuracy);
            sum += accuracy;
        }
        
        sum /= 5;

        System.out.println("Five cross validation accuracy: " + sum);
    }
    
    /**
     * Compares the accuracy of the Id3 Algorithm, by comparing test results and its real values.
     * 
     * @param results results from the Id3Algorithm after being trained
     * @param data, real data values
     * @return the accuracy
     */
    public static double compareResults(ArrayList<String> results, ArrayList<ArrayList<String>> data) {
        double accuracy = 0;
        
        double posCount = 0;
        double counter = 0;
        int index = data.get(0).size() - 1;
        
        for (int i = 0; i < results.size() && i < data.size(); i++) {
//            System.out.println(results.get(i) + "|" + data.get(i).get(index));
            if (results.get(i).equals(data.get(i).get(index))) {
                posCount++;
            }
            counter++;
        }
        
        accuracy = (posCount / counter);
        
        return accuracy;
    }
    
    /**
     * Splits the array into parts depending on the value of the split.
     * 
     * @param split the number of parts the data is being split up to
     * @param part the section of the data being used to test
     * @return the accuracy of the test
     */
    public static double validationTest(int split, int part) {
        int n = dm.getData().get(0).size();
        
        n /= split;
        part--;
        
        int beg = n * part;
        
        ArrayList<ArrayList<String>> modifiedDataArr = dm.dataSplit(beg, n);
        
        Id3Tree id3Tree = new Id3Tree();
        id3Tree.buildTree(modifiedDataArr);
        ArrayList<ArrayList<String>> splitData = dm.getDataSplit();
        ArrayList<String> results = id3Tree.testData(splitData);
        double accuracy = compareResults(results, splitData);
    
        return accuracy;
    }
        
}
