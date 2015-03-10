package org.rogach.jopenvoronoi;

import java.util.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PlanarGraph {

    public List<Point2D> points;
    public List<Segment> segments;

    public PlanarGraph(List<Point2D> points, List<Segment>segments) {
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

   public static PlanarGraph fromPolygon(List<Point2D> points) {
        List<Segment> segments = new ArrayList<>();
        for (int q = 0; q < points.size(); q++) {
            if (q != points.size() - 1) {
                segments.add(new Segment(points.get(q), points.get(q + 1)));
            } else {
                segments.add(new Segment(points.get(points.size() - 1), points.get(0)));
            }
        }
        return new PlanarGraph(points, segments);
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
        try (PrintWriter w = new PrintWriter(f)) {
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
        }
    }

    public void writeToFile(String fname) throws Exception {
        File f = new File(fname);
        f.getAbsoluteFile().getParentFile().mkdirs();
        try (OutputStream os = new FileOutputStream(f);
             GZIPOutputStream gzos = new GZIPOutputStream(os)
        ) {
            int i = 0;
            Map<Point2D, Integer> pointIndices = new HashMap<>();
            for (Point2D p : points) {
                pointIndices.put(p, i++);
            }

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();

            Element root = doc.createElement("PSLG");
            doc.appendChild(root);

            Element pointSites = doc.createElement("PointSites");
            root.appendChild(pointSites);
            for (Point2D p : points) {
                Element pointSite = doc.createElement("PointSite");
                pointSites.appendChild(pointSite);
                pointSite.setAttribute("idx", String.valueOf(pointIndices.get(p)));
                pointSite.setAttribute("x", String.valueOf(p.getX()));
                pointSite.setAttribute("y", String.valueOf(p.getY()));
            }

            Element lineSites = doc.createElement("LineSites");
            root.appendChild(lineSites);
            for (Segment s : segments) {
                Element lineSite = doc.createElement("LineSite");
                lineSites.appendChild(lineSite);
                lineSite.setAttribute("source_idx", String.valueOf(pointIndices.get(s.stt)));
                lineSite.setAttribute("target_idx", String.valueOf(pointIndices.get(s.end)));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(gzos);
            transformer.transform(domSource, streamResult);
        }
    }

    public static PlanarGraph readFromFile(String fname) throws Exception {
        try (FileInputStream fis = new FileInputStream(fname);
             GZIPInputStream gzis = new GZIPInputStream(fis)
        ) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = documentBuilder.parse(gzis);

            List<Point2D> points = new ArrayList<>();
            List<Segment> segments = new ArrayList<>();
            Map<Integer, Point2D> pointIndices = new HashMap<>();

            NodeList pointSites = doc.getElementsByTagName("PointSite");
            for (int q = 0; q < pointSites.getLength(); q++) {
                Node ps = pointSites.item(q);
                int idx = Integer.valueOf(ps.getAttributes().getNamedItem("idx").getTextContent());
                Point2D p = new Point2D.Double(
                        Double.valueOf(ps.getAttributes().getNamedItem("x").getTextContent()),
                        Double.valueOf(ps.getAttributes().getNamedItem("y").getTextContent()));
                pointIndices.put(idx, p);
                points.add(p);
            }

            NodeList lineSites = doc.getElementsByTagName("LineSite");
            for (int q = 0; q < lineSites.getLength(); q++) {
                Node ls = lineSites.item(q);
                segments.add(new Segment(
                        pointIndices.get(Integer.valueOf(ls.getAttributes().getNamedItem("source_idx").getTextContent())),
                        pointIndices.get(Integer.valueOf(ls.getAttributes().getNamedItem("target_idx").getTextContent()))));
            }

            return new PlanarGraph(points, segments);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanarGraph that = (PlanarGraph) o;

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
