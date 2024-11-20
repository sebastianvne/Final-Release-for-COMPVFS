import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Save Class, do save implementations
 */
public class Save {
    private Disk currentDisk;
    private Map<String,File> FileMap;
    private int count=0;

    /**
     * Save:used to accept two argument
     * @param currentDisk it is the disk create before.
     */
    Save(Disk currentDisk)
    {
        this.currentDisk = currentDisk;
    }

    /**
     * Save: used to accept three argument
     * @param currentDisk it is the disk create before.
     * @param fileMap filemap contains the files that should be saved
     */
    Save(Disk currentDisk, Map<String,File> fileMap)
    {
        this.currentDisk = currentDisk;
        this.FileMap = fileMap;
        this.count=1;

    }

    /**
     * start-up function
     * @param path  get the name of directory that you want to save in.
     * @throws IOException  considering IO runtime error
     */
    public void save(String path) throws IOException {

        Directory rootDir = currentDisk.getRootDir();
        Path relativepath=Paths.get("./hk.edu.polyu.comp.comp2021.cvfs.model/"+path);
        Path localpath=relativepath.toAbsolutePath().normalize();
        path=localpath.toString();
//?
        savein(path, rootDir);


    }

    /**
     * accept parameters from save, and realise the method.
     * @param path relative path, get from the save.
     * @param dir   current directory.
     * @throws IOException considering IO runtime error
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
                savein(filepath, (Directory) file);}}}}