package org.rogach.jopenvoronoi;

import java.util.List;
import java.util.ArrayList;

// this solver is called when we want to position a vertex on a SEPARATOR edge
// a SEPARATOR edge exists between a LineSite and one of its PointSite end-points
// the input sites are thus s1=LineSite and s2=PointSite  (if arcs are supported in the future then s1=ArcSite is possible)
// s3 can be either a LineSite or a PointSite (of arcs are supported, s3=ArcSite is possible)
//
//  s1 (LineSite) offset eq. is     a1 x + b1 y + c1 + k1 t = 0
//  s2 (PointSite) offset eq. is    (x-x2)^2 + (y-y2)^2 = t^2
//
// Two possibilities for s3:
//   s3 (LineSite)   a3 x + b3 y + c3 + k3 t = 0           (1)
//   s3 (PointSite)  (x-x3)^2 + (y-y3)^2 = t^2             (2)
//
// This configuration constrains the solution to lie on the separator edge.
// The separator is given by
// SEP = p2 + t* sv
// where p2 is the location of s2, and the separator direction sv is
// sv = (-a1,-b1)   if k1=-1
// sv = (a1,b1)   if k1=+1
// thus points on the separator are located at:
//
//  x_sep = x2 + t*sv.x
//  y_sep = y2 + t*sv.y
//
//  This can be inserted into (1) or (2) above, which leads to a linear equation in t.
//
//  Insert into (1):
//    a3 (x2 + t*sv.x) + b3 (y2 + t*sv.y) + c3 + k3 t = 0
//      ==>
//          t = -( a3*x2 + b3*y2 + c3 ) / (sv.x*a3 + sv.y*b3 + k3)
//
//  Insert into (2):
//    (x2 + t*sv.x-x3)^2 + (y2 + t*sv.y-y3)^2 = t^2
//    ==> (using dx= x2-x3 and dy = x2-x3)
//   t^2 (sv.x^2 + sv.y^1 - 1)  + t (2*dx*sv.x + 2*dy*sv.y) + dx^2 + dy^2 = 0
//    ==>  (since sv is a unit-vector sv.x^2 + sv.y^1 - 1 = 0)
//         t = - (dx^2+dy^2) / (2*(dx*sv.x + dy*sv.y))
//
//  FIXME: what happens if we get a divide by zero situation ??
//
/// \brief alternative ::SEPARATOR Solver
public class ALTSEPSolver extends Solver {

    public int solve( Site s1, double k1,
                      Site s2, double k2,
                      Site s3, double k3, List<Solution> slns ) {
        Site lsite;
        Site psite;
        Site third_site;
        double lsite_k,  third_site_k;

        if ( type == 0 ) {
            lsite = s3; lsite_k = k3;
            psite = s1; // psite_k = k1;    l3 / p1 form a separator
            third_site = s2;      third_site_k = 1;
        } else if ( type == 1 ) {
            lsite = s3; lsite_k = k3;
            psite = s2; // psite_k = k2;    l3 / p2 form a separator
            third_site = s1; third_site_k = 1;
        } else {
            throw new RuntimeException("ALTSEPSolver FATAL ERROR! type not known.");
        }
        // separator direction
        Point sv = (k3 == - 1) ? new Point(lsite.a(),lsite.b()) : new Point(-lsite.a(),-lsite.b());

        // now we should have this:
        assert( lsite.isLine() && psite.isPoint() );

        double tsln = 0;

        if ( third_site.isPoint() ) {
            double dx = psite.x() - third_site.x();
            double dy = psite.y() - third_site.y();
            if ( Math.abs(2*( dx*sv.x+dy*sv.y  )) > 0 ) {
                tsln = -(dx*dx+dy*dy) / (2*( dx*sv.x+dy*sv.y  )); // check for divide-by-zero?
            } else {
                //std::cout << " no solutions. (isPoint)\n";
                return 0;
            }
        } else if (third_site.isLine()) {
            if ( Math.abs(( sv.x*third_site.a() + sv.y*third_site.b() + third_site_k )) > 0 ) {
                tsln = -(third_site.a()*psite.x()+third_site.b()*psite.y()+third_site.c()) /
                    ( sv.x*third_site.a() + sv.y*third_site.b() + third_site_k );
            } else {
                //std::cout << " no solutions. (isLine)\n";
                return 0;
            }
        } else {
            assert(false);
        }
        Point psln = new Point(psite.x(), psite.y()).add(sv.mult(tsln));
        slns.add( new Solution( psln, tsln, k3 ) );
        return 1;
    }

}
