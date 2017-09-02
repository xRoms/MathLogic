import javafx.util.Pair;

import java.util.*;

/**
 * Created by xRoms on 15.04.2017.
 */
public class ProofComplete {
    private String axioms_schemas[] = new String[10];
    private Expression exp_axioms_schemas[] = new Expression[10];
    private String axioms[] = new String[10];
    private Expression exp_axioms[] = new Expression[10];

    private HashSet<String> suppos = new HashSet<>();

    private Expression alpha;


    private HashMap<String, String> subsfree = new HashMap<>();

    private Map<String, Integer> modusint1 = new HashMap<String, Integer>();
    private Map<String, Integer> modusint2 = new HashMap<String, Integer>();
    private Map<String, Vector<Expression>> modusexp = new HashMap<String, Vector<Expression>>();
    private Set<String> implementations = new HashSet<>();
    private Map<String, Integer> sup = new HashMap<String, Integer>();
    private int cntmondus = -1;

    private ArrayList<Expression> proofList = new ArrayList<>();

    private void Clear() {
        modusint1 = new HashMap<String, Integer>();
        modusint2 = new HashMap<String, Integer>();
        modusexp = new HashMap<String, Vector<Expression>>();
        implementations = new HashSet<>();
        sup = new HashMap<String, Integer>();
        cntmondus = -1;
    }

    public Expression substitute(Expression q, Expression b, Expression var) {
        Expression a = copy(q);
        for (int i = 0; i < a.args.size(); i++) {
            if (a.args.get(i).equals(var)) {
                a.args.set(i, b);
            }
            else {
                a.args.set(i, substitute(a.args.get(i), b, var));
            }
        }
        return a;
    }

    public Expression copy (Expression a) {
        if (a instanceof Addiction) {
            return new Addiction(copy(a.args.get(0)), copy(a.args.get(1)));
        }
        if (a instanceof Apostrophe) {
            return new Apostrophe(copy(a.args.get(0)));
        }
        if (a instanceof Conjunction) {
            return new Conjunction(copy(a.args.get(0)), copy(a.args.get(1)));
        }
        if (a instanceof Disjunction) {
            return new Disjunction(copy(a.args.get(0)), copy(a.args.get(1)));
        }
        if (a instanceof Equalation) {
            return new Equalation(copy(a.args.get(0)), copy(a.args.get(1)));
        }
        if (a instanceof Existential) {
            return new Existential(((Existential) a).variable, copy(a.args.get(0)));
        }
        if (a instanceof Implication) {
            return new Implication(copy(a.args.get(0)), copy(a.args.get(1)));
        }
        if (a instanceof Multiplication) {
            return new Multiplication(copy(a.args.get(0)), copy(a.args.get(1)));
        }
        if (a instanceof Negate) {
            return new Negate(copy(a.args.get(0)));
        }
        if (a instanceof Predicat) {
            ArrayList<Expression> newargs = new ArrayList<>();
            for (int i = 0; i < a.args.size(); i++) {
                newargs.add(copy(a.args.get(i)));
            }
            return new Predicat(((Predicat) a).name, newargs);
        }
        if (a instanceof Scheme) {
            return new Scheme(((Scheme) a).name);
        }
        if (a instanceof Term) {
            ArrayList<Expression> newargs = new ArrayList<>();
            for (int i = 0; i < a.args.size(); i++) {
                newargs.add(copy(a.args.get(i)));
            }
            return new Term(((Term) a).name, newargs);
        }
        if (a instanceof Universal) {
            return new Universal(((Universal) a).variable, copy(a.args.get(0)));
        }
        if (a instanceof Variable) {
            return new Variable(((Variable) a).s);
        }
        return  new Zero();
    }

    public Pair<Boolean, ArrayList<Integer>> trailx(Expression a, Variable x) {
        boolean exists = false;
        ArrayList<Integer> path = new ArrayList<>();
        for (int i = 0; (i < a.args.size()) && !exists; i++) {
            if (a.args.get(i).equals(x)) {
                exists = true;
                path.add(i);
            }
            else {
                Pair<Boolean, ArrayList<Integer>> got = trailx(a.args.get(i), x);
                if (got.getKey()) {
                    exists = true;
                    path = got.getValue();
                    path.add(i);
                }
            }
        }
        return new Pair<>(exists, path);
    }

    public Pair<Boolean, Expression> onx(Expression a, Expression b, Variable x) {
        Pair<Boolean, ArrayList<Integer>> trailing = trailx(a, x);
        if (!trailing.getKey()) {
            return new Pair<>(false, null);
        }
        for (int i = 0; i < trailing.getValue().size(); i++) {
            int j = trailing.getValue().get(trailing.getValue().size() - 1 - i);
            if (b.args.size() > j) {
                b = b.args.get(j);
            }
            else {
                return new Pair<>(false, null);
            }
        }
        return new Pair<>(true, b);

    }

    private HashSet<String> FreeVariables(Expression a) {
        HashSet<String> result = new HashSet<>();
        if (a instanceof Variable) {
            result.add(a.evaluate());
            return result;
        }
        for (int i = 0; i < a.args.size(); i++) {
            result.addAll(FreeVariables(a.args.get(i)));
        }
        if (a instanceof Universal) {
            result.remove(((Universal) a).variable);
        }
        if (a instanceof Existential) {
            result.remove(((Existential) a).variable);
        }
        return result;
    }

    private Pair<Boolean, HashSet<String>> NotFreeVariables(Expression a, Variable x) {
        HashSet<String> result = new HashSet<>();
        boolean nice = false;
        if (a.equals(x)) {
            return new Pair<>(true, result);
        }
        for (int i = 0; i < a.args.size(); i++) {
            Pair<Boolean, HashSet<String>> vars = NotFreeVariables(a.args.get(i), x);
            if (vars.getKey()) {
                nice = true;
                result.addAll(vars.getValue());
            }
        }
        if (a instanceof Universal) {
            result.add(((Universal) a).variable);
        }
        if (a instanceof Existential) {
            result.add(((Existential) a).variable);
        }
        return new Pair<>(nice, result);
    }

    private boolean isFreeToSubstitute(Expression a, Expression b, Variable x) {
        if (b.equals(x)) {
            return true;
        }
        HashSet<String> bvars = FreeVariables(b);
        HashSet<String> avars = NotFreeVariables(a, x).getValue();

        for (String q: bvars) {
            if (avars.contains(q)) {
                return false;
            }
        }
        return true;
    }

    private boolean CheckAxiom(Expression a, Expression b) {
        Map<Character, String> checker = new HashMap<Character, String>();
        Queue<Expression> aq = new ArrayDeque<Expression>(), bq = new ArrayDeque<Expression>();

        aq.add(a);
        bq.add(b);
        Expression f, s;
        while (!aq.isEmpty()) {
            f = aq.poll();
            s = bq.poll();
            if ((f == null) && (s != null)) {
                return false;
            }
            if ((f == null) && (s == null)) {
                continue;
            }
            if (s.getOp().equals("Var") && !f.getOp().equals("Var")) {
                return false;
            }
            int strt = 0;
            if (s.getOp().equals("Sch") || s.getOp().equals("Var")) {
                if (s.getOp().equals("Sch")) {
                    strt = 1;
                }
                if (checker.containsKey(s.evaluate().charAt(strt))) {
                    if (!checker.get(s.evaluate().charAt(strt)).equals(f.evaluate())) {
                        return false;
                    }
                }
                else {
                    checker.put(s.evaluate().charAt(strt), f.evaluate());
                }
            }
            else {
                if (!s.getOp().equals(f.getOp())) {
                    return false;
                }
                if (s.args.size() != f.args.size()) {
                    return  false;
                }
                for (int i = 0 ; i < f.args.size(); i++) {
                    aq.add(f.args.get(i));
                    bq.add(s.args.get(i));
                }
            }
        }
        return true;

    }

    private boolean isAxiom(Expression exp) {
        for (int i = 0; i < 10; i++) {
            if (CheckAxiom(exp, exp_axioms_schemas[i])) {
                return true;
            }
        }
        for (int i = 0; i < 8; i++) {
            if (CheckAxiom(exp, exp_axioms[i])) {
                return true;
            }
        }
        if (CheckUniversalScheme(exp).getKey()) {
            return true;
        }
        if (CheckExistentialScheme(exp).getKey()) {
            return true;
        }
        return CheckAxiom9(exp);
    }

    private Pair<Boolean, Expression> CheckMondusPondus(Expression exp) {
        if (modusexp.containsKey(exp.evaluate())) {
            Vector<Expression> q = modusexp.get(exp.evaluate());
            for (int iter = 0; iter < q.size(); iter++) {
                if (modusint1.containsKey(q.get(iter).args.get(0).evaluate())) {
                    return new Pair<>(true, q.get(iter));
                }
            }
        }
        return new Pair<>(false, null);
    }

    private boolean CheckAxiom9(Expression exp) {
        if (!(exp instanceof Implication)) {
            return false;
        }
        Expression psi = exp.args.get(1);
        Expression con = exp.args.get(0);
        if (!(con instanceof Conjunction)) {
            return false;
        }
        Expression psi0 = con.args.get(0);
        Expression universal = con.args.get(1);
        if (!(universal instanceof Universal)) {
            return false;
        }
        Variable x = new Variable (((Universal) universal).variable);
        if (!psi0.equals(substitute(psi, new Zero(), x))) {
            return false;
        }
        if (!(universal.args.get(0) instanceof Implication)) {
            return false;
        }
        Expression leftpsi = universal.args.get(0).args.get(0);
        Expression psiaph = universal.args.get(0).args.get(1);
        if (!leftpsi.equals(psi)) {
            return false;
        }
        if (!psiaph.equals(substitute(psi, new Apostrophe(x), x))) {
            return false;
        }
        return true;
    }

    private Pair<Boolean, String> CheckUniversalScheme(Expression exp) {
        if (!(exp instanceof Implication)) {
            return new Pair<>(false, "");
        }
        if (!(exp.args.get(0) instanceof Universal)) {
            return new Pair<>(false, "");
        }
        Variable x = new Variable(((Universal) exp.args.get(0)).variable);
        Expression psi = exp.args.get(0).args.get(0);
        Expression substedpsi = exp.args.get(1);
        Pair<Boolean, Expression> subst = onx(psi, substedpsi, x);

        if ((subst.getValue() == null) && (psi.equals(substedpsi))) {
            return new Pair<>(true, "ok");
        }

        if (substedpsi.equals(substitute(psi, subst.getValue(), x))) {
            if (isFreeToSubstitute(psi, subst.getValue(), x)) {
                return new Pair<>(true, "ok");
            }
            else {
                return new Pair<>(true, "Терм " + subst.getValue().evaluate() + " не свободен для подстановки в формулу " + psi.evaluate() + " вместо переменной " + x.evaluate());
            }
        }

        return new Pair<>(false, "");
    }

    private Pair<Boolean, String> CheckExistentialScheme(Expression exp) {
        if (!(exp instanceof Implication)) {
            return new Pair<>(false, "");
        }
        if (!(exp.args.get(1) instanceof Existential)) {
            return new Pair<>(false, "");
        }
        Variable x = new Variable(((Existential) exp.args.get(1)).variable);
        Expression psi = exp.args.get(1).args.get(0);
        Expression substedpsi = exp.args.get(0);
        Pair<Boolean, Expression> subst = onx(psi, substedpsi, x);
        if ((subst.getValue() == null) && (psi.equals(substedpsi))) {
            return new Pair<>(true, "ok");
        }
        if (substedpsi.equals(substitute(psi, subst.getValue(), x))) {
            if (isFreeToSubstitute(psi, subst.getValue(), x)) {
                return new Pair<>(true, "ok");
            }
            else {
                return new Pair<>(true, "Терм " + subst.getValue().evaluate() + " не свободен для подстановки в формулу " + psi.evaluate() + " вместо переменной " + x.evaluate());
            }
        }
        return new Pair<>(false, "");
    }

    private Pair<Boolean, String> CheckUniversalRule(Expression exp) {
        if (!(exp instanceof Implication)) {
            return new Pair<>(false, "");
        }
        if (!(exp.args.get(1) instanceof Universal)) {
            return new Pair<>(false, "");
        }
        Expression find = new Implication(exp.args.get(0), exp.args.get(1).args.get(0));
        if (implementations.contains(find.evaluate())) {
            Expression phi = exp.args.get(0);
            Expression psi = exp.args.get(1).args.get(0);
            String x = ((Universal) exp.args.get(1)).variable;

            if (FreeVariables(phi).contains(x)) {
                return new Pair<>(true, "Переменная " + x + " входит свободно в формулу " + find.evaluate());
            }
            if (subsfree.containsKey(x)) {
                return new Pair<>(true, "Используется правило с квантором по переменной " + x + ", входящей свободно в допущение " + subsfree.get(x));
            }
            return new Pair<>(true, "ok");
        }
        return new Pair<>(false, "");
    }

    private Pair<Boolean, String> CheckExistentialRule(Expression exp) {
        if (!(exp instanceof Implication)) {
            return new Pair<>(false, "");
        }
        if (!(exp.args.get(0) instanceof Existential)) {
            return new Pair<>(false, "");
        }
        Expression find = new Implication(exp.args.get(0).args.get(0), exp.args.get(1));
        if (implementations.contains(find.evaluate())) {
            Expression phi = exp.args.get(1);
            Expression psi = exp.args.get(0).args.get(0);
            String x = ((Existential) exp.args.get(0)).variable;
            if (FreeVariables(phi).contains(x)) {
                return new Pair<>(true, "Переменная " + x + "входит свободно в формулу " + find.evaluate());
            }
            if (subsfree.containsKey(x)) {
                return new Pair<>(true, "Используется правило с квантором по переменной " + x + ", входящей свободно в допущение " + subsfree.get(x));
            }
            return new Pair<>(true, "ok");
        }
        return new Pair<>(false, "");
    }

    private String Check(Expression expression, int number) {
        if (suppos.contains(expression.evaluate())) {
            return "ok";
        }
        if (CheckMondusPondus(expression).getKey()) {
            return "ok";
        }
        if (isAxiom(expression)) {
            return "ok";
        }

        String problem = "";
        Pair<Boolean, String> res = CheckUniversalScheme(expression);
        if (res.getKey()){
            if (res.getValue().equals("ok")) {
                return "ok";
            }
            else {
                problem = res.getValue();
            }
        }

        res = CheckExistentialScheme(expression);
        if (res.getKey()){
            if (res.getValue().equals("ok")) {
                return "ok";
            }
            else {
                problem = res.getValue();
            }
        }

        res = CheckExistentialRule(expression);
        if (res.getKey()){
            if (res.getValue().equals("ok")) {
                return "ok";
            }
            else {
                problem = res.getValue();
            }
        }

        res = CheckUniversalRule(expression);
        if (res.getKey()){
            if (res.getValue().equals("ok")) {
                return "ok";
            }
            else {
                problem = res.getValue();
            }
        }
        String bad = "Вывод некорректен начиная с формулы номер " + Integer.toString(number);
        return bad + " : " + problem;
    }

    private void AddMondusPondus(Expression exp) {
        cntmondus++;
        if (!modusint1.containsKey(exp.evaluate())) {
            modusint1.put(exp.evaluate(), cntmondus);
        }
        if (exp.getOp().equals("Impl")) {
            if (modusexp.containsKey(exp.args.get(1).evaluate())) {
                Vector<Expression> q = modusexp.get(exp.args.get(1).evaluate());
                q.add(exp);
                modusexp.remove(exp.args.get(1).evaluate());
                modusexp.put(exp.args.get(1).evaluate(), q);
            } else {
                Vector<Expression> q = new Vector<Expression>();
                q.add(exp);
                modusexp.put(exp.args.get(1).evaluate(), q);
            }
            if (!modusint2.containsKey(exp.evaluate())) {
                modusint2.put(exp.evaluate(), cntmondus);
            }
        }
    }

    private void AddImplementations(Expression exp) {
        if (exp instanceof Implication) {
            implementations.add(exp.evaluate());
        }
    }

    private void Add (Expression expression) {
        AddMondusPondus(expression);
        AddImplementations(expression);
    }

    private ArrayList<Expression> Proof (Expression expression) {
        ArrayList<Expression> proof = new ArrayList<>();
        if (isAxiom(expression)) {
            proof.add(expression);
            proof.add(substitute(substitute(exp_axioms_schemas[0], alpha, new Scheme("b")), expression, new Scheme("a")));
            proof.add(new Implication(alpha, expression));
            return proof;
        }
        if (suppos.contains(expression.evaluate())) {
            if (expression.equals(alpha)) {
                Expression a_a_a = substitute(substitute(exp_axioms_schemas[0], alpha, new Scheme("a")), alpha, new Scheme("b"));
                Expression a_aa_a = substitute(substitute(exp_axioms_schemas[0], new Implication(alpha, alpha), new Scheme("b")), alpha, new Scheme("a"));
                proof.add(a_a_a);
                proof.add(a_aa_a);
                proof.add(substitute(substitute(substitute(exp_axioms_schemas[1], new Implication(alpha, alpha), new Scheme("b")), alpha, new Scheme("a")), alpha, new Scheme("c")));
                proof.add((new Implication(a_aa_a, new Implication(alpha, alpha))));
                proof.add((new Implication(alpha, alpha)));
                return proof;
            } else {
                proof.add(expression);
                proof.add(substitute(substitute(exp_axioms_schemas[0], alpha, new Scheme("b")), expression, new Scheme("a")));
                proof.add((new Implication(alpha, expression)));
                return proof;
            }
        }
        if (CheckMondusPondus(expression).getKey()) {
            Expression modusponus = CheckMondusPondus(expression).getValue().args.get(0);
            Expression prooff = substitute(substitute(substitute(exp_axioms_schemas[1], alpha, new Scheme("a")), modusponus, new Scheme("b")), expression, new Scheme("c"));
            proof.add(prooff);
            System.err.println("MP " + prooff.evaluate());
            proof.add(prooff.args.get(1));
            proof.add(prooff.args.get(1).args.get(1));
            return proof;
        }
        if (CheckUniversalRule(expression).getKey()) {
            Expression phi = expression.args.get(0);
            Expression psi = expression.args.get(1).args.get(0);
            Expression conj = new Conjunction(alpha, phi);
            Expression ax = substitute(substitute(exp_axioms_schemas[2], alpha, new Scheme("a")), phi, new Scheme("b"));
            ArrayList<Expression> newsupps = new ArrayList<>();
            ArrayList<Expression> newproofs = new ArrayList<>();
            newsupps.add((new Implication(alpha, new Implication(phi, psi))));
            newsupps.add(conj);
            newproofs.add(conj);
            newproofs.add(substitute(substitute(exp_axioms_schemas[3], alpha,new Scheme("a")), phi, new Scheme("b")));
            newproofs.add(substitute(substitute(exp_axioms_schemas[4], alpha,new Scheme("a")), phi, new Scheme("b")));
            System.err.println("phi psi " + phi.evaluate() + " " + psi.evaluate());
            newproofs.add(alpha);
            newproofs.add(phi);
            System.err.println("phi psi " + phi.evaluate() + " " + psi.evaluate());
            newproofs.add(new Implication(alpha, new Implication(phi, psi)));
            System.err.println("phi psi " + phi.evaluate() + " " + psi.evaluate());
            newproofs.add(new Implication(phi, psi));
            System.err.println("phi psi " + phi.evaluate() + " " + psi.evaluate());
            newproofs.add(psi);
            ProofComplete newpc = new ProofComplete(newproofs, newsupps);
            proof.addAll(newpc.execute());
            newsupps.clear();
            newproofs.clear();
            proof.add(new Implication(conj, expression.args.get(1)));
            newsupps.add(new Implication(conj, expression.args.get(1)));
            newsupps.add(alpha);
            newsupps.add(phi);
            newproofs.add(alpha);
            newproofs.add(phi);
            newproofs.add(ax);
            newproofs.add(ax.args.get(1));
            newproofs.add(ax.args.get(1).args.get(1));
            newproofs.add(new Implication(conj, expression.args.get(1)));
            newproofs.add(expression.args.get(1));
            newpc = new ProofComplete(newproofs, newsupps);
            ArrayList<Expression> newestproofs = newpc.execute();
            newsupps.remove(phi);
            ArrayList<Expression> finalver = (new ProofComplete(newestproofs, newsupps)).execute();
            proof.addAll(finalver);
            return proof;
        }
        if (CheckExistentialRule(expression).getKey()) {
            Expression phi = expression.args.get(1);
            Expression psi = expression.args.get(0).args.get(0);
            Expression impl = new Implication(alpha, new Implication(psi, phi));
            ArrayList<Expression> newsupps = new ArrayList<>();
            ArrayList<Expression> newproofs = new ArrayList<>();
            newsupps.add(impl);
            newsupps.add(psi);
            newsupps.add(alpha);
            newproofs.add(alpha);
            newproofs.add(psi);
            newproofs.add(impl);
            newproofs.add(impl.args.get(1));
            newproofs.add(phi);
            ProofComplete alphaphi = new ProofComplete(newproofs, newsupps);
            ArrayList<Expression> newproofed = alphaphi.execute();
            newsupps.remove(alpha);
            proof.addAll((new ProofComplete(newproofed, newsupps)).execute());
            newproofs.clear();
            newsupps.clear();
            Expression exist = new Implication(expression.args.get(0), new Implication(alpha, phi));
            proof.add(exist);
            newsupps.add(exist);
            newsupps.add(alpha);
            newsupps.add(expression.args.get(0));
            newproofs.add(alpha);
            newproofs.add(expression.args.get(0));
            newproofs.add(exist);
            newproofs.add(exist.args.get(1));
            newproofs.add(phi);

            newproofed = (new ProofComplete(newproofs, newsupps)).execute();
            newsupps.remove(expression.args.get(0));
            proof.addAll((new ProofComplete(newproofed, newsupps)).execute());

            return proof;
        }
        System.err.println(expression.evaluate() + " LOlwtf\n");
        return proof;
    }

    public ProofComplete(ArrayList<Expression> proofs, ArrayList<Expression> substitutions) {
        proofList = proofs;
        suppos.clear();
        System.err.println("ProofComplete ");
        for (Expression i: substitutions) {
            suppos.add(i.evaluate());
            System.err.println(i.evaluate());
        }
        System.err.println(" - ");
        for (Expression i: proofs) {
            System.err.println(i.evaluate());
        }
        System.err.println();
        System.err.println();
        if (substitutions.size() != 0) {
            alpha = substitutions.get(substitutions.size() - 1);
            HashSet<String> frees = FreeVariables(alpha);
            for (String i : frees) {
                subsfree.put(i, alpha.evaluate());
            }
        }
        else {
            alpha = null;
        }

        axioms_schemas[0] = "($a)->(($b)->($a))";
        axioms_schemas[1] = "(($a)->($b))->(($a)->($b)->($c))->(($a)->($c))";
        axioms_schemas[2] = "($a)->($b)->($a)&($b)";
        axioms_schemas[3] = "($a)&($b)->($a)";
        axioms_schemas[4] = "($a)&($b)->($b)";
        axioms_schemas[5] = "($a)->($a)|($b)";
        axioms_schemas[6] = "($b)->($a)|($b)";
        axioms_schemas[7] = "(($a)->($c))->(($b)->($c))->(($a)|($b)->($c))";
        axioms_schemas[8] = "(($a)->($b))->(($a)->!($b))->!($a)";
        axioms_schemas[9] = "!!($a)->($a)";
        for (int i = 0; i < 10; i++) {
            exp_axioms_schemas[i] = NewSolve.ExpressionParse(axioms_schemas[i]);
        }
        axioms[0]  = "a=b->a'=b'";
        axioms[1]  = "a=b->a=c->b=c";
        axioms[2]  = "a'=b'->a=b";
        axioms[3]  = "!a'=0";
        axioms[4]  = "a+b'=(a+b)'";
        axioms[5]  = "a+0=a";
        axioms[6]  = "a*0=0";
        axioms[7]  = "a*b'=a*b+a";
        for (int i = 0; i < 8; i++) {
            exp_axioms[i] = NewSolve.ExpressionParse(axioms[i]);
        }
    }

    public String correct() {
        Clear();
        int cnt = 1;
        for (Expression exp: proofList) {
            if (!Check(exp, cnt).equals("ok")) {
                return Check(exp, cnt);
            }
            Add(exp);
            cnt++;
        }
        return "ok";
    }


    public ArrayList<Expression> execute() {
        if (alpha == null) {
            return proofList;
        }
        Clear();
        ArrayList<Expression> newproof = new ArrayList<>();
        for (Expression i: proofList) {
            newproof.addAll(Proof(i));
            Add(i);
        }
        return newproof;

    }

}
