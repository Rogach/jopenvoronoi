package org.rogach.jopenvoronoi;

import java.awt.geom.Point2D;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (new File("failure.txt").exists()) {
            VoronoiDiagram vd = processPolygon(RandomPolygon.readFromText("failure.txt"));
            SvgOutput.output(vd, "medial_axis.svg");
        } else {
            for (int i = 0; i < 3000000; i++) {
                if (i % 1000 == 0) {
                    System.out.printf("test %d\n", i);
                }

                List<Point2D> polygon = RandomPolygon.generate_polygon(5);
                try {
                    processPolygon(polygon);
                } catch (Exception e) {
                    RandomPolygon.writeToText("failure.txt", polygon);
                    RandomPolygon.writeToSvg("failure.svg", polygon);
                    throw e;
                }
            }
        }
    }
    public static VoronoiDiagram processPolygon(List<Point2D> polygon) {
        VoronoiDiagram vd = new VoronoiDiagram();
        List<Vertex> vs = new ArrayList<>();
        for (Point2D p : polygon) {
            vs.add(vd.insert_point_site(new Point(p.getX(), p.getY())));
        }

        for (int q = 0; q < vs.size() - 1; q++) {
            vd.insert_line_site(vs.get(q), vs.get(q+1));
        }
        vd.insert_line_site(vs.get(vs.size()-1), vs.get(0));
        return vd;
    }
}
