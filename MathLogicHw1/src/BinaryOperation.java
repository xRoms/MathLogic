
public abstract class BinaryOperation implements Expression {
    private Expression left, right;
    public BinaryOperation(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    protected abstract String evaluate(String left, String right);
    @Override
    public String evaluate() {return evaluate(left.evaluate(), right.evaluate());}

    public Expression getLeft() {return left;};
    public Expression getRight() {return right;};
    public abstract String getOp();


}
