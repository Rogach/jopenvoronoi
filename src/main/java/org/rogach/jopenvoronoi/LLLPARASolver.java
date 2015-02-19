package org.rogach.jopenvoronoi;

import java.util.List;
import java.util.ArrayList;

/// \brief line-line-line Solver (parallel line-segment case)
///
/// solves 3x3 system.
public class LLLPARASolver extends Solver {
// parallel linesegment edge case.
//  a1 x + b1 y + c1 + k1 t = 0
//  a2 x + b2 y + c2 + k2 t = 0
//  a3 x + b3 y + c3 + k3 t = 0
//
// s1 and s2 are parallel, so they have a PARA_LINELINE edge between them
//
// this constrains the solution to lie on a line parallel to s1/s2
// passing through a point equidistant from s1/s2
//
// equation of bisector is:
// ab x + bb y + cb = 0
// ab = a1
// bb = b1
// cb = (c1+c2)2
// all points on the bisector have a t value
// tb = fabs(c1-c2)/2
//
// find intersection of bisector and offset of third site
//  ab x + bb y + cb = 0
//  a3 x + b3 y + c3 + k3 tb = 0
//  or
//  ( ab  bb ) ( x ) = ( -cb )
//  ( a3  b3 ) ( y ) = ( -c3-k3*tb )
//


    public int solve( Site s1, double k1,
               Site s2, double k2,
               Site s3, double k3, List<Solution> slns ) {
        assert( s1.isLine() && s2.isLine() && s3.isLine() );

        Eq bisector = new Eq();
        bisector.a = s1.a();
        bisector.b = s1.b();
        double s2c = s2.c();

        // if s1 and s2 have opposite (a,b) normals, flip the sign of s2c
        Point n0 = new Point(s1.a(), s1.b());
        Point n1 = new Point(s2.a(), s2.b());
        if (n0.dot(n1) < 0)
            {
                s2c = -s2c;
            }

        bisector.c = (s1.c() + s2c)*0.5;
        double tb = 0.5*Math.abs(s1.c() - s2c); // bisector offset distance

        Pair<Double, Double> xy = two_by_two_solver(bisector.a, bisector.b, s3.a(), s3.b(), -bisector.c, -s3.c()-k3*tb);
        if (xy != null) {
            Point psln = new Point(xy.getFirst(), xy.getSecond());
            if (s1.end().sub(s1.start()).cross(psln.sub(s1.start())) * k1 < 0 ||
                s2.end().sub(s2.start()).cross(psln.sub(s2.start())) * k2 < 0 ||
                s3.end().sub(s3.start()).cross(psln.sub(s3.start())) * k3 < 0) {
                // solution lies on the wrong side from one of the lines
                return 0;
            } else {
                slns.add(new Solution(psln, tb, k3));
                return 1;
            }
        } else {
            return 0;
        }
    }

    /// solve 2z2 system Ax = y by inverting A
    /// x = Ainv * y
    /// returns false if det(A)==0, i.e. no solution found
    Pair<Double, Double> two_by_two_solver( double a,
                                            double b,
                                            double c,
                                            double d,
                                            double e,
                                            double f) {
        //  [ a  b ] [u] = [ e ]
        //  [ c  d ] [v] = [ f ]
        // matrix inverse is
        //          [ d  -b ]
        //  1/det * [ -c  a ]
        //  so
        //  [u]              [ d  -b ] [ e ]
        //  [v]  =  1/det *  [ -c  a ] [ f ]
        double det = a*d-c*b;
        if ( Math.abs(det) < 1e-15 ) // TOLERANCE!!
            return null;
        double u = (1.0/det) * (d*e - b*f);
        double v = (1.0/det) * (-c*e + a*f);
        return new Pair<Double, Double>(u, v);
    }

};
