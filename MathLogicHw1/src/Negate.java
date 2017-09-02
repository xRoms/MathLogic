
public class Negate implements Expression {
    Expression left;
    public Negate(Expression left) {
        this.left = left;
    }
    public String evaluate() {
        return "!(" + left.evaluate() + ")";
    }
    public String getOp() {
        return "Negate";
    }
    public Expression getLeft() {
        return left;
    }
    public Expression getRight() {
        return left;
    }
}
