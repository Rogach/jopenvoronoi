package org.rogach.jopenvoronoi;

import java.util.*;

public class BenchmarkRandomPoints {
    public static void main(String[] args) throws Exception {
        System.out.println("| Number of points | Number of tests | Time per one test |  usec/n*log2(n)   |");
        int max_points = 10000;
        for (int num_points = 10; num_points <= max_points; num_points *= 10) {
            int num_tests = max_points / num_points;
            long total_elapsed = 0;
            for (int test = 0; test < num_tests; test++) {
                List<Point> points = new ArrayList<>(num_points);
                for (int i = 0; i < num_points; i++) {
                    points.add(random_point());
                }

                long stt = System.nanoTime();
                construct_voronoi_diagram(points);
                long end = System.nanoTime();
                total_elapsed += end - stt;
            }
            total_elapsed /= num_tests;

            double time_per_test = total_elapsed / num_tests;
            double complexity = (time_per_test/1e3)/(num_points*Math.log(num_points)/Math.log(2));
            System.out.printf("| %16s | %15s | %17.3f | %17f |\n",
                              num_points, num_tests, time_per_test / 1e6, complexity);
        }
    }

    static Point random_point() {
        return new Point(Math.random() * 1.4 - 0.7, Math.random() * 1.4 - 0.7);
    }

    static void construct_voronoi_diagram(List<Point> points) {
        VoronoiDiagram vd = new VoronoiDiagram();
        for (Point p : points) {
            vd.insert_point_site(p);
        }
        assert(vd.check());
    }
}
