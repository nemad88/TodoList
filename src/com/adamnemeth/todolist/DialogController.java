package com.adamnemeth.todolist;

import com.adamnemeth.todolist.datamodel.TodoData;
import com.adamnemeth.todolist.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.time.LocalDate;

public class DialogController {

    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadlinePicker;

    public TodoItem processResults() throws SQLException {
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadLineValue = deadlinePicker.getValue();
        int ID = TodoData.getInstance().getLastID()+1;
        TodoItem newItem = new TodoItem(ID, shortDescription, details, deadLineValue);
        TodoData.getInstance().storeTodoItems(newItem);
        TodoData.getInstance().addTodoItem(newItem);
        return newItem;
    }
}
