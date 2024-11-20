import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TestCVFS {
    private CVFS cvfs;

    @BeforeEach
    void setUp() {
        cvfs = new CVFS();
    }


    @Test
    void testHelp() throws Exception {
        cvfs.commandLineMatch("help",false);

    }

    @Test
    void testNewDiskWithInsufficientArguments() {
        String command = "newdisk";  // one parameter
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cvfs.commandLineMatch(command, false);
        });

        Assertions.assertEquals("New disk requires exactly one argument.", exception.getMessage());
    }


    @Test
    void testNewDiskWithCorrectArguments() {
        String command = "newdisk 1000";

        Assertions.assertDoesNotThrow(() -> {
            cvfs.commandLineMatch(command, false);
        });
    }
    @Test
    void testNewDisk() throws Exception {
        cvfs.commandLineMatch("newdisk 1000",false);
        Disk currentDisk=cvfs.getCurrentDisk();
        Assertions.assertNotNull(currentDisk);
        Assertions.assertEquals(1000,currentDisk.getDiskSize());
    }

    @Test
    void testNewDocWithIncorrectArguments() {
        String command = "newdoc nn";

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cvfs.commandLineMatch(command, false);
        });
        Assertions.assertEquals("New document requires exactly three arguments", exception.getMessage());
    }

    @Test
    void testNewDocWithCorrectArguments() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        String command = "newdoc nn txt 123";
        Assertions.assertDoesNotThrow(() -> {
            cvfs.commandLineMatch(command, false);
        });
    }
    @Test
    void testNewDocWithInCorrectName() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        String command = "newdoc %^ txt 123";
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cvfs.commandLineMatch(command, false);
        });

        Assertions.assertEquals("The file name is invalid.", exception.getMessage());
    }

    @Test
    void testNewDocWithInCorrectType() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        String command = "newdoc test py 123";
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cvfs.commandLineMatch(command, false);
        });

        Assertions.assertEquals("Invalid file type.", exception.getMessage());
    }
    @Test
    void testchangeDirnoeexit() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newdir nm", false);
        String command="changedir a";
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cvfs.commandLineMatch(command, false);
        });

        Assertions.assertEquals("Can not find the directory", exception.getMessage());
    }

    @Test
    void testNewDoc() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newdoc docu txt 123", false);
        Disk currentDisk=cvfs.getCurrentDisk();
        Assertions.assertEquals("docu",currentDisk.getCwd().getFiles().get("docu").getName());
        cvfs.commandLineMatch("newdoc docujava java 123", false);
        Assertions.assertEquals("docujava",currentDisk.getCwd().getFiles().get("docujava").getName());
        cvfs.commandLineMatch("newdoc docucss css 123", false);
        Assertions.assertEquals("docucss",currentDisk.getCwd().getFiles().get("docucss").getName());
        cvfs.commandLineMatch("newdoc docuhtml html 123", false);
        Assertions.assertEquals("docuhtml",currentDisk.getCwd().getFiles().get("docuhtml").getName());

    }


    @Test
    void testNewDirWithIncorrectArguments() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        String command = "newdir ";

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cvfs.commandLineMatch(command, false);
        });
        Assertions.assertEquals("New directory requires a name", exception.getMessage());
    }
    @Test
    void testNewDirWithCorrectArguments() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        String command = "newdir testFile";
        Assertions.assertDoesNotThrow(() -> {
            cvfs.commandLineMatch(command, false);
        });
    }
    @Test
    void testNewDir() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        Disk currentDisk=cvfs.getCurrentDisk();
        cvfs.commandLineMatch("newdir testFile", false);
        Assertions.assertEquals("testFile",currentDisk.getCwd().getFiles().get("testFile").getName());
    }



    @Test
    void testDeleteWithIncorrectArguments() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        String command = "delete";
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cvfs.commandLineMatch(command, false);
        });
        Assertions.assertEquals("Delete requires exactly one argument", exception.getMessage());

    }
    @Test
    void testDelete() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        Disk currentDisk=cvfs.getCurrentDisk();
        cvfs.commandLineMatch("newdir nn", false);
        Assertions.assertEquals("nn",currentDisk.getCwd().getFiles().get("nn").getName());
        cvfs.commandLineMatch("delete nn", false);
        Assertions.assertNull(currentDisk.getCwd().getFiles().get("nn"));
    }

    @Test
    void testRenameWithInCorrectArguments() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        String command = "rename";
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cvfs.commandLineMatch(command, false);
        });
        Assertions.assertEquals("Rename need exactly two arguments", exception.getMessage());

    }

    @Test
    void testRename() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        Disk currentDisk=cvfs.getCurrentDisk();
        cvfs.commandLineMatch("newdir ndir", false);
        cvfs.commandLineMatch("newdoc ndoc txt 123", false);
        cvfs.commandLineMatch("rename ndir testdir", false);
        cvfs.commandLineMatch("rename ndoc testdoc", false);
        Assertions.assertEquals("testdir",currentDisk.getCwd().getFiles().get("testdir").getName());
        Assertions.assertEquals("testdoc",currentDisk.getCwd().getFiles().get("testdoc").getName());

    }

    @Test
    void testChangedirWithOneArguments() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);

        //Disk currentDisk=new Disk(1000);
        Disk currentDisk=cvfs.getCurrentDisk();
        cvfs.commandLineMatch("newdir secondlevel", false);
        cvfs.commandLineMatch("changedir secondlevel", false);
        cvfs.commandLineMatch("newdir thirdlevel", false);
        cvfs.commandLineMatch("changedir", false);
        Map<String,File> Files=currentDisk.getCwd().getFiles();
        File file=Files.get("secondlevel");
        Assertions.assertEquals("secondlevel", file.getName());
    }
    @Test
    void testChangedirWithTwoArguments() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        Disk currentDisk=cvfs.getCurrentDisk();
        cvfs.commandLineMatch("newdir secondlevel", false);
        cvfs.commandLineMatch("changedir secondlevel", false);
        cvfs.commandLineMatch("newdir thirdlevel", false);
        cvfs.commandLineMatch("changedir thirdlevel", false);
        cvfs.commandLineMatch("newdir forthlevel", false);
        cvfs.commandLineMatch("changeDir ..", false);
        Assertions.assertEquals("thirdlevel",currentDisk.getCwd().getFiles().get("thirdlevel").getName());
        cvfs.commandLineMatch("changeDir thirdlevel", false);
        Assertions.assertEquals("forthlevel",currentDisk.getCwd().getFiles().get("forthlevel").getName());
    }

    @Test
    void testList() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        Disk currentDisk=cvfs.getCurrentDisk();
        cvfs.commandLineMatch("newdir firstdir", false);
        cvfs.commandLineMatch("newdoc firstdoc txt 123 ", false);
        cvfs.commandLineMatch("changedir firstdir", false);
        cvfs.commandLineMatch("newdir seconddir", false);
        cvfs.commandLineMatch("newdoc seconddoc txt 123 ", false);
        cvfs.commandLineMatch("changedir", false);
        cvfs.commandLineMatch("ls", false);
        cvfs.commandLineMatch("rlist", false);

        Map<String,File> Files=currentDisk.rList(0,false,null);
        File firstDir=Files.get("firstdir");
        Document firstDoc=(Document) Files.get("firstdoc");

        Assertions.assertEquals("firstdoc",firstDoc.getName());
        Assertions.assertEquals("firstdir",firstDir.getName());
        Assertions.assertEquals("txt",firstDoc.getType());
        Assertions.assertEquals(46,firstDoc.getSize());
        Assertions.assertEquals(126,firstDir.getSize());

        Map<String,File> rFiles=currentDisk.rList(0,true,null);
        File firstDir2=rFiles.get("firstdir");
        Document firstDoc2=(Document) rFiles.get("firstdoc");
        File secondDir2=rFiles.get("seconddir");
        Document secondDoc2=(Document) rFiles.get("seconddoc");

        Assertions.assertEquals("firstdoc",firstDoc2.getName());
        Assertions.assertEquals("firstdir",firstDir2.getName());
        Assertions.assertEquals("txt",firstDoc2.getType());
        Assertions.assertEquals(46,firstDoc2.getSize());
        Assertions.assertEquals(126,firstDir2.getSize());

        Assertions.assertEquals("seconddoc",secondDoc2.getName());
        Assertions.assertEquals("seconddir",secondDir2.getName());
        Assertions.assertEquals("txt",secondDoc2.getType());
        Assertions.assertEquals(46,secondDoc2.getSize());
        Assertions.assertEquals(40,secondDir2.getSize());
    }

    @Test
    void testLoadNocri() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        Disk currentDisk=cvfs.getCurrentDisk();
        cvfs.commandLineMatch("load testload", false);
        Map<String,File> Files=currentDisk.rList(0,true,null);
        File f66=Files.get("66");
        File f88=Files.get("88");
        File f99=Files.get("99");
        File nm=Files.get("nm");
        File in=Files.get("in");
        File uu=Files.get("uu");

        Assertions.assertEquals("66",f66.getName());
        Assertions.assertEquals("88",f88.getName());
        Assertions.assertEquals("99",f99.getName());
        Assertions.assertEquals("nm",nm.getName());
        Assertions.assertEquals("in",in.getName());
        Assertions.assertEquals("uu",uu.getName());

    }

    @Test
    void testLoadwithcri() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        Disk currentDisk=cvfs.getCurrentDisk();
        cvfs.commandLineMatch("newsimplecri cc size > 45",false);
        cvfs.commandLineMatch("load testload cc", false);
        Map<String,File> Files=currentDisk.rList(0,true,null);

        Assertions.assertFalse(Files.containsKey("66"));

    }

    @Test
    void testSaveNocri() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        Disk currentDisk=cvfs.getCurrentDisk();
        cvfs.commandLineMatch("load testload", false);
        cvfs.commandLineMatch("save testsave", false);
        //save successfully

        cvfs.commandLineMatch("changedir", false);
        cvfs.commandLineMatch("changedir nm", false);
        cvfs.commandLineMatch("delete in", false);
        cvfs.commandLineMatch("delete uu", false);
        cvfs.commandLineMatch("changedir", false);
        cvfs.commandLineMatch("delete 66", false);
        cvfs.commandLineMatch("delete 88", false);
        cvfs.commandLineMatch("delete 99", false);
        cvfs.commandLineMatch("delete nm", false);
        //delete all the files in the environment

        cvfs.commandLineMatch("load testsave", false);
        Map<String,File> Files=currentDisk.rList(0,true,null);
        Assertions.assertTrue(Files.containsKey("66"));
        Assertions.assertTrue(Files.containsKey("88"));
        Assertions.assertTrue(Files.containsKey("99"));
        Assertions.assertTrue(Files.containsKey("nm"));
        Assertions.assertTrue(Files.containsKey("in"));
        Assertions.assertTrue(Files.containsKey("uu"));
    }

    @Test
    void testSaveWithcri() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        Disk currentDisk=cvfs.getCurrentDisk();
        cvfs.commandLineMatch("load testload", false);
        cvfs.commandLineMatch("newsimplecri cc size > 45", false);

        cvfs.commandLineMatch("save testsavecri cc", false);
        //save successfully

        cvfs.commandLineMatch("changedir", false);
        cvfs.commandLineMatch("changedir nm", false);
        cvfs.commandLineMatch("delete in", false);
        cvfs.commandLineMatch("delete uu", false);
        cvfs.commandLineMatch("changedir", false);
        cvfs.commandLineMatch("delete 66", false);
        cvfs.commandLineMatch("delete 88", false);
        cvfs.commandLineMatch("delete 99", false);
        cvfs.commandLineMatch("delete nm", false);
        //delete all the files in the environment

        cvfs.commandLineMatch("load testsavecri", false);
        Map<String,File> Files=currentDisk.rList(0,true,null);
        Assertions.assertFalse(Files.containsKey("66"));
        Assertions.assertTrue(Files.containsKey("88"));
        Assertions.assertTrue(Files.containsKey("99"));
        Assertions.assertTrue(Files.containsKey("nm"));
        Assertions.assertTrue(Files.containsKey("in"));
        Assertions.assertTrue(Files.containsKey("uu"));
    }

    @Test
    void testUndoRedo() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        Disk currentDisk=cvfs.getCurrentDisk();
        cvfs.commandLineMatch("newdoc nn txt 123", false);
        cvfs.commandLineMatch("undo", false);
        Assertions.assertNull(currentDisk.getCwd().getFiles().get("nn"));
        cvfs.commandLineMatch("redo", false);
        Assertions.assertEquals("nn",currentDisk.getCwd().getFiles().get("nn").getName());


    }

    @Test
    public void testcri1() throws Exception {

        String end;
        String target;
        target = "";
        String index;
        String targetall = "";

        String[] testset = {"newsimplecri aa name contains \"j\" ", "newsimplecri bb size <= 114514 ", "newsimplecri cc type equals \"java\" ", "newsimplecri dd size > 2048 ", "newbinarycri ee aa || bb ", "newbinarycri ff cc && dd ", "newbinarycri gg ee || ff "};
        for (int i = 0; i < testset.length; i++) {
            cvfs.commandLineMatch(testset[i], false);
            target = testset[i].substring(16);
            if (i == 4) target = "(name contains \"j\" || size <= 114514 )";
            if (i == 5) target = "(type equals \"java\" && size > 2048 )";
            if (i == 6) target = "((name contains \"j\" || size <= 114514 )|| (type equals \"java\" && size > 2048 ))";
            targetall += (target + "\n");
            index = Character.toString((char)(97 + i)) + Character.toString((char)(97 + i));
            Assert.assertEquals(target, cvfs.getcri().get(index).toString());
        }
        Collection<Criteria> values = cvfs.getcri().values();
        end = "";
        for (Criteria value : values) {
            end += value.toString();
            end += "\n";
        }
        Assertions.assertEquals(targetall, end);
    }
    @Test
    public void testIsdocument() throws Exception
    {
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newsimplecri IsDocument", false);
        cvfs.commandLineMatch("newdoc a txt a", false);
        Assertions.assertEquals("IsDocument",cvfs.getcri().get("IsDocument").to());
    }
    @Test
    public void testNegation() throws Exception
    {
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newsimplecri cc size == 42", false);
        cvfs.commandLineMatch("newnegation cr cc", false);
        Assertions.assertEquals("cr size != 42 \n",cvfs.getcri().get("cr").to());
    }
    @Test
    public void testBinaryNegation() throws Exception
    {
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newsimplecri cc size == 42", false);
        cvfs.commandLineMatch("newsimplecri cr type equals \"txt\"", false);
        cvfs.commandLineMatch("newbinarycri cx cr || cc", false);
        cvfs.commandLineMatch("newnegation ck cx", false);
        Assertions.assertEquals("ck (type not equals \"txt\" && size != 42 )\n",cvfs.getcri().get("ck").to());
    }
    @Test
    public void testSearch() throws Exception
    {
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newsimplecri cc size == 42", false);
        cvfs.commandLineMatch("newdoc a txt a", false);
        cvfs.commandLineMatch("newdoc b txt b",false);
        Assertions.assertEquals(cvfs.getCurrentDisk().getCwd().getFiles(),cvfs.getCurrentDisk().rList(0,false,cvfs.getcri().get("cc")));
    }
    @Test
    public void testrSearchSize() throws Exception
    {
        Map<String,File> tempmap = new HashMap<>();
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newsimplecri cc size == 42", false);
        cvfs.commandLineMatch("newdoc a txt a", false);
        cvfs.commandLineMatch("newdoc b txt b",false);
        cvfs.commandLineMatch("newdir c",false);
        cvfs.commandLineMatch("cd c",false);
        cvfs.commandLineMatch("newdoc d txt d",false);
        tempmap.put("d",cvfs.getCurrentDisk().getCwd().getFiles().get("d"));
        cvfs.commandLineMatch("cd",false);
        tempmap.put("a",cvfs.getCurrentDisk().getCwd().getFiles().get("a"));
        tempmap.put("b",cvfs.getCurrentDisk().getCwd().getFiles().get("b"));
        Assertions.assertEquals(tempmap,cvfs.getCurrentDisk().rList(0,true,cvfs.getcri().get("cc")));
    }
    @Test
    public void testrSearchName() throws Exception
    {
        Map<String,File> tempmap = new HashMap<>();
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newsimplecri cr name contains \"d\"", false);
        cvfs.commandLineMatch("newdoc a txt a", false);
        cvfs.commandLineMatch("newdoc b txt b",false);
        cvfs.commandLineMatch("newdir c",false);
        cvfs.commandLineMatch("cd c",false);
        cvfs.commandLineMatch("newdoc d txt d",false);
        tempmap.put("d",cvfs.getCurrentDisk().getCwd().getFiles().get("d"));
        cvfs.commandLineMatch("cd",false);
        //tempmap.put("a",cvfs.getCurrentDisk().getCwd().getFiles().get("a"));
        //tempmap.put("b",cvfs.getCurrentDisk().getCwd().getFiles().get("b"));
        Assertions.assertEquals(tempmap,cvfs.getCurrentDisk().rList(0,true,cvfs.getcri().get("cr")));
    }
    @Test
    public void testrSearchCompos1() throws Exception
    {
        Map<String,File> tempmap = new HashMap<>();
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newsimplecri cr name contains \"d\"", false);
        cvfs.commandLineMatch("newsimplecri cc type equals \"txt\"", false);
        cvfs.commandLineMatch("newbinarycri cx cr || cc", false);
        cvfs.commandLineMatch("newdoc a txt a", false);
        cvfs.commandLineMatch("newdoc b txt b",false);
        cvfs.commandLineMatch("newdir c",false);
        cvfs.commandLineMatch("cd c",false);
        cvfs.commandLineMatch("newdoc d txt d",false);
        tempmap.put("d",cvfs.getCurrentDisk().getCwd().getFiles().get("d"));
        cvfs.commandLineMatch("cd",false);
        tempmap.put("a",cvfs.getCurrentDisk().getCwd().getFiles().get("a"));
        tempmap.put("b",cvfs.getCurrentDisk().getCwd().getFiles().get("b"));
        Assertions.assertEquals(tempmap,cvfs.getCurrentDisk().rList(0,true,cvfs.getcri().get("cx")));
    }
    @Test
    public void testrSearchCompos2() throws Exception
    {
        Map<String,File> tempmap = new HashMap<>();
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newsimplecri cr name contains \"d\"", false);
        cvfs.commandLineMatch("newsimplecri cc type equals \"txt\"", false);
        cvfs.commandLineMatch("newbinarycri cx cr && cc", false);
        cvfs.commandLineMatch("newdoc a txt a", false);
        cvfs.commandLineMatch("newdoc b txt b",false);
        cvfs.commandLineMatch("newdir c",false);
        cvfs.commandLineMatch("cd c",false);
        cvfs.commandLineMatch("newdoc d txt d",false);
        tempmap.put("d",cvfs.getCurrentDisk().getCwd().getFiles().get("d"));
        cvfs.commandLineMatch("cd",false);
        cvfs.commandLineMatch("rsearch cx",false);
        cvfs.commandLineMatch("search cx",false);
        tempmap.put("a",cvfs.getCurrentDisk().getCwd().getFiles().get("a"));
        tempmap.put("b",cvfs.getCurrentDisk().getCwd().getFiles().get("b"));
        Assertions.assertEquals(tempmap,cvfs.getCurrentDisk().rList(0,true,cvfs.getcri().get("cx")));
    }
    @Test
    public void testCdUndo() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newdir first", false);
        cvfs.commandLineMatch("cd first", false);
        cvfs.commandLineMatch("newdir second", false);
        cvfs.commandLineMatch("cd", false);
        cvfs.commandLineMatch("cd first ", false);
        Assertions.assertEquals("second",cvfs.getCurrentDisk().getCwd().getFiles().get("second").getName());
        cvfs.commandLineMatch("undo", false);
        Assertions.assertEquals("first",cvfs.getCurrentDisk().getCwd().getFiles().get("first").getName());
        cvfs.commandLineMatch("redo", false);
        Assertions.assertEquals("second",cvfs.getCurrentDisk().getCwd().getFiles().get("second").getName());

    }
    @Test
    public void testCriUndo() throws Exception {
        cvfs.commandLineMatch("newdisk 1000", false);
        cvfs.commandLineMatch("newsimplecri cc size > 60", false);
        cvfs.commandLineMatch("undo", false);
        cvfs.commandLineMatch("redo", false);
        Assertions.assertEquals("cc size > 60 \n",cvfs.getcri().get("cc").to());
    }








}