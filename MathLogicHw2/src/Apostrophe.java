import java.util.ArrayList;

/**
 * Created by xRoms on 19.02.2017.
 */
public class Apostrophe extends Expression {
    public Apostrophe(Expression left) {
        args = new ArrayList<>();
        args.add(left);
    }
    public String evaluate() {
        return "(" + args.get(0).evaluate() + ")'";
    }
    public String getOp() {
        return "Apostrophe";
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
