import java.util.HashMap;
import java.util.Map;

public class Disk
{
    private int diskSize;
    private Directory rootDir;
    private Directory cwd = rootDir;
    Disk (int diskSize)
    {
        this.diskSize = diskSize;
        this.rootDir = new Directory("/",this,null);
        this.cwd = rootDir;
    }

    public Directory getRootDir() {return this.rootDir;}
    public Directory getCwd(){return this.cwd;}
    public int getDiskSize(){return this.diskSize;}//get methods

    public void setCwd(Directory cwd) {this.cwd = cwd;}
    public void setDiskSize(int diskSize) {this.diskSize = diskSize;}
    public void setRootDir(Directory rootDir) {this.rootDir = rootDir;}

    public void changeDirectory(String dirName)//debug
    {
        if (dirName.isEmpty())
        {
            cwd = rootDir;
        }
        else if (dirName.equals("..") && !(cwd.equals(rootDir)))
        {
            cwd = cwd.getParentDir();
        }
        else if (cwd.isExistFile(dirName) && cwd.getFiles().get(dirName) instanceof Directory)
        {
            cwd = (Directory) cwd.getFiles().get(dirName);
        }
        else throw new IllegalArgumentException("Can not find the directory");
    }


    public int[] rList(int i)
    {
        int totalNumber = 0;
        Map<String,File>  files = cwd.getFiles();

        String formatIndex = String.format("|%-10s|%-10s|%-10s|%-10s|","directory","document","size","type");
        System.out.println("-".repeat(i*2)+formatIndex);
        for(File file: files.values())
        {
            totalNumber++;
            String output = String.format("|%-10s|%-10s|%-10d|%-10s|",
                    file instanceof Directory? file.getName() : "",
                    file instanceof Document? file.getName() : "",
                    file.getSize(),
                    file instanceof Document ? ((Document)file).getType() : "");
            System.out.println("-".repeat(i*2)+output);
            if(file instanceof Directory)
            {
                cwd = (Directory) file;
                int[] summary = rList(i+1);
                cwd = cwd.getParentDir();
                totalNumber += summary[0];
            }
        }
        return new int[] {totalNumber,cwd.getSize()};
    }
    public int[] list()
    {
        int totalNumber = 0;
        Map<String,File>  files = cwd.getFiles();
        String formatIndex = String.format("|%-10s|%-10s|%-10s|%-10s|","directory","document","size","type");
        System.out.println(formatIndex);
        for(File file: files.values())
        {
            totalNumber++;
            String output = String.format("|%-10s|%-10s|%-10d|%-10s|",
                    file instanceof Directory? file.getName() : "",
                    file instanceof Document? file.getName() : "",
                    file.getSize(),
                    file instanceof Document ? ((Document)file).getType() : "");
            System.out.println(output);
        }
        int size = cwd.getSize();
        return new int[] {totalNumber,size};
    }
    public int[] search(Criteria C){
        int totalNumber = 0;
        int size = 40;
        Map<String,File>  files = cwd.getFiles();
        Map<String,File> satisfiedFiles = new HashMap<String,File>();
        for(File file: files.values()){
            if(C.Compare(file, C)){
                totalNumber++;
                satisfiedFiles.put(file.getName(),file);
            }
        }
        for(File file : satisfiedFiles.values())
        {
            file.size = file.calculateSize();
            size+=file.size;
        }
        return new int[] {totalNumber,size};
    }
    public int[] rSearch(Criteria C,int size){
        int totalNumber = 0;
        Map<String,File>  files = cwd.getFiles();
        Map<String,File> satisfiedFiles = new HashMap<String,File>();
        for(File file: files.values()){
            if(C.Compare(file, C)){
                totalNumber++;
                satisfiedFiles.put(file.getName(),file);
            }
            if(file instanceof Directory)
            {
                cwd = (Directory) file;
                int[] summary = rSearch(C,size);
                cwd = cwd.getParentDir();
                totalNumber += summary[0];
            }
        }
        for(File file : satisfiedFiles.values())
        {
            file.size = file.calculateSize();
            size+=file.size;
        }
        return new int[] {totalNumber,size};
    }
}
