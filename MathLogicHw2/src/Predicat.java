import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by xRoms on 19.02.2017.
 */
public class Predicat extends Expression {
    String name;
    public Predicat(String name, ArrayList<Expression> args) {
        this.name = name;
        this.args = args;
    }
    public String evaluate() {
        String out = name;
        if (args.size() != 0) {
            out += "(";
        }
        for (int i = 0; i < args.size(); i++) {
            if (i != 0) {
                out += ',';
            }
            out += args.get(i).evaluate();
        }
        if (args.size() != 0) {
            out += ")";
        }

        return out;
    }
    public String getOp() {
        return "Predicate";
    }
    public boolean equals(Expression a) {
        if (!(a instanceof Predicat)) {
            return false;
        }
        if (args.size() != a.args.size()) {
            return false;
        }
        if (!name.equals(((Predicat) a).name)) {
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
