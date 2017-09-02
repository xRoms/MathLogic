import java.util.ArrayList;

/**
 * Created by xRoms on 06.06.2016.
 */
public class Negate extends Expression {
    public Negate(Expression left) {
        args = new ArrayList<>(); args.add(left);
    }
    public String evaluate() {
        return "!(" + args.get(0).evaluate() + ")";
    }
    public String getOp() {
        return "Negate";
    }
    public boolean equals(Expression a) {
        if (!(a instanceof Negate)) {
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
