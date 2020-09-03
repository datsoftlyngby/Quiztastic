package examples;

public class StaticExample {
    public static String x = "static";

    public String y = "non-static";

    public static StaticExample createStaticExample(String withString) {
        StaticExample stat = new StaticExample();
        stat.y = withString;
        return stat;
    }

    public static void main(String[] args) {
        System.out.println(StaticExample.x);

        StaticExample a = new StaticExample();
        System.out.println(a.y);

        StaticExample b = new StaticExample();
        b.y = "other";
        System.out.println("a " + a.y);
        System.out.println("b " + b.y);

        b.x = "other";
        System.out.println("a " + a.x);
        System.out.println("b " + b.x);

        StaticExample c = StaticExample.createStaticExample("Fisk");
        System.out.println("c " + c.y);
    }

}
