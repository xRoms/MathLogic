import java.io.*;
import java.util.*;

/**
 * Created by xRoms on 17.10.2016.
 */
public class Solve {

    static int cntmid;
    static Expression midarr[] = new Expression[5000];
    static int cntmiddle;
    static Expression middlearr[] = new Expression[5000];

    private static boolean check(Expression a, Expression b) {
        Map<Character, String> checker = new HashMap<Character, String>();
        Queue<Expression> aq= new ArrayDeque<Expression>(), bq = new ArrayDeque<Expression>();

        aq.add(a);
        bq.add(b);
        Expression f, s;
        while (!aq.isEmpty()) {
            f = aq.poll();
            s = bq.poll();
            if ((f != null) ^ (s != null)) {
                return false;
            }
            if ((f == null) && (s == null)) {
               continue;
            }
            if (f.getOp() == "Var") {
                if (checker.containsKey(f.evaluate().charAt(0))) {
                    if (!checker.get(f.evaluate().charAt(0)).equals(s.evaluate())) {
                        return false;
                    }
                }
                else {
                    checker.put(f.evaluate().charAt(0), s.evaluate());
                    continue;
                }
                continue;
            }
            if (f.getOp() != s.getOp()) {
                return false;
            }
            aq.add(f.getLeft());
            bq.add(s.getLeft());
            if (f.getOp() == "Negate") {
                continue;
            }
            aq.add(f.getRight());
            bq.add(s.getRight());
        }
        return true;

    }

    private static int cnt = 0;
    private static String curs;
    private static char token;

    private static Expression Parse(String s) {
        cnt = 0;
        curs = s;
        nextToken();
        return  lowPriority();
    }

    private static void nextToken() {
        if (cnt == curs.length()) {
            token = '~';
            return;
        }
        token = curs.charAt(cnt);
        cnt++;
        return;
    }

    private static Expression lowPriority() {
        cntmid = 0;
        Expression left = midPriority();
        if (cntmid != 0) {
            Expression left2 = left;
            int qq = 0;
            while (cntmid != qq) {
                midarr[qq] = left2.getLeft();
                left2 = left2.getRight();
                qq++;
            }
            midarr[cntmid] = left2;
            qq = 1;
            left = midarr[0];
            while (cntmid >= qq) {
                left = new Disjunction(left, midarr[qq]);
                qq++;
            }
            cntmid = 0;
        }
        if (token == '-') {
            nextToken();
            nextToken();
            left = new Implecation(left, lowPriority());
        }

        return left;
    }

    private static Expression midPriority() {
        cntmiddle = 0;
        Expression left = middlePriority();
        if (cntmiddle != 0) {
            Expression left2 = left;
            int qq = 0;
            while (cntmiddle != qq) {
                middlearr[qq] = left2.getLeft();
                left2 = left2.getRight();
                qq++;
            }
            middlearr[cntmiddle] = left2;
            qq = 1;
            left = middlearr[0];
            while (cntmiddle >= qq) {
                left = new Conjunction(left, middlearr[qq]);
                qq++;
            }
            cntmiddle = 0;
        }
        if (token == '|') {
            cntmid++;
            nextToken();
            left = new Disjunction(left, midPriority());
        }
        return left;
    }
    private static Expression middlePriority() {
        Expression left = highPriority();
        if (token == '&') {
            cntmiddle++;
            nextToken();
            left = new Conjunction(left, middlePriority());
        }
        return left;
    }

    private static Expression highPriority() {
        Expression left = new Variable("");
        if (token == '(') {
            nextToken();
            left = lowPriority();
            nextToken();
        }
        else {
            if ((('a' <= token) && (token <= 'z')) || ((('A') <= token) && (token <= 'Z'))) {
                String now = "";
                while ((('a' <= token) && (token <= 'z')) || ((('A') <= token) && (token <= 'Z')) || (('0' <= token) && (token <= '9'))) {
                    now += token;
                    nextToken();
                }
                left = new Variable(now);
            }
            else {
                if (token == '!') {
                    nextToken();
                    left = new Negate(highPriority());
                }
            }
        }
        return left;
    }



    public static void main(String[] args) throws Exception {
        Map<String, Integer> modusint1 = new HashMap<String, Integer>();
        Map<String, Integer> modusint2 = new HashMap<String, Integer>();
        Map<String, Vector<Expression>> modusexp = new HashMap<String, Vector<Expression>>();
        Map<String, Integer> sup = new HashMap<String, Integer>();
        sup.clear();
        File f = new File("input.txt");
        File f2 = new File("output.txt");
        BufferedReader in = new BufferedReader(new FileReader(f));
        BufferedWriter out = new BufferedWriter(new FileWriter(f2));
        String sups = in.readLine();
        out.write(sups+ "\n");
        String axioms_s[] = new String[10];
        Expression axioms[] = new Expression[10];
        axioms_s[0] = "(a)->((b)->(a))";
        axioms_s[1] = "((a)->(b))->((a)->(b)->(c))->((a)->(c))";
        axioms_s[2] = "(a)->(b)->(a)&(b)";
        axioms_s[3] = "(a)&(b)->(a)";
        axioms_s[4] = "(a)&(b)->(b)";
        axioms_s[5] = "(a)->(a)|(b)";
        axioms_s[6] = "(b)->(a)|(b)";
        axioms_s[7] = "((a)->(c))->((b)->(c))->((a)|(b)->(c))";
        axioms_s[8] = "((a)->(b))->((a)->!(b))->!(a)";
        axioms_s[9] = "!!(a)->(a)";

        for (int i = 0; i < 10; i++) {
            axioms[i] = Parse(axioms_s[i]);
            Expression z = axioms[i];
        }

        String cur = "";
        int i = 0;
        int cnt = 1;
        while (sups.charAt(i) != '|') {
            if ((sups.charAt(i) == ',') || ((sups.charAt(i) == '-') && (sups.charAt(i + 1) == '|'))) {
                sup.put(Parse(cur).evaluate(), cnt);
                cur = "";
                cnt++;
            } else {
                cur += sups.charAt(i);
            }
            i++;
        }
        i++;
        String provable = "";
        while (i != sups.length()) {
            provable += sups.charAt(i);
            i++;
        }
        String expression = in.readLine();
        cnt = 0;
        boolean found = false;
        while (expression != null) {
            found = false;
            cnt++;
            Expression exp = Parse(expression);
            if (sup.containsKey(exp.evaluate())) {
                found = true;
                out.write(expression + "Предп. " + sup.get(exp.evaluate()) + "\n");
            }
            for (int j = 0; j < 10; j++) {
                if (check(axioms[j], exp)) {
                    out.write(expression + " Сх. акс. " + (j + 1) + "\n");
                    j = 10;
                    found = true;
                }
            }
            if (modusexp.containsKey(exp.evaluate()) && !found) {
                Vector<Expression> q = modusexp.get(exp.evaluate());
                for (int iter = 0; iter < q.size(); iter++) {
                    if (modusint1.containsKey(q.get(iter).getLeft().evaluate())) {
                        out.write(expression + " M.P. " + modusint1.get(q.get(iter).getLeft().evaluate()) + ", " + modusint2.get(q.get(iter).evaluate()) + "\n");
                        iter = q.size();
                        found = true;
                    }
                }
            }
            if (!modusint1.containsKey(exp.evaluate())) {
                modusint1.put(exp.evaluate(), cnt);
            }
            if (exp.getOp().equals("Impl")) {
                if (modusexp.containsKey(exp.getRight().evaluate())) {
                    Vector<Expression> q = modusexp.get(exp.getRight().evaluate());
                    q.add(exp);
                    modusexp.remove(exp.getRight().evaluate());
                    modusexp.put(exp.getRight().evaluate(), q);
                } else {
                    Vector<Expression> q = new Vector<Expression>();
                    q.add(exp);
                    modusexp.put(exp.getRight().evaluate(), q);
                }
                if (!modusint2.containsKey(exp.evaluate())) {
                    modusint2.put(exp.evaluate(), cnt);
                }
            }
            if (!found) {
                out.write(expression + " Не доказано\n");
            }
            expression = in.readLine();
        }
        out.close();
    }
}

