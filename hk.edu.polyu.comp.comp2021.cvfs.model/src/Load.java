import java.nio.file.*;
import java.io.IOException;
import java.util.Objects;

/**
 * Load class that can do load manipulations
 */
public class Load {

    final private Disk currentDisk;

    /**
     * construction method
     * @param currentDisk the disk that local files should be loaded to
     */
    Load(Disk currentDisk){
        this.currentDisk = currentDisk;
    }

    /**
     * activate method
     * @param path the local path where the files should be loaded.
     */
    public void load(String path){
        Inload(path,currentDisk.getRootDir());

    }

    /**
     * recursively load
     * @param path  the local path where the files should be loaded.
     * @param dir current directory
     */
    public void Inload(String path, Directory dir){
        Path localpath=Paths.get(path);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(localpath)) {
            for (Path p : stream) {

                if(Files.isRegularFile(p)){

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
                    currentDisk.setCwd(currentDisk.getCwd().getParentDir());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}