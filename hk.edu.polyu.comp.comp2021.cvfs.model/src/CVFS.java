import java.util.*;
/**
 * CVFS class for all the manipulation.
 */
public class CVFS
{

    private Disk currentDisk;
    final private HashMap<String,Criteria> criteriaMap = new HashMap<>();
    final private Stack<String> undoStack = new Stack<>();
    final private Stack<String> redoStack = new Stack<>();
    final private Stack<File> tempTrashStack = new Stack<>();
    final private Stack<File> tempRestoreStack = new Stack<>();
    final private Stack<Criteria> trashCriteriaStack = new Stack<>();

    /**
     * return the disk of cvfs
     * @return Class Disk, the disk of cvfs.
     */
    public Disk getCurrentDisk() {return currentDisk;}
    /**
     *
     * @param args get commandline parameters.
     */
    public static void main(String[] args)
    {
        CVFS cvfs = new CVFS();
        cvfs.commandLineInput();
    }

    /**
     * conduct commands
     */
    public void commandLineInput()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\t\t\t\t<<<WELCOME TO COMP VFS>>>");
        while(true)
        {
            System.out.print("$:");
            String command = scanner.nextLine();
            if(Objects.equals(command, "quit")) break;
            try {commandLineMatch(command,false);}
            catch(Exception e) {System.out.println(e.getMessage());}
        }
    }

    /**
     *
     * @param undo to indicate if the method is undo manipulation
     * @param processStack the stack that stores the process we should pop and do it reversely
     * @param fileStack the stack that stores some deleted files.
     * @throws Exception the method may throw exceptions out
     */
    public void reverseDo(boolean undo,Stack<String> processStack,Stack<File> fileStack) throws Exception
    {
        if(processStack.isEmpty()) throw new IllegalArgumentException("There is nothing to do.");
        String[] lastCmds = processStack.pop().split(" ");
        if(lastCmds[0].equalsIgnoreCase("newdoc") || lastCmds[0].equalsIgnoreCase("newdir") || lastCmds[0].equalsIgnoreCase("touch") || lastCmds[0].equalsIgnoreCase("mkdir"))
        {
             commandLineMatch("delete " + lastCmds[1], undo);
        }
        if(lastCmds[0].equalsIgnoreCase("delete") || lastCmds[0].equalsIgnoreCase("rm"))
        {
            File restoreFile = fileStack.pop();
            if(restoreFile.getClass() .getSimpleName().equals("Directory")) commandLineMatch("newdir "+restoreFile.getName(),true);
            if(restoreFile.getClass() .getSimpleName().equals("Document")) commandLineMatch("newdoc "+restoreFile.getName()+
                            " "+((Document) restoreFile).getType()+
                            " "+((Document) restoreFile).getContent(),
                    true);
        }
        if(lastCmds[0].equalsIgnoreCase("rename"))
            commandLineMatch("rename "+lastCmds[2]+" "+lastCmds[1],true);
        if(lastCmds[0].equalsIgnoreCase("changedir") || lastCmds[0].equalsIgnoreCase("cd"))
        {
            if(undo)
            {
                currentDisk.setCwd((Directory) fileStack.pop());
                redoStack.push("changedir "+lastCmds[1]);
            }
            if(!undo) commandLineMatch("changedir "+lastCmds[1],true);
        }
        if(lastCmds[0].equalsIgnoreCase("newsimplecri"))
        {
            if(undo)
            {
                trashCriteriaStack.push(criteriaMap.get(lastCmds[1]));
                redoStack.push("newsimplecri "+lastCmds[1]+" "+lastCmds[2]+" "+lastCmds[3]+" "+lastCmds[4]);
                criteriaMap.remove(lastCmds[1]);
            }
            else criteriaMap.put(lastCmds[1],trashCriteriaStack.pop());
        }
        if(lastCmds[0].equalsIgnoreCase("newnegation"))
        {
            if(undo)
            {
                trashCriteriaStack.push(criteriaMap.get(lastCmds[1]));
                redoStack.push("newnegation "+lastCmds[1]+" "+lastCmds[2]);
                criteriaMap.remove(lastCmds[1]);
            }
            else criteriaMap.put(lastCmds[1],trashCriteriaStack.pop());
        }
    }

    /**
     * @param fileMap file that should not be deleted, if any file not in the map should be deleted
     */
    public void LoadDelete(Map<String,File> fileMap){
        Directory rootdir=currentDisk.getRootDir();
        LoadDeleteIn(fileMap,rootdir);

    }
    /**
     * recursively delete.
     * @param FileMap file that should not be deleted, if any file not in the map should be deleted
     * @param dir current directory.
     */
    public void LoadDeleteIn(Map<String,File> FileMap,Directory dir){
        Map<String, File> files = dir.getFiles();
        int i=0;
        int length=files.size();
        String[] namelist=new String[length];
        for (File file : files.values()) {
            if (file instanceof Document) {
                if(!FileMap.containsValue(file)){
                    String name = file.getName();
                    namelist[i]=name;
                    i++;
                }
            }
        }
        for(int k=0;k<i;k++){
            currentDisk.getCwd().deleteFile(namelist[k]);
        }
        for (File file : files.values()) {
            if (file instanceof Directory) {
                LoadDeleteIn(FileMap,(Directory) file);
            }

        }

    }
    /**
     *
     * @param command commands that we eneter in terminal
     * @param rec if the method is called by reversedo, or is called by commandlineinput
     * @throws Exception throw exceptions that may encounter in the method call
     */
    public void commandLineMatch(String command,boolean rec) throws Exception {
        String[] cmds = command.split(" ");
        switch (cmds[0].toLowerCase())
        {
            case "undo":
                reverseDo(true,undoStack,tempTrashStack);
                break;
            case "redo":
            {
                reverseDo(false,redoStack,tempRestoreStack);
                break;
            }
            case "newdisk":
                if(cmds.length != 2) throw new IllegalArgumentException("New disk requires exactly one argument.");
                currentDisk = new Disk(Integer.parseInt(cmds[1]));
                System.out.println("New disk successfully created with size of "+cmds[1]);
                break;
            case "touch":
            case "newdoc":
                if(cmds.length != 4) throw new IllegalArgumentException("New document requires exactly three arguments");
                StringBuilder sb = new StringBuilder();
                for(int i=3;i<cmds.length;i++)
                {
                    sb.append(cmds[i]);
                    sb.append(" ");
                }
                currentDisk.getCwd().newDoc(cmds[1],cmds[2],sb.toString());
                if(!rec)
                {
                    undoStack.push(command);
                    redoStack.clear();
                    System.out.println("New document successfully created with name of "+cmds[1]);
                }
                else
                {
                    redoStack.push(command);
                    System.out.println("Successfully restore file");
                }
                break;
            case "mkdir":
            case "newdir":
                if(cmds.length != 2) throw new IllegalArgumentException("New directory requires a name");
                currentDisk.getCwd().newDirectory(cmds[1]);
                if(!rec)
                {
                    undoStack.push(command);
                    redoStack.clear();
                    System.out.println("New directory successfully created with name of "+cmds[1]);
                }
                else
                {
                    redoStack.push(command);
                    System.out.println("Successfully restore file");
                }
                break;
            case "ls":
            case "list":
                currentDisk.rList(0,false,null);
                //System.out.println("Total number of files: "+summary[0]+", total size:"+(summary[1]-40));
                break;
            case "rlist":
                currentDisk.rList(0,true,null);
                //System.out.println("Total number of files: "+all[0]+", total size:"+(all[1]-40));
                break;
            case "rm":
            case "delete":
                if(cmds.length != 2) throw new IllegalArgumentException("Delete requires exactly one argument");
                File tempFile = currentDisk.getCwd().search(cmds[1]);
                currentDisk.getCwd().deleteFile(cmds[1]);
                if(!rec)
                {
                    undoStack.push(command);
                    redoStack.clear();
                    tempTrashStack.push(tempFile);
                    System.out.println("Delete successfully");
                }
                else
                {
                    redoStack.push(command);
                    tempRestoreStack.push(tempFile);
                    System.out.println("successfully undo");
                }
                break;
            case "rename":
                if(cmds.length != 3) throw new IllegalArgumentException("Rename need exactly two arguments");
                currentDisk.getCwd().renameFile(cmds[1],cmds[2]);
                if(!rec)
                {
                    undoStack.push(command);
                    redoStack.clear();
                    System.out.println("Rename successfully");
                }
                else
                {
                    redoStack.push(command);
                    System.out.println("Rename to the former name successfully");
                }
                break;
            case "cd":
            case "changedir":
                File lastDir = currentDisk.getCwd();
                if(cmds.length==1) currentDisk.changeDirectory("");
                else currentDisk.changeDirectory(cmds[1]);
                if(!rec)
                {
                    undoStack.push(command);
                    redoStack.clear();
                    tempTrashStack.push(lastDir);
                }
                else
                {
                    redoStack.push(command);
                    tempRestoreStack.push(lastDir);
                }
                break;
            case "save":
                if(cmds.length==1) throw new IllegalArgumentException("Requires a local path");
                else if(cmds.length==3){
                    if(!criteriaMap.containsKey(cmds[2])) throw new IllegalArgumentException("The criterion" + cmds[2] + " does not exist");
                    Map<String,File> filemap=currentDisk.rList(0,true,criteriaMap.get(cmds[2]));
                    System.out.println(currentDisk.rList(0,true,criteriaMap.get(cmds[2])));
                    System.out.println(filemap);
                    Save saveInstance = new Save(currentDisk,filemap);
                    saveInstance.save(cmds[1]);
                }
                else {
                    Save saveInstance = new Save(currentDisk);
                    saveInstance.save(cmds[1]);
                }
                break;
            case "load":
                if(cmds.length==1) throw new IllegalArgumentException("Requires a local path");
                else if(cmds.length==3){
                    Load loadInstance = new Load(currentDisk);
                    loadInstance.load(cmds[1]);
                    System.out.println(currentDisk.rList(0,true,criteriaMap.get(cmds[2])));
                    Map<String,File> filemap=currentDisk.rList(0,true,criteriaMap.get(cmds[2]));
                    System.out.println(filemap+"-----");
                    LoadDelete(filemap);
                }
                else{
                    Load loadInstance = new Load(currentDisk);
                    loadInstance.load(cmds[1]);
                }
                break;
            case "newsimplecri":
                if(cmds.length != 5 && cmds.length != 2) throw new IllegalArgumentException("Invalid input for NewSimpleCri");
                if(criteriaMap.containsKey(cmds[1])) throw new IllegalArgumentException("The criterion" + cmds[1] + " already exists");
                else if(cmds.length == 2){
                    Criteria criteria = new Criteria(cmds[1]);
                    criteriaMap.put(cmds[1],criteria);
                }
                else{
                    Criteria criteria = new Criteria(cmds[1],cmds[2],cmds[3],cmds[4]);
                    criteriaMap.put(cmds[1],criteria);
                }
                if(!rec)
                {
                    undoStack.push(command);
                    redoStack.clear();
                }
                System.out.println("Criterion created");
                break;
            case "newnegation":
                if(cmds.length != 3) throw new IllegalArgumentException("NewNegation requires at least two arguments");
                if(!criteriaMap.containsKey(cmds[2])) throw new IllegalArgumentException("The criterion" + cmds[2] + " does not exist");
                Criteria output = Criteria.newNegation(cmds[1],criteriaMap.get(cmds[2]));
                criteriaMap.put(cmds[1],output);
                System.out.println("New negation criteria created.");
                if(!rec) undoStack.push(command);
                break;
            case "newbinarycri":
                if(cmds.length != 5) throw new IllegalArgumentException("NewBinaryCri requires at least four arguments");
                if(criteriaMap.containsKey(cmds[2])) throw new IllegalArgumentException("The criterion" + cmds[2] + " does not exist");
                else if(criteriaMap.containsKey(cmds[3])) throw new IllegalArgumentException("The criterion" + cmds[3] + " does not exist");
                Criteria cri3 = criteriaMap.get(cmds[2]),cri4 = criteriaMap.get(cmds[4]);
                Criteria newcri = Criteria.newBinaryCri(cmds[1],cri3,cmds[3],cri4);
                criteriaMap.put(cmds[1],newcri);
                System.out.println("New binary criteria created.");
                break;
            case "printallcriteria":
                if(cmds.length != 1) throw new IllegalArgumentException("PrintAllCriteria should have no arguments");
                for(Criteria criteria : criteriaMap.values()){
                    criteria.printAllCriteria();
                }
                break;
            case "search":
                if(cmds.length != 2) throw new IllegalArgumentException("Search requires one argument");
                if(!criteriaMap.containsKey(cmds[1])) throw new IllegalArgumentException("The criterion" + cmds[1] + " does not exist");//?
                currentDisk.rList(0,false,criteriaMap.get(cmds[1]));
                //System.out.println("Total number of files: "+ criSearch[0]+", total size:"+(criSearch[1]-40));
                break;
            case "rsearch":
                if(cmds.length != 2) throw new IllegalArgumentException("Search requires one argument");
                if(!criteriaMap.containsKey(cmds[1])) throw new IllegalArgumentException("The criterion" + cmds[1] + " does not exist");//?
                currentDisk.rList(0,true,criteriaMap.get(cmds[1]));
                //System.out.println("Total number of files: "+ criSearchAll[0]+", total size:"+(criSearchAll[1]-40));
                break;
            default:
                throw new IllegalArgumentException("Unknown command");

        }

    }
}
