import java.util.ArrayList;

/**
 * Created by xRoms on 06.06.2016.
 */
public class Disjunction extends Expression {
    public Disjunction(Expression left, Expression right) { args = new ArrayList<>(); args.add(left); args.add(right);}
    public ArrayList<Expression> getArgs () { return args; }
    public String evaluate () {
        return "(" + args.get(0).evaluate() + ")|(" + args.get(1).evaluate() + ")";
    }
    public String getOp() {
        return "Disj";
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
