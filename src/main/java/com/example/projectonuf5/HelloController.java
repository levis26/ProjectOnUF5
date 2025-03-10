package com.example.projectonuf5;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class HelloController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> fileListView;

    @FXML
    private Button createDirButton;

    @FXML
    private Button createFileButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button renameButton;

    @FXML
    private VBox mainVBox;

    private File currentDirectory;

    @FXML
    public void initialize() {
        currentDirectory = new File(System.getProperty("user.home"));
        updateFileList();
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            updateFileList();
        } else {
            List<String> filteredFiles = getFilesInDirectory(currentDirectory).stream()
                    .filter(file -> file.toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            fileListView.getItems().setAll(filteredFiles);
        }
    }

    @FXML
    private void handleCreateDirectory() {
        TextInputDialog dialog = new TextInputDialog("New Directory");
        dialog.setTitle("Create Directory");
        dialog.setHeaderText("Enter the name of the new directory:");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            File newDir = new File(currentDirectory, name);
            if (newDir.mkdir()) {
                updateFileList();
            } else {
                showAlert("Error", "Could not create directory.");
            }
        });
    }

    @FXML
    private void handleCreateFile() {
        TextInputDialog dialog = new TextInputDialog("New File");
        dialog.setTitle("Create File");
        dialog.setHeaderText("Enter the name of the new file:");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            File newFile = new File(currentDirectory, name);
            try {
                if (newFile.createNewFile()) {
                    updateFileList();
                } else {
                    showAlert("Error", "Could not create file.");
                }
            } catch (Exception e) {
                showAlert("Error", "Could not create file.");
            }
        });
    }

    @FXML
    private void handleDelete() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            File fileToDelete = new File(currentDirectory, selectedFile);
            if (fileToDelete.delete()) {
                updateFileList();
            } else {
                showAlert("Error", "Could not delete file or directory.");
            }
        }
    }

    @FXML
    private void handleRename() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            TextInputDialog dialog = new TextInputDialog(selectedFile);
            dialog.setTitle("Rename");
            dialog.setHeaderText("Enter the new name:");
            dialog.setContentText("Name:");

            dialog.showAndWait().ifPresent(newName -> {
                File fileToRename = new File(currentDirectory, selectedFile);
                File newFile = new File(currentDirectory, newName);
                if (fileToRename.renameTo(newFile)) {
                    updateFileList();
                } else {
                    showAlert("Error", "Could not rename file or directory.");
                }
            });
        }
    }

    @FXML
    private void handleBrowse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory");
        File selectedDirectory = directoryChooser.showDialog(mainVBox.getScene().getWindow());
        if (selectedDirectory != null) {
            currentDirectory = selectedDirectory;
            updateFileList();
        }
    }

    private void updateFileList() {
        fileListView.getItems().setAll(getFilesInDirectory(currentDirectory));
    }

    private List<String> getFilesInDirectory(File directory) {
        return List.of(directory.list());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}