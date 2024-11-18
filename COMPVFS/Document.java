import java.io.IOException;

public class Document extends File //extends File
{
    private String type;
    private String content;
    private static final String[] FILE_TYPES = {"txt","java","html","css"};
    public Document(String name,String type,String content,Directory directory) throws IllegalArgumentException
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
        this.parentDir = directory;
        this.size = calculateSize(content);
    }

    public String getType(){return type;}
    public String getContent(){return content;}
    public Directory getDirectory(){return parentDir;}

    public void setType(String type){this.type = type;}
    public void setContent(String content)
    {
        this.content = content;
        this.size = calculateSize(content);
    }
    public void setDirectory(Directory directory){this.parentDir = directory;}

    private boolean isValidFileType(String type) {
        if (type == null) return false;
        for (String fileType : FILE_TYPES) {
            if (fileType.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
    public int calculateSize(String content) {
        return 40 + (content != null ? content.length() * 2-2 : 0);
    }
    public int calculateSize()
    {
        return calculateSize(this.content);
    }
}
