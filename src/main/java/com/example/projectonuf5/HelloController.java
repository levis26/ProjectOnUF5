package com.example.projectonuf5;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;
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
            updateFileList(); // Si no hay texto de búsqueda, mostrar la lista actual
        } else {
            List<String> searchResults = searchFiles(currentDirectory, searchText); // Buscar recursivamente
            fileListView.getItems().setAll(searchResults);
        }
    }

    private List<String> searchFiles(File directory, String searchText) {
        List<String> results = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().toLowerCase().contains(searchText)) {
                        results.add(file.getAbsolutePath()); // Añadir la ruta completa
                    }
                    if (file.isDirectory()) {
                        results.addAll(searchFiles(file, searchText)); // Búsqueda recursiva en subdirectorios
                    }
                }
            }
        }
        return results;
    }

    @FXML
    private void handleCreateDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Location to Create Directory");
        File selectedDirectory = directoryChooser.showDialog(mainVBox.getScene().getWindow());

        if (selectedDirectory != null) {
            TextInputDialog dialog = new TextInputDialog("New Directory");
            dialog.setTitle("Create Directory");
            dialog.setHeaderText("Enter the name of the new directory:");
            dialog.setContentText("Name:");

            dialog.showAndWait().ifPresent(name -> {
                File newDir = new File(selectedDirectory, name);
                if (newDir.mkdir()) {
                    currentDirectory = selectedDirectory;
                    updateFileList(); // Actualizar la lista después de crear el directorio
                } else {
                    showAlert("Error", "Could not create directory.");
                }
            });
        }
    }

    @FXML
    private void handleCreateFile() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Location to Create File");
        File selectedDirectory = directoryChooser.showDialog(mainVBox.getScene().getWindow());

        if (selectedDirectory != null) {
            TextInputDialog dialog = new TextInputDialog("New File");
            dialog.setTitle("Create File");
            dialog.setHeaderText("Enter the name of the new file:");
            dialog.setContentText("Name:");

            dialog.showAndWait().ifPresent(name -> {
                File newFile = new File(selectedDirectory, name);
                try {
                    if (newFile.createNewFile()) {
                        currentDirectory = selectedDirectory;
                        updateFileList(); // Actualizar la lista después de crear el archivo
                    } else {
                        showAlert("Error", "Could not create file.");
                    }
                } catch (Exception e) {
                    showAlert("Error", "Could not create file.");
                }
            });
        }
    }

    @FXML
    private void handleDelete() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            File fileToDelete = new File(selectedFile);
            if (fileToDelete.delete()) {
                updateFileList(); // Actualizar la lista después de eliminar
            } else {
                showAlert("Error", "Could not delete file or directory.");
            }
        }
    }

    @FXML
    private void handleRename() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            File fileToRename = new File(selectedFile);
            TextInputDialog dialog = new TextInputDialog(fileToRename.getName());
            dialog.setTitle("Rename");
            dialog.setHeaderText("Enter the new name:");
            dialog.setContentText("Name:");

            dialog.showAndWait().ifPresent(newName -> {
                File newFile = new File(fileToRename.getParent(), newName);
                if (fileToRename.renameTo(newFile)) {
                    updateFileList(); // Actualizar la lista después de renombrar
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
        return List.of(directory.listFiles()).stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}