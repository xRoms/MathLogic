import java.io.*;
import java.util.Scanner;

/**
 * Created by xRoms on 08.05.2017.
 */
public class Proof {
    static String gn(int a) {
        String ret = "0";
        for (int i = 0; i < a; ++i) {
            ret += "'";
        }
        return ret;
    }


    private static void reverse(String a, String b) {
        System.out.println("@a@b(b=a->a=b)->@b(b=(" + b + ")->(" + b + ")=b)");
        System.out.println("@b(b=(" + b + ")->(" + b + ")=b)");
        System.out.println("@b(b=(" + b + ")->(" + b + ")=b)->((" + a + ")=(" + b + ")->(" + b + ")=(" + a + "))");
        System.out.println("((" + a + ")=(" + b + ")->(" + b + ")=(" + a + "))");
        System.out.println("(" + b + ")=(" + a + ")");
    }


    private static void generateLessOrEqual(int a, int b) {
        System.out.println("|-?p(" + gn(a) + "+p=" + gn(b) + ")");
        try {
            BufferedReader header = new BufferedReader(new FileReader("header"));
            String str = header.readLine();
            while (str != null) {
                System.out.println(str);
                str = header.readLine();
            }
        } catch (FileNotFoundException e) {return;} catch (IOException x) {
            System.err.println(x.getMessage());
        }


        System.out.println("@a(a+0=a)->(" + gn(a) + "+0=" + gn(a) + ")");
        System.out.println(gn(a) + "+0=" + gn(a));
        for (int i = 0; i < b - a; ++i) {
            String sa = "(" + gn(a) + "+" + gn(i) + ")'";
            String sb = gn(a + i + 1);
            String sc = gn(a) + "+" + gn(i + 1);
            System.out.println("@a@b(a=b->a'=b')->@b(" + gn(a) + "+" + gn(i) + "=b->" + sa + "=b')");
            System.out.println("@b(" + gn(a) + "+" + gn(i) + "=b->" + sa + "=b')");
            System.out.println("@b(" + gn(a) + "+" + gn(i) + "=b->" + sa + "=b')->(" + gn(a) + "+" + gn(i) + "=" + gn(a + i) + "->" + sa + "=" + sb + ")");
            System.out.println(gn(a) + "+" + gn(i) + "=" + gn(a + i) + "->" + sa + "=" + sb);
            System.out.println(sa + "=" + sb);

            System.out.println("@a@b(a+b'=(a+b)')->@b(" + gn(a) + "+b'=(" + gn(a) + "+b)'" + ")");
            System.out.println("@b(" + gn(a) + "+b'=(" + gn(a) + "+b)'" + ")");
            System.out.println("@b(" + gn(a) + "+b'=(" + gn(a) + "+b)'" + ")->" + sc + "=" + sa);
            System.out.println(sc + "=" + sa);
            reverse(sc, sa);

            System.out.println("@a@b@c(a=b->a=c->b=c)->@b@c(" + sa + "=b->" + sa + "=c->b=c)");
            System.out.println("@b@c(" + sa + "=b->" + sa + "=c->b=c)");
            System.out.println("@b@c(" + sa + "=b->" + sa + "=c->b=c)->@c(" + sa + "=" + sb + "->" + sa + "=c->" + sb + "=c)");
            System.out.println("@c(" + sa + "=" + sb + "->" + sa + "=c->" + sb + "=c)");
            System.out.println("@c(" + sa + "=" + sb + "->" + sa + "=c->" + sb + "=c)->" + sa + "=" + sb + "->" + sa + "=" + sc + "->" + sb + "=" + sc);
            System.out.println(sa + "=" + sb + "->" + sa + "=" + sc + "->" + sb + "=" + sc);
            System.out.println(sa + "=" + sc + "->" + sb + "=" + sc);
            System.out.println(sb + "=" + sc);
            reverse(sb, sc);
        }
        System.out.println(gn(a) + "+" + gn(b - a) + "=" + gn(b) + "->?p(" + gn(a) + "+p=" + gn(b) + ")");
        System.out.println("?p(" + gn(a) + "+p=" + gn(b) + ")");
    }


    private static void generateNotLessOrEqual(int na, int nb) {
        System.out.println("|-@p(!(p+" + gn(na) + "=" + gn(nb) + "))");
        try {
            BufferedReader header = new BufferedReader(new FileReader("header"));
            String str = header.readLine();
            while (str != null) {
                System.out.println(str);
                str = header.readLine();
            }
        } catch (FileNotFoundException e) {return;} catch (IOException x) {
            System.err.println(x.getMessage());
        }


        String a = "(p+" + gn(na - nb - 1) + ")'";
        System.out.println("@a(!(a'=0))->(!(" + a + "=0))");
        System.out.println("(!(" + a + "=0))");

        String b = "0";
        for (int i = 0; i < nb; ++i) {
            System.out.println("@a@b(!(a=b)->!(a'=b'))->@b(!(" + a + "=b)->!(" + a + "'=b'))");
            System.out.println("@b(!(" + a + "=b)->!(" + a + "'=b'))");
            System.out.println("@b(!(" + a + "=b)->!(" + a + "'=b'))->(!(" + a + "=" + b + ")->!(" + a + "'=" + b + "'))");
            System.out.println("(!(" + a + "=" + b + ")->!(" + a + "'=" + b + "'))");
            System.out.println("!(" + a + "'=" + b + "')");
            a += "'";
            b += "'";
        }

        a = "(p+" + gn(na - nb - 1) + ")'";
        b = "p+" + gn(na - nb - 1) + "'";
        System.out.println("@a@b((a+b)'=(a+b'))->@b((p+b)'=(p+b'))");
        System.out.println("@b((p+b)'=(p+b'))");
        System.out.println("@b((p+b)'=(p+b'))->(" + a + "=" + b + ")");
        System.out.println("(" + a + "=" + b + ")");
        for (int i = 0; i < nb; ++i) {
            System.out.println("@a@b(a=b->b=a)->@b((" + a + "=b)->(b=" + a + "))");
            System.out.println("@b((" + a + "=b)->(b=" + a + "))");
            System.out.println("@b((" + a + "=b)->(b=" + a + "))->((" + a + "=" + b +")->("+ b + "=" + a + "))");
            System.out.println("((" + a + "=" + b +")->("+ b + "=" + a + "))");
            System.out.println("("+ b + "=" + a + ")");

            System.out.println("@a@b((a=b)->(a'=b'))->@b((" + b + "=b)->((" + b + ")'=b'))");
            System.out.println("@b((" + b + "=b)->((" + b + ")'=b'))");
            System.out.println("@b((" + b + "=b)->((" + b + ")'=b'))->((" + b + "=" + a + ")->((" + b + ")'=" + a + "'))");
            System.out.println("((" + b + "=" + a + ")->((" + b + ")'=" + a + "'))");
            System.out.println("((" + b + ")'=" + a + "')");

            System.out.println("@a@b((a+b)'=(a+b'))->@b((p+b)'=(p+b'))");
            System.out.println("@b((p+b)'=(p+b'))");
            System.out.println("@b((p+b)'=(p+b'))->((" + b + ")'=" + b + "')");
            System.out.println("((" + b + ")'=" + b + "')");

            System.out.println("@a@b@c((a=b)->(a=c)->(b=c))->@b@c(((" + b + ")'=b)->((" + b + ")'=c)->(b=c))");
            System.out.println("@b@c(((" + b + ")'=b)->((" + b + ")'=c)->(b=c))");
            System.out.println("@b@c(((" + b + ")'=b)->((" + b + ")'=c)->(b=c))->@c(((" + b + ")'=" + a + "')->((" + b + ")'=c)->(" + a + "'=c))");
            System.out.println("@c(((" + b + ")'=" + a + "')->((" + b + ")'=c)->(" + a + "'=c))");
            System.out.println("@c(((" + b + ")'=" + a + "')->((" + b + ")'=c)->(" + a + "'=c))->(((" + b + ")'=" + a + "')->((" + b + ")'=" + b + "')->(" + a + "'=" + b + "'))");
            System.out.println("(((" + b + ")'=" + a + "')->((" + b + ")'=" + b + "')->(" + a + "'=" + b + "'))");
            System.out.println("((" + b + ")'=" + b + "')->(" + a + "'=" + b + "')");
            System.out.println("(" + a + "'=" + b + "')");
            a += "'";
            b += "'";
        }
        a = "(p+" + gn(na - nb - 1) + ")";
        for (int i = 0; i < nb + 1; ++i) a += "'";
        b = "p+" + gn(na);
        String c = gn(nb);

        System.out.println("@a@b@c((a=b)->(!(a=c))->(!(b=c)))->@b@c((" + a + "=b)->(!(" + a + "=c))->(!(b=c)))");
        System.out.println("@b@c((" + a + "=b)->(!(" + a + "=c))->(!(b=c)))");
        System.out.println("@b@c((" + a + "=b)->(!(" + a + "=c))->(!(b=c)))->@c((" + a + "=" + b + ")->(!(" + a + "=c))->(!(" + b + "=c)))");
        System.out.println("@c((" + a + "=" + b + ")->(!(" + a + "=c))->(!(" + b + "=c)))");
        System.out.println("@c((" + a + "=" + b + ")->(!(" + a + "=c))->(!(" + b + "=c)))->((" + a + "=" + b + ")->(!(" + a + "=" + c + "))->(!(" + b + "=" + c + ")))");
        System.out.println("((" + a + "=" + b + ")->(!(" + a + "=" + c + "))->(!(" + b + "=" + c + ")))");
        System.out.println("(!(" + a + "=" + c + "))->(!(" + b + "=" + c + "))");
        System.out.println("!(" + b + "=" + c + ")");
        String ab = "!(" + b + "=" + c + ")";
        System.out.println(ab + "->((0=0)->(0=0)->(0=0))->" + ab);
        System.out.println("((0=0)->(0=0)->(0=0))->" + ab);
        System.out.println("((0=0)->(0=0)->(0=0))->@p(" + ab + ")");
        System.out.println("@p(" + ab + ")");
    }


    public static void main(String args[]) throws  FileNotFoundException {
        Scanner in = new Scanner(new File("input.txt"));
        System.setOut(new PrintStream("output.txt"));
        int a = in.nextInt();
        int b = in.nextInt();
        if (a <= b) {
            generateLessOrEqual(a, b);
        }
        else {
            generateNotLessOrEqual(a, b);
        }
    }
}
