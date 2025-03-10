package com.example.projectonuf5;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {

    public static List<String> getFilesInDirectory(File directory) {
        return List.of(directory.list());
    }

    public static boolean createDirectory(File directory, String name) {
        return new File(directory, name).mkdir();
    }

    public static boolean createFile(File directory, String name) {
        try {
            return new File(directory, name).createNewFile();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteFileOrDirectory(File file) {
        return file.delete();
    }

    public static boolean renameFileOrDirectory(File file, String newName) {
        return file.renameTo(new File(file.getParent(), newName));
    }
}