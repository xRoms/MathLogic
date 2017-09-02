import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by xRoms on 22.02.2017.
 */
public class Term extends Expression {
    String name;
    public Term (String name, ArrayList<Expression> args) {
        this.name = name;
        this.args = args;
    }
    public String evaluate() {
        String out = name + "(";
        for (int i = 0; i < args.size(); i++) {
            if (i != 0) {
                out += ',';
            }
            out += args.get(i).evaluate();
        }
        return out + ")";
    }
    public String getOp() {
        return "Term";
    }
    public boolean equals(Expression a) {
        if (!(a instanceof Term)) {
            return false;
        }
        if (args.size() != a.args.size()) {
            return false;
        }
        if (!name.equals(((Term) a).name)) {
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
