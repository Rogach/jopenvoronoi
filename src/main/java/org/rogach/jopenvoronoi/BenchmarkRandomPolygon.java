package org.rogach.jopenvoronoi;

import java.awt.geom.Point2D;
import java.util.*;

public class BenchmarkRandomPolygon extends Benchmark{
    public static void main(String[] args) throws Exception {
        new BenchmarkRandomPolygon().execute();
    }

    List<Point2D> points;
    public void prepare(int num_points) {
        points = RandomPolygon.generate_polygon(num_points);
    }

    public void run(int num_points) {
        try {
            VoronoiDiagram vd = new VoronoiDiagram();
            List<Vertex> vs = new ArrayList<>();
            for (Point2D p : points) {
                vs.add(vd.insert_point_site(new Point(p.getX(), p.getY())));
            }
            for (int q = 0; q < vs.size() - 1; q++) {
                vd.insert_line_site(vs.get(q), vs.get(q+1));
            }
            vd.insert_line_site(vs.get(vs.size()-1), vs.get(0));
        } catch (Exception e) {
            try {
                RandomPolygon.writeToSvg("failure.svg", points);
            } catch (Exception e2) {
                throw e;
            }
            throw e;
        }
    }
}
