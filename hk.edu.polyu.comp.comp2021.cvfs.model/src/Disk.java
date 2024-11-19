import java.util.HashMap;
import java.util.Map;

/**
 * Disk class for a virtual disk
 */
public class Disk
{
    final private int diskSize;
    final private Directory rootDir = new Directory("/",this,null);
    private Directory cwd = rootDir;

    /**
     * construction method
     * @param diskSize the max disk size of the virtual disk
     */
    Disk (int diskSize)
    {
        this.diskSize = diskSize;
    }

    /**
     * get the disk's root directory
     * @return a Directory object, which is the root directory of the disk
     */
    public Directory getRootDir() {return this.rootDir;}
    /**
     * get the disk's current working directory
     * @return a Directory object, which is the current working directory of the disk
     */
    public Directory getCwd(){return this.cwd;}

    /**
     * get the disk's size
     * @return  an integer, which is the inputted disk size(the maximum size of the disk).
     */
    public int getDiskSize(){return this.diskSize;}//get methods
    /**
     * set the disk's current working directory to a certain directory
     * @param cwd the directory that the cwd should be set to.
     */
    public void setCwd(Directory cwd) {this.cwd = cwd;}

    /**
     * change the current working directory
     * @param dirName the directory that a working directory should be set to.
     */
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

    /**
     * a compound method, which can be used for "list", "rList", "search" and "rsearch"
     * @param i the depth of the recursion, used when rec is true, only used for rsearch or rlist
     * @param rec indicates if the method should recursively get all files below.
     * @param cri the search criteria, when "list", "rList", that should be null
     * @return return a map that contains all the files that fulfil the search criteria(when it is list or rlist, it would be all the files below)
     */
    public Map<String,File> rList(int i, boolean rec, Criteria cri)
    {
        final int INIT_SIZE = 40;
        int totalNumber = 0;
        Map<String,File> criFiles = new HashMap<>();
        Map<String,File> Files = cwd.getFiles();
        if (cri == null || i == 0) {
            String formatIndex = String.format("|%-10s|%-10s|%-10s|%-10s|", "directory", "document", "size", "type");
            for (int j = 0; j < i; j++) System.out.print("--");
            System.out.println(formatIndex);
        }
        if(cri != null)
        {
            for(File file: Files.values())
            {
                if(cri.Compare(file,cri))
                {
                    criFiles.put(file.getName(), file);
                }
                if(file instanceof Directory && rec)
                {
                    cwd = (Directory) file;
                    Map<String,File> map = rList(i+1, true,cri);
                    cwd = cwd.getParentDir();
                    criFiles.putAll(map);
                }
            }
            Files = criFiles;
        }
        Map<String, File> temp = new HashMap<>(Files);
        for(File file: Files.values())
        {
            totalNumber++;
            if(cri == null || i == 0)
            {
                String output = String.format("|%-10s|%-10s|%-10d|%-10s|",
                        file instanceof Directory? file.getName() : "",
                        file instanceof Document? file.getName() : "",
                        file.getSize(),
                        file instanceof Document ? ((Document)file).getType() : "");
                for(int j = 0;j<i;j++) System.out.print("--");
                System.out.println(output);
            }
            if(file instanceof Directory && rec)
            {
                cwd = (Directory) file;
                Map<String,File> files = rList(i+1, true,cri);
                cwd = cwd.getParentDir();
                temp.putAll(files);
                totalNumber += files.size();
            }
        }
        if(i == 0)
        {
            //int totalNumber = 0;
            int size = 0;
            if(cri != null)
            {
                //totalNumber = 0;
                for (File file : temp.values()) {
                    //totalNumber++;
                    size += file.getSize();
                }
                totalNumber = temp.size();
            }
            else size = cwd.getSize() - INIT_SIZE;
            System.out.println("Total number of files: "+totalNumber+", total size:" +size);
        }
        return temp;
    }
}
