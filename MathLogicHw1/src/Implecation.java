/**
 * Created by xRoms on 06.06.2016.
 */
public class Implecation extends BinaryOperation {
    public Implecation(Expression left, Expression right) {super(left, right);}
    protected String evaluate (String left, String right) {
        return "(" + left + ")->(" + right + ")";
    }
    public String getOp() {
        return "Impl";
    }

}
