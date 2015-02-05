package org.rogach.jopenvoronoi;

import java.util.List;
import java.util.ArrayList;
import static org.rogach.jopenvoronoi.Numeric.sq;

/// point-point-point Solver (based on Sugihara & Iri paper)
public class PPPSolver extends Solver {
    public int solve( Site s1, double k1, Site s2, double k2, Site s3, double k3, List<Solution> slns ) {
        assert( s1.isPoint() && s2.isPoint() && s3.isPoint() );
        Point pi = s1.position();
        Point pj = s2.position();
        Point pk = s3.position();

        if ( pi.is_right(pj,pk) ) {
            Point tmp = pi;
            pi = pj;
            pj = tmp;
        }
        assert( !pi.is_right(pj,pk) );
        // 2) point pk should have the largest angle. largest angle is opposite longest side.
        double longest_side = pi.sub(pj).norm();
        while (  (pj.sub(pk).norm() > longest_side) || ((pi.sub(pk).norm() > longest_side)) ) {
            // cyclic rotation of points until pk is opposite the longest side pi-pj
            Point tmp = pk;
            pk = pj;
            pj = pi;
            pi = tmp;
            longest_side = pi.sub(pj).norm();
        }
        assert( !pi.is_right(pj,pk) );
        assert( pi.sub(pj).norm() >=  pj.sub(pk).norm() );
        assert( pi.sub(pj).norm() >=  pk.sub(pi).norm() );

        double J2 = (pi.y-pk.y)*(sq(pj.x-pk.x)+sq(pj.y-pk.y) )/2.0 -
            (pj.y-pk.y)*( sq(pi.x-pk.x)+sq(pi.y-pk.y) )/2.0;
        double J3 = (pi.x-pk.x)*( sq(pj.x-pk.x)+sq(pj.y-pk.y) )/2.0 -
            (pj.x-pk.x)*( sq(pi.x-pk.x)+sq(pi.y-pk.y) )/2.0;
        double J4 = (pi.x-pk.x)*(pj.y-pk.y) - (pj.x-pk.x)*(pi.y-pk.y);
        assert( J4 != 0.0 );
        if (J4==0.0) {
            throw new RuntimeException(" PPPSolver: Warning divide-by-zero!!");
        }
        Point sln_pt = new Point( -J2/J4 + pk.x, J3/J4 + pk.y );
        double dist = sln_pt.sub(pi).norm();
        slns.add( new Solution(  sln_pt , dist , +1) );
        return 1;
    }

}
