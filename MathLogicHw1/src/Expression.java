
public interface Expression {
    String evaluate();
    Expression getLeft();
    Expression getRight();
    String getOp();
}
