package org.rogach.jopenvoronoi;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates space-filling curves using Lindenmayer systems
 */
public class LindenmayerCurve {
    public static EuclideanInput generateMooreCurve(int k) {
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

    private static EuclideanInput generateCurve(String curve, double da, Point2D start, double length) {
        List<Point2D> points = new ArrayList<>();
        List<EuclideanInput.Segment> segments = new ArrayList<>();
        Point2D p = start;
        double angle = -Math.PI / 2;
        points.add(p);
        for (int q = 0; q < curve.length(); q++) {
            char c = curve.charAt(q);
            if (c == 'F') {
                Point2D next = new Point2D.Double(p.getX() + Math.cos(angle) * length, p.getY() + Math.sin(angle) * length);
                points.add(next);
                segments.add(new EuclideanInput.Segment(p, next));
                p = next;
            } else if (c == '-') {
                angle -= Math.PI / 2;
            } else if (c == '+') {
                angle += Math.PI / 2;
            }
        }
        return new EuclideanInput(points, segments);
    }
}
