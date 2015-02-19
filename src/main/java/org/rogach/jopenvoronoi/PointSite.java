package org.rogach.jopenvoronoi;

/// vertex Site
public class PointSite extends Site {
    private Point _p; ///< position
    public Vertex v; ///< vertex descriptor of this PointSite

    public PointSite(Point p) {
        this._p = p;
        face = null;
        eq.q = true;
        eq.a = -2*p.x;
        eq.b = -2*p.y;
        eq.k = 0;
        eq.c = p.x*p.x + p.y*p.y;
    }

    /// ctor
    public PointSite(Point p, Face f) {
        this._p = p;
        face = f;
        eq.q = true;
        eq.a = -2*p.x;
        eq.b = -2*p.y;
        eq.k = 0;
        eq.c = p.x*p.x + p.y*p.y;
    }

    /// ctor
    public PointSite(Point p, Face f, Vertex vert) {
        this.v = vert;
        this._p = p;
        face = f;
        eq.q = true;
        eq.a = -2*p.x;
        eq.b = -2*p.y;
        eq.k = 0;
        eq.c = p.x*p.x + p.y*p.y;
    }

    public Point apex_point(Point p) { return _p; }
    public Ofs offset(Point p1,Point p2) {
        double rad = p1.sub(_p).norm();
        return new ArcOfs(p1, p2, _p, rad);
    }
    public Point position() { return _p; }
    public double x() {return _p.x;}
    public double y() {return _p.y;}
    public double r() {return 0;}
    public double k() {return 0;}
    public boolean isPoint() {return true;}
    public boolean in_region(Point p) {return true;}
    public double in_region_t(Point p) {return -1;}
    public Vertex vertex() {return v;}

    @Override
    public String toString() {
        return String.format("PS(%s)", _p);
    }
};
