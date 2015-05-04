/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accenture.master.template;

import com.accenture.utils.MasterTemplateUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chethan seenappa
 *
 */
public class TemplateGenerator {

    public String filePath;
    private String destinationFilePath;

    private TemplateGenerator(String fileName) {
        String completePath = "C:\\monkParser\\"+fileName;
        this.setFilePath(completePath);
    }
    
    private void setFilePath(String filePath){
        this.filePath = filePath;
        this.setDestinationFilePath(filePath);
    }
    
    public String getFilePath(){
        return this.filePath;
    }
    
    public void setDestinationFilePath(String destinationFilePath) {
        String[] tempPath= destinationFilePath.split("\\.");
        String tempdestinationFilePath = tempPath[0]+"FinalTemplate.csv";
        this.destinationFilePath = tempdestinationFilePath;
    }
    
    public String getDestinationFilePath() {
        return this.destinationFilePath;
    }
    
    public static void main(String[] args) {
        MasterTemplateUtil masterTemplate = new MasterTemplateUtil();
        masterTemplate.buildMSHSegement();
        HashMap mshSegment = masterTemplate.getMSHFieldValuePair();
        String fileName;
        if(args.length > 0){
            fileName = args[0];
        }else{
            Scanner scanInput = new Scanner(System.in);
            System.out.println("Enter file name with extention:");
            fileName = scanInput.nextLine();
        }
        TemplateGenerator templateGenerator = new TemplateGenerator(fileName);
        try {
            templateGenerator.generateTemplateWithHL7Standards(mshSegment);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TemplateGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TemplateGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void generateTemplateWithHL7Standards(HashMap mshSegment) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(this.getFilePath())));
        FileWriter fw = new FileWriter(new File(this.getDestinationFilePath()));
        try (BufferedWriter writeToFile = new BufferedWriter(fw)) {
            String result;
            LinkedList mshSegementValues = new LinkedList();
            while((result = br.readLine())!= null){
                String[] segements = result.split(",");
                if(segements[0].contains("MSH")){
                    mshSegementValues.add(result);
                }
            }
            Iterator iterate = mshSegment.entrySet().iterator();
            while (iterate.hasNext()) {
                Entry element = (Entry) iterate.next();
                String key = element.getKey().toString();
                String[] cardinalityAndDescription = (String[]) element.getValue();
                String currentValue = key;
                for (String cardinality : cardinalityAndDescription) {
                    currentValue += (", " + cardinality);
                }
                if(mshSegementValues.size() >0){
                    String firstRow = mshSegementValues.getFirst().toString();
                    String[] elements = firstRow.split(",");
                    if(elements[0].equals(key)){
                        String[] values= mshSegementValues.removeFirst().toString().split(",");
                        for(int i = values.length-4; i <values.length; i++){
                            currentValue += (","+values[i]);
                        }
                    }
                }
                writeToFile.write(currentValue);
                writeToFile.write(System.lineSeparator());
            }
        }
    }
    
}
