import java.io.*;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static final String UNPACKED = "unpacked_";
    public static final String PACKED = "packed_";

    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        String intDir = "C:\\Games";
        File initialDir = new File(intDir);
        StringBuilder log = new StringBuilder();
        File srcFile = createDir(initialDir, "src", log);
        File resFile = createDir(initialDir, "res", log);
        File savegamesFile = createDir(initialDir, "savegames", log);
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

        GameProgress gp1 = new GameProgress(100, 0, 1, 1);
        GameProgress gp2 = new GameProgress(90, 5, 10, 99);
        GameProgress gp3 = new GameProgress(95, 3, 7, 60);

        saveGame(savegamesFile.getAbsolutePath() + File.separator + "save1.dat", gp1);
        saveGame(savegamesFile.getAbsolutePath() + File.separator + "save2.dat", gp2);
        saveGame(savegamesFile.getAbsolutePath() + File.separator + "save3.dat", gp3);

        String[] filesToZip = new String[]{
                savegamesFile.getAbsolutePath() + File.separator + "save1.dat",
                savegamesFile.getAbsolutePath() + File.separator + "save2.dat",
                savegamesFile.getAbsolutePath() + File.separator + "save3.dat"
        };
        zipFiles(savegamesFile.getAbsolutePath() + File.separator + "saveData.zip", filesToZip);

        openZip(savegamesFile.getAbsolutePath() + File.separator + "saveData.zip", savegamesFile.getAbsolutePath());

        GameProgress gp4 = openProgress(savegamesFile.getAbsolutePath() + File.separator + UNPACKED + "save1.dat");
        System.out.println(gp4.toString());

        System.out.println(openProgress(savegamesFile.getAbsolutePath() + File.separator + UNPACKED + "save2.dat").toString());
        System.out.println(openProgress(savegamesFile.getAbsolutePath() + File.separator + UNPACKED + "save3.dat").toString());
    }

    private static GameProgress openProgress(String filePath) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(filePath);
            ois = new ObjectInputStream(fis);
            return (GameProgress) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (ois!=null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (fis!=null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return null;
    }

    private static void openZip(String zipPath, String outputDirectory) {
        FileInputStream fis = null;
        ZipInputStream zis = null;
        try {
            fis = new FileInputStream(zipPath);
            zis = new ZipInputStream(fis);
            ZipEntry ze = null;
            String name = null;
            while ((ze = zis.getNextEntry())!=null) {
                name = ze.getName();
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(outputDirectory + File.separator + getNameNoPckage(name));
                    for (int c = zis.read(); c != -1; c = zis.read()) {
                        fout.write(c);
                    }
                    fout.flush();
                } finally {
                    if (fout!=null) {
                        try {
                            fout.close();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    zis.closeEntry();
                }

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private static String getNameNoPckage(String name) {
        if (name!=null && name.startsWith(PACKED)) {
            return UNPACKED +name.substring(PACKED.length());
        }
        return name;
    }

    private static void zipFiles(String zipPath, String[] filesToZip) {
        //ZipOutputStream и FileOutputStream.
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        boolean wasError = false;
        try {
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(fos) ;

            for (int i=0;i< filesToZip.length;i++) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(filesToZip[i]);
                    File f = new File(filesToZip[i]);
                    ZipEntry ze = new ZipEntry(PACKED +f.getName());
                    zos.putNextEntry(ze);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zos.write(buffer);
                    zos.closeEntry();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    wasError = true;
                } finally {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        wasError = true;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            wasError = true;
        } finally {
            if (zos!=null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    wasError = true;
                }
            }
            if (fos!=null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    wasError = true;
                }
            }
        }
        if (!wasError) {
            for (int i = 0; i < filesToZip.length; i++) {
                File f = new File(filesToZip[i]);
                if (f.delete()) {
                    System.out.println("Файл "+f.getAbsolutePath() + " был удален");
                }
            }
        }
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



   // saveGame(String "C:\Games\savegame", GameProgress gameProgress);

    public static void saveGame(String filePath, GameProgress gameProgress) {
        File file = new File(filePath);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(gameProgress);

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (oos!=null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (fos!=null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    }

}