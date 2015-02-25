package org.rogach.jopenvoronoi;

import java.awt.geom.Point2D;
import java.util.*;

public class RandomLabyrinth {
    public static EuclideanInput generateLabyrinth(int size, double loadFactor) {
        // stores bitwise fields to indicate connections between cells
        // 1 - up, 2 - right, 4 - down, 8 - left
        int[][] directions = new int[size][size];

        // generate random connections between cells
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                // horizontal connections
                if (x < size - 1 && Math.random() < loadFactor) {
                    directions[x][y] |= 2;
                    directions[x+1][y] |= 8;
                }
                // vertical connections
                if (y < size - 1 && Math.random() < loadFactor) {
                    directions[x][y] |= 4;
                    directions[x][y+1] |= 1;
                }
            }
        }

        Set<Point2D> points = new HashSet<>();
        List<EuclideanInput.Segment> segments = new ArrayList<>();

        for (int y = 0; y < size; y++) {
            extractSegments(0, y * 2, size, 1, 0, 1, 2, directions, points, segments);
            extractSegments(0, y * 2 + 1, size, 1, 0, 4, 2, directions, points, segments);
        }
        for (int x = 0; x < size; x++) {
            extractSegments(x * 2, 0, size, 0, 1, 8, 4, directions, points, segments);
            extractSegments(x * 2 + 1, 0, size, 0, 1, 2, 4, directions, points, segments);
        }
        return new EuclideanInput(new ArrayList<>(points), segments);
    }

    private static void extractSegments(int x, int y, int size,
                                        int dx, int dy,
                                        int maskA, int maskB,
                                        int[][] directions,
                                        Set<Point2D> points, List<EuclideanInput.Segment> segments) {
        double sideLength = 0.7 * 2 / (size * 2 - 1);
        Point2D stt = new Point2D.Double(-0.7 + x * sideLength, -0.7 + y * sideLength);
        Point2D end = stt;
        while (x < size * 2 && y < size * 2) {
            if ((directions[x / 2][y / 2] & maskA) == 0) {
                end = new Point2D.Double(-0.7 + (x + dx) * sideLength, -0.7 + (y + dy) * sideLength);
            } else {
                if (!stt.equals(end)) {
                    points.add(stt);
                    points.add(end);
                    segments.add(new EuclideanInput.Segment(stt, end));
                }
                stt = new Point2D.Double(-0.7 + (x + dx) * sideLength, -0.7 + (y + dy) * sideLength);
                end = stt;
            }

            if ((directions[x / 2][y / 2] & maskB) != 0) {
                end = new Point2D.Double(-0.7 + (x + 2 * dx) * sideLength, -0.7 + (y + 2 * dy) * sideLength);
            } else {
                if (!stt.equals(end)) {
                    points.add(stt);
                    points.add(end);
                    segments.add(new EuclideanInput.Segment(stt, end));
                }
                stt = new Point2D.Double(-0.7 + (x + 2 * dx) * sideLength, -0.7 + (y + 2 * dy) * sideLength);
                end = stt;
            }

            x += 2 * dx;
            y += 2 * dy;
        }
    }
}
