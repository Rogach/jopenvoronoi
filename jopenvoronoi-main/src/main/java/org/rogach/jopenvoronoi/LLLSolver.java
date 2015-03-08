package org.rogach.jopenvoronoi;

import java.util.List;
import java.util.ArrayList;
import static org.rogach.jopenvoronoi.Numeric.determinant;
import static org.rogach.jopenvoronoi.Numeric.chop;

/// \brief line-line-line Solver
///
/// solves 3x3 system.
public class LLLSolver extends Solver {

//  a1 x + b1 y + c1 + k1 t = 0
//  a2 x + b2 y + c2 + k2 t = 0
//  a3 x + b3 y + c3 + k3 t = 0
//
// or in matrix form
//
//  ( a1 b1 k1 ) ( x )    ( c1 )
//  ( a2 b2 k2 ) ( y ) = -( c2 )          Ax = b
//  ( a3 b3 k3 ) ( t )    ( c3 )
//
//  Cramers rule x_i = det(A_i)/det(A)
//  where A_i is A with column i replaced by b

    public int solve( Site s1, double k1,
                      Site s2, double k2,
                      Site s3, double k3, List<Solution> slns ) {

        assert( s1.isLine() && s2.isLine() && s3.isLine() ) : " s1.isLine() && s2.isLine() && s3.isLine() ";

        List<Eq> eq = new ArrayList<>(); // equation-parameters, in quad-precision
        Site[] sites = new Site[] { s1, s2, s3 };
        double[] kvals = new double[] { k1, k2, k3 };
        for (int i=0;i<3;i++)
            eq.add( sites[i].eqp( kvals[i] ) );

        int i = 0, j=1, k=2;
        double d = chop( determinant( eq.get(i).a, eq.get(i).b, eq.get(i).k,
                                      eq.get(j).a, eq.get(j).b, eq.get(j).k,
                                      eq.get(k).a, eq.get(k).b, eq.get(k).k ) );
        double det_eps = 1e-6;
        if ( Math.abs(d) > det_eps ) {
            double t = determinant(  eq.get(i).a, eq.get(i).b, -eq.get(i).c,
                                     eq.get(j).a, eq.get(j).b, -eq.get(j).c,
                                     eq.get(k).a, eq.get(k).b, -eq.get(k).c ) / d ;
            if (t >= 0) {
                double sol_x = determinant(  -eq.get(i).c, eq.get(i).b, eq.get(i).k,
                                             -eq.get(j).c, eq.get(j).b, eq.get(j).k,
                                             -eq.get(k).c, eq.get(k).b, eq.get(k).k ) / d ;
                double sol_y = determinant(  eq.get(i).a, -eq.get(i).c, eq.get(i).k,
                                             eq.get(j).a, -eq.get(j).c, eq.get(j).k,
                                             eq.get(k).a, -eq.get(k).c, eq.get(k).k ) / d ;

                slns.add( new Solution( new Point( sol_x, sol_y ), t, k3 ) ); // kk3 just passes through without any effect!?
                return 1;
            }
        } else {
            // Try parallel solver as fallback, if the small determinant is due to nearly parallel edges
            for (i = 0; i < 3; i++)
                {
                    j = (i+1)%3;
                    double delta = Math.abs(eq.get(i).a*eq.get(j).b - eq.get(j).a*eq.get(i).b);
                    if (delta <= 1e-300)
                        {
                            LLLPARASolver para_solver = new LLLPARASolver();
                            List<Solution> paraSolutions = new ArrayList<>();
                            para_solver.solve(sites[i], kvals[i], sites[j], kvals[j], sites[(i+2)%3], kvals[(i+2)%3], paraSolutions);
                            int solution_count = 0;
                            for (Solution s : paraSolutions) {
                                // check that solution has proper offset-direction
                                if (s3.end().sub(s3.start()).cross(s.p.sub(s3.start())) * k3 >= 0) {
                                    slns.add(s);
                                    solution_count++;
                                }
                            }
                            return solution_count;
                        }
                }
        }
        return 0; // no solution if determinant zero, or t-value negative
    }

}
