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
        
        NaiveBayesTables nbTable = new NaiveBayesTables(dm.getData());
        
        ArrayList<ArrayList<String>> positiveData = nbTable.getPosData();
        ArrayList<ArrayList<String>> negativeData = nbTable.getNegData();

        System.out.println(positiveData.get(0).size());
        System.out.println(negativeData.get(0).size());
        
        HashMap positiveTable = nbTable.createProbabilityTable(positiveData);
        
        double sum = 0;
        int fold = 5;
        
        for (int i = 1; i <= fold; i++) {
            ArrayList<ArrayList<String>> trainingData = splitData(fold, i);
            ArrayList<ArrayList<String>> testData = dm.getDataSplit();
            
            double accuracy = 0;
            sum += accuracy;
        }
        sum /= 5;

        System.out.println("Five cross validation accuracy: " + sum);
    }
    
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
    
}
