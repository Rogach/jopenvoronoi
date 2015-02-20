package org.rogach.jopenvoronoi;

import java.util.List;
import java.util.ArrayList;
import static org.rogach.jopenvoronoi.Numeric.chop;
import static org.rogach.jopenvoronoi.Numeric.quadratic_roots;

/// \brief quadratic-linear-linear Solver
public class QLLSolver extends Solver {
    public int solve( Site s1, double k1,
               Site s2, double k2,
               Site s3, double k3, List<Solution> slns ) {
        // equation-parameters, in quad-precision
        List<Eq> quads = new ArrayList<>();
        List<Eq> lins = new ArrayList<>();
        Site[] sites = new Site[] { s1,s2,s3 };
        double[] kvals = new double[] {k1,k2,k3};
        for (int i=0;i<3;i++) {
            Eq eqn = sites[i].eqp(kvals[i]);
            if (sites[i].is_linear() ) // store site-equations in lins or quads
                lins.add( eqn );
            else
                quads.add( eqn );
        }
        assert( !quads.isEmpty() ) : " !quads.isEmpty() ";

        if ( lins.size()==1 || lins.size() == 0 ) {
            assert( quads.size() == 3 || quads.size() == 2 ) : " quads.size() == 3 || quads.size() == 2 ";
            for (int i=1;i<quads.size();i++) {
                quads.get(i).subEq(quads.get(0));
                lins.add(quads.get(i));
            }
        }
        assert( lins.size() == 2) : " lins.size() == 2";

        // TODO:  pick the solution appraoch with the best numerical stability.
        // call all three permutations
        // index shuffling determines if we solve:
        // x and y in terms of t
        // y and t in terms of x
        // t and x in terms of y
        qll_solver( lins, 0, 1, 2, quads.get(0), k3, slns);
        qll_solver( lins, 2, 0, 1, quads.get(0), k3, slns);
        qll_solver( lins, 1, 2, 0, quads.get(0), k3, slns);

        return slns.size();
    }


    /// \brief qll solver
    // l0 first linear eqn
    // l1 second linear eqn
    // xi,yi,ti  indexes to shuffle around
    // xk, yk, kk, rk = params of one ('last') quadratic site (point or arc)
    // solns = output solution triplets (x,y,t) or (u,v,t)
    // returns number of solutions found
    private int qll_solver(List<Eq> lins, int xi, int yi, int ti,
                           Eq quad, double k3, List<Solution> solns) {
        assert( lins.size() == 2 ) : " lins.size() == 2 ";
        double ai = lins.get(0).get(xi); // first linear
        double bi = lins.get(0).get(yi);
        double ki = lins.get(0).get(ti);
        double ci = lins.get(0).c;

        double aj = lins.get(1).get(xi); // second linear
        double bj = lins.get(1).get(yi);
        double kj = lins.get(1).get(ti);
        double cj = lins.get(1).c;

        double d = chop( ai*bj - aj*bi ); // chop! (determinant for 2 linear eqns (?))
        if (d == 0) // no solution can be found!
            return -1;
        // these are the w-equations for qll_solve()
        // (2) u = a1 w + b1
        // (3) v = a2 w + b2
        double a0 =  (bi*kj - bj*ki) / d;
        double a1 = -(ai*kj - aj*ki) / d;
        double b0 =  (bi*cj - bj*ci) / d;
        double b1 = -(ai*cj - aj*ci) / d;
        // based on the 'last' quadratic of (s1,s2,s3)
        double[][] aargs = new double[3][2];
        aargs[0][0] = 1.0;
        aargs[0][1] = quad.a;
        aargs[1][0] = 1.0;
        aargs[1][1] = quad.b;
        aargs[2][0] = -1.0;
        aargs[2][1] = quad.k;

        double[][] isolns = new double[2][3];
        // this solves for w, and returns either 0, 1, or 2 triplets of (u,v,t) in isolns
        // NOTE: indexes of aargs shuffled depending on (xi,yi,ti) !
        int scount = qll_solve( aargs[xi][0], aargs[xi][1],
                                aargs[yi][0], aargs[yi][1],
                                aargs[ti][0], aargs[ti][1],
                                quad.c, // xk*xk + yk*yk - rk*rk,
                                a0, b0,
                                a1, b1, isolns);
        double[][] tsolns = new double[2][3];
        for (int i=0; i<scount; i++) {
            tsolns[i][xi] = isolns[i][0];       // u       x
            tsolns[i][yi] = isolns[i][1];       // v       y
            tsolns[i][ti] = isolns[i][2];       // t       t  chop!
            solns.add(new Solution(new Point( tsolns[i][0], tsolns[i][1] ),
                                       tsolns[i][2], k3 ) );
        }
        //std::cout << " k3="<<kk3<<" qqq_solve found " << scount << " roots\n";
        return scount;
    }

    /// Solve a system of one quadratic equation, and two linear equations.
    ///
    /// (1) a0 u^2 + b0 u + c0 v^2 + d0 v + e0 w^2 + f0 w + g0 = 0
    /// (2) u = a1 w + b1
    /// (3) v = a2 w + b2
    /// solve (1) for w (can have 0, 1, or 2 roots)
    /// then substitute into (2) and (3) to find (u, v, t)
    private int qll_solve( double a0, double b0, double c0, double d0,
                           double e0, double f0, double g0,
                           double a1, double b1,
                           double a2, double b2,
                           double soln[][])
    {
        //std::cout << "qll_solver()\n";
        // TODO:  optimize using abs(a0) == abs(c0) == abs(d0) == 1
        double a = chop( (a0*(a1*a1) + c0*(a2*a2) + e0) );
        double b = chop( (2*a0*a1*b1 + 2*a2*b2*c0 + a1*b0 + a2*d0 + f0) );
        double c = a0*(b1*b1) + c0*(b2*b2) + b0*b1 + b2*d0 + g0;
        List<Double> roots = quadratic_roots(a, b, c); // solves a*w^2 + b*w + c = 0
        if ( roots.isEmpty() ) { // No roots, no solutions
            return 0;
        } else {
            for (int i=0; i<roots.size(); i++) {
                double w = roots.get(i);
                soln[i][0] = a1*w + b1; // u
                soln[i][1] = a2*w + b2; // v
                soln[i][2] = w;         // t
            }
            return roots.size();
        }
    }

};
