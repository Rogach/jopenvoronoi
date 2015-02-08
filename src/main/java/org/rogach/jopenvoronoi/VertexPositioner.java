package org.rogach.jopenvoronoi;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.*;

/// Calculates the (x,y) position of a VoronoiVertex in the VoronoiDiagram
public class VertexPositioner {
    /// predicate for rejecting out-of-region solutions
    class in_region_filter {
        /// the Site
        Site site_;
        /// \param s Site for in_region check
        public in_region_filter(Site s) {
            this.site_ = s;
        }
        /// is Solution \a s in_region of Site \a site_ ?
        public boolean apply(Solution s) {
            return !site_.in_region(s.p);
        }
    }

    /// predicate for filtering solutions based on t-value in [tmin,tmax] range
    class t_filter {
        /// minimum offset-distance value
        double tmin_;
        /// maximum offset-distance value
        double tmax_;

        /// create filter for [tmin,tmax]
        public t_filter(double tmin, double tmax) {
            this.tmin_ = tmin;
            this.tmax_ = tmax;
        }

        /// is the given Solution \a s in the offset-distance interval [tmin,tmax] ?
        public boolean apply(Solution s) {
            double eps=1e-9;
            double tround=s.t;
            if ( Math.abs(s.t-tmin_) < eps )
                tround=tmin_;
            else if (Math.abs(s.t-tmax_)<eps)
                tround=tmax_;
            return (tround<tmin_) || (tround>tmax_); // these points rejected!
        }
    };

// solvers, to which we dispatch, depending on the input sites

    Solver ppp_solver; ///< point-point-point solver
    Solver lll_solver; ///< line-line-line solver
    Solver lll_para_solver; ///< solver
    Solver qll_solver; ///< solver
    Solver sep_solver; ///< separator solver
    Solver alt_sep_solver; ///< alternative separator solver
// DATA
    HalfEdgeDiagram g;  ///< reference to the VD graph.
    double t_min; ///< minimum offset-distance
    double t_max; ///< maximum offset-distance
    Edge edge;  ///< the edge on which we position a new vertex
    List<Double> errstat = new ArrayList<>(); ///< error-statistics
    boolean silent; ///< silent mode (outputs no warnings to stdout)

    /// create positioner, set graph.
    public VertexPositioner(HalfEdgeDiagram gi) {
        this.g = gi;
        ppp_solver =      new PPPSolver();
        lll_solver =      new LLLSolver();
        qll_solver =      new QLLSolver();
        sep_solver =      new SEPSolver();
        alt_sep_solver =  new ALTSEPSolver();
        lll_para_solver = new LLLPARASolver();
        silent = false;
        errstat.clear();
    }

    /// \brief position a new vertex on given HEEdge \a e when inserting the new Site \a s3
    ///
    /// calculate the position of a new voronoi-vertex lying on the given edge.
    /// The new vertex is equidistant to the two sites that defined the edge
    /// and to the new site.
    // the edge e holds information about which face it belongs to.
    // each face holds information about which site created it
    // so the three sites defining the position of the vertex are:
    // - site to the left of HEEdge e
    // - site to the right of HEEdge e
    // - given new Site s
    Solution position(Edge e, Site s3) {
        edge = e;
        Face face = e.face;
        Edge twin = e.twin;
        Face twin_face = twin.face;

        Vertex src = e.source;
        Vertex trg = e.target;
        double t_src = src.dist();
        double t_trg = trg.dist();
        t_min = Math.min( t_src, t_trg ); // the solution we seek must have t_min<t<t_max
        t_max = Math.max( t_src, t_trg );

        Site s1 =  face.site;
        Site s2 = twin_face.site;

        Solution sl = position(  s1 , e.k, s2, twin.k, s3 );

        assert( solution_on_edge(sl) );
        //assert( check_far_circle(sl) );
        assert( check_dist(edge, sl, s3) );

        return sl;
    }

    /// position new vertex
    // find vertex that is equidistant from s1, s2, s3
    // should lie on the k1 side of s1, k2 side of s2
    // we try both k3=-1 and k3=+1 for s3
    Solution position(Site s1, double k1, Site s2, double k2, Site s3) {
        assert( (k1==1) || (k1 == -1) );
        assert( (k2==1) || (k2 == -1) );
        List<Solution> solutions = new ArrayList<>();

        solver_dispatch(s1,k1,s2,k2,s3,+1, solutions); // a single k3=+1 call for s3->isPoint()

        if (!s3.isPoint())
            solver_dispatch(s1,k1,s2,k2,s3,-1, solutions); // for lineSite or ArcSite we try k3=-1 also

        if ( solutions.size() == 1 && (t_min<=solutions.get(0).t) && (t_max>=solutions.get(0).t) && (s3.in_region( solutions.get(0).p)) )
            return solutions.get(0);

        // choose only in_region() solutions
        List<Solution> rejected_solutions = new ArrayList<>();
        for (Solution s : solutions) {
            if (!s3.in_region(s.p)) {
                rejected_solutions.add(s);
            } else if (new t_filter(t_min, t_max).apply(s)) {
                rejected_solutions.add(s);
            }
        }
        solutions.removeAll(rejected_solutions);

        if ( solutions.size() == 1) // if only one solution is found, return that.
            return solutions.get(0);
        else if (solutions.size()>1) {
            // two or more points remain so we must further filter here!
            // filter further using edge_error
            double min_error=100;
            Solution min_solution = new Solution(new Point(0,0),0,0);
            //std::cout << " edge_error filter: \n";
            for (Solution s : solutions) {
                double err = edge_error(s); //g[edge].error(s);
                if ( err < min_error) {
                    min_solution = s;
                    min_error = err;
                }
            }
            return min_solution;
        }


        // either 0, or >= 2 solutions found. This is an error.
        //throw new RuntimeException("None, or too many solutions found!");
        return desperate_solution(s3);
    }

    /// search numerically for a desperate solution along the solution-edge
    Solution desperate_solution(Site s3) {
        VertexError err_functor = new VertexError(g, edge, s3);
        Vertex src = edge.source;
        Vertex trg = edge.target;
        Point src_p = src.position;
        Point trg_p = trg.position;

        BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-14);
        double t_sln = optimizer.optimize(new MaxEval(1000),
                                          new UnivariateObjectiveFunction(err_functor),
                                          GoalType.MINIMIZE,
                                          new SearchInterval(t_min, t_max)).getPoint();
        Point p_sln = err_functor.edge_point(t_sln); //g[edge].point(t_sln);
        double desp_k3 = 0;
        if (s3.isPoint())
            desp_k3 = 1;
        else if ( s3.isLine() ) {
            // find out on which side the desperate solution lies
            Point src_se = s3.start();
            Point trg_se = s3.end();
            Point left = src_se.add(trg_se).mult(0.5).add(trg_se.sub(src_se).xy_perp());
            if (p_sln.is_right(src_se,trg_se)) {
                desp_k3 = (s3.k()==1) ? -1 : 1;
            } else {
                desp_k3 = (s3.k()==1) ? 1 : -1;
            }
        }
        return new Solution( p_sln, t_sln, desp_k3 );
    }

    /// dispatch to the correct solver based on the sites
    int solver_dispatch(Site s1, double k1, Site s2, double k2, Site s3, double k3,
                                          List<Solution> solns) {


        if ( edge.type == EdgeType.SEPARATOR ) {
            // this is a SEPARATOR edge with two LineSites adjacent.
            // find the PointSite that defines the SEPARATOR, so that one LineSite and one PointSite
            // can be submitted to the Solver.
            if ( s1.isLine() && s2.isLine() ) {
                // the parallell lineseg case      v0 --s1 --> pt -- s2 --> v1
                // find t
                if ( edge.has_null_face ) {
                    s2 = edge.null_face.site;
                    assert( s2.isPoint() ); // the sites of null-faces are allwais PointSite
                    k2 = +1;
                } else if (edge.twin.has_null_face ) {
                    s2 = edge.twin.null_face.site;
                    assert( s2.isPoint() );
                    k2 = +1;
                }
            } else if ( s1.isPoint() && s2.isLine() ) {
                // a normal SEPARATOR edge, defined by a PointSite and a LineSite
                // swap sites, so SEPSolver can assume s1=line s2=point
                Site tmp = s1;
                double k_tmp = k1;
                s1 = s2;
                s2 = tmp;
                k1 = k2;
                k2 = k_tmp;
                assert( s1.isLine() );
                assert( s2.isPoint() );
            }
            assert( s1.isLine() && s2.isPoint() ); // we have previously set s1(line) s2(point)
            return sep_solver.solve(s1,k1,s2,k2,s3,k3,solns);
        } else if ( edge.type == EdgeType.PARA_LINELINE  && s3.isLine() ) { // an edge betwee parallel LineSites
            //std::cout << " para lineline! \n";
            return lll_para_solver.solve( s1,k1,s2,k2,s3,k3, solns );
        } else if ( s1.isLine() && s2.isLine() && s3.isLine() )
            return lll_solver.solve( s1,k1,s2,k2,s3,k3, solns ); // all lines.
        else if ( s1.isPoint() && s2.isPoint() && s3.isPoint() )
            return ppp_solver.solve( s1,1,s2,1,s3,1, solns ); // all points, no need to specify k1,k2,k3, they are all +1
        else if ( (s3.isLine() && s1.isPoint() ) ||
                  (s1.isLine() && s3.isPoint() ) ||
                  (s3.isLine() && s2.isPoint() ) ||
                  (s2.isLine() && s3.isPoint() ) // bad coverage for this line?
                  ) {
            // if s1/s2 form a SEPARATOR-edge, this is dispatched automatically to sep-solver
            // here we detect for a separator case between
            // s1/s3
            // s2/s3
            if (s3.isLine() && s1.isPoint() ) {
                if ( detect_sep_case(s3,s1) ) {
                    alt_sep_solver.set_type(0);
                    return alt_sep_solver.solve(s1, k1, s2, k2, s3, k3, solns );
                }
            }
            if (s3.isLine() && s2.isPoint() ) {
                if ( detect_sep_case(s3,s2) ) {
                    alt_sep_solver.set_type(1);
                    return alt_sep_solver.solve(s1, k1, s2, k2, s3, k3, solns );
                }
            }
        }

        // if we didn't dispatch to a solver above, we try the general solver
        return qll_solver.solve( s1,k1,s2,k2,s3,k3, solns ); // general case solver

    }

    /// detect separator-case, so we can dispatch to the correct Solver
    boolean detect_sep_case(Site lsite, Site psite) {
        Edge le = lsite.edge();
        Vertex src = le.source;
        Vertex trg = le.target;
        // now from segment end-points get the null-vertex
        Edge src_out = null;
        for (Edge e : src.out_edges) {
            if (e.type == EdgeType.NULLEDGE) {
                src_out = e;
            }
        }
        Edge trg_out = null;
        for (Edge e : trg.out_edges) {
            if (e.type == EdgeType.NULLEDGE) {
                trg_out = e;
            }
        }

        Face src_null_face = src_out.face;
        if (src_null_face.is_null_face == false ) {
            // take twin face instead
            Edge src_out_twin = src_out.twin;
            src_null_face = src_out_twin.face;
        }

        Face trg_null_face = trg_out.face;
        if ( trg_null_face.is_null_face == false ) {
            Edge trg_out_twin = trg_out.twin;
            trg_null_face = trg_out_twin.face;
        }
        assert( src_null_face.is_null_face && trg_null_face.is_null_face );

        // do we want src_out face??
        // OR src_out_twin face??
        // we want the null-face !

        Site src_site = src_null_face.site;
        Site trg_site = trg_null_face.site;
        if (src_site == null || trg_site == null ) {
            throw new RuntimeException();
        }
        if ( !src_site.isPoint() || !trg_site.isPoint() ) {
            throw new RuntimeException();
        }
        Vertex src_vertex = src_site.vertex();
        Vertex trg_vertex = trg_site.vertex();
        if ( src_vertex == psite.vertex() ) {
            return true;
        }
        if ( trg_vertex == psite.vertex() ) {
            return true;
        }
        return false;
    }

    /// error from solution to corresponding point on the edge
    double edge_error(Solution sl) {
        Point p;
        if (edge.type==EdgeType.PARA_LINELINE) {
            p = projection_point( sl );
        } else {
            p = edge.point( sl.t );
        }
        return p.sub(sl.p).norm();
    }

    /// when the edge is not parametrized by t-value as normal edges
    /// so we need a projection of sl onto the edge instead
    Point projection_point(Solution sl) {
        assert( edge.type == EdgeType.PARA_LINELINE );
        // edge given by
        // p = p0 + t * (p1-p0)   with t in [0,1]
        Point p0 = new Point(edge.source.position);
        Point p1 = new Point(edge.target.position);
        Point v = p1.sub(p0);

        double t = sl.p.sub(p0).dot(v) / v.dot(v);
        // clamp to [0,1]
        if ( t>1)
            t=1;
        else if (t<0)
            t=0;
        return p0.add(v.mult(t));
    }

    /// check that the new solution lies on the edge
    boolean solution_on_edge(Solution s) {
        double err = edge_error(s);
        double limit = 9E-4;
        return (err<limit);
    }


    /// new vertices should lie within the far_radius
    boolean check_far_circle(Solution s) {
        if (!(s.p.norm() < 18*1)) {
            return false;
        }
        return true;
    }

    /// distance sanity check
    // all vertices should be of degree three, i.e. three adjacent faces/sites
    // distance to the three adjacent sites should be equal
    boolean check_dist(Edge e, Solution sl, Site s3) {
        Face face = e.face;
        Edge tw_edge = e.twin;
        Face twin_face = tw_edge.face;

        Site s1 = face.site;
        Site s2 = twin_face.site;

        double d1 = sl.p.sub(s1.apex_point(sl.p)).norm();
        double d2 = sl.p.sub(s2.apex_point(sl.p)).norm();
        double d3 = sl.p.sub(s3.apex_point(sl.p)).norm();

        double maxd = Math.max( Math.max( Math.abs(sl.t-d1),Math.abs(sl.t-d2)) , Math.abs(sl.t-d3));
        errstat.add(maxd);

        if ( !equal(d1,d2) || !equal(d1,d3) || !equal(d2,d3) ||
             !equal(sl.t,d1) || !equal(sl.t,d2) || !equal(sl.t,d3) ) {
            return false;
        }
        return true;
    }

    /// distance-error
    // new vertices should be equidistant to the three adjacent sites that define the vertex
    // we here calculate the distances d1, d2, d3 from the Solution to the three sites s1, s2, s3
    // and return the max deviation from the solution t-value.
    // this works as a sanity check for the solver.
    // a high error value here is also an indication of numerical instability in the solver
    double dist_error(Edge e, Solution sl, Site s3) {
        Face face = e.face;
        Edge tw_edge = e.twin;
        Face twin_face = tw_edge.face;

        Site s1 = face.site;
        Site s2 = twin_face.site;

        double d1 = sl.p.sub(s1.apex_point(sl.p) ).norm();
        double d2 = sl.p.sub(s2.apex_point(sl.p) ).norm();
        double d3 = sl.p.sub(s3.apex_point(sl.p) ).norm();

        return Math.max( Math.max( Math.abs(sl.t-d1),Math.abs(sl.t-d2)) , Math.abs(sl.t-d3));

    }

    /// are \a d1 and \a d2 roughly equal?
    boolean equal(double d1, double d2) {
        double tol = 1e-3;
        if ( Math.abs(d1-d2) < 1e-15 )
            return true;
        if ( Math.abs(d1-d2) > tol*Math.max(d1,d2) )
            return false;
        return true;
    }

};
