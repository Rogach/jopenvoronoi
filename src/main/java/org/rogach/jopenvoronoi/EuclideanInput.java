package org.rogach.jopenvoronoi;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class EuclideanInput {

    public List<Point2D> points;
    public List<Segment> segments;

    public EuclideanInput(List<Point2D> points, List<Segment>segments) {
        this.points = points;
        this.segments = segments;
    }

    public static class Segment {
        public Point2D stt;
        public Point2D end;

        public Segment(Point2D end, Point2D stt) {
            this.end = end;
            this.stt = stt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Segment segment = (Segment) o;

            if (end != null ? !end.equals(segment.end) : segment.end != null) return false;
            if (stt != null ? !stt.equals(segment.stt) : segment.stt != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = stt != null ? stt.hashCode() : 0;
            result = 31 * result + (end != null ? end.hashCode() : 0);
            return result;
        }
    }

    public static EuclideanInput fromPolygon(List<Point2D> points) {
        List<Segment> segments = new ArrayList<>();
        for (int q = 0; q < points.size(); q++) {
            if (q != points.size() - 1) {
                segments.add(new Segment(points.get(q), points.get(q + 1)));
            } else {
                segments.add(new Segment(points.get(points.size() - 1), points.get(0)));
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
        for (Segment s : segments) {
            vd.insert_line_site(vertices.get(s.stt), vertices.get(s.end));
        }
        return vd;
    }

    public void writeToSvg(String fname) throws IOException {
        File f = new File(fname);
        f.getAbsoluteFile().getParentFile().mkdirs();
        PrintWriter w = new PrintWriter(f);
        w.println("<svg width='1024px' height='1024px'>");
        w.println("<rect width='1024px' height='1024px' fill='rgb(200,200,200)'/>");

        for (Segment s : segments) {
            w.printf("<polyline points='%f,%f %f,%f' fill='none' stroke-width='1' stroke='rgb(255,255,0)'/>\n",
                     s.stt.getX()*512 + 512, -s.stt.getY()*512 + 512,
                     s.end.getX()*512 + 512, -s.end.getY()*512 + 512);
        }
        for (Point2D p : points) {
            w.printf("<circle cx='%f' cy='%f' r='%f' fill='rgb(0,0,255)'/>\n",
                     p.getX()*512 + 512, -p.getY()*512 + 512, 2d);
        }
        w.println("</svg>");
        w.close();
    }

    public void writeToText(String fname) throws IOException {
        File f = new File(fname);
        f.getAbsoluteFile().getParentFile().mkdirs();
        try (PrintWriter w = new PrintWriter(f)) {
            write(w);
        }
    }

    public void writeToGZip(String fname) throws IOException {
        File f = new File(fname);
        f.getAbsoluteFile().getParentFile().mkdirs();
        try (OutputStream os = new FileOutputStream(f);
             GZIPOutputStream gzos = new GZIPOutputStream(os);
             PrintWriter w = new PrintWriter(gzos)
        ) {
            write(w);
        }
    }

    public void write(PrintWriter w) throws IOException {
        for (Point2D p : points) {
            w.printf("%s,%s\n", p.getX(), p.getY());
        }
        for (Segment s : segments) {
            w.printf("%s,%s->%s,%s\n", s.stt.getX(), s.stt.getY(), s.end.getX(), s.end.getY());
        }
    }

    public static EuclideanInput readFromText(String fname) throws IOException {
        try (FileReader r = new FileReader(fname);
             BufferedReader br = new BufferedReader(r)) {
            return read(br);
        }
    }

    public static EuclideanInput readFromGZip(String fname) throws IOException {
        try (FileInputStream fis = new FileInputStream(fname);
             GZIPInputStream gzis = new GZIPInputStream(fis);
             InputStreamReader r = new InputStreamReader(gzis);
             BufferedReader br = new BufferedReader(r)) {
            return read(br);
        }
    }

    public static EuclideanInput read(BufferedReader r) throws IOException {
        List<Point2D> points = new ArrayList<>();
        List<Segment> segments = new ArrayList<>();
        String l;
        while ((l = r.readLine()) != null) {
            String[] p = l.split(",|->");
            if (p.length == 4) {
                segments.add(new Segment(
                        new Point2D.Double(Double.valueOf(p[0]), Double.valueOf(p[1])),
                        new Point2D.Double(Double.valueOf(p[2]), Double.valueOf(p[3]))));
            } else {
                points.add(new Point2D.Double(Double.valueOf(p[0]), Double.valueOf(p[1])));
            }
        }
        return new EuclideanInput(points, segments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EuclideanInput that = (EuclideanInput) o;

        if (points != null ? !points.equals(that.points) : that.points != null) return false;
        if (segments != null ? !segments.equals(that.segments) : that.segments != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = points != null ? points.hashCode() : 0;
        result = 31 * result + (segments != null ? segments.hashCode() : 0);
        return result;
    }
}
