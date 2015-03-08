package org.rogach.jopenvoronoi;

/// Base-class for a voronoi-diagram site, or generator.
public abstract class Site {
    /// the HEFace of this Site
    Face face;
    /// equation parameters
    Eq eq = new Eq();

    /// return closest point on site to given point p
    public abstract Point apex_point(Point p);

    /// return offset of site
    public abstract Ofs offset(Point p1, Point p2);

    /// position of site for PointSite
    public Point position() {
        throw new UnsupportedOperationException();
    }

    /// start point of site (for LineSite and ArcSite)
    public Point start() {
        throw new UnsupportedOperationException();
    }

    /// end point of site (for LineSite and ArcSite)
    public Point end() {
        throw new UnsupportedOperationException();
    }

    /// return equation parameters
    public Eq eqp() {
        return eq;
    }

    /// return equation parameters
    public Eq eqp(double kk) {
        Eq eq2 = new Eq(eq);
        eq2.k *= kk;
        return eq2;
    }

    /// true for LineSite
    public boolean is_linear() {
        return isLine();
    }

    /// true for PointSite and ArcSite
    public boolean is_quadratic() {
        return isPoint();
    }

    /// x position
    public double x() {
        throw new UnsupportedOperationException();
    }

    /// y position
    public double y() {
        throw new UnsupportedOperationException();
    }

    /// radius (zero for PointSite)
    public double r() {
        throw new UnsupportedOperationException();
    }

    /// offset direction
    public double k() {
        throw new UnsupportedOperationException();
    }

    /// LineSite a parameter
    public double a() {
        throw new UnsupportedOperationException();
    }

    /// LineSite b parameter
    public double b() {
        throw new UnsupportedOperationException();
    }

    /// LineSite c parameter
    public double c() {
        throw new UnsupportedOperationException();
    }

    public void set_c(Point p) {
        throw new UnsupportedOperationException();
    }

    /// true for PointSite
    public boolean isPoint() { return false; }
    /// true for LineSite
    public boolean isLine() { return false;}
    /// true for ArcSite
    public boolean isArc() { return false;}
    /// true for CW oriented ArcSite
    public boolean cw() { return false; }
    /// is given Point in_region ?
    public abstract boolean in_region(Point p);
    /// is given Point in region?
    public double in_region_t(Point p) {
        throw new UnsupportedOperationException();
    }

    /// in-region t-valye
    public double in_region_t_raw(Point p) {
        throw new UnsupportedOperationException();
    }

    /// return edge (if this is a LineSite or ArcSite
    public Edge edge() {
        throw new UnsupportedOperationException();
    }

    /// return vertex, if this is a PointSite
    public Vertex vertex() {
        throw new UnsupportedOperationException();
    }
};
