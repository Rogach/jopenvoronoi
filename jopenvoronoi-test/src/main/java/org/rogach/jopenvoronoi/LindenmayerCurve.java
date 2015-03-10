package org.rogach.jopenvoronoi;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates space-filling curves using Lindenmayer systems
 */
public class LindenmayerCurve {
    public static PlanarGraph generateMooreCurve(int k) {
        String axiom = "LFL+F+LFL";
        Map<Character, String> rules = new HashMap<>();
        rules.put('L', "-RF+LFL+FR-");
        rules.put('R', "+LF-RFR-FL+");
        double length;
        if (k > 1) {
            length = 1.4 / (k * k - 1);
        } else {
            length = 1.4;
        }
        return generateCurve(produce(axiom, rules, k-1), Math.PI / 2, new Point2D.Double(0 - length/2, 0.7), length);
    }

    public static PlanarGraph generateGosperCurve(int k) {
        String axiom = "A";
        Map<Character, String> rules = new HashMap<>();
        rules.put('A', "A-B--B+A++AA+B-");
        rules.put('B', "+A-BB--B-A++A+B");
        double length;
        if (k > 3) {
            length = 1.4 * (2.4 / Math.pow(Math.sqrt(7), k));
        } else {
            length = 0.15;
        }
        return generateCurve(produce(axiom, rules, k-1), Math.PI / 3, new Point2D.Double(0.7, 0), length);
    }

    private static String produce(String input, Map<Character, String> rules, int k) {
        while (k > 0) {
            input = produce(input, rules);
            k--;
        }
        return input;
    }

    private static String produce(String input, Map<Character, String> rules) {
        StringBuilder sb = new StringBuilder();
        for (int q = 0; q < input.length(); q++) {
            String replacement = rules.get(input.charAt(q));
            if (replacement != null) {
                sb.append(replacement);
            } else {
                sb.append(input.charAt(q));
            }
        }
        return sb.toString();
    }

    private static PlanarGraph generateCurve(String curve, double da, Point2D start, double length) {
        List<Point2D> points = new ArrayList<>();
        List<PlanarGraph.Segment> segments = new ArrayList<>();
        Point2D p1 = start;
        Point2D p2 = p1;
        double angle = -Math.PI / 2;
        points.add(p1);
        for (int q = 0; q < curve.length(); q++) {
            char c = curve.charAt(q);
            if (c == 'F' || c == 'A' || c == 'B') {
                Point2D next = new Point2D.Double(p2.getX() + Math.cos(angle) * length, p2.getY() + Math.sin(angle) * length);
                Line2D l = new Line2D.Double(p1, p2);
                if (l.ptLineDist(next) < 1e-7 || p1.equals(p2)) {
                    p2 = next;
                } else {
                    points.add(p2);
                    segments.add(new PlanarGraph.Segment(p1, p2));
                    p1 = p2;
                    p2 = next;
                }
            } else if (c == '-') {
                angle -= da;
            } else if (c == '+') {
                angle += da;
            }
        }
        points.add(p2);
        segments.add(new PlanarGraph.Segment(p1, p2));
        return new PlanarGraph(points, segments);
    }
}
