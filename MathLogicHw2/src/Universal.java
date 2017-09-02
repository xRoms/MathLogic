import java.util.ArrayList;

/**
 * Created by xRoms on 19.02.2017.
 */
public class Universal extends Expression {
    String variable;
    public Universal(String variable, Expression left) {
        this.variable = variable;
        args = new ArrayList<>();
        args.add(left);
    }
    public String evaluate() {
        return "@" + variable + "(" + args.get(0).evaluate() + ")";
    }
    public String getOp() {
        return "Universal";
    }
    public boolean equals(Expression a) {
        if (!(a instanceof Universal)) {
            return false;
        }
        if (!variable.equals(((Universal) a).variable)) {
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
