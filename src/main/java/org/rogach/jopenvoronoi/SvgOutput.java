package org.rogach.jopenvoronoi;

import java.awt.Color;
import java.io.*;
import java.util.*;

public class SvgOutput {
    private static double WIDTH = 1;

    public static void output(VoronoiDiagram vd, String fname) throws FileNotFoundException {
        PrintWriter w = new PrintWriter(fname);
        HalfEdgeDiagram g = vd.get_graph_reference();;

        // write header
        w.println("<svg width=\"1024px\" height=\"1024px\">");

        for (Edge e : g.edges) {
            if (e.valid) {
                writeEdge(w, e);
            }
        }
        for (Vertex v : g.vertices) {
            writeVertex(w, v);
        }

        // write footer
        w.println("</svg>");

        w.close();
    }

    public static void writeVertex(PrintWriter w, Vertex v) {
        String col = color_string(vertex_color(v));
        Point p = scale(v.position);
        w.printf("<circle cx=\"%.3f\" cy=\"%.3f\" r=\"%f\" fill=\"%s\"/>\n",
                 p.x, p.y, WIDTH * 1.5, col);
    }

    public static void writeEdge(PrintWriter w, Edge e) {
        Vertex src = e.source;
        Vertex trg = e.target;
        Point src_p = scale(src.position);
        Point trg_p = scale(trg.position);

        String col = edge_color_string(e);

        List<Point> points = new ArrayList<>();
        if (e.type == EdgeType.SEPARATOR ||
            e.type == EdgeType.LINE ||
            e.type == EdgeType.LINESITE ||
            e.type == EdgeType.OUTEDGE ||
            e.type == EdgeType.LINELINE ||
            e.type == EdgeType.PARA_LINELINE) {
            // edge drawn as two points
            points.add(src_p);
            points.add(trg_p);
        } else if (e.type == EdgeType.PARABOLA) {
            double t_src = src.dist();
            double t_trg = trg.dist();
            double t_min = Math.min(t_src, t_trg);
            double t_max = Math.max(t_src, t_trg);
            int nmax = 40;
            for (int n = 0; n < nmax; n++) {
                double t = t_min + ((t_max - t_min)/((nmax - 1)*(nmax - 1)))*n*n;
                points.add(scale(e.point(t)));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Point p : points) {
            sb.append(String.format("%.3f,%.3f", p.x, p.y));
            sb.append(" ");
        }
        if (!points.isEmpty()) {
            w.printf("<polyline points=\"%s\" fill=\"none\" stroke-width=\"%f\" stroke=\"%s\" />\n",
                     sb.toString(), WIDTH, col);
        }
    }

    public static Point  scale(Point p) {
        double s = 512d;
        return new Point(p.x * s + s, -p.y * s + s);
    }

    public static String edge_color_string(Edge e) {
        return color_string(edge_color(e));
    }

    public static String color_string(Color c) {
        return String.format("rgb(%d,%d,%d)", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static Color edge_color(Edge e) {
        switch (e.type) {
        case LINESITE: return Color.YELLOW;
        case PARABOLA: return Color.CYAN;
        case SEPARATOR: return Color.MAGENTA;
        case LINELINE: return new Color(0, 128, 0);
        case PARA_LINELINE: return new Color(0, 255, 0);
        case OUTEDGE: return Color.ORANGE;
        default: return Color.BLUE;
        }
    }

    public static Color vertex_color(Vertex v) {
        switch (v.type) {
        case OUTER: return Color.GRAY;
        case NORMAL: return Color.RED;
        case POINTSITE: return new Color(250, 5, 126);
        default: return Color.BLUE;
        }
    }

}
