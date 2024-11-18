import java.util.HashMap;
import java.util.Map;

public class Directory extends File
{
    private Map<String,File> files;
    Directory(String name,Disk parentDisk,Directory parentDir)//init of root dir
    {
        this.name=name;
        this.files = new HashMap<>();
        this.size = calculateSize();
        this.parentDisk = parentDisk;
        this.parentDir = parentDir;
    }

    public File search(String name)
    {
        for(File file:files.values())
        {
            if(file.getName().equals(name)) return file;
        }
        return null;
    }

    public Map<String,File> getFiles(){return files;}
    public void setFiles(Map<String,File> files){this.files = files;}

    public boolean isExistFile(String name) {return files.containsKey(name);}
    public boolean isExistSubDirectory()
    {
        for(File file : files.values()) if(file.isDirectory()) return true;
        return false;
    }
    public boolean isExistSubDoc()
    {
        for(File file : files.values()) if(file.isDocument()) return true;
        return false;
    }
    public int calculateSize()
    {
        int size = 40;
        //if(!isExistSubDirectory()) return size;
        for(File file : files.values())
        {
            file.size = file.calculateSize();
            size+=file.size;
        }
        return size;
    }

    public void newDoc(String docName,String docType,String docContent)
    {
        if(isExistFile(docName)) throw new IllegalArgumentException("Document already exists");
        Directory docDirectory = this;
        Document doc = new Document(docName,docType,docContent,docDirectory);
        if(parentDisk.getRootDir().getSize()+ doc.getSize()>parentDisk.getDiskSize()) throw new IllegalArgumentException("The disk cannot have the file due to size");
        files.put(docName,doc);
        this.parentDisk.getRootDir().size = parentDisk.getRootDir().calculateSize();
    }
    public void deleteFile(String name)
    {
        if (isExistFile(name) ) files.remove(name);
        else throw new IllegalArgumentException("The file does not exist");
        this.parentDisk.getRootDir().size = parentDisk.getRootDir().calculateSize();
    }
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
    public void newDirectory(String name)
    {
        if (isExistFile(name)) throw new IllegalArgumentException("The directory already exists.");
        Directory newdirectory = new Directory(name,parentDisk,this);
        int tempsize = newdirectory.calculateSize();
        if (parentDisk.getRootDir().getSize()+tempsize> parentDisk.getDiskSize()) throw new IllegalArgumentException("The disk cannot have the file due to size");
        files.put(name,newdirectory);
        this.parentDisk.getRootDir().size = parentDisk.getRootDir().calculateSize();
    }
}
