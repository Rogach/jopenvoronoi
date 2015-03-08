package org.rogach.jopenvoronoi;

/// \brief offset-element of PointSite or ArcSite
public class ArcOfs extends Ofs {
    Point _start; ///< start
    Point _end;   ///< end
    Point c;      ///< center
    double r;     ///< radius

    /// \param p1 start Point
    /// \param p2 end Point
    /// \param cen center Point
    /// \param rad radius
    public ArcOfs(Point p1, Point p2, Point cen, double rad) {
        this._start = p1;
        this._end = p2;
        this.c = cen;
        this.r = rad;
    }

    @Override
    public String toString() {
        return String.format("ArcOfs from %s to %s r=%f\n", _start, _end, r);
    }

    public double radius() {return r;}
    public Point center() {return c;}
    public Point start() {return _start;}
    public Point end() {return _end;}
};
