import java.util.ArrayList;

/**
 * Created by xRoms on 19.02.2017.
 */
public class Existential extends Expression {
    String variable;
    public Existential(String variable, Expression left) {
        this.variable = variable;
        args = new ArrayList<>();
        args.add(left);
    }
    public String evaluate() {
        return "?" + variable + "(" + args.get(0).evaluate() + ")";
    }
    public String getOp() {
        return "Existential";
    }
    public boolean equals(Expression a) {
        if (!(a instanceof Existential)) {
            return false;
        }
        if (!variable.equals(((Existential) a).variable)) {
            return false;
        }
        if (args.size() != a.args.size()) {
            return false;
        }
        for (int i = 0; i < args.size(); i++) {
            if (!args.get(i).equals(a.args.get(i))) {
                return false;
            }
        }
        return true;
    }
}
