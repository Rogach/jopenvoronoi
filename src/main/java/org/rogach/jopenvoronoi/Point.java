package org.rogach.jopenvoronoi;

/// \brief a point or vector in 2D with coordinates (x, y)
public class Point {
    public double x;
    public double y;

    public Point() {
        this.x = 0;
        this.y = 0;
    }
    public Point(double xi, double yi) {
        this.x = xi;
        this.y = yi;
    }
    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public double dot(Point p) {
        return x * p.x + y * p.y;
    }
    public double cross(Point p) {
        return x * p.y - y * p.x;
    }
    public double norm() {
        return Math.sqrt(x*x + y*y);
    }
    public double norm_sq() {
        return x*x + y*y;
    }
    public void normalize() {
        if (this.norm() != 0.0) {
            this.multEq(1/this.norm());
        }
    }

    /// return perpendicular in the xy plane, rotated 90 degree to the left
    public Point xy_perp() {
        return new Point(-y, x);
        // 2D rotation matrix:
        //   cos   -sin
        //   sin   cos
        // for theta = 90
        //   0   -1   ( x )
        //   1    0   ( y )  = ( -y  x )

    }

    /// is this Point right of line through points \a p1 and \a p2 ?
    public boolean is_right(Point p1, Point p2) {
        // this is an ugly way of doing a determinant
        // should be prettyfied sometime...
        /// \todo FIXME: what if p1==p2 ? (in the XY plane)
        double a1 = p2.x - p1.x;
        double a2 = p2.y - p1.y;
        double t1 = a2;
        double t2 = -a1;
        double b1 = x - p1.x;
        double b2 = y - p1.y;

        double t = t1 * b1 + t2 * b2;
        if (t > 0.0)
            return true;
        else
            return false;
    }

    public void addEq(Point p) {
        x += p.x;
        y += p.y;
    }
    public void subEq(Point p) {
        x -= p.x;
        y -= p.y;
    }
    public Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }
    public Point sub(Point p) {
        return new Point(x - p.x, y - p.y);
    }
    public void multEq(double a) {
        x *= a;
        y *= a;
    }
    public Point mult(double a) {
        return new Point(x * a, y * a);
    }

    @Override
    public boolean equals(Object other) {
        return (this == other) || (x == ((Point) other).x && y == ((Point) other).y);
    }
    @Override
    public int hashCode() {
        return Double.valueOf(x).hashCode() * 42 + Double.valueOf(y).hashCode();
    }
    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }
}
