package org.rogach.jopenvoronoi;

import java.awt.geom.*;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        EuclideanInput input = EuclideanInput.readFromText("etr.txt");
        if (BugHunter.isSelfIntersected(input)) {
            System.out.println("self-intersecting");
            System.exit(1);
        }
        EuclideanInput minimized = BugHunter.minimizeFailure(input);
        minimized.writeToSvg("minimized.svg");
        minimized.writeToText("minimized.txt");
    }
}
