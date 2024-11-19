import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Map;

/**
 * The class can conduct save methods
 */
public class Save {
    final private Disk currentDisk;
    private Map<String,File> FileMap;
    private int count=0;

    /**
     * construction method
     * @param currentDisk the disk that a save object should be constructed
     */
    Save(Disk currentDisk)
    {
        this.currentDisk = currentDisk;
    }

    /**
     * construction method for the case that we should save files with a certain criteria.
     * @param currentDisk current disk
     * @param fileMap a map contains file that need to be stored
     */
    Save(Disk currentDisk,Map<String,File> fileMap)
    {
        this.currentDisk = currentDisk;
        this.FileMap = fileMap;
        this.count=1;

    }

    /**
     * activate method for save
     * @param path the local path that the file should be saved
     * @throws IOException when Files.createDirectory failed.
     */
    public void save(String path) throws IOException {

        Directory rootDir = currentDisk.getRootDir();
        savein(path, rootDir);
    }

    /**
     * recursively save in
     * @param path local path that the file should be saved
     * @param dir current working directory to be saved.
     * @throws IOException when Files.createDirectory failed.
     */
    public void savein(String path, Directory dir) throws IOException {

        Map<String, File> files = dir.getFiles();

        if (files == null || files.isEmpty()) {
            System.out.println("No files in the directory.");
            return;
        }


        for (File file : files.values()) {
            System.out.println("Processing file: " + file.getName());
            System.out.println("Size: " + file.getSize());
            System.out.println(FileMap);

            if (file instanceof Document) {

                if (count == 1) {
                    if (!FileMap.containsValue(file)) {
                        System.out.println("file not contains");
                        continue;
                    }
                }
                String filepath = Paths.get(path, file.getName() + "." + ((Document) file).getType()).toString();
                Path Thispath = Paths.get(filepath);
                String content = ((Document) file).getContent();
                try {
                    if (Files.notExists(Thispath)) {
                        Files.createFile(Thispath);
                    }
                    Files.write(Thispath, content.getBytes(StandardCharsets.UTF_8));
                    System.out.println("File created and content written successfully!");
                } catch (IOException e) {
                    System.err.println("An error occurred while creating the file: " + e.getMessage());
                }
            }
        }


        for (File file : files.values()) {
            if (file instanceof Directory) {

                System.out.println("Processing directory: " + file.getName());
                String filepath = Paths.get(path, file.getName()).toString();
                Path Thispath = Paths.get(filepath);
                Files.createDirectories(Thispath);
                savein(filepath, (Directory) file);
            }
        }
    }
}

