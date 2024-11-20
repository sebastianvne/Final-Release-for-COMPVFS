import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * load class to load the files in the local computer into the virtual file system
 */
public class Load {

    private Disk currentDisk;

    /**
     * accept two argument
     * @param currentDisk current disk create in the system
     */
    Load(Disk currentDisk){
        this.currentDisk = currentDisk;
    }

    /**
     * start up function of load
     * @param path it is the name of destiny directory
     */
    public void load(String path){
        Path relativepath=Paths.get("./hk.edu.polyu.comp.comp2021.cvfs.model/"+path);
        Path localpath=relativepath.toAbsolutePath().normalize();
        Inload(localpath.toString(),currentDisk.getRootDir());

    }

    /**
     * accept the parameters from load.
     * @param path it is the relative path of destiny directory
     * @param dir  it is the current directory
     */

    public void Inload(String path, Directory dir){
        //path是local的文件
        //dir是写入的虚拟文件系统的dir
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
                    String newpath=localpath.toString()+"/"+Stringname;

                    Inload(newpath,currentDisk.getCwd());
                    currentDisk.setCwd(currentDisk.getCwd().getParentDir());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}