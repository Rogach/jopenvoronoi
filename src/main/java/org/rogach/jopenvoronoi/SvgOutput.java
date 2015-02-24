package org.rogach.jopenvoronoi;

import java.awt.Color;
import java.io.*;
import java.util.*;

public class SvgOutput {

    private VoronoiDiagram vd;
    public double CX = 0;
    public double CY = 0;
    public double WIDTH = 0.2;
    public double SCALE = 256;

    public SvgOutput(VoronoiDiagram vd) {
        this.vd = vd;
    }

    public static void output(VoronoiDiagram vd, String fname) {
        new SvgOutput(vd).writeTo(fname);
    }

    public void writeTo(String fname) {
        PrintWriter w;
        try {
            w = new PrintWriter(fname);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        HalfEdgeDiagram g = vd.get_graph_reference();;

        // write header
        w.println("<svg width='1024px' height='1024px'>");
        // write background
        w.println("<rect width='1024px' height='1024px' fill='rgb(200,200,200)'/>");

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

    private void writeVertex(PrintWriter w, Vertex v) {
        String col = color_string(vertex_color(v));
        Point p = scale(v.position);
        w.printf("<circle cx='%f' cy='%f' r='%f' fill='%s'/>\n",
                 p.x, p.y, WIDTH * 1.5, col);
        if (v.status == VertexStatus.NEW) {
            w.printf("<circle cx='%f' cy='%f' r='%f' stroke='green' fill='none' stroke-width='%f'/>\n",
                     p.x, p.y, WIDTH * 2, WIDTH * 0.5);
        }
        if (v.status == VertexStatus.IN) {
            w.printf("<circle cx='%f' cy='%f' r='%f' stroke='red' fill='none' stroke-width='%f'/>\n",
                     p.x, p.y, WIDTH * 3, WIDTH * 0.5);
        }
        w.printf("<text x='%f' y='%f' font-size='%f'>(%.3f,%.3f)</text>\n",
                 p.x, p.y, WIDTH * 5, v.position.x, v.position.y);
    }

    private void writeEdge(PrintWriter w, Edge e) {
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
            sb.append(String.format("%f,%f", p.x, p.y));
            sb.append(" ");
        }
        if (!points.isEmpty()) {
            w.printf("<polyline points='%s' fill='none' stroke-width='%f' stroke='%s' />\n",
                     sb.toString(), WIDTH, col);
        }
    }

    private Point scale(Point p) {
        return new Point((p.x - CX) * SCALE + 512, -(p.y-CY) * SCALE + 512);
    }

    private static String edge_color_string(Edge e) {
        return color_string(edge_color(e));
    }

    private static String color_string(Color c) {
        return String.format("rgb(%d,%d,%d)", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static Color edge_color(Edge e) {
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

    private static Color vertex_color(Vertex v) {
        switch (v.type) {
        case OUTER: return Color.GRAY;
        case NORMAL: return Color.RED;
        case POINTSITE: return new Color(250, 5, 126);
        default: return Color.BLUE;
        }
    }

}
