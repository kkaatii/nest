package nest;

import java.lang.reflect.Array;
import java.util.*;

import nest.data.*;
import nest.query.GraphContainer;
import nest.util.Stopwatch;

public class TestA {

    public void test1() {

        List<Integer> lst1 = new ArrayList<>();
        List<Integer> lst2 = new LinkedList<>();
        Stopwatch stopwatch = new Stopwatch();
        int LOOP = 10000000;

        stopwatch.start();
        for (int i = 0; i < LOOP; i++) {
            lst1.add(i);
        }
        stopwatch.stop();
        System.out.printf("Running %d addAll() to ArrayList: %dms\n", LOOP, stopwatch.millis());

        stopwatch.start();
        for (int i = 0; i < LOOP; i++) {
            lst2.add(i);
        }
        stopwatch.stop();
        System.out.printf("Running %d addAll() to LinkedList: %dms\n", LOOP, stopwatch.millis());

        long temp = 0;

        stopwatch.start();
        for (int i = 0; i < LOOP; i++) {
            temp += lst1.size();
        }
        stopwatch.stop();
        System.out.printf("Running %d currentIndex() to ArrayList: %dms\n", LOOP, stopwatch.millis());

        stopwatch.start();
        for (int i = 0; i < LOOP; i++) {
            temp += lst2.size();
        }
        stopwatch.stop();
        System.out.printf("Running %d currentIndex() to LinkedList: %dms\n", LOOP, stopwatch.millis());

        stopwatch.start();
        for (Integer i : lst1) {
            temp += i.intValue();
        }
        stopwatch.stop();
        System.out.printf("Running %d get() to ArrayList: %dms\n", LOOP, stopwatch.millis());

        stopwatch.start();
        for (Integer i : lst2) {
            temp += i.intValue();
        }
        stopwatch.stop();
        System.out.printf("Running %d get() to LinkedList: %dms\n", LOOP, stopwatch.millis());
    }

    public void testGraph() {
        GraphContainer g = new GraphContainer();
        int TESTTIMES = 2000;
        Point[] points = new Point[TESTTIMES];

        for (int i = 0; i < TESTTIMES; i++) {
            points[i] = new Point(new Node("" + i, NodeType.NODE));
            points[i].setId(i);
        }
        for (int i = 0; i < TESTTIMES; i++) {
            g.add(points[i], i);
        }

        for (int i = 0; i < TESTTIMES; i++) {
            for (int j = i + 1; j < TESTTIMES; j++) {
                if ((j + i) % 2 == 0)
                    g.addArrow(new Arrow(points[i].getId(), ArrowType.paired, points[j].getId()));
            }
        }

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        g.organize();
        stopwatch.stop();
        System.out.println("Time for organize(): " + stopwatch.millis());

        stopwatch.start();
        List<Arrow> al = g.sliceByRank(TESTTIMES / 2, TESTTIMES - 1).arrows();
        stopwatch.stop();
        System.out.println("Time for sliceByRank(): " + stopwatch.millis());
        System.out.println(al.get(TESTTIMES / 2 - 1).getOrigin());
        al.stream().filter(a -> (a.getTarget() > (TESTTIMES - 10)) && (a.getOrigin() > (TESTTIMES - 10))).forEach(System.out::println);
    }

    public void testList() {
        int TIMES = 5000000;
        Stopwatch stopwatch = new Stopwatch();
        List<Integer> total = new ArrayList<>();

        List<Integer>[] al = (List<Integer>[]) Array.newInstance(ArrayList.class, TIMES);
        for (int i = 0; i < TIMES; i++) {
            al[i] = new ArrayList<>();
        }

        stopwatch.start();
        for (int i = 0; i < TIMES; i++) {
            if (! al[i].isEmpty())
                total.addAll(al[i]);
        }
        stopwatch.stop();
        System.out.println(stopwatch.millis());

        List<Integer> templ = Arrays.asList(200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215);
        for (int i = 0; i < TIMES; i++) {
            al[i].addAll(templ);
        }

        total = new ArrayList<>();
        stopwatch.start();
        for (int i = 0; i < TIMES; i++) {
            if (! al[i].isEmpty())
                total.addAll(al[i]);
        }
        stopwatch.stop();
        System.out.println(stopwatch.millis());
        System.out.println(total.size()+total.get(16));

        total = new ArrayList<>();
        stopwatch.start();
        for (int i = 0; i < TIMES; i++) {
            for (Integer j : al[i])
                total.add(templ.get(j-200));
        }
        stopwatch.stop();
        System.out.println(stopwatch.millis());
        System.out.println(total.size()+total.get(16));
    }

    public static class A {
        public static A get() {
            throw new RuntimeException();
        }
    }

	public static void main(String[] args) {
        int i = Integer.MIN_VALUE;
        System.out.println(i);
    }
}
