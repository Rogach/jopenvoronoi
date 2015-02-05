package org.rogach.jopenvoronoi;

/// \brief a new vertex position solution (position, offset-distance, side)
///
/// includes the offset-distamce t, and the offset direction k3
public class Solution {
    /// position
    public Point p;
    /// clearance-disk radius
    public double t;
    /// offset direction to third adjacent Site
    public double k3;

    /// \param pt vertex position
    /// \param tv clearance-disk radius
    /// \param k offset direction
    public Solution(Point pt, double tv, double k) {
        this.p = pt;
        this.t = tv;
        this.k3 = k;
    }
}
