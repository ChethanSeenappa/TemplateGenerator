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

/**
 *
 * @author Chethan seenappa
 *
 */
public class TemplateGenerator {

    public String filePath;
    private String destinationFilePath;

    public TemplateGenerator(String filePath){
        this.setFilePath(filePath);
    }
    
    private void setFilePath(String filePath){
        this.filePath = filePath;
        this.setDestinationFilePath(filePath);
    }
    
    public String getFilePath(){
        return this.filePath;
    }
    
    public void setDestinationFilePath(String destinationFilePath) {
        String[] tempPath= destinationFilePath.split("\\\\");
        String fileName = tempPath[tempPath.length -1], tempdestinationFilePath = "";
        String[] appendFileName = fileName.split("\\.");
        fileName = "Legacy_"+appendFileName[0]+"_TR.csv";
        for(int i =0 ; i< tempPath.length-1; i++){
            tempdestinationFilePath += (tempPath[i]+"\\");
        }
        tempdestinationFilePath += fileName;
        this.destinationFilePath = tempdestinationFilePath;
    }
    
    public String getDestinationFilePath() {
        return this.destinationFilePath;
    }
    
    public void generateTemplateWithHL7Standards(MasterTemplateUtil masterTemplate) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(this.getFilePath())));
        FileWriter fw = new FileWriter(new File(this.getDestinationFilePath()));
        try (BufferedWriter writeToFile = new BufferedWriter(fw)) {
            String result;
            LinkedList mshSegmentValues = new LinkedList(),evnSegmentValues = new LinkedList(), 
                pidSegmentValues = new LinkedList(), pv1SegmentValues = new LinkedList(),
                pv2SegmentValues = new LinkedList(), orcSegmentValues = new LinkedList(),
                obxSegmentValues = new LinkedList(), obrSegmentValues = new LinkedList(),
                dg1SegmentValues = new LinkedList(), remainingSegmentValues = new LinkedList();
            while((result = br.readLine())!= null){
                String[] segments = result.split(",");
                if(segments[0].contains("MSH")){
                    mshSegmentValues.add(result);
                }else if(segments[0].contains("EVN")){
                    evnSegmentValues.add(result);
                }else if(segments[0].contains("PID")){
                    pidSegmentValues.add(result);
                }else if(segments[0].contains("PV1")){
                    pv1SegmentValues.add(result);
                }else if(segments[0].contains("PV2")){
                    pv2SegmentValues.add(result);
                }else if(segments[0].contains("ORC")){
                    orcSegmentValues.add(result);
                }else if(segments[0].contains("OBX")){
                    obxSegmentValues.add(result);
                }else if(segments[0].contains("OBR")){
                    obrSegmentValues.add(result);
                }else if(segments[0].contains("DG1")){
                    dg1SegmentValues.add(result);
                }else{
                    remainingSegmentValues.add(result);
                }
            }
            if(mshSegmentValues.size() > 0){
                this.writeContentToMasterTemplate(masterTemplate.getMSHFieldValuePair(), mshSegmentValues, writeToFile);
            }else if(evnSegmentValues.size() > 0){
                this.writeContentToMasterTemplate(masterTemplate.getEVNFieldValuePair(), evnSegmentValues, writeToFile);
            }else if(pidSegmentValues.size() > 0){
                this.writeContentToMasterTemplate(masterTemplate.getPIDFieldValuePair(), pidSegmentValues, writeToFile);
            }else if(pv1SegmentValues.size() > 0){
                this.writeContentToMasterTemplate(masterTemplate.getPIDFieldValuePair(), pv1SegmentValues, writeToFile);
            }else if(pv2SegmentValues.size() > 0){
                this.writeContentToMasterTemplate(masterTemplate.getPIDFieldValuePair(), pv2SegmentValues, writeToFile);
            }else if(orcSegmentValues.size() > 0){
                this.writeContentToMasterTemplate(masterTemplate.getPIDFieldValuePair(), orcSegmentValues, writeToFile);
            }else if(obxSegmentValues.size() > 0){
                this.writeContentToMasterTemplate(masterTemplate.getPIDFieldValuePair(), obxSegmentValues, writeToFile);
            }else if(obrSegmentValues.size() > 0){
                this.writeContentToMasterTemplate(masterTemplate.getPIDFieldValuePair(), obrSegmentValues, writeToFile);
            }else if(dg1SegmentValues.size() > 0){
                this.writeContentToMasterTemplate(masterTemplate.getPIDFieldValuePair(), dg1SegmentValues, writeToFile);
            }else if(remainingSegmentValues.size() > 0){
                this.writeContentToMasterTemplate(masterTemplate.getPIDFieldValuePair(), remainingSegmentValues, writeToFile);
            }
        }
    }

    private void writeContentToMasterTemplate(HashMap currentSegment, LinkedList currentSegementValues, BufferedWriter writeToFile) throws IOException {
        Iterator iterate = currentSegment.entrySet().iterator();
        while (iterate.hasNext()) {
            Entry element = (Entry) iterate.next();
            String key = element.getKey().toString();
            String[] cardinalityAndDescription = (String[]) element.getValue();
            String currentValue = key;
            for (String cardinality : cardinalityAndDescription) {
                currentValue += (", " + cardinality);
            }
            if(currentSegementValues.size() > 0){
                String firstRow = currentSegementValues.getFirst().toString();
                String[] elements = firstRow.split(",");
                if(elements[0].equals(key)){
                    String[] values= currentSegementValues.removeFirst().toString().split(",");
                    for(int i = values.length-4; i <values.length; i++){
                        currentValue += (","+values[i]);
                    }
                }
            }
            writeToFile.write(currentValue);
            writeToFile.write(System.lineSeparator());
        }
        for (Object currentSegementValue : currentSegementValues) {
            writeToFile.write(currentSegementValue.toString());
            writeToFile.write(System.lineSeparator());
        }
    }
    
}
