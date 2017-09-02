import java.util.ArrayList;

/**
 * Created by xRoms on 19.02.2017.
 */
public class Zero extends Expression {
    public Zero () { args = new ArrayList<>();}
    public String evaluate() {
        return "0";
    }
    public String getOp() {
        return "Zero";
    }
    public boolean equals(Expression a) {
        if (a instanceof Zero) {
            return true;
        }
        return false;
    }
}
