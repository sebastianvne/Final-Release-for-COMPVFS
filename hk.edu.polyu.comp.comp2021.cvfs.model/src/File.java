/**
 * Class File, the parent of Class Directory and Class Document
 */
public class File
{
    /**
     * initial size of any file should be 40;
     */
    protected final int INIT_SIZE = 40;
    /**
     * protected variable, only its child can access
     */
    protected String name;
    /**
     * protected variable, only its child can access
     */
    protected int size = INIT_SIZE;
    /**
     * protected variable, only its child can access
     */
    protected Disk parentDisk;

    /**
     * get name method
     * @return return the name of the file.
     */
    public String getName(){return name;}

    /**
     * get size method
     * @return return the size of the file.
     */
    public int getSize(){return size;}

    /**
     * set the file's name to a certain String
     * @param name the String that the name should be set to.
     */
    public void setName(String name){this.name = name;}

    /**
     * set the size of the file
     * @param size the size that the file should be set to.
     */
    public void setSize(int size){this.size = size;}

    /**
     * calculate the size of the file. Due to we do not know its type, we set it to be 40
     * @return the size of the file
     */
    public int calculateSize()
    {
        return INIT_SIZE;
    }
}
