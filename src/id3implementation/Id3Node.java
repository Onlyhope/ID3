/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id3implementation;

import java.util.ArrayList;

/**
 *
 * @author Aaron
 */
public class Id3Node {
    
    private ArrayList<Id3Node> children;
    private String name;
    
    public Id3Node(String newName) {
        children = new ArrayList<>();
        name = newName;
    }
    
    
}
