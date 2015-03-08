package org.rogach.jopenvoronoi;

import java.util.*;
import org.apache.commons.math3.fitting.*;
import org.apache.commons.math3.analysis.*;

public abstract class Benchmark {

    public void execute() {
        int max_points = 16384;

        System.out.println("| Number of points | Number of tests | Time per one test |");
        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int num_points = 64; num_points <= max_points; num_points += 64) {
            int num_tests = max_points / num_points;
            long total_elapsed = 0;
            for (int test = 0; test < num_tests; test++) {
                prepare(num_points);
                long stt = System.nanoTime();
                run(num_points);
                long end = System.nanoTime();
                total_elapsed += end - stt;
            }
            double time_per_test = (total_elapsed / num_tests) / 1e6;
            obs.add(num_points, time_per_test);
            System.out.printf("| %16s | %15s | %17.3f |\n",
                              num_points, num_tests, time_per_test);
        }

        DisplayableFunction f = new ExponentialFunction();
        SimpleCurveFitter fitter =
            SimpleCurveFitter.create(f, new double[] { 1, 2, 0 }); //.withMaxIterations(100000);
        double[] c = fitter.fit(obs.toList());
        System.out.println(f.toString(c));
    }

    public abstract void prepare(int num_points);
    public abstract void run(int num_points);

    interface DisplayableFunction extends ParametricUnivariateFunction {
        public abstract String toString(double... parameters);
    }
    class ExponentialFunction implements DisplayableFunction {
        public double value(double x, double... parameters) {
            double a = parameters[0];
            double b = parameters[1];
            double c = parameters[2];
            return a * Math.pow(x, b) + c;
        }
        public double[] gradient(double x, double... parameters) {
            double a = parameters[0];
            double b = parameters[1];
            double c = parameters[2];
            double[] g = new double[3];
            g[0] = Math.pow(x, b);
            g[1] = a * Math.pow(x, b) * Math.log(x);
            g[2] = 1.0;
            return g;
        }
        public String toString(double... parameters) {
            return String.format("%.6f * x^%.6f + %.6f", parameters[0], parameters[1], parameters[2]);
        }
    }

}
