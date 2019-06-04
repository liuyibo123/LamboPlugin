package com.lambo.util;
import com.intellij.ide.ui.EditorOptionsTopHitProvider;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SqlGeneratorUtil {
    private static final Logger logger = LoggerFactory.getLogger(SqlGeneratorUtil.class);
    public static List<String> generate(File file,List<String> tableNames) {
        List<String> result = new ArrayList<>();
        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(file);

            Element appData = document.getRootElement();
            List<Element> elements=appData.elements();

            if(tableNames!=null&&!tableNames.isEmpty()){
                for(String tableName:tableNames){
                    String temp1 = getSql(getTable(elements,tableName),true);
                    result.add(temp1);
                }
            }else{
                List<Element> tables = getTables(elements);
                for(Element element:tables){
                    String sql = getSql(element,true);
                    result.add(sql);
                }
            }
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }
    public static List<String> getTableNames(File file){
        List<String> result = new ArrayList<>();
        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            Element appData = document.getRootElement();
            List<Element> elements=appData.elements();
            List<Element> tables = getTables(elements);
            for(Element table:tables){
                String tableId = table.attributeValue("id");
                result.add(tableId);
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return result;
    }
    public static String getSql(Element table,boolean delete) {
        String tableId = table.attributeValue("id");
        List<Element> columns = table.elements("column");
        StringBuilder stringBuilder = new StringBuilder();
//        if(delete){
//            System.out.println("drop table "+tableId+";");
//        }
        stringBuilder.append("create table if not exists " + tableId + "(\n");
        List<String> primaryKeyList = new ArrayList<>();
        for (Element column : columns) {
            String id = column.attributeValue("id");
            String name = column.attributeValue("name");
            String type = column.attributeValue("type");
            String size = column.attributeValue("size");
            String required = column.attributeValue("required");
            String primaryKey = column.attributeValue("primaryKey");
            stringBuilder.append("\t`" + id + "` " + type);
            if (size != null) {
                stringBuilder.append("(" + size + ")");
            }
            if (required != null && "true".equals(required)) {
                stringBuilder.append(" not null");
            }
            if (primaryKey != null && "true".equals(primaryKey)) {
                primaryKeyList.add(id);
            }
            stringBuilder.append(" comment '" + name + "',\n");
        }
        stringBuilder.append("\t primary key(");
        for (int i = 0; i < primaryKeyList.size(); i++) {
            stringBuilder.append(primaryKeyList.get(i));
            if (i != primaryKeyList.size() - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append(")\n");
        stringBuilder.append(");\n");
        return stringBuilder.toString();
    }

    private static Element getTable(List<Element> elements,String tableName) {
        List<Element> tables = new ArrayList<>();
        for(Element t:elements){
            if(t.getName().equals("table")){
                if(t.attributeValue("id").equals(tableName)){
                    return t;
                }
            }else if(t.elements().size()>0){
                Element element = getTable(t.elements(),tableName);
                if(element!=null){
                    return element;
                }
            }
        }
        return null;
    }
    private static List<Element> getTables(List<Element> elements) {
        List<Element> tables = new ArrayList<>();
        for(Element t:elements){
            if(t.getName().equals("table")){
                tables.add(t);
            }else if(t.elements().size()>0){
                tables.addAll(getTables(t.elements()));
            }
        }
        return tables;
    }
}
