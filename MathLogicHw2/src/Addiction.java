import java.util.ArrayList;

/**
 * Created by xRoms on 19.02.2017.
 */
public class Addiction extends Expression {
    public Addiction(Expression left, Expression right) {
        args = new ArrayList<>();
        args.add(left);
        args.add(right);
    }
    public String evaluate () {
        return "(" + args.get(0).evaluate() + ")+(" + args.get(1).evaluate() + ")";
    }
    public String getOp() {
        return "Add";
    }

    public boolean equals(Expression a) {
        if (!getOp().equals(a.getOp())) {
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
