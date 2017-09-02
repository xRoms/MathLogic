
public class Variable implements Expression {
    String s;
    public Variable(String s) {
        this.s = s;
    }
    public String evaluate() {
        return s;
    }
    public String getOp() {
        return "Var";
    }
    public Expression getLeft() {
        return null;
    }
    public Expression getRight() {
        return null;
    }
}
