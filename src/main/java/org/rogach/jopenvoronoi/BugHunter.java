package org.rogach.jopenvoronoi;

import java.awt.geom.*;
import java.util.regex.*;
import java.util.*;
import java.io.*;

public class BugHunter {

    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("reclassify")) {
            new BugHunter().reclassify();
        } else if (args.length > 0 && args[0].equals("collect")) {
            new BugHunter().collectFailures();
        } else {
            throw new Error("unknown bug hunting action: " + Arrays.toString(args));
        }
    }

    public void collectFailures() throws IOException {
        for (int q = 0; ; q++) {
            EuclideanInput input =
                EuclideanInput.fromPolygon(RandomPolygon.generate_polygon(512));
            try {
                input.buildVoronoiDiagram();
                System.out.printf("☺");
                System.out.flush();
            } catch (Throwable tOrig) {
                System.out.println();
                EuclideanInput minimized = minimizeFailure(input);
                Throwable tMin = null;
                try {
                    minimized.buildVoronoiDiagram();
                } catch (Throwable tNew) {
                    tMin = tNew;
                }
                int id = new Random().nextInt(1000000);
                String sz = sizeEstimate(input);
                String fn = getFailureName(tOrig);
                String f = String.format("failures/_%s-%s-%d.txt", fn, sz, id);
                System.err.printf("Saving to %s\n", f);
                input.writeToText(f);

                int mid = new Random().nextInt(1000000);
                String msz = sizeEstimate(minimized);
                String mfn = getFailureName(tMin);
                String mf = String.format("failures/_%s-%s-%d.txt", mfn, msz, mid);
                tMin.printStackTrace();
                System.err.printf("Saving to %s\n", mf);
                minimized.writeToText(mf);
            }
        }
    }

    public void reclassify() throws IOException {
        boolean assertionsOn = false;
        try {
            assert(false) : "false";
        } catch (AssertionError ae) {
            assertionsOn = true;
        }
        if (!assertionsOn) {
            System.err.println("You need assertions turned on for failure reclassifying");
            System.exit(1);
        }

        int fixedFailures = 0;
        int newFailures = 0;
        Map<String, Throwable> catchedErrors = new HashMap<>();
        for (String f : new File("failures").list()) {
            boolean isFailure = !f.contains("noerror");
            Throwable t = null;
            EuclideanInput input = EuclideanInput.readFromText("failures/" + f);
            try {
                input.buildVoronoiDiagram();
            } catch (Throwable th) {
                t = th;
            }

            if (isFailure && t == null) {
                fixedFailures++;
            } else if (!isFailure && t != null) {
                newFailures++;
            }

            Matcher m = Pattern.compile("\\d+").matcher(f);
            if (!m.find()) {
                throw new RuntimeException(String.format("Strange file name: '%s'", f));
            }
            String id = m.group();
            String sz = sizeEstimate(input);
            String fn = t == null ? "noerror" : getFailureName(t);
            if (t != null) {
                catchedErrors.put(fn, t);
            }
            String newF = String.format("%s-%s-%s.txt", fn, sz, id);
            if (!f.equals(newF)) {
                System.out.printf("%s => %s\n", f, newF);
                new File("failures/" + f).renameTo(new File("failures/" + newF));
            }
        }
        for (String fn : catchedErrors.keySet()) {
            Throwable t = catchedErrors.get(fn);
            System.out.printf("%s: %s, %s\n", fn, t.getClass().getName(), t.getMessage());
            for (StackTraceElement ste : t.getStackTrace()) {
                if (ste.getClassName().startsWith("org.rogach.jopenvoronoi") &&
                    !ste.getClassName().startsWith("org.rogach.jopenvoronoi.BugHunter") &&
                    !ste.getClassName().startsWith("org.rogach.jopenvoronoi.EuclideanInput")) {
                    System.out.printf("    at %s.%s(%s:%s)\n",
                                      ste.getClassName(),
                                      ste.getMethodName(),
                                      ste.getFileName(),
                                      ste.getLineNumber());
                }
            }
        }

        System.out.printf("fixed %d failures, introduced %d failures\n", fixedFailures, newFailures);
    }

    static List<String> bugNames = new ArrayList<>();

    public static void loadBugNames() {
        try {
            BufferedReader r = new BufferedReader(new FileReader("/usr/share/dict/british-english"));
            String l;
            while ((l = r.readLine()) != null) {
                if (Pattern.matches("[A-Za-z]{4,6}", l) &&
                    !Pattern.matches(".*[sy]", l)) {
                    bugNames.add(l.toLowerCase());
                }
            }
            r.close();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static String getFailureName(Throwable t) {
        if (bugNames.isEmpty()) {
            loadBugNames();
        }
        String fingerprint = t.getClass().getName() + t.getMessage();
        if (t.getStackTrace().length > 0) {
            fingerprint += t.getStackTrace()[0].getClassName();
            fingerprint += t.getStackTrace()[0].getMethodName();
            fingerprint += t.getStackTrace()[0].getFileName();
        }
        return bugNames.get(Math.abs(fingerprint.hashCode()) % bugNames.size());
    }

    public static String sizeEstimate(EuclideanInput ei) {
        if (ei.points.size() > 300) {
            return "huge";
        } else if (ei.points.size() > 50) {
            return "big";
        } else if (ei.points.size() > 20) {
            return "small";
        } else {
            return "tiny";
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
                c = 0;
                System.out.printf("@%dx%d@", batch, current.points.size() / batch);
                System.out.flush();
                for (int offset = 0; offset + batch <= current.segments.size(); offset += batch) {
                    List<Point2D> pts = new ArrayList<>(current.segments.keySet());
                    LinkedHashMap<Point2D, Point2D> lessSegments =
                        new LinkedHashMap<>(current.segments);
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
