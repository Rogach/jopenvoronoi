package org.rogach.jopenvoronoi;

import org.junit.*;
import java.io.*;

public class RunTest {

    @Test
    public void runTestFiles() throws Exception {
        boolean hadFailures = false;
        for (File f : new File("src/test/resources/").listFiles()) {
            System.out.printf("testing %s\n", f);
            try {
                PlanarGraph pg = PlanarGraph.readFromFile(f.getAbsolutePath());
                VoronoiDiagram vd = pg.buildVoronoiDiagram();
                vd.check();
            } catch (Throwable t) {
                hadFailures = true;
                System.out.printf("%s: %s, %s\n",
                                  BugHunter.getFailureName(t),
                                  t.getClass().getName(),
                                  t.getMessage());
                for (StackTraceElement ste : t.getStackTrace()) {
                    if (ste.getClassName().startsWith("org.rogach.jopenvoronoi") &&
                        !ste.getClassName().startsWith("org.rogach.jopenvoronoi.EuclideanInput")) {
                        System.out.printf("    at %s.%s(%s:%s)\n",
                                          ste.getClassName(),
                                          ste.getMethodName(),
                                          ste.getFileName(),
                                          ste.getLineNumber());
                    }
                }
            }
        }
        if (hadFailures) throw new Exception("There were failures when running test graphs");
    }

}
