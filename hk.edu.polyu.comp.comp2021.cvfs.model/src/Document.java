

/**
 * Document class extend from file
 */
public class Document extends File //extends File
{
    final private String type;
    final private String content;
    private static final String[] FILE_TYPES = {"txt","java","html","css"};

    /**
     * construction method
     * @param name the name of new doc
     * @param type the type of new doc
     * @param content the content of new doc
     * @throws IllegalArgumentException the construction may encounter wrong input format.
     */
    public Document(String name,String type,String content) throws IllegalArgumentException
    {
        if (!(name != null && name.matches("^[a-zA-Z0-9]{1,10}$") && name.length()<=10))
        {
            throw new IllegalArgumentException("Invalid file name.");
        }
        if (!isValidFileType(type)) {
            throw new IllegalArgumentException("Invalid file type.");
        }
        this.name=name;
        this.type = type;
        this.content = content;
        this.size = calculateSize(content);
    }

    /**
     * get the doc's type
     * @return return type
     */
    public String getType(){return type;}

    /**
     * get the doc's content.
     * @return return type
     */
    public String getContent(){return content;}

    /**
     * detect if the file created doc has an invalid type.
     * @param type the type that be entered by user
     * @return return boolean
     */
    private boolean isValidFileType(String type) {
        if (type == null) return false;
        for (String fileType : FILE_TYPES) {
            if (fileType.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * calculate the size of a doc
     * @param content doc's content
     * @return the calculated size
     */
    public int calculateSize(String content)
    {
        final int INIT_SIZE = 40;
        return INIT_SIZE + (content != null ? content.length() * 2-2 : 0);
    }

    /**
     * override the file's calculate method, activate calculate
     * @return the calculated size
     */
    @Override
    public int calculateSize()
    {
        return calculateSize(this.content);
    }
}
