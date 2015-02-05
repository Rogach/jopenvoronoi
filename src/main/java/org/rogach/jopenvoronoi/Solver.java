package org.rogach.jopenvoronoi;

import java.util.List;

/// \brief abstract base-class for voronoi vertex position solvers
///
/// The input to the solver is three Sites (s1,s2,s3) and three offset-directions (k1,k2,k3).
/// The optput is a vector with one or more Solution.
public abstract class Solver {

    /// \brief solve for position of VoronoiVertex with given adjacent sites and directions
    ///
    /// \param s1 first adjacent Site
    /// \param k1 direction from \a s1 to new VoronoiVertex
    /// \param s2 second adjacent Site
    /// \param k2 direction from \a s2 to new VoronoiVertex
    /// \param s3 third adjacent Site
    /// \param k3 direction from \a s3 to new VoronoiVertex
    /// \param slns Solution vector, will be updated by Solver
    public abstract int solve(Site s1, double k1,
                              Site s2, double k2,
                              Site s3, double k3, List<Solution> slns );

    /// used by alt_sep_solver
    public void set_type(int t) {
        type=t;
    }

    /// set the debug mode to \a b
    public void set_debug(boolean b) {
        debug=b;
    }

    /// no warnings/messages to stdout will be written, if silent is set true.
    public void set_silent(boolean b) {
        silent=b;
    }

    /// flag for debug output
    boolean debug;
    /// separator case type.
    /// - type = 0 means l3 / p1 form a separator
    /// - type = 1 means l3 / p2 form a separator
    int  type;
    boolean silent; ///< suppress all warnings or other stdout output
}
