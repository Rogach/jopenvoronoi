package org.rogach.jopenvoronoi;

import java.util.*;

public class BenchmarkRandomPoints extends Benchmark {
    public static void main(String[] args) {
        new BenchmarkRandomPoints().execute();
    }

    List<Point> points;
    public void prepare(int num_points) {
        points = new ArrayList<>(num_points);
        for (int i = 0; i < num_points; i++) {
            points.add(random_point());
        }
    }
    public void run(int num_points) {
        construct_voronoi_diagram(points);
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
