package org.rogach.jopenvoronoi;

import java.io.*;
import java.util.*;
import java.awt.geom.Point2D;

public class EuclideanInput {
    public List<Point2D> points;
    public Map<Point2D, Point2D> segments;

    public EuclideanInput(List<Point2D> points, Map<Point2D, Point2D> segments) {
        this.points = points;
        this.segments = segments;
    }

    public static EuclideanInput fromPolygon(List<Point2D> points) {
        Map<Point2D, Point2D> segments = new HashMap<Point2D, Point2D>();
        for (int q = 0; q < points.size(); q++) {
            if (q != points.size() - 1) {
                segments.put(points.get(q), points.get(q+1));
            } else {
                segments.put(points.get(points.size()-1), points.get(0));
            }
        }
        return new EuclideanInput(points, segments);
    }

    public VoronoiDiagram buildVoronoiDiagram() {
        Map<Point2D, Vertex> vertices = new HashMap<>();
        VoronoiDiagram vd = new VoronoiDiagram();
        for (Point2D p : points) {
            vertices.put(p, vd.insert_point_site(new Point(p.getX(), p.getY())));
        }
        for (Point2D src : segments.keySet()) {
            Point2D trg = segments.get(src);
            vd.insert_line_site(vertices.get(src), vertices.get(trg));
        }
        return vd;
    }

    public void writeToSvg(String fname) throws IOException {
        File f = new File(fname);
        f.getParentFile().mkdirs();
        PrintWriter w = new PrintWriter(f);
        w.println("<svg width='1024px' height='1024px'>");
        w.println("<rect width='1024px' height='1024px' fill='rgb(200,200,200)'/>");

        for (Point2D src : segments.keySet()) {
            Point2D trg = segments.get(src);
            w.printf("<polyline points='%f,%f %f,%f' fill='none' stroke-width='1' stroke='rgb(255,255,0)'/>\n",
                     src.getX()*512 + 512, -src.getY()*512 + 512,
                     trg.getX()*512 + 512, -trg.getY()*512 + 512);
        }
        for (Point2D p : points) {
            w.printf("<circle cx='%f' cy='%f' r='%f' fill='rgb(0,0,255)'/>\n",
                     p.getX()*512 + 512, p.getY()*512 + 512, 2);
        }
        w.println("</svg>");
        w.close();
    }

    public void writeToText(String fname) throws IOException {
        File f = new File(fname);
        f.getParentFile().mkdirs();
        PrintWriter w = new PrintWriter(f);
        for (Point2D p : points) {
            w.printf("%s,%s\n", p.getX(), p.getY());
        }
        for (Point2D src : segments.keySet()) {
            Point2D trg = segments.get(src);
            w.printf("%s,%s->%s,%s\n", src.getX(), src.getY(), trg.getX(), trg.getY());
        }
        w.close();
    }

    public static EuclideanInput readFromText(String fname) throws IOException {
        List<Point2D> points = new ArrayList<>();
        Map<Point2D, Point2D> segments = new HashMap<>();
        BufferedReader r = new BufferedReader(new FileReader(fname));
        String l;
        while ((l = r.readLine()) != null) {
            String[] p = l.split(",|->");
            if (p.length == 4) {
                segments.put(new Point2D.Double(Double.valueOf(p[0]), Double.valueOf(p[1])),
                             new Point2D.Double(Double.valueOf(p[2]), Double.valueOf(p[3])));
            } else {
                points.add(new Point2D.Double(Double.valueOf(p[0]), Double.valueOf(p[1])));
            }
        }
        r.close();
        return new EuclideanInput(points, segments);
    }

}
