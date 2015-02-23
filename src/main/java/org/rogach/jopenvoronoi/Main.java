package org.rogach.jopenvoronoi;

import java.awt.geom.*;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        EuclideanInput input = EuclideanInput.readFromText("failures/lubed-big-874009.txt");
        EuclideanInput minimized = BugHunter.minimizeFailure(input);
        minimized.writeToSvg("minimized.svg");
        minimized.writeToText("minimized.txt");
    }
}
