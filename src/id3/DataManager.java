package id3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Aaron
 */
public class DataManager {

    private ArrayList<ArrayList<String>> data;
    private ArrayList<ArrayList<String>> dataSplit;
    private ArrayList<ArrayList<String>> conFullData;
    
    public DataManager() {
        data = new ArrayList<>();
    }
    
    
    
    /**
     * Loads the data file and pre-process the target data
     * into "less" and "more" for ease of use.
     * @param filePath 
     */
    public void loadDataFile(String filePath) {
        File file = new File(filePath);
        BufferedReader br;
        FileReader fr;

        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String line;
            String[] strArr;

            if ((line = br.readLine()) != null) {
                strArr = line.split("\t"); 
                ArrayList<String> attributeData;

                for (int i = 0; i < strArr.length; i++) {
                    attributeData = new ArrayList<>();
                    attributeData.add(strArr[i]);
                    data.add(attributeData);
                }
            }

            while ((line = br.readLine()) != null) {
                strArr = line.split("\t");

                for (int i = 0; i < data.size() && i < strArr.length; i++) {
                    data.get(i).add(strArr[i]);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        preProcessData();
    }
    

    /**
     * Changes all values in the last column to "less" or "more". "less" = less
     * than 50k "more" = more than 50k This is for ease of interpretation in the
     * code.
     */
    public void preProcessData() {
        int lastIndex = data.size() - 1;

        ArrayList<String> dataToModify = data.get(lastIndex);

        for (int i = 0; i < dataToModify.size(); i++) {
            if (dataToModify.get(i).charAt(0) == '<') {
                dataToModify.set(i, "less");
            } else if (dataToModify.get(i).charAt(0) == '>') {
                dataToModify.set(i, "more");
            }
        }
    }
    
    /**
     * Makes a deep copy of the main data. Modify it by removing data
     * values at the index beg. This is done n times and each time
     * the value is copied over to dataSplit. dataSplit is arranged 
     * differently and is used as training data.
     * 
     * @param beg index at where data are removed
     * @param n times the data is removed and added
     * @return the modified deep copy of the main data
     */
    public ArrayList<ArrayList<String>> dataSplit(int beg, int n) {
        ArrayList<ArrayList<String>> newDataArr = deepCopy(data);
        int dataSize = newDataArr.get(0).size();
        
        dataSplit = new ArrayList();
        
        for (int i = 0; i < n; i++) {
            ArrayList<String> temp = new ArrayList();
            for (int j = 0; j < newDataArr.size(); j++) {
                temp.add(newDataArr.get(j).get(beg));
                newDataArr.get(j).remove(beg);
            }
            dataSplit.add(temp);
        }
        
        return newDataArr;
    }
    
    /**
     * Makes a deep copy of the main data.
     * 
     * @param arrayToCopy the data to be copied (main data)
     * @return deepCopy
     */
    private ArrayList<ArrayList<String>> deepCopy(ArrayList<ArrayList<String>> arrayToCopy) {
        ArrayList<ArrayList<String>> deepCopy = new ArrayList();
        
        for (int i = 0; i < arrayToCopy.size(); i++) {
            ArrayList<String> temp = new ArrayList();
            for (int j = 0; j < arrayToCopy.get(i).size(); j++) {
                temp.add(arrayToCopy.get(i).get(j));
            }
            deepCopy.add(temp);
        }
        
        return deepCopy;
    }
    
    public void separateContinuous(ArrayList<ArrayList<String>> dataToSeparate, int... i) {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int index : i) {
            ArrayList<String> curConData = dataToSeparate.get(index);
            conFullData.add(curConData);
            indices.add(index);
        }
        
        Collections.sort(indices, Collections.reverseOrder());
        
        for (int index : indices) {
            dataToSeparate.remove(index);
        }
        
    }
    
    /**
     * Process continuous data into a discrete binary variable.
     * Finds the threshold by iterating over the binValue, until 
     * the best threshold is found. The range is to the max value.
     * Anything lower or equal to this threshold
     * is classify as less than or equal to threshold. Anything greater than
     * threshold is classify as greater than threshold.
     * @param index Column index of data to be processed.
     * @param binValue Value to increase threshold.
     */
    public void processContinuousData(int index, int binValue) {
        ArrayList<String> targetData = data.get(index);
        ArrayList<String> resultData = data.get(data.size() - 1);

        ArrayList<ArrayList<String>> dataToSort = new ArrayList();

        int size1 = targetData.size();
        int size2 = resultData.size();

        ArrayList<String> tempData;

        for (int i = 0; i < size1 && i < size2; i++) {
            tempData = new ArrayList();
            tempData.add(targetData.get(i));
            tempData.add(resultData.get(i));

            dataToSort.add(tempData);
        }

        try {
            mergeSort(dataToSort, 0, (dataToSort.size() - 1));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        double infoGain = 1;
        int bestThreshold = 0;
        int max = Integer.parseInt(dataToSort.get(dataToSort.size() - 1).get(0));

        for (int i = 0; i <= max; i += binValue) {
            ArrayList<String> transformedData = transformData(targetData, i);
            ArrayList<ArrayList<String>> testData = new ArrayList();
            testData.add(transformedData);
            testData.add(resultData);

            Id3Node testNode = new Id3Node();
            testNode.setNodeData(testData);
            testNode.bestSplit();
            
            double curIG = testNode.calcInfoGain(testNode.getChildren());
           
            if (curIG < infoGain) {
                infoGain = curIG;
                bestThreshold = i;
            }
        }
        
        data.set(index, (transformData(targetData, bestThreshold)));
    }
    
    /**
     * Transform the data into binary discrete value, using the given threshold.
     * @param data to be transformed.
     * @param threshold to be used
     * @return 
     */
    private ArrayList<String> transformData(ArrayList<String> data, int threshold) {
        String s = Integer.toString(threshold);
        ArrayList<String> transformedData = new ArrayList();

        for (int i = 0; i < data.size(); i++) {
            if (Integer.parseInt(data.get(i)) < threshold) {
                transformedData.add("less than " + s);
            } else if (Integer.parseInt(data.get(i)) > threshold) {
                transformedData.add("greater than " + s);
            } else if (Integer.parseInt(data.get(i)) == threshold) {
                transformedData.add("equal to " + s);
            }
        }

        return transformedData;
    }
    
    /**
     * Removes the data column at index.
     * @param index of column to be removed.
     */
    public void removeDataSet(int index) {
        data.remove(index);
    }

    // Personal MergeSort
    private static void merge(ArrayList<ArrayList<String>> array, int beg, int mid, int end) throws Exception {
        int n1 = mid - beg + 1;
        int n2 = end - mid;
        ArrayList<ArrayList<String>> left = new ArrayList();
        ArrayList<ArrayList<String>> right = new ArrayList();

        for (int i = 0; i < n1; i++) {
            left.add(array.get(beg + i));
        }

        for (int i = 0; i < n2; i++) {
            right.add(array.get(mid + i + 1));
        }

        int i = 0;
        int j = 0;

        for (int k = beg; k <= end; k++) {
            if (i == n1) {
                array.set(k, right.get(j));
                j++;
            } else if (j == n2) {
                array.set(k, left.get(i));
                i++;
            } else if (compString(left.get(i).get(0), right.get(j).get(0))) {
                array.set(k, left.get(i));
                i++;
            } else if (compString(right.get(j).get(0), left.get(i).get(0))) {
                array.set(k, right.get(j));
                j++;
            }
        }

    }

    private static boolean compString(String left, String right) {
        int a = Integer.parseInt(left);
        int b = Integer.parseInt(right);

        if (a <= b) {
            return true;
        } else if (b > a) {
            return false;
        } else {
            return false;
        }
    }

    private static void mergeSort(ArrayList<ArrayList<String>> array, int beg, int end) throws Exception {
        if (beg < end) {
            int mid = (beg + end) / 2;

            mergeSort(array, beg, mid);
            mergeSort(array, (mid + 1), end);
            merge(array, beg, mid, end);
        } else {
            return;
        }
    }

    // Getters
    public ArrayList<ArrayList<String>> getData() {
        return data;
    }
    
    public ArrayList<ArrayList<String>> getDataSplit() {
        return dataSplit;
    }
}
