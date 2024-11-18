import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Map;


public class Save {
    private Disk currentDisk;
    private String require;
    private String variable;
    private String operator;
    private String value;
    private int count=0;
    Save(Disk currentDisk)
    {
        this.currentDisk = currentDisk;
    }
    Save(Disk currentDisk,String require)
    {
        this.currentDisk = currentDisk;
        this.require=require;
        count=1;
        String regex = "(\\w+)(>=|<=|>|<|=)(\\w+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(require);

        if (matcher.matches()) {
            this.variable = matcher.group(1); // 变量名
            this.operator = matcher.group(2); // 运算符
            this.value = matcher.group(3);    // 值

        } else {
            System.out.println("Invalid input format.");
        }
    }

    public boolean passQ(File f){

        if(variable.equals("size")){
            int Dsize=Integer.parseInt(value);
            switch (operator) {
                case ">":
                    return Dsize < f.getSize();
                case "<":
                    return Dsize > f.getSize();
                case "==":
                    return Dsize == f.getSize();
            }
        }
        if(variable.equals("type")){
            String Dtype=value;
            Document document = (Document) f;
            return Dtype.equals(document.getType());
        }

        return true;
    }

    public void save(String path) throws IOException {

        Directory rootDir = currentDisk.getRootDir();
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

            if (file instanceof Document) {
                if(count==1) {
                    if (!passQ(file)) continue;
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
