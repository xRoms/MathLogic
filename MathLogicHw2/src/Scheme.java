import java.util.ArrayList;

/**
 * Created by xRoms on 23.02.2017.
 */
public class Scheme extends Expression {
    String name;
    public Scheme (String name) {
        args = new ArrayList<>(); this.name = name;
    }

    public String evaluate() {
        return "$" + name;
    }

    public String getOp() {
        return "Sch";
    }
    public boolean equals(Expression a) {
        if (!(a instanceof Scheme)) {
            return false;
        }
        if (!name.equals(((Scheme) a).name)) {
            return false;
        }
        return true;
    }
}
