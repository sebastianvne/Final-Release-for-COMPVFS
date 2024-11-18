import java.util.*;

public class Criteria {
    private final String criName;
    private String attrName;
    private String op;
    private String val;
    private Criteria compositeC1;
    private Criteria compositeC2;
    private boolean isCompos = false;
    private static final String[] op_set = {"contains","equals",">","<",">=","<=","==","!="};
    private static final String[] neg_op_set = {"not contains","not equals","<=",">=","<",">","!=","=="};
    private static final String[] logicOp_set = {"&&","||"};


    //constructor for non-composite criteria
    public Criteria(String criName)
    {
        if(!criName.equals("IsDocument")){
            throw new IllegalArgumentException("invalid input of criteria name");
        }
        this.criName = criName;
        this.isCompos = false;
    }//IsDocument
    public Criteria(String criName, String attrName, String op, String val){
        boolean validOp = Arrays.asList(op_set).contains(op) || Arrays.asList(neg_op_set).contains(op);
        int int_op = Arrays.asList(op_set).indexOf(op);
        if(!validOp){
            throw new IllegalArgumentException("Invalid input of operator: "+op);
        }
        if(!(criName.matches("^[A-Za-z]{2}$"))){
            throw new IllegalArgumentException("The Criteria name should contain exactly two letters");
        }
        else if (!(attrName.equals("name") && op.equals("contains") && (((String) val).matches("^\".*\"$")))
                && !(attrName.equals("type") && op.equals("equals") && (((String) val).matches("^\".*\"$")))
                && !(attrName.equals("name") && op.equals("not contains") && (((String) val).matches("^\".*\"$")))
                && !(attrName.equals("type") && op.equals("not equals") && (((String) val).matches("^\".*\"$")))
                && !(isInteger(val) && int_op > 1 && attrName.equals("size"))
        ) {
            throw new IllegalArgumentException("Wrong input format");
        }
        this.criName = criName;
        this.attrName = attrName;
        this.op = op;
        this.val = val;
        this.isCompos = false;
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
        if(Objects.equals(criName2.getCriName(), "IsDocument") || Objects.equals(criName4.getCriName(), "isDocument")){
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
    public boolean isCompos() { return this.isCompos; }
    public String getCriName() { return this.criName; }
    public String getAttrName() { return this.attrName; }
    public String getOp() { return this.op; }
    public String getVal() { return this.val; }

    public static boolean isInteger(String str) {
        try { Integer.parseInt(str); return true; }
        catch (NumberFormatException e) { return false; }
    }
    public void getAllCriteria(){
        if(this.isCompos()){
            System.out.print("(");
            this.getCompositeC1().getAllCriteria();
            System.out.print(this.getOp() + " ");
            this.getCompositeC2().getAllCriteria();
            System.out.print(")");
        }
        else{
            System.out.print(this.getAttrName() + " ");
            System.out.print(this.getOp() + " ");
            System.out.print(this.getVal() + " ");
        }
    }
    public void printAllCriteria(){
        if(Objects.equals(this.getCriName(), "IsDocument")) System.out.println(this.getCriName() + " ");
        else {
            System.out.print(this.getCriName() + " ");
            this.getAllCriteria();
        }
    }
    public static Criteria newBinaryCri(String criName1, Criteria criName2, String lop, Criteria criName3){
        return new Criteria(criName1, criName2, lop, criName3);
    }
    public static Criteria newNegation(String criName1,Criteria criName2){
        if(!(criName1.matches("^[A-Za-z]{2}$"))){
            throw new IllegalArgumentException("The Criteria name should contain exactly two letters");
        }
        if(Objects.equals(criName2.getCriName(), "IsDocument")){
            throw new IllegalArgumentException("IsDocument criterion should not be used as the argument");
        }
        if(criName2.isCompos()){
            int opIndexC1 = Arrays.asList(op_set).indexOf(criName2.getCompositeC1().getOp());
            int opIndexC2 = Arrays.asList(op_set).indexOf(criName2.getCompositeC2().getOp());
            String newOp = criName2.getOp().equals("&&") ? "||" : "&&";
            String opC1 = Arrays.asList(neg_op_set).get(opIndexC1);
            String opC2 = Arrays.asList(neg_op_set).get(opIndexC2);
            Criteria C1 = new Criteria(criName2.getCompositeC1().getCriName(), criName2.getCompositeC1().getAttrName(),opC1,criName2.getCompositeC1().getVal());
            Criteria C2 = new Criteria(criName2.getCompositeC2().getCriName(), criName2.getCompositeC2().getAttrName(),opC2,criName2.getCompositeC2().getVal());
            return new Criteria(criName1,C1,newOp,C2);
        }
        else{
            int opIndex = Arrays.asList(op_set).indexOf(criName2.getOp());
            String newOp = Arrays.asList(neg_op_set).get(opIndex);
            return new Criteria(criName1, criName2.getAttrName(),newOp,criName2.getVal());
        }
    }
    public boolean Compare(File file, Criteria criteria) {
        if (criteria.isCompos()) {
            return recCompare(file, criteria.getCompositeC1(), criteria.getCompositeC2(), criteria.getOp());
        } else {
            return check(file, criteria);
        }
    }

    public boolean recCompare(File file, Criteria criteriaC1, Criteria criteriaC2, String lop) {
        boolean result1 = criteriaC1.isCompos() ? recCompare(file, criteriaC1.getCompositeC1(), criteriaC1.getCompositeC2(), criteriaC1.getOp()) : check(file, criteriaC1);
        boolean result2 = criteriaC2.isCompos() ? recCompare(file, criteriaC2.getCompositeC1(), criteriaC2.getCompositeC2(), criteriaC2.getOp()) : check(file, criteriaC2);

        return composCheck(result1, result2, lop);
    }

    public boolean check(File file, Criteria criteria) {
        if (Objects.equals(criteria.getCriName(), "IsDocument")) return file.isDocument();
        if (Objects.equals(criteria.getAttrName(), "name")) {
            if (Objects.equals(criteria.getOp(), "contains"))
                return file.getName().contains(criteria.getVal().substring(1, criteria.getVal().length() - 1));
            else return !file.getName().contains(criteria.getVal().substring(1, criteria.getVal().length() - 1));
        }
        else if (Objects.equals(criteria.getAttrName(), "size")) {
            if(Objects.equals(criteria.getOp(), ">=")) return file.getSize() >= Integer.parseInt(criteria.getVal());
            else if(Objects.equals(criteria.getOp(), "<=")) return file.getSize() <= Integer.parseInt(criteria.getVal());
            else if(Objects.equals(criteria.getOp(), ">")) return file.getSize() > Integer.parseInt(criteria.getVal());
            else if(Objects.equals(criteria.getOp(), "<")) return file.getSize() < Integer.parseInt(criteria.getVal());
            else if(Objects.equals(criteria.getOp(), "==")) return file.getSize() == Integer.parseInt(criteria.getVal());
            else if(Objects.equals(criteria.getOp(), "!=")) return file.getSize() != Integer.parseInt(criteria.getVal());
        }
        else if (Objects.equals(criteria.getAttrName(), "type") && file instanceof Document) {
            if (Objects.equals(criteria.getOp(), "equals")) return criteria.getVal().contains(((Document) file).getType());
            else return !criteria.getVal().contains(((Document) file).getType());
        }
        return false;
    }

    public boolean composCheck(boolean result1, boolean result2, String lop) {
        switch (lop) {
            case "&&":
                return result1 && result2;
            case "||":
                return result1 || result2;
        }
        return false;
    }

}