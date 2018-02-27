package com.adamnemeth.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class TodoData {
    private static TodoData instance = new TodoData();
    private static String filename = "TodoListItems.txt";
    private ObservableList<TodoItem> todoItems;
    private DateTimeFormatter formatter;
    private Connection connection;
    private Statement statement;
    private String sql;
    private ResultSet resultSet;

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }

    public static TodoData getInstance(){
        return instance;
    }

//    private TodoData(){
//        formatter =DateTimeFormatter.ofPattern("dd-MM-yyyy");
//    }

    private TodoData(){
        formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public void addTodoItem(TodoItem item){
        todoItems.add(item);
    }

    public Connection getConnection() {
        return connection;
    }

    //    public void loadTodoItems() throws IOException {
//        todoItems = FXCollections.observableArrayList();
//        Path path = Paths.get(filename);
//        BufferedReader br = Files.newBufferedReader(path);
//
//        String input;
//
//        try {
//            while ((input = br.readLine()) != null) {
//                String[] itemPieces = input.split("\t");
//                String shortDescription = itemPieces[0];
//                String details = itemPieces[1];
//                String dateString = itemPieces[2];
//
//                LocalDate date = LocalDate.parse(dateString, formatter);
//                TodoItem todoItem = new TodoItem(shortDescription, details, date);
//                todoItems.add(todoItem);
//            }
//        } finally {
//            if (br != null) {
//                br.close();
//            }
//        }
//    }

//    public void storeTodoItems() throws IOException{
//        Path path = Paths.get(filename);
//        BufferedWriter bw = Files.newBufferedWriter(path);
//        try {
//            Iterator<TodoItem> iter = todoItems.iterator();
//            while (iter.hasNext()){
//                TodoItem item = iter.next();
//                bw.write(String.format("%s\t%s\t%s", item.getShortDescription(), item.getDetails(), item.getDeadline().format(formatter)));
//                bw.newLine();
//            }
//        } finally {
//            if (bw != null){
//                bw.close();
//            }
//        }
//    }


    public void storeTodoItems() throws IOException{
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);
//        try {
//            Iterator<TodoItem> iter = todoItems.iterator();
//            while (iter.hasNext()){
//                TodoItem item = iter.next();
//                bw.write(String.format("%s\t%s\t%s", item.getShortDescription(), item.getDetails(), item.getDeadline().format(formatter)));
//                bw.newLine();
//            }
//        } finally {
//            if (bw != null){
//                bw.close();
//            }
//        }

        try {
            Iterator<TodoItem> iter = todoItems.iterator();
            while (iter.hasNext()){
                TodoItem item = iter.next();
                String sql = "INSERT INTO TODO " +
                        "VALUES (NULL, '"+ item.getShortDescription()+"', '"+item.getDetails()+"', '"+item.getDeadline().format(formatter)+"')";
                System.out.println(sql);
                TodoData.getInstance().getConnection().createStatement().executeUpdate(sql);
            }
        } catch (SQLException sqlException){
            System.out.println(sqlException);
        } finally {
            if (bw != null){
                bw.close();
            }
        }

    }

    public void getTodoItemsFromDatabase() throws SQLException {
        todoItems = FXCollections.observableArrayList();

        setDatabaseConnection();
        statement = connection.createStatement();
        sql = "SELECT * FROM TODO";
        resultSet = statement.executeQuery(sql);
        String shortDescription;
        String details;
        String dateString;
        while (resultSet.next()) {
            shortDescription = resultSet.getString("SHORTDESC");
            details = resultSet.getString("LONGDESC");
            dateString = resultSet.getString("DATE");
            LocalDate date = LocalDate.parse(dateString, formatter);
            TodoItem todoItem = new TodoItem(shortDescription, details, date);
            todoItems.add(todoItem);
        }
        resultSet.close();
    }


    public void setDatabaseConnection() throws SQLException{
        connection = DriverManager.getConnection("jdbc:h2:file:./db/todos", "", "");
    }

    public void deleteTodoItem(TodoItem item){
        todoItems.remove(item);
    }
}
