package org.rogach.jopenvoronoi;

import java.awt.geom.*;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        EuclideanInput failure = EuclideanInput.readFromText("failures/failure-370011.txt");
        EuclideanInput minimized = minimizeFailure(failure);
        minimized.writeToSvg("minimized.svg");
        minimized.writeToText("minimized.txt");
    }

    public static void collectedFailures() throws IOException {
        for (String f : new File("failures").list()) {
            System.out.printf("\r??? %s", f);
            System.out.flush();
            boolean success = false;
            Throwable t = null;
            try {
                EuclideanInput.readFromText("failures/" + f).buildVoronoiDiagram();
                success = true;
            } catch (Throwable th) {
                t = th;
            }
            if (success) {
                System.out.printf("\r+++ %s\n", f);
            } else {
                System.out.printf("\r*** %s\n", f);
            }
        }
    }

    public static EuclideanInput minimizeFailure(EuclideanInput input) throws IOException {
        System.out.printf("Minimizing input with %d points and %d segments\n",
                          input.points.size(), input.segments.size());
        EuclideanInput current = input;
        int batch = current.points.size() / 2;
        while (true) {
            System.out.printf("\nAt the start of the iteration: %d points and %d segments\n",
                              current.points.size(), current.segments.size());
            int c = 0;
            for (batch = batch > 0 ? batch : 1; batch >= 1; batch /= 2) {
                System.out.printf("@%dx%d@", batch, current.points.size() / batch);
                System.out.flush();
                for (int offset = 0; offset + batch <= current.segments.size(); offset += batch) {
                    List<Point2D> pts = new ArrayList<>(current.segments.keySet());
                    Map<Point2D, Point2D> lessSegments = new HashMap<>(current.segments);
                    for (int q = offset; q < offset + batch; q++) {
                        lessSegments.remove(pts.get(q));
                    }
                    EuclideanInput modified = new EuclideanInput(current.points, lessSegments);
                    try {
                        modified.buildVoronoiDiagram();
                        System.out.printf("|");
                        System.out.flush();
                    } catch (Throwable t) {
                        current = modified;
                        for (int q = 0; q < batch; q++) {
                            System.out.printf("-");
                        }
                        System.out.flush();
                        c += batch;
                    }
                }

                for (int offset = 0; offset + batch <= current.points.size(); offset += batch) {
                    List<Point2D> lessPoints = new ArrayList<>(current.points);
                    int pointsRemoved = 0;
                    for (int q = offset; q < offset + batch; q++) {
                        if (!current.segments.keySet().contains(current.points.get(q)) &&
                            !current.segments.values().contains(current.points.get(q))) {
                            lessPoints.remove(current.points.get(q));
                            pointsRemoved++;
                        }
                    }
                    if (pointsRemoved > 0) {
                        EuclideanInput modified = new EuclideanInput(lessPoints, current.segments);
                        try {
                            modified.buildVoronoiDiagram();
                            System.out.printf("*");
                            System.out.flush();
                        } catch (Throwable t) {
                            current = modified;
                            for (int q = 0; q < pointsRemoved; q++) {
                                System.out.printf(".");
                            }
                            System.out.flush();
                            c += pointsRemoved;
                        }
                    } else {
                        System.out.printf("*");
                    }
                }
            }
            if (c == 0) {
                break;
            }
        }
        System.out.printf("\nMinimized input to %d points and %d segments\n",
                          current.points.size(), current.segments.size());
        return current;
    }
}
