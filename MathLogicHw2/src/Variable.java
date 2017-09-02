import java.util.ArrayList;

/**
 * Created by xRoms on 06.06.2016.
 */
public class Variable extends Expression {
    String s;
    public Variable(String s) {
        args = new ArrayList<>();
        this.s = s;
    }
    public String evaluate() {
        return s;
    }
    public String getOp() {
        return "Var";
    }
    public boolean equals(Expression a) {
        if (!(a instanceof Variable)) {
            return false;
        }
        if (!s.equals(((Variable) a).s)) {
            return false;
        }
        return true;
    }

}
