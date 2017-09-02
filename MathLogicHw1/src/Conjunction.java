
public class Conjunction extends BinaryOperation {
    public Conjunction(Expression left, Expression right) {
        super(left, right);
    }
    protected String evaluate (String left, String right) {
        return "(" + left + ")&(" + right + ")";
    }
    public String getOp() {
        return "Conj";
    }
}
