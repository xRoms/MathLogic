import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by xRoms on 17.10.2016.
 */


public class NewSolve {

    static String operations[] = {"->", "|", "&"};
    static Class operationsClass[] = {Implication.class, Disjunction.class, Conjunction.class};
    static String termoperations[] = {"+", "*"};
    static Class termoperationClass[] = {Addiction.class, Multiplication.class};


    private static boolean VariableCheck(Character c) {
        return (('0' <= c) && (c <= '9')) || (('a' <= c) && (c <= 'z'));
    }

    private static Expression OperationsParse(String s, int level) {
        if (level >= operations.length) {
            return UnaryParse(s);
        }
        String find = operations[level];
        Class operation = operationsClass[level];
        int balance = 0;
        int position = -1;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                balance++;
            }
            if (s.charAt(i) == ')') {
                balance--;
            }
            boolean cool = false;
            if (balance == 0 && (i + find.length() < s.length())) {
                cool = true;
                for (int j = 0; j < find.length(); j++) {
                    if (s.charAt(i + j) != find.charAt(j)) {
                        cool = false;
                    }
                }
            }
            if (cool) {
                position = i;
                if (level == 0) {
                    break;
                }
            }
        }
        if (position == -1) {
            return OperationsParse(s, level + 1);
        } else {
            Expression left, right;
            if (level != 0) {
                left = OperationsParse(s.substring(0, position), level);
                right = OperationsParse(s.substring(position + 1, s.length()), level + 1);
            } else {
                left = OperationsParse(s.substring(0, position), level + 1);
                right = OperationsParse(s.substring(position + 2, s.length()), level);
            }
            try {
                return (Expression) operation.getConstructor(Expression.class, Expression.class).newInstance(left, right);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                System.out.println("WTF");
                return null;
            }
        }
    }

    public static Expression ExpressionParse(String s) {
        return OperationsParse(s, 0);
    }

    private static Expression UnaryParse(String s) {

        if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
            int balance = 1;
            boolean ex = false;
            for (int i = 1; i < s.length() - 1; i++) {
                if (s.charAt(i) == '(') {
                    balance++;
                }
                if (s.charAt(i) == ')') {
                    balance--;
                }
                if (balance == 0) {
                    ex = true;
                }
            }
            if (!ex) {
                return ExpressionParse(s.substring(1, s.length() - 1));
            }
        }
        if (s.charAt(0) == '!') {
            return new Negate(UnaryParse(s.substring(1, s.length())));
        }
        if (s.charAt(0) == '$') {
            return new Scheme(s.substring(1, s.length()));
        }
        if ((s.charAt(0) == '@') || (s.charAt(0) == '?')) {
            String var = "";
            int iter = 1;
            while (VariableCheck(s.charAt(iter))) {
                var += s.charAt(iter);
                iter++;
            }
            if (s.charAt(0) == '@') {
                return new Universal(var, UnaryParse(s.substring(iter, s.length())));
            } else {
                return new Existential(var, UnaryParse(s.substring(iter, s.length())));
            }
        }
        return PredicateParse(s);
    }

    private static Expression PredicateParse(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '=') {
                return new Equalation(TermOperationParse(s.substring(0, i), 0), TermOperationParse(s.substring(i + 1, s.length()), 0));
            }
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                ArrayList<Expression> args = new ArrayList<Expression>();
                String name = s.substring(0, i);
                int lasti = i + 1;
                int iter = i + 1;
                int balance = 0;
                while (iter < s.length()) {
                    if (s.charAt(iter) == '(') {
                        balance++;
                    }
                    if (s.charAt(iter) == ')') {
                        balance--;
                    }
                    if (((s.charAt(iter) == ',') && (balance == 0)) || ((s.charAt(iter) == ')') && (iter == (s.length() - 1)))) {
                        args.add(TermOperationParse(s.substring(lasti, iter), 0));
                        lasti = iter + 1;
                    }
                    iter++;
                }
                return new Predicat(name, args);
            }
        }
        return new Predicat(s, new ArrayList<Expression>());
    }

    private static Expression TermOperationParse(String s, int level) {
        if (level >= termoperations.length) {
            return UnaryTermParse(s);
        }
        String find = termoperations[level];
        Class operation = termoperationClass[level];
        int balance = 0;
        int position = -1;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                balance++;
            }
            if (s.charAt(i) == ')') {
                balance--;
            }
            boolean cool = false;
            if (balance == 0 && (i + find.length() < s.length())) {
                cool = true;
                for (int j = 0; j < find.length(); j++) {
                    if (s.charAt(i + j) != find.charAt(j)) {
                        cool = false;
                    }
                }
            }
            if (cool) {
                position = i;
                break;
            }
        }
        if (position == -1) {
            return TermOperationParse(s, level + 1);
        } else {
            Expression left = TermOperationParse(s.substring(0, position), level + 1);
            Expression right = TermOperationParse(s.substring(position + 1, s.length()), level);
            try {
                return (Expression) operation.getConstructor(Expression.class, Expression.class).newInstance(left, right);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                System.out.println("WTF");
                return null;
            }
        }
    }

    private static Expression UnaryTermParse(String s) {
        if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
            int balance = 1;
            boolean ex = false;
            for (int i = 1; i < s.length() - 1; i++) {
                if (s.charAt(i) == '(') {
                    balance++;
                }
                if (s.charAt(i) == ')') {
                    balance--;
                }
                if (balance == 0) {
                    ex = true;
                }
            }
            if (!ex) {
                return TermOperationParse(s.substring(1, s.length() - 1), 0);
            }
        }
        if (s.charAt(s.length() - 1) == '\'') {
            return new Apostrophe(UnaryTermParse(s.substring(0, s.length() - 1)));
        }
        if (s.charAt(0) == '0') {
            return new Zero();
        }
        if (s.charAt(s.length() - 1) == ')') {
            int iter = 0;
            while (VariableCheck(s.charAt(iter))) {
                iter++;
            }
            ArrayList<Expression> args = new ArrayList<>();
            String name = s.substring(0, iter);
            iter++;
            int lasti = iter;
            int balance = 1;
            while (iter < s.length()) {
                if (s.charAt(iter) == '(') {
                    balance++;
                }
                if (s.charAt(iter) == ')') {
                    balance--;
                }
                if ((balance == 0) && ((s.charAt(iter) == ',') || (s.charAt(iter) == ')'))) {
                    args.add(TermOperationParse(s.substring(lasti, iter), 0));
                    lasti = iter + 1;
                }
                iter++;
            }
            return new Term(name, args);
        }
        return new Variable(s);
    }

    public static void main(String[] args) throws Exception {
        FileWriter writer = new FileWriter("output.txt");
        File f = new File("input.txt");
        BufferedReader in = new BufferedReader(new FileReader(f));
        String sups = in.readLine();
        Expression alpha;
        ArrayList<Expression> suppos = new ArrayList<>();
        int iter = 1;
        int last = 0;
        int balance = 0;
        while (sups.charAt(iter - 1) != '|') {
            if (sups.charAt(iter) == '(') {
                balance++;
            }
            if (sups.charAt(iter) == ')') {
                balance--;
            }
            if ((balance == 0) && (sups.charAt(iter) == ',' || sups.charAt(iter) == '|')) {
                alpha = ExpressionParse(sups.substring(last, iter));
                suppos.add(alpha);
                last = iter + 1;
            }
            iter++;
        }
        ArrayList<Expression> proof = new ArrayList<>();
        String cur = in.readLine();
        int cnt = 0;
        while (cur != null) {
            if (cur.equals("")) {
                cur = in.readLine();
                continue;
            }
            System.err.println(cur);
            cnt++;
            proof.add(ExpressionParse(cur));
            cur = in.readLine();
        }
        ProofComplete q = new ProofComplete(proof, suppos);

        String res = q.correct();
        if (!res.equals("ok")) {
            System.err.println(res);
            return;
        }

        ArrayList<Expression> mainproof = q.execute();
        for (Expression w: mainproof) {
            writer.write(w.evaluate() + "\n");
        }
        writer.close();
        in.close();
    }
}


