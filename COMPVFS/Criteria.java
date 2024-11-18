import java.util.*;

public class Criteria {
    private String criName;
    private String attrName;
    private String op;
    private Object val;
    private Criteria compositeC1;
    private Criteria compositeC2;
    private boolean isCompos = false;
    private static final String[] op_set = {"contains","equals",">","<",">=","<=","==","!="};
    private static final String[] neg_op_set = {"not contains","not equals","<=",">=","<",">","!=","=="};
    private static final String[] logicOp_set = {"&&","||"};

    //public Criteria[] getComCri() {return new Criteria[]{compositeC1, compositeC2};}
    //constructor for non-composite criteria
    public Criteria(String criName)
    {
        if(!criName.equals("IsDocument")){
            throw new IllegalArgumentException("invalid input of criteria name");
        }
        this.criName = criName;
        this.isCompos = false;
        this.compositeC1 = null;
        this.compositeC2 = null;
    }//IsDocument
    public Criteria(String criName, String attrName, String op, Object val){
        boolean validOp = Arrays.asList(op_set).contains(op);
        int int_op = Arrays.asList(op_set).indexOf(op);
        if(!validOp){
            throw new IllegalArgumentException("Invalid input of operator: "+op);
        }
        if(!(criName.matches("^[A-Za-z]{2}$"))){
            throw new IllegalArgumentException("The Criteria name should contain exactly two letters");
        }
        else if(!(val instanceof String || val instanceof Integer)){
            throw new IllegalArgumentException("Wrong input format");
        }
        else if (!(attrName.equals("name") && op.equals("contains") && (val instanceof String && ((String) val).matches("^\".*\"$")))
                && !(attrName.equals("type") && op.equals("equals") && (val instanceof String && ((String) val).matches("^\".*\"$")))
                && !(val instanceof Integer && int_op > 1 && attrName.equals("size"))//bug
        ) {
            throw new IllegalArgumentException("Wrong input format");
        }
        this.criName = criName;
        this.attrName = attrName;
        this.op = op;
        this.val = val;
        this.isCompos = false;
        this.compositeC1 = null;
        this.compositeC2 = null;
    }

    //constructor for composite criteria
    public Criteria(String criName1, Criteria criName2, String logicOp, Criteria criName4){
        boolean validOp = Arrays.asList(logicOp_set).contains(logicOp);
        if(!(criName1.matches("^[A-Za-z]{2}$"))){
            throw new IllegalArgumentException("The Criteria name should contain exactly two letters");
        }
        if(!validOp){
            throw new IllegalArgumentException("Invalid input of logicOp: "+logicOp);
        }
        if(Objects.equals(criName2.criName, "IsDocument") || Objects.equals(criName4.criName, "isDocument")){
            throw new IllegalArgumentException("IsDocument criterion should not be used as the argument");
        }
        this.criName = criName1;
        this.op = logicOp;
        this.compositeC1 = criName2;
        this.compositeC2 = criName4;
        this.isCompos = true;
    }
    public Criteria getCompositeC1() {
        return this.compositeC1;
    }
    public Criteria getCompositeC2() {
        return this.compositeC2;
    }

    public void getAllCriteria(){
        if(this.isCompos){
            this.getCompositeC1().getAllCriteria();
            System.out.print(this.op + " ");
            this.getCompositeC2().getAllCriteria();
        }
        else{
            System.out.print(this.attrName + " ");
            System.out.print(this.op + " ");
            System.out.println(this.val);
        }
    }
    public void printAllCriteria(){
        if(!Objects.equals(this.criName, "IsDocument")) System.out.println(this.criName + " ");
        else {
            System.out.print(this.criName + " ");
            this.getAllCriteria();
        }
    }
    boolean isComposite(){
        return this.isCompos;
    }
    public static Criteria newBinaryCri(String criName1, Criteria criName2, String lop, Criteria criName3){
        return new Criteria(criName1, criName2, lop, criName3);
    }
    public static Criteria newNegation(String criName1,Criteria criName2){
        if(!(criName1.matches("^[A-Za-z]{2}$"))){
            throw new IllegalArgumentException("The Criteria name should contain exactly two letters");
        }
        if(Objects.equals(criName2.criName, "IsDocument")){
            throw new IllegalArgumentException("IsDocument criterion should not be used as the argument");
        }
        if(criName2.isCompos){
            int opIndexC1 = Arrays.asList(op_set).indexOf(criName2.compositeC1.op);
            int opIndexC2 = Arrays.asList(op_set).indexOf(criName2.compositeC2.op);
            String newOp = criName2.op.equals("&&") ? "||" : "&&";
            String opC1 = Arrays.asList(neg_op_set).get(opIndexC1);
            String opC2 = Arrays.asList(neg_op_set).get(opIndexC2);
            Criteria C1 = new Criteria(criName2.compositeC1.criName, criName2.compositeC1.attrName,opC1,criName2.compositeC1.val);
            Criteria C2 = new Criteria(criName2.compositeC2.criName, criName2.compositeC2.attrName,opC2,criName2.compositeC2.val);
            return new Criteria(criName1,C1,newOp,C2);
        }
        else{
            int opIndex = Arrays.asList(op_set).indexOf(criName2.op);
            String newOp = Arrays.asList(neg_op_set).get(opIndex);
            return new Criteria(criName1, criName2.attrName,newOp,criName2.val);
        }
    }
    public boolean Compare(File file,Criteria criteria){
        if(criteria.isCompos){
            return recCompare(file, criteria.compositeC1,criteria.compositeC2,criteria.op);
        }
        else{
            return check(file,criteria);
        }
    }
    public boolean recCompare(File file,Criteria criteriaC1,Criteria criteriaC2,String lop){
        if(criteriaC1.isCompos){
            recCompare(file, criteriaC1.compositeC1,criteriaC1.compositeC2,criteriaC1.compositeC1.op);
        }
        if(criteriaC2.isCompos){
            recCompare(file,criteriaC2.compositeC1,criteriaC2.compositeC2,criteriaC2.compositeC2.op);
        }
        return composCheck(file,criteriaC1,criteriaC2,lop);
    }
    public boolean check(File file,Criteria criteria){
        if(Objects.equals(criteria.criName,"IsDocuemnt")) return file.isDocument();
        if(Objects.equals(criteria.attrName, "name") && file.name.contains((String)criteria.val)){
            return true;
        }
        else if(Objects.equals(criteria.attrName, "size") && file.size == (int)val){
            return true;
        }
        else if(Objects.equals(criteria.attrName, "type") && file instanceof Document){
            return ((Document) file).getType().equals(val);
        }
        return false;
    }
    public boolean composCheck(File file,Criteria C1,Criteria C2,String lop){
        if(lop.equals("&&")){
            return check(file, C1) && check(file, C2);
        }
        else if(lop.equals("||")){
            return check(file, C1) || check(file, C2);
        }
        return false;
    }
}