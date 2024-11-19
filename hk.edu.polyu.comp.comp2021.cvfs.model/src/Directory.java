import java.util.HashMap;
import java.util.Map;

/**
 * Directory class, means directory in disk, extends from file class.
 */
public class Directory extends File
{
    final private Map<String,File> files;
    final private Directory parentDir;

    /**
     * construction method.
     * @param name directory name that can be search
     * @param parentDisk indicates the disk that the directory belongs to
     * @param parentDir indicates its parent directory.
     */
    Directory(String name,Disk parentDisk,Directory parentDir)//init of root dir
    {
        this.name=name;
        this.files = new HashMap<>();
        this.size = calculateSize();
        this.parentDisk = parentDisk;
        this.parentDir = parentDir;
    }

    /**
     *
     * @param name to be search the directory
     * @return a File object, if the file can be found
     */
    public File search(String name)
    {
        for(File file:files.values())
        {
            if(file.getName().equals(name)) return file;
        }
        return null;
    }

    /**
     * get the directory's parent directory
     * @return return the parentdir, a Directory object
     */
    public Directory getParentDir(){return parentDir;}
    /**
     * get all the files belongs to the directory.
     * @return return a map that contains all the files(Documents,Directories) in the directory.
     */
    public Map<String,File> getFiles(){return files;}



    /**
     * search if the current directory have the searched file directly.
     * @param name the file to be searched
     * @return if there is such a file.
     */
    public boolean isExistFile(String name) {return files.containsKey(name);}

    /**
     * calculate the size of the whole directory recursively, update the child files' size and return it.
     * @return return the size of the directory.
     */
    @Override
    public int calculateSize()
    {
        //if(!isExistSubDirectory()) return size;
        for(File file : files.values())
        {
            file.setSize(file.calculateSize());
            size+=file.getSize();
        }
        return size;
    }

    /**
     * newdoc method
     * @param docName document name for docs should be created
     * @param docType document type
     * @param docContent document contents, we get size with that.
     */
    public void newDoc(String docName,String docType,String docContent)
    {
        if(isExistFile(docName)) throw new IllegalArgumentException("Document already exists");
        Document doc = new Document(docName,docType,docContent);
        if(parentDisk.getRootDir().getSize()+ doc.getSize()>parentDisk.getDiskSize()) throw new IllegalArgumentException("The disk cannot have the file due to size");
        files.put(docName,doc);
        this.parentDisk.getRootDir().setSize(parentDisk.getRootDir().calculateSize());
    }

    /**
     * delete files(Document and Directory)
     * @param name the name of the file to be deleted
     */
    public void deleteFile(String name)
    {
        if (isExistFile(name)) files.remove(name);
        else throw new IllegalArgumentException("The file does not exist");
        this.parentDisk.getRootDir().setSize(parentDisk.getRootDir().calculateSize());
    }

    /**
     * rename a certaian file
     * @param oldFileName origin file name of a certain file
     * @param newFileName new file name to be given for a certain file
     */
    public void renameFile(String oldFileName, String newFileName)
    {
        if (isExistFile(oldFileName))
        {
            if(isExistFile(newFileName)) throw new IllegalArgumentException("File already exists");
            File tempDoc = files.get(oldFileName);
            tempDoc.setName(newFileName);
            files.remove(oldFileName);
            files.put(newFileName,tempDoc);
        }
        else throw new IllegalArgumentException("The file does not exist.");
    }

    /**
     * construct a directory
     * @param name the name of the new directory
     */
    public void newDirectory(String name)
    {
        if (isExistFile(name)) throw new IllegalArgumentException("The directory already exists.");
        Directory newdirectory = new Directory(name,parentDisk,this);
        int tempsize = newdirectory.calculateSize();
        if (parentDisk.getRootDir().getSize()+tempsize> parentDisk.getDiskSize()) throw new IllegalArgumentException("The disk cannot have the file due to size");
        files.put(name,newdirectory);
        this.parentDisk.getRootDir().setSize(parentDisk.getRootDir().calculateSize());
    }
}
