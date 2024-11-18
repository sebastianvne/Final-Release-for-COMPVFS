import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;

public class CVFS
{
    private Disk currentDisk;
    private HashMap<String,Criteria> criteriaMap = new HashMap<>();
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private Stack<File> tempTrashStack = new Stack<>();
    private Stack<File> tempRestoreStack = new Stack<>();
    private Stack<Criteria> trashCriteriaStack = new Stack<>();
    public static void main(String[] args)
    {
        CVFS cvfs = new CVFS();
        cvfs.commandLineInput();
    }
    public void commandLineInput()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\t\t\t\t<<<WELCOME TO COMP VFS>>>");
        while(true)
        {
            System.out.print("$:");
            String command = scanner.nextLine();
            if(Objects.equals(command, "quit")) break;
            try
            {
                commandLineMatch(command,false);
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
    public void reverseDo(boolean undo,Stack<String> processStack,Stack<File> fileStack) throws Exception
    {
        if(processStack.isEmpty()) throw new IllegalArgumentException("There is nothing to do.");
        String[] lastCmds = processStack.pop().split(" ");
        if(lastCmds[0].equalsIgnoreCase("newdoc") || lastCmds[0].equalsIgnoreCase("newdir"))
            commandLineMatch("delete "+lastCmds[1],true);
        if(lastCmds[0].equalsIgnoreCase("delete"))
        {
            File restoreFile = fileStack.pop();
            if(restoreFile instanceof Directory) commandLineMatch("newdir "+restoreFile.getName(),true);
            if(restoreFile instanceof Document) commandLineMatch("newdoc "+restoreFile.getName()+
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
            if(!undo)
            {
                commandLineMatch("changedir "+lastCmds[1],true);
            }
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
                if(cmds.length<=1) throw new IllegalArgumentException("New disk requires at least one argument");
                currentDisk = new Disk(Integer.valueOf(cmds[1]));
                System.out.println("New disk successfully created with size of "+cmds[1]);
                break;
            case "touch":
            case "newdoc":
                if(cmds.length<=2) throw new IllegalArgumentException("New document requires at least two argument");
                StringBuilder sb = new StringBuilder();
                for(int i=3;i<cmds.length;i++)
                {
                    sb.append(cmds[i]);
                    sb.append(" ");
                }
                currentDisk.getCwd().newDoc(cmds[1],cmds[2],sb.toString());
                if(!rec) undoStack.push(command);
                else redoStack.push(command);
                break;
            case "mkdir":
            case "newdir":
                if(cmds.length<=1) throw new IllegalArgumentException("New directory requires a name");
                currentDisk.getCwd().newDirectory(cmds[1]);
                if(!rec) undoStack.push(command);
                else redoStack.push(command);
                break;
            case "ls":
            case "list":
                int[] summary = currentDisk.list();
                System.out.println("Total number of files: "+summary[0]+", total size:"+(summary[1]-40));
                break;
            case "rlist":
                int[] all = currentDisk.rList(0);
                System.out.println("Total number of files: "+all[0]+", total size:"+(all[1]-40));
                break;
            case "rm":
            case "delete":
                if(cmds.length<=1) throw new IllegalArgumentException("Delete requires at least one argument");
                File tempFile = currentDisk.getCwd().search(cmds[1]);
                currentDisk.getCwd().deleteFile(cmds[1]);
                if(!rec)
                {
                    undoStack.push(command);
                    tempTrashStack.push(tempFile);
                }
                else
                {
                    redoStack.push(command);
                    tempRestoreStack.push(tempFile);
                }
                break;
            case "mv":
            case "rename":
                if(cmds.length<=2) throw new IllegalArgumentException("Rename requires at least two argument");
                currentDisk.getCwd().renameFile(cmds[1],cmds[2]);
                if(!rec) undoStack.push(command);
                else redoStack.push(command);
                break;
            case "cd":
            case "changedir":
                File lastDir = currentDisk.getCwd();
                if(cmds.length<=1) currentDisk.changeDirectory("");
                else currentDisk.changeDirectory(cmds[1]);
                if(!rec)
                {
                    undoStack.push(command);
                    tempTrashStack.push(lastDir);
                }
                break;
            case "save":
                if(cmds.length<=1) throw new IllegalArgumentException("Requires a local path");
                else if(cmds.length==3){
                    Save saveInstance = new Save(currentDisk,cmds[2]);
                    saveInstance.save(cmds[1]);
                }
                else {
                    Save saveInstance = new Save(currentDisk);
                    saveInstance.save(cmds[1]);
                }
                break;
            case "load":
                if(cmds.length<=1) throw new IllegalArgumentException("Requires a local path");
                else if(cmds.length==3){
                    Load loadInstance = new Load(currentDisk,cmds[2]);
                    loadInstance.load(cmds[1]);
                }
                else{
                    Load loadInstance = new Load(currentDisk);
                    loadInstance.load(cmds[1]);
                }
                break;
            case "newsimplecri":
                if(cmds.length != 5 && cmds.length != 2) throw new IllegalArgumentException("Invalid input for NewSimpleCri");
                else if(cmds.length == 2){
                    Criteria criteria = new Criteria(cmds[1]);
                    criteriaMap.put(cmds[1],criteria);
                }
                else{
                    Criteria criteria = new Criteria(cmds[1],cmds[2],cmds[3],cmds[4]);;
                    criteriaMap.put(cmds[1],criteria);
                }
                if(!rec) undoStack.push(command);
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
                if(criteriaMap.containsKey(cmds[1])) throw new IllegalArgumentException("The criterion" + cmds[1] + " does not exist");//?
                int[] criSearch = currentDisk.search(criteriaMap.get(cmds[1]));
                System.out.println("Total number of files: "+ criSearch[0]+", total size:"+(criSearch[1]-40));
                break;
            case "rsearch":
                if(cmds.length != 2) throw new IllegalArgumentException("Search requires one argument");
                if(criteriaMap.containsKey(cmds[1])) throw new IllegalArgumentException("The criterion" + cmds[1] + " does not exist");//?
                int[] criSearchAll = currentDisk.rSearch(criteriaMap.get(cmds[1]),currentDisk.getCwd().calculateSize());
                System.out.println("Total number of files: "+ criSearchAll[0]+", total size:"+(criSearchAll[1]-40));
                break;
            default:
                throw new IllegalArgumentException("Unknown command");

        }

    }
}
