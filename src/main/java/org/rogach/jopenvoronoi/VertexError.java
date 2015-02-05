package org.rogach.jopenvoronoi;

/// \brief error functor for edge-based desperate solver
///
/// minimize error by searching for a point on the solution-edge
public class VertexError {
    HalfEdgeDiagram g; ///< vd-graph
    Edge edge; ///< existing edge on which we have positioned a new vertex
    Site s3; ///< newly inserted Site

    /// \param gi vd-graph
    /// \param sln_edge solution edge
    /// \param si3 newly inserted Site
    public VertexError(HalfEdgeDiagram gi, Edge sln_edge, Site si3) {
        this.g = gi;
        this.edge = sln_edge;
        this.s3 = si3;
    }

    /// return the vertex-error t-d3 where
    /// t3 is the distance from edge-point(t) to s3, and
    /// t is the offset-distance of the solution
    double apply(double t) {
        Point p = edge_point(t);
        double s3_dist = p.sub(s3.apex_point(p)).norm();
        return Math.abs(t-s3_dist);
    }
    /// return a point on the edge at given offset-distance
    /// \param t offset-distance ( >= 0 )
    Point edge_point(double t) {
        Point p;
        if ( edge.type == EdgeType.LINELINE ) { // this is a workaround because the LINELINE edge-parameters are wrong? at least in some cases?
            Vertex src = edge.source;
            Vertex trg = edge.target;
            Point src_p = src.position;
            Point trg_p = trg.position;
            double src_t = src.dist();
            double trg_t = trg.dist();
            // edge is src_p -> trg_p
            if ( trg_t > src_t ) {
                double frac = (t-src_t) / (trg_t-src_t);
                p = src_p.sub(trg_p.sub(src_p).mult(frac));
            } else {
                double frac = (t-trg_t) / (src_t-trg_t);
                p = trg_p.sub(src_p.sub(trg_p).mult(frac));
            }

        } else
            p = edge.point(t);
        return p;
    }
}
