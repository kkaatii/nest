package photon.tube.action;

/**
 * Created by Dun Liu on 2/23/2017.
 */
public class ActionTest {

    static class ZeroProvider extends Action<Void, Integer> {
        public ZeroProvider(ActionManager manager) {
            super(manager);
        }

        @Override
        public Integer doRun(Void _void) {
            try {
                System.out.println("Preparing the 0...");
                Thread.sleep(1000L);
                System.out.println("0 prepared after one second");
                return 0;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class Incrementer extends Action<Integer, Integer> {
        public Incrementer(ActionManager manager) {
            super(manager);
        }

        @Override
        public Integer doRun(Integer integer) {
            System.out.println("Got integer: " + integer);
            if (integer < 5) {
                return integer + 1;
            } else throw new RuntimeException("Integer too large!");
        }
    }

    static class AntecedentInspector extends Action<Void, Void> {
        protected AntecedentInspector(ActionManager manager) {
            super(manager);
        }

        @Override
        public void abort() {
            super.abort();
            Action<?, ?> antecedent = antecedent();
            while (antecedent != null) {
                System.out.println(antecedent.status);
                antecedent = antecedent.antecedent();
            }
        }

        @Override
        protected Void doRun(Void input) {
            return null;
        }
    }

    public void test() {
        Action<Void, Integer> zero = new ZeroProvider(ActionManager.INSTANCE);
        Action<Integer, Integer> inc = new Incrementer(ActionManager.INSTANCE);
        ActionListenerAction<Integer> print = new ActionListenerAction<>(ActionManager.INSTANCE, new ActionListener<Integer>() {
            @Override
            public void onSuccess(Integer input) {
                System.out.println("The result is " + input);
            }

            @Override
            public void onFailure() {

            }
        });
        Action<Void, Void> inspector = new AntecedentInspector(ActionManager.INSTANCE);
        inc.waitFor(zero);
        print.waitFor(inc);
        inspector.waitFor(print);
        zero.perform();

        try {
            Thread.sleep(2000L);
        } catch (Exception e) {

        }
    }

}
