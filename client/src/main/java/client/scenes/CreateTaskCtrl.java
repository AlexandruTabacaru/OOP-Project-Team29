package client.scenes;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Task;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

public class CreateTaskCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final ListCtrl listCtrl;

    @FXML
    private TextField taskName;

    @FXML
    private TextField taskDesc;

    //this sets up the server, mainctrl and listctrl variables
    @Inject
    public CreateTaskCtrl(final ServerUtils server, final MainCtrl mainCtrl,
                          final ListCtrl listCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.listCtrl = listCtrl;

    }

    //this is run when the cancel button is pressed, it sends the user back to the overview
    public void cancel() {
        clearFields();
        mainCtrl.showBoardOverview();
    }

    //this is run when the confirm button is pressed,
    //it is meant to inform the server to create a new task with the
    //appropriate name and description however this interface does not work yet
    public void confirm() {
        try {
            Task task = getTask();
            listCtrl.addCard(task);
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainCtrl.showBoardOverview();
    }

    //this is run to get the description and name of
    //the task out of the text boxes and to create a new Task object
    private Task getTask() {
        return new Task(taskName.getText(), taskDesc.getText());
    }

    //this clears the text fields of the UI to allow them to be reusable
    private void clearFields() {
        taskDesc.clear();
        taskName.clear();
    }
}