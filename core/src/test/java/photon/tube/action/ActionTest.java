package photon.tube.action;

import java.util.Random;

public class ActionTest {

    public double calculatePi(int N) {
        if (N < 0) return Double.NaN;

        double sum = 0;
        Random random1 = new Random();

        for (int j = 0; j < N; j++) {
            double x = random1.nextDouble();
            double y = random1.nextDouble();
            if (x * x + y * y < 1)
                sum++;
        }
        return (sum / N);
    }

    public static class NumSupplier extends Transformation<Void, Integer> {
        private int n;

        public NumSupplier(int n) {
            this.n = n;
        }

        @Override
        public Integer transform(Void _void) {
            return n;
        }

        @Override
        public boolean isImmediate() {
            return true;
        }
    }


}
