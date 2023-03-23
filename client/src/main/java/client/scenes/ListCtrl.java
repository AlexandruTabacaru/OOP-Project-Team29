package client.scenes;

import client.utils.ServerUtils;
import client.utils.TaskListUtils;
import client.utils.TaskUtils;
import com.google.inject.Inject;
import client.utils.customExceptions.TaskException;
import commons.Task;
import commons.TaskList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ListCtrl implements Initializable {
    private static final DataFormat taskCustom = new DataFormat("task.custom");
    private MainCtrl mainCtrl;
    @FXML
    ListView<Task> list;
    @FXML
    Text title;
    @FXML
    VBox vBox;

    private TaskList taskList;
    private long boardID;
    private TaskListUtils taskListUtils;
    private TaskUtils taskUtils;
    private ServerUtils server;


    @Inject
    public ListCtrl(final MainCtrl mainCtrl, final TaskListUtils taskListUtils,
                    final TaskUtils taskUtils) {
        this.taskListUtils = taskListUtils;
        this.taskUtils = taskUtils;
        this.mainCtrl = mainCtrl;
    }

    private void setDragHandlers(final ListCtrl listCtrl) {
        list.setOnDragDetected(event -> dragDetected(listCtrl, event));
        list.setOnDragEntered(event -> dragEntered(listCtrl, event));
        list.setOnDragOver(event -> dragOver(listCtrl, event));
        list.setOnDragExited(event -> dragExited(listCtrl, event));
        list.setOnDragDropped(event -> dragDropped(listCtrl, event));
        list.setOnDragDone(event -> {
            try {
                dragDone(listCtrl, event);
            } catch (TaskException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void dragDetected(final ListCtrl listCtrl, final MouseEvent event) {
        ListView<Task> lv = listCtrl.list;
        Dragboard dragboard = lv.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent cc = new ClipboardContent();
        if (lv.getSelectionModel().getSelectedItem() == null)
            return;
        var selectedTask=lv.getSelectionModel().getSelectedItem();
        cc.put(taskCustom, selectedTask);
        dragboard.setContent(cc);
        event.consume();
    }
    public void dragEntered(final ListCtrl listCtrl, final DragEvent event) {
        ListView<Task> lv = listCtrl.list;
        lv.setStyle("-fx-effect: innershadow(gaussian, rgba(0,0,0,0.8), 20, 0, 0, 0);");
        event.consume();
    }

    public void dragExited(final ListCtrl listCtrl, final DragEvent event) {
        ListView<Task> lv = listCtrl.list;
        lv.setStyle("-fx-effect: none;");
        event.consume();
    }

    public void dragOver(final ListCtrl listCtrl, final DragEvent event) {
        ListView<Task> lv = listCtrl.list;
        if (event.getDragboard().hasContent(taskCustom))
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        event.consume();
    }

    public void dragDropped(final ListCtrl listCtrl, final DragEvent event) {
        ListView<Task> lv = listCtrl.list;
        if (event.getDragboard().hasContent(taskCustom)) {
            Task task = (Task) event.getDragboard().getContent(taskCustom);
            lv.getItems().add(task);

            event.setDropCompleted(true);
        } else
            event.setDropCompleted(false);
        event.consume();
    }

    public void dragDone(final ListCtrl listCtrl, final DragEvent event) throws TaskException {
        ListView<Task> lv = listCtrl.list;
        Task selectedTask = lv.getSelectionModel().getSelectedItem();
        if (selectedTask != null && event.getTransferMode() == TransferMode.MOVE &&
            event.getEventType()==DragEvent.DRAG_DONE) {
            lv.getItems().remove(selectedTask);

            taskUtils.deleteTask(listCtrl.boardID, listCtrl.taskList.id, selectedTask.id);
            taskUtils.addTask(listCtrl.boardID, taskList.id, selectedTask);
        }
        event.consume();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        ListCtrl controller = this;
        setDragHandlers(controller);
        list.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(final Task task, final boolean empty) {
                super.updateItem(task, empty);
                if (task == null || empty) {
                    setGraphic(null);
                } else {
                    try {
                        var cardLoader = new FXMLLoader(getClass().getResource("Card.fxml"));
                        Node card = cardLoader.load();
                        CardCtrl cardCtrl = cardLoader.getController();
                        cardCtrl.initialize(task, controller, taskListUtils);
                        setGraphic(card);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void setLayoutX(final double x){
        list.setLayoutX(x);
    }

    /**
     * This refreshes the tasks of the list.
     *
     * @param newTaskList the list for which the tasks must be refreshed.
     */
    public void refresh(final TaskList newTaskList, final long boardID) {
        this.boardID = boardID;
        this.taskList = newTaskList;
        if (!Objects.equals(title.getText(), newTaskList.getName())) // if the title is different
            title.setText(newTaskList.getName()); // update it

        list.getItems().retainAll(newTaskList.getTasks()); // retain only the tasks
        // that are also in newTaskList
        for (Task task : newTaskList.getTasks()) { // go through all the received tasks
            if (!list.getItems().contains(task)) // if this task isn't there
                list.getItems().add(task); // add it
        }
    }

    public void hardRefresh(final TaskList newTaskList, final long boardID) {
        this.boardID = boardID;
        this.taskList = newTaskList;
        if (!Objects.equals(title.getText(), newTaskList.getName())) // if the title is different
            title.setText(newTaskList.getName()); // update it

        list.getItems().setAll(newTaskList.getTasks());// Change the tasklist to the new tasklist
    }

    /**
     * this adds a task to a specific list
     *
     * @param task the task to be added
     */
    public void addCard(final Task task) {
        try {
            if (list == null) {
                list = new ListView<Task>();
            }
            list.getItems().add(task);
            taskUtils.addTask(boardID, this.getTaskList().id, task);
        } catch (TaskException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * this adds a task to a specific list
     */
    public void addCard() {
        mainCtrl.showCreateTask(this);
    }

    public VBox getRoot() {
        return vBox;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public long getBoardID() {
        return boardID;
    }

    public void setServer(final ServerUtils server) {
        this.server = server;
        this.taskUtils = new TaskUtils(server);
        this.taskListUtils = new TaskListUtils(server);
    }
}
