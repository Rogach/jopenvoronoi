package org.rogach.jopenvoronoi;

public class KdPoint {
    Point p;
    Face face;

    public KdPoint(Point p, Face face) {
        this.p = p;
        this.face = face;
    }
}
