package org.rogach.jopenvoronoi;

import java.awt.geom.Point2D;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        VoronoiDiagram vd = new VoronoiDiagram();

        // Point p0 = new Point(-0.1, -0.2);
        // Point p1 = new Point(0.2, 0.1);
        // Point p2 = new Point(0.4, 0.2);
        // Point p3 = new Point(0.6, 0.6);
        // Point p4 = new Point(-0.6, 0.3);

        // Vertex v0 = vd.insert_point_site(p0);
        // Vertex v1 = vd.insert_point_site(p1);
        // Vertex v2 = vd.insert_point_site(p2);
        // Vertex v3 = vd.insert_point_site(p3);
        // Vertex v4 = vd.insert_point_site(p4);

        // vd.insert_line_site(v0, v1);
        // vd.insert_line_site(v1, v2);
        // vd.insert_line_site(v2, v3);
        // vd.insert_line_site(v3, v4);
        // vd.insert_line_site(v4, v0);

        List<Point2D> polygon = RandomPolygon.generate_polygon(320);

        List<Vertex> vs = new ArrayList<>();
        for (Point2D p : polygon) {
            vs.add(vd.insert_point_site(new Point(p.getX(), p.getY())));
        }

        for (int q = 0; q < vs.size() - 1; q++) {
            vd.insert_line_site(vs.get(q), vs.get(q+1));
        }
        vd.insert_line_site(vs.get(vs.size()-1), vs.get(0));

        vd.check();

        vd.filter(new MedialAxisFilter());

        SvgOutput.output(vd, "medial_axis_test.svg");
    }
}
