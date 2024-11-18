import java.nio.file.*;
import java.io.IOException;
import java.util.Objects;

public class Load {

    private Disk currentDisk;
    private String require;
    private String variable;
    private String operator;
    private String value;
    private int count=0;

    Load(Disk currentDisk){
        this.currentDisk = currentDisk;
    }
    Load(Disk currentDisk, String require){
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


    public void load(String path){
        Inload(path,currentDisk.getRootDir());

    }

    public boolean passQ(Path p) throws IOException {
        if(variable.equals("size")){
            switch (operator) {
                case ">":
                    return Files.size(p) > Integer.parseInt(value);
                case "<":
                    return Files.size(p) < Integer.parseInt(value);
                case "==":
                    return Files.size(p) == Integer.parseInt(value);
            }
        }
        if(variable.equals("type")) {
            String filename=p.getFileName().toString();
            String Dtype="."+value;
            return filename.toLowerCase().endsWith(Dtype);

        }
        return true;
    }

    public void Inload(String path, Directory dir){
        //path是local的文件
        //dir是写入的虚拟文件系统的dir
        Path localpath=Paths.get(path);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(localpath)) {
            for (Path p : stream) {

                if(Files.isRegularFile(p)){
                    if(count==1) {
                        if (!passQ(p)) continue;
                    }
                    Path name= p.getFileName();
                    //String Stringname= name.toString();
                    String Stringname=p.getFileName().toString().split("\\.")[0];
                    if (p.getFileName().toString().equals(".DS_Store")) {
                        continue;  // 跳过 .DS_Store 文件
                    }
                    String type=p.getFileName().toString().split("\\.")[1];
                    if(!Objects.equals(type, "java") && !Objects.equals(type, "txt") && !Objects.equals(type, "html") && !Objects.equals(type, "css")){
                        System.out.println("wrong type");
                        continue;
                    }
                    byte[] bytes = Files.readAllBytes(p);
                    String content = new String(bytes);
                    System.out.println(Stringname+" "+type+" "+content);
                    dir.newDoc(Stringname,type,content);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(localpath)) {
            for (Path p : stream) {
                if(Files.isDirectory(p)){

                    Path name= p.getFileName();
                    String Stringname=name.getFileName().toString();
                    dir.newDirectory(Stringname);

                    currentDisk.changeDirectory(Stringname);
                    String newpath=path+"/"+Stringname;

                    Inload(newpath,currentDisk.getCwd());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}