package org.rogach.jopenvoronoi;

/// \brief offset-element of LineSite
public class LineOfs extends Ofs {
    protected Point _start; ///< start point
    protected Point _end;  ///< end point
    /// \param p1 start point
    /// \param p2 end point
    public LineOfs(Point p1, Point p2) {
        this._start = p1;
        this._end = p2;
    }

    public double radius()  {return -1;}
    public Point center() {return new Point(0,0);}
    public Point start() {return _start;}
    public Point end() {return _end;}
};
