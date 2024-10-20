import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        String intDir = "C:\\Games";
        File initialDir = new File(intDir);
        StringBuilder log = new StringBuilder();
        File srcFile = createDir(initialDir, "src", log);
        File resFile = createDir(initialDir, "res", log);
        createDir(initialDir, "savegames", log);
        File tempFile = createDir(initialDir, "temp", log);
        File mainFile = createDir(srcFile, "main", log);
        createDir(srcFile, "test", log);
        createDir(resFile, "drawables", log);
        createDir(resFile, "vectors", log);
        createDir(resFile, "icons", log);
        createFileInDir(mainFile, "Main.java", log);
        createFileInDir(mainFile, "Utils.java", log);
        File tmpFile = createFileInDir(tempFile, "temp.txt", log);
        writeToFile(tmpFile, log.toString());
    }

    private static void writeToFile(File fileToWrite, String text) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileToWrite);
            fw.write(text);
//            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    ;
                }
            }
        }
    }

    private static File createFileInDir(File folder, String fileName, StringBuilder log) {
        File file = new File(folder + File.separator + fileName);
        try {
            if (file.createNewFile()) {
                log.append("Файл создан ").append(file.getAbsolutePath()).append("\n");
            } else {
                log.append("Файл не создан ").append(file.getAbsolutePath()).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private static File createDir(File folder, String subFolder, StringBuilder log) {
        File subFolderFile = new File(folder + File.separator + subFolder);
        if (subFolderFile.mkdir()) {
            log.append("Каталог создан ").append(subFolderFile.getAbsolutePath()).append("\n");
        } else {
            log.append("Каталог не создан ").append(subFolderFile.getAbsolutePath()).append("\n");
        }
        return subFolderFile;
    }
}