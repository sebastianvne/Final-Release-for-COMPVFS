import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


public class Save {
    private Disk currentDisk;
    private Map<String,File> FileMap;
    private int count=0;
    Save(Disk currentDisk)
    {
        this.currentDisk = currentDisk;
    }
    Save(Disk currentDisk, Map<String,File> fileMap)
    {
        this.currentDisk = currentDisk;
        this.FileMap = fileMap;
        this.count=1;

    }


    public void save(String path) throws IOException {

        Directory rootDir = currentDisk.getRootDir();
        Path relativepath=Paths.get("./hk.edu.polyu.comp.comp2021.cvfs.model/"+path);
        Path localpath=relativepath.toAbsolutePath().normalize();
        path=localpath.toString();
//?
        savein(path, rootDir);


    }

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