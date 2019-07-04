/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.questback;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class QuestBackImporterTree {
    private List<QuestBackImportRow> retList = new ArrayList();
    private ArrayList<String> content;
    private QuestBackImportRow currentTopRow;
    private QuestBackImportRow currentQuestion;

    public QuestBackImporterTree() {
        loadFileContent();
    }
    
    public List<QuestBackImportRow> create() {
       for (String line : content) {
            List<String> rowContent = new ArrayList<String>(Arrays.asList(line.split(";")));
            if (!rowContent.get(0).isEmpty()) {
                createCurrentTopLevel(rowContent);
            }
            
            if (rowContent.size() > 1 && !rowContent.get(1).isEmpty()) { 
                craeteCurrentQuestion(rowContent);
            }
            
            if (rowContent.size() > 3 && rowContent.get(0).isEmpty() && rowContent.get(1).isEmpty() && rowContent.get(2).isEmpty()) { 
                craeteCurrentAnswerOption(rowContent);
            }
        }
       
        return retList;
    }
    
    private void loadFileContent() {
        try {
            String fileLocation = "/datafiles/promeister/qbimport.csv";
            Path path = Paths.get(fileLocation);
            String stringFromFile = java.nio.file.Files.lines(path).collect(Collectors.joining("\n"));

            content = new ArrayList<String>(Arrays.asList(stringFromFile.split("\n")));
        } catch (IOException ex) {
            Logger.getLogger(QuestBackManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        QuestBackImporterTree tree = new QuestBackImporterTree();
        tree.create();
    }

    private void createCurrentTopLevel(List<String> rowContent) {
        QuestBackImportRow row = createRow(rowContent);
        currentTopRow = row;
        retList.add(currentTopRow);
    }

    private void craeteCurrentQuestion(List<String> rowContent) {
        QuestBackImportRow row = createRow(rowContent);
        currentQuestion = row;
        currentTopRow.children.add(currentQuestion);
    }

    private QuestBackImportRow createRow(List<String> rowContent) {
        QuestBackImportRow row = new QuestBackImportRow();
        row.col1 = rowContent.get(0);
        
        if (rowContent.size() > 1) {
            row.col2 = rowContent.get(1);
        }
        if (rowContent.size() > 2) {
            row.col3 = rowContent.get(2);
        }
        if (rowContent.size() > 3) {
            row.col4 = rowContent.get(3);
        }
        if (rowContent.size() > 4) {
            row.col5 = rowContent.get(4);
        }
        
        return row;
    }

    private void craeteCurrentAnswerOption(List<String> rowContent) {
        QuestBackImportRow row = createRow(rowContent);
        currentQuestion.children.add(row);
    }
}
