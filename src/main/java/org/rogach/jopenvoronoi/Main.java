package org.rogach.jopenvoronoi;

import java.awt.geom.*;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
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

}
