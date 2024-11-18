public class File
{
    protected String name;
    protected int size;
    protected Directory parentDir;
    protected Disk parentDisk;

    public String getName(){return name;}
    public Directory getParentDir(){return parentDir;}
    public int getSize(){return size;}
    public Disk getParentDisk(){return parentDisk;}

    public void setName(String name){this.name = name;}
    public void setParentDir(Directory parentDir){this.parentDir = parentDir;}
    public void setSize(int size){this.size = size;}
    public void setParentDisk(Disk parentDisk){this.parentDisk = parentDisk;}


    public boolean isDocument() {return (this instanceof Document);}
    public boolean isDirectory() {return (this instanceof Directory);}

    public int calculateSize()
    {
        return 40;
    }
}
