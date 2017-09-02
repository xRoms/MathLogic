import java.util.ArrayList;

/**
 * Created by xRoms on 06.06.2016.
 */
abstract public class Expression {
    ArrayList <Expression> args;
    public abstract String evaluate();
    public ArrayList<Expression> getArgs() {
        return args;
    }
    public abstract String getOp();
    public abstract boolean equals(Expression a);
}
