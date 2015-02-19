package org.rogach.jopenvoronoi;

import java.awt.geom.*;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        EuclideanInput failure = EuclideanInput.readFromText("failures/failure-13340.txt");
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
        Throwable origException = null;
        try {
            input.buildVoronoiDiagram();
            System.out.println("No minimization: input doesn't fail");
            return input;
        } catch (Throwable th) {
            origException = th;
        }
        EuclideanInput current = input;
        while (true) {
            int c = 0;
            for (Point2D src : current.segments.keySet()) {
                Point2D trg = current.segments.get(src);
                Map<Point2D, Point2D> lessSegments = new HashMap<>(current.segments);
                lessSegments.remove(src);
                EuclideanInput modified = new EuclideanInput(current.points, lessSegments);
                try {
                    modified.buildVoronoiDiagram();
                } catch (Throwable t) {
                    if (t.getClass().equals(origException.getClass()) &&
                        (t.getMessage() == origException.getMessage() ||
                         t.getMessage().equals(origException.getMessage()))) {
                        current = modified;
                        System.out.printf("|");
                        System.out.flush();
                        c++;
                    }
                }
            }
            for (Point2D p : current.points) {
                if (!current.segments.keySet().contains(p) &&
                    !current.segments.values().contains(p)) {
                    List<Point2D> lessPoints = new ArrayList<>(current.points);
                    EuclideanInput modified = new EuclideanInput(lessPoints, current.segments);
                    try {
                        modified.buildVoronoiDiagram();
                    } catch (Throwable t) {
                        if (t.getClass().equals(origException.getClass()) &&
                            (t.getMessage() == origException.getMessage() ||
                             t.getMessage().equals(origException.getMessage()))) {
                            System.out.printf(".");
                            System.out.flush();
                            current = modified;
                            c++;
                        }
                    }
                }
            }
            if (c == 0) {
                break;
            }
        }
        System.out.printf("Resulting input with %d points and %d segments\n",
                          current.points.size(), current.segments.size());
        return current;
    }
}
