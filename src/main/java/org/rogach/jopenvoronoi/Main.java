package org.rogach.jopenvoronoi;

import java.awt.geom.Point2D;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        VoronoiDiagram vd = new VoronoiDiagram();

        List<Point2D> polygon = RandomPolygon.generate_polygon(100);

        long stt = System.currentTimeMillis();
        List<Vertex> vs = new ArrayList<>();
        for (Point2D p : polygon) {
            vs.add(vd.insert_point_site(new Point(p.getX(), p.getY())));
        }

        for (int q = 0; q < vs.size() - 1; q++) {
            vd.insert_line_site(vs.get(q), vs.get(q+1));
        }
        vd.insert_line_site(vs.get(vs.size()-1), vs.get(0));

        long end = System.currentTimeMillis();
        System.out.printf("elapsed %d ms\n", end - stt);

        vd.check();

        vd.filter(new MedialAxisFilter());

        SvgOutput.output(vd, "medial_axis_test.svg");
    }
}
