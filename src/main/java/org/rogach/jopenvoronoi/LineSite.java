package org.rogach.jopenvoronoi;

/// line segment Site
public class LineSite extends Site {
    Point _start; ///< start Point of LineSite
    Point _end; ///< end Point of LineSite
    Edge e; ///< edge_descriptor to the ::LINESITE pseudo-edge

    public LineSite(Point st, Point en, double koff) {
        this(st, en, koff, null);
    }
    public LineSite(Site s) {
        this.eq = s.eqp();
        this.face = s.face;
        this._start = s.start();
        this._end = s.end();
    }

    /// create line-site between start and end Point.
    public LineSite(Point st, Point en, double koff, Face f) {
        this._start = st;
        this._end = en;
        face = f;
        eq.q = false;
        eq.a = _end.y - _start.y;
        eq.b = _start.x - _end.x;
        eq.k = koff; // ??
        eq.c = _end.x*_start.y - _start.x*_end.y;
        // now normalize
        double d = Math.sqrt( eq.a*eq.a + eq.b*eq.b );
        eq.a /= d;
        eq.b /= d;
        eq.c /= d;
        assert( Math.abs( eq.a*eq.a + eq.b*eq.b -1.0 ) < 1e-5) : " Math.abs( eq.a*eq.a + eq.b*eq.b -1.0 ) < 1e-5";
    }

    public Ofs offset(Point p1, Point p2) { return new LineOfs(p1, p2); }

    /// closest point on start-end segment to given point.
    /// project onto line and return either the projected point
    /// or one endpoint of the linesegment
    public Point apex_point(Point p) {
        Point s_p = p.sub(_start);
        Point s_e = _end.sub(_start);
        double t = s_p.dot(s_e) / s_e.dot(s_e);
        if (t<0)
            return _start;
        if (t>1)
            return _end;
        else {
            return _start.add(_end.sub(_start).mult(t));
        }
    }
    public boolean in_region(Point p) {
        double t = in_region_t(p);
        return ( (t>=0) && (t<=1) );
    }
    public double in_region_t(Point p) {
        Point s_p = p.sub(_start);
        Point s_e = _end.sub(_start);
        double t = s_p.dot(s_e) / s_e.dot(s_e);
        double eps = 1e-7;
        if (Math.abs(t) < eps)  // rounding... UGLY
            t = 0.0;
        else if ( Math.abs(t-1.0) < eps )
            t = 1.0;
        return t;
    }
    public double in_region_t_raw(Point p) {
        Point s_p = p.sub(_start);
        Point s_e = _end.sub(_start);
        double t = s_p.dot(s_e) / s_e.dot(s_e);
        return t;
    }
    public boolean isLine() {return true;}
    public double a() { return eq.a; }
    public double b() { return eq.b; }
    public double c() { return eq.c; }
    public double k() {
        assert( eq.k==1 || eq.k==-1 ) : " eq.k==1 || eq.k==-1 ";
        return eq.k;
    }
    public void set_c(Point p) {
        eq.c = -( eq.a * p.x + eq.b * p.y );
    }
    public Point start() {return _start;}
    public Point end() {return _end;}
    public Edge edge() {return e;}

    @Override
    public String toString() {
        return String.format("LS(%s>%s)", _start, _end);
    }
};
