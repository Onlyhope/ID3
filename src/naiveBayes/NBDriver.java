/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naiveBayes;

import id3.DataManager;
import id3.Id3Tree;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Aaron
 */
public class NBDriver {
    
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
        
        double sum = 0;
        int fold = 5;
        
        int i = 1;
               
        for (int x = 1; x <= fold; x++) {           
            ArrayList<ArrayList<String>> trainingData = splitData(fold, x);
            ArrayList<ArrayList<String>> testData = dm.getDataSplit();
            
            NaiveBayesTables nbt = new NaiveBayesTables(trainingData);
            nbt.createProbMaps();
            
            ArrayList<String> results = nbt.testData(testData);
            ArrayList<String> actualResults = nbt.getActualResults();
            
            double accuracy = compareResults(results, actualResults);
            System.out.println(accuracy);
            sum += accuracy;
        }
        sum /= 5;

        System.out.println("Five cross validation accuracy: " + sum);
    }
    
    public static double compareResults(ArrayList<String> results, ArrayList<String> data) {
        double accuracy = 0;
        
        double posCount = 0;
        double counter = 0;
        
        System.out.println("x123: " + results.size() + " " + data.size());
        
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).equals(data.get(i))) {
                posCount++;
            }
            counter++;
        }
        
        accuracy = (posCount / counter);
        
        return accuracy;
    }
    
    public static ArrayList<ArrayList<String>> splitData(int split, int part) {
        int n = dm.getData().get(0).size();
        
        n /= split;
        part--;
        
        int beg = n * part;
        
        ArrayList<ArrayList<String>> modifiedDataArr = dm.dataSplit(beg, n);
        
        return modifiedDataArr;
        
    }
    
    public static void printData(ArrayList<ArrayList<String>> dataToPrint) {
        int count = 1;
        
        for (int i = 0; i < dataToPrint.get(0).size(); i++) {
            System.out.print(count++ + " ");
            for (int j = 0; j < dataToPrint.size(); j++) {
                System.out.print(dataToPrint.get(j).get(i));
            }
            System.out.println();
        }
    }
    
    /**
     * This is used to print test data.
     * @param dataToPrint 
     */
    public static void printData2(ArrayList<ArrayList<String>> dataToPrint) {
        int count = 1;
        
        for (int i = 0; i < dataToPrint.size(); i++) {
            System.out.print(count++ + " ");
            for (int j = 0; j < dataToPrint.get(0).size(); j++) {
                System.out.print(dataToPrint.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }
    
}
