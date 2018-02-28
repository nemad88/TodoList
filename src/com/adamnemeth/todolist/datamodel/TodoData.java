package com.adamnemeth.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TodoData {
    private static TodoData instance = new TodoData();
    private static String filename = "TodoListItems.txt";
    private ObservableList<TodoItem> todoItems;
    private DateTimeFormatter formatter;
    private Connection connection;
    private Statement statement;
    private String sql;
    private ResultSet resultSet;

    //region Getter, Setter
    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }

    public static TodoData getInstance(){
        return instance;
    }

    private TodoData(){
        formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public void addTodoItem(TodoItem item){
        todoItems.add(item);
    }

    public Connection getConnection() {
        return connection;
    }
    //endregion

    public void storeTodoItems(TodoItem todoItem) throws SQLException {
        String sql = "INSERT INTO TODO VALUES (NULL, '" + todoItem.getShortDescription() + "', '" + todoItem.getDetails() + "', '" + todoItem.getDeadline().format(formatter) + "')";
        System.out.println(sql);
        System.out.println(todoItem.toString());
        TodoData.getInstance().getConnection().createStatement().executeUpdate(sql);
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
        int id;
        while (resultSet.next()) {
            id = resultSet.getInt("ID");
            shortDescription = resultSet.getString("SHORTDESC");
            details = resultSet.getString("LONGDESC");
            dateString = resultSet.getString("DATE");
            LocalDate date = LocalDate.parse(dateString, formatter);
            TodoItem todoItem = new TodoItem(id, shortDescription, details, date);
            System.out.println(todoItem.getId());
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

    public int getLastID(){
        int lastID=0;
        try {
            ResultSet rs = TodoData.getInstance().getConnection().createStatement().executeQuery("SELECT MAX(ID) AS MAXID FROM TODO");
            while (rs.next()){
                lastID = rs.getInt("MAXID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("ez"+lastID);
        return lastID;
    }

}
