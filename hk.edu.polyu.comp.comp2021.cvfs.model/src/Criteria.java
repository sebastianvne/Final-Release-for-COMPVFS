import java.util.*;

/**
 * Criteria Class, the criteria of search
 */
public class Criteria {
    private final String criName;
    private String attrName;
    private String op;
    private String val;
    private Criteria compositeC1;
    private Criteria compositeC2;
    final private boolean isCompos;
    private static final String[] op_set = {"contains","equals",">","<",">=","<=","==","!="};
    private static final String[] neg_op_set = {"not contains","not equals","<=",">=","<",">","!=","=="};
    private static final String[] logicOp_set = {"&&","||"};


    /**
     * A constructor of class Criteria
     * Construct a new simple Criteria object
     *
     * @param criName is the criName of the new Criteria object, and it must be "IsDocument".
     * @throws IllegalArgumentException if criName is not "IsDocument".
     */


    public Criteria(String criName)
    {
        if(!criName.equals("IsDocument")){
            throw new IllegalArgumentException("invalid input of criteria name");
        }
        this.criName = criName;
        this.isCompos = false;
    }

    /**
     * A constructor of class Criteria
     * Construct a new simple Criteria object
     *
     * @param criName is the criName of the new Criteria object, it must contain exactly two English letters
     * @param attrName is the attName of the new Criteria object, it should be either "name", "type" or "size".
     * @param op is the op of the new Criteria object, its value depends on attrName,
     *           if attrName is "name", op should be "contains".
     *           if attrName is "type", op should be "equals".
     *           if attrName is "size", op should be ">","<",">=","<=","==" or "!=".
     * @param val is the val of the new Criteria object, its value depends on attrName,
     *            if attrName is "name", val should be a string in the double quote.
     *            if attrName is "type", val should be a string in the double quote that denotes the type of the file, which is "txt", "java", "html" or "css".
     *            if attrName is "size", val should be a string denotes an integer.
     * @throws IllegalArgumentException if the parameters received does not meet the above conditions.
     */

    public Criteria(String criName, String attrName, String op, String val){
        boolean validOp = Arrays.asList(op_set).contains(op) || Arrays.asList(neg_op_set).contains(op);
        int int_op = Arrays.asList(op_set).indexOf(op);
        if(!validOp){
            throw new IllegalArgumentException("Invalid input of operator: "+op);
        }
        if(!(criName.matches("^[A-Za-z]{2}$"))){
            throw new IllegalArgumentException("The Criteria name should contain exactly two letters");
        }
        else if (!(attrName.equals("name") && op.equals("contains") && (( val).matches("^\".*\"$")))
                && !(attrName.equals("type") && op.equals("equals") && (( val).matches("^\".*\"$")))
                && !(attrName.equals("name") && op.equals("not contains") && (( val).matches("^\".*\"$")))
                && !(attrName.equals("type") && op.equals("not equals") && (( val).matches("^\".*\"$")))
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

    /**
     * A constructor of class Criteria
     * Construct a new composite Criteria object
     *
     * @param criName1 is the criName of the new Criteria object, it must contain exactly two English letters.
     * @param criName2 is the criName of the first Criteria object that used to construct criName1.
     * @param logicOp is the op of the Criteria object criName1, which is either "&&" or "||".
     * @param criName4 is the criName of the second Criteria object that used to construct criName1.
     * @throws IllegalArgumentException if the parameters received does not meet the above conditions.
     */

    public Criteria(String criName1, Criteria criName2, String logicOp, Criteria criName4){
        boolean validOp = Arrays.asList(logicOp_set).contains(logicOp);
        if(!(criName1.matches("^[A-Za-z]{2}$"))){
            throw new IllegalArgumentException("The Criteria name should contain exactly two letters");
        }
        if(!validOp){
            throw new IllegalArgumentException("Invalid input of logicOp: "+logicOp);
        }
        this.criName = criName1;
        this.op = logicOp;
        this.compositeC1 = criName2;
        this.compositeC2 = criName4;
        this.isCompos = true;
    }

    /**
     * @return the first Criteria that participates in the composition of a composite Criteria object.
     */
    public Criteria getCompositeC1() {
        return this.compositeC1;
    }

    /**
     * @return the first Criteria that participates in the composition of a composite Criteria object.
     */
    public Criteria getCompositeC2() {
        return this.compositeC2;
    }

    /**
     * return the value of private boolean field isCompos of a Criteria object.
     *
     * @return if the Criteria object is a composite object, return true, otherwise return false.
     */
    public boolean isCompos() { return this.isCompos; }

    /**
     * @return the value of private String field criName of a Criteria object.
     */
    public String getCriName() { return this.criName; }

    /**
     * @return the value of private String field attrName of a Criteria object.
     */
    public String getAttrName() { return this.attrName; }

    /**
     * @return the value of private String field op of a Criteria object.
     */
    public String getOp() { return this.op; }

    /**
     * @return the value of private String field val of a Criteria object.
     */
    public String getVal() { return this.val; }

    /**
     * @param str String that need to be determined if it is integer
     * @return the value of private String field attrName of a Criteria object.
     */
    public static boolean isInteger(String str) {
        try { Integer.parseInt(str); return true; }
        catch (NumberFormatException e) { return false; }
    }


    /**
     * call the constructor which constructs a new composite Criteria object
     *
     * @param criName1 is the criName of the new Criteria object, it must contain exactly two English letters.
     * @param criName2 is the criName of the first Criteria object that used to construct criName1.
     * @param lop is the op of the Criteria object criName1, which is either "&&" or "||".
     * @param criName3 is the criName of the second Criteria object that used to construct criName1.
     * @return call the constructor and return a new Criteria object named criName1
     */
    public static Criteria newBinaryCri(String criName1, Criteria criName2, String lop, Criteria criName3){
        return new Criteria(criName1, criName2, lop, criName3);
    }

    /**
     * create a new Criteria object which is the negation of an existing criterion named criName2.
     *
     * @param criName1 is the criName of the new Criteria object, it must contain exactly two English letters.
     * @param criName2 is the criName of an existing Criteria object that used to construct criName1.
     * @throws IllegalArgumentException if the parameters received does not meet the above conditions or when the criName2 is an "IsDocument" criterion.
     * @return a new Criteria object named criName1.
     */
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

    /**
     * check whether a file satisfies certain criterion.
     * if the Criterion object is a composite criterion, call the method recCompare, otherwise, call the method check.
     *
     * @param file a file object that needs to be checked.
     * @param criteria an existing Criteria object that is used as the criterion.
     * @return returns a boolean value by calling the boolean method recCompare or check.
     */
    public boolean Compare(File file, Criteria criteria) {
        if (criteria.isCompos()) {
            return recCompare(file, criteria.getCompositeC1(), criteria.getCompositeC2(), criteria.getOp());
        } else {
            return check(file, criteria);
        }
    }

    /**
     * this method is called if the input Criteria object of method Compare is a composite criterion.
     * check whether criteriaC1 and criteriaC2 is composite criterion if yes, call itself recursively, otherwise, call method check
     * get the return value of method check and then call method composCheck
     *
     * @param file a file object that needs to be checked.
     * @param criteriaC1 the first Criteria object that used to construct the composite Criteria object that is the argument of method Compare.
     * @param criteriaC2 the second Criteria object that used to construct the composite Criteria object that is the argument of method Compare.
     * @param lop the op of the composite Criteria object that is the argument of method Compare.
     * @return returns a boolean value by calling the boolean method compoCheck.
     */
    public boolean recCompare(File file, Criteria criteriaC1, Criteria criteriaC2, String lop) {
        boolean result1 = criteriaC1.isCompos() ? recCompare(file, criteriaC1.getCompositeC1(), criteriaC1.getCompositeC2(), criteriaC1.getOp()) : check(file, criteriaC1);
        boolean result2 = criteriaC2.isCompos() ? recCompare(file, criteriaC2.getCompositeC1(), criteriaC2.getCompositeC2(), criteriaC2.getOp()) : check(file, criteriaC2);
        return composCheck(result1, result2, lop);
    }


    /**
     * receive a File object and a simple Criteria object
     * check whether a file satisfies certain criterion by comparing each attribute of the file and the simple Criteria object.
     *
     * @param file a file object that needs to be checked.
     * @param criteria an existing Criteria object that is used as the criterion.
     * @return returns a boolean value by comparing each attribute of the file and the simple Criteria object.
     */
    public boolean check(File file, Criteria criteria) {
        if (Objects.equals(criteria.getCriName(), "IsDocument")) return file instanceof Document;
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

    /**
     * get the boolean value of whether the file object satisfies the compositeC1 and compositeC2 of certain composite criterion.
     * get the logic operator that connects compositeC1 and compositeC2, check whether that (compositeC1 lop compositeC2) is true
     *
     * @param result1 the truth value of whether the file object satisfies the simple criteria compositeC1.
     * @param result2 the truth value of whether the file object satisfies the simple criteria compositeC2.
     * @param lop logic operator connects compositeC1 and compositeC2.
     * @return returns the truth value of (compositeC1 lop compositeC2).
     */
    public boolean composCheck(boolean result1, boolean result2, String lop)
    {
        boolean out = false;
        switch (lop) {
            case "&&":
                out = result1 && result2;
            case "||":
                out = result1 || result2;
        }
        return out;
    }

    /**
     * override the toString method, get criteria to be the String.
     * @return String, the criteria's String
     */
    @Override
    public String toString() {
        String end = "";
        if (this.isCompos()) {
            end += "(";
            end += this.getCompositeC1().toString();
            end += (this.getOp() + " ");
            end += this.getCompositeC2().toString();
            end += ")";
        } else {
            end += (this.getAttrName() + " ");
            end += (this.getOp() + " ");
            end += (this.getVal() + " ");
        }
        return end;
    }

    /**
     * activate function
     * @return String with all criteria attribute, including criteria name.
     */
    public String to(){
        if(this.getCriName().equals("IsDocument")) return this.getCriName();
        return this.getCriName() + " " +this.toString()+"\n";
    }


}