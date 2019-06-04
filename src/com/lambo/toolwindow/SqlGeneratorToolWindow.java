package com.lambo.toolwindow;

import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;
import com.lambo.util.SqlGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SqlGeneratorToolWindow {
    private final Logger logger = LoggerFactory.getLogger(SqlGeneratorToolWindow.class);
    private JPanel sqlGenContext;
    private JButton fileChooseButton;
    private ComboBox tableComboBox;
    private JBList sqlList;
    private JButton buttonRun;
    private JLabel fileName;
    private JButton resetButton;
    private File file;
    private String selectedTables;
    public SqlGeneratorToolWindow(){

        fileChooseButton.addActionListener(e -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
            VirtualFile vfile = FileChooser.chooseFile(descriptor,null ,null );
            if(vfile == null){
                return ;
            }
            file = new File(vfile.getPath());
            fileName.setText(file.getName());
            List<String> tables = (List<String>) SqlGeneratorUtil.getTableNames(file);
            tableComboBox.addItem("全部");
            for(String tableName:tables){
                tableComboBox.addItem(tableName);
            }
        });
        tableComboBox.addActionListener(e -> {
            selectedTables = (String) tableComboBox.getSelectedItem();

        });
        resetButton.addActionListener(e -> {
            tableComboBox.removeAllItems();
            fileName.setText("");
            file = null;
            sqlList.setListData(new String[]{});
        });
        buttonRun.addActionListener(e -> {
            if(file ==null){
                return ;
            }
            List<String> tableNames = new ArrayList<>();

            if(selectedTables!=null&&!"全部".equals(selectedTables)){
                tableNames.add(selectedTables);
            }
            List<String> sqls = SqlGeneratorUtil.generate(file,tableNames);
            sqlList.setListData(sqls.toArray());
        });
    }

    public JPanel getContext(){
        return sqlGenContext;
    }
}
