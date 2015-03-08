package org.rogach.jopenvoronoi;

/// \brief Filter for retaining the medial-axis of a voronoi diagram
///
/// (approximate) medial-axis Filter.
/// marks the valid-property true for edges belonging to the medial axis
/// and false for other edges.
public class MedialAxisFilter extends Filter {
    double _dot_product_threshold; ///< a dot-product threshold in [0,1] for filtering out edges between nearly parallel LineSite segments

    /// \param thr dot-product threshold used to decide whether the segments
    /// that connect to a given Edge are nearly parallel
    public MedialAxisFilter() {
        _dot_product_threshold = 0.8;
    }
    public MedialAxisFilter( double thr) {
        _dot_product_threshold = thr;
    }

    /// predicate that decides if an edge is to be included or not.
    public boolean apply(Edge e) {
        if (e.type == EdgeType.LINESITE || e.type == EdgeType.NULLEDGE)
            return true; // we keep linesites and nulledges
        if (e.type == EdgeType.SEPARATOR)
            return false; // separators are allways removed

        if (both_endpoints_positive(e)) // these are interior edges which we keep.
            return true;

        // this leaves us with edges where one end connects to the polygon (dist==0)
        // and the other end does not.
        // figure out the angle between the adjacent line-segments and decide based on the angle.
        if (segments_parallel(e))
            return false;

        return true; // otherwise we keep the edge
    }

    /// return true if this is an internal edge, i.e. both endpoints have a nonzero clearance-disk radius
    private boolean both_endpoints_positive(Edge e) {
        Vertex src = e.source;
        Vertex trg = e.target;
        return (src.dist()>0) && (trg.dist()>0);
    }
    /// return true if the segments that connect to the given Edge are nearly parallel
    private boolean segments_parallel(Edge e ) {
        Vertex endp1 = find_endpoint(e);
        Vertex endp2 = find_endpoint(e.twin );
        // find the segments
        Edge e1 = find_segment(endp1);
        Edge e2 = find_segment(endp2);
        e2 = e2.twin; // this makes the edges oriented in the same direction
        double dotprod = edge_dotprod(e1,e2);
        return dotprod >_dot_product_threshold;
    }

    /// \brief calculate the dot-product between unit vectors aligned along edges e1->e2
    ///
    /// since e1 and e2 are both line-sites the direction is easy to find
    /// FIXME: more code needed here for tangent calculation if we have arc-sites
    private double edge_dotprod(Edge e1, Edge e2) {
        Vertex src1 = e1.source;
        Vertex trg1 = e1.target;
        Vertex src2 = e2.source;
        Vertex trg2 = e2.target;
        Point sp1 = src1.position;
        Point tp1 = trg1.position;
        Point sp2 = src2.position;
        Point tp2 = trg2.position;

        Point dir1 = tp1.sub(sp1);
        Point dir2 = tp2.sub(sp2);
        dir1.normalize();
        dir2.normalize();
        return dir1.dot(dir2);
    }

    /// find the LineSite edge that connects to \a v
    Edge find_segment(Vertex v) {
        for (Edge e : v.out_edges) {
            if (e.type == EdgeType.LINESITE) {
                return e;
            }
        }
        throw new RuntimeException("Failed to find line segment from vertex");
    }

    /// find an ::ENDPOINT vertex that connects to Edge e through a ::NULLEDGE at either the source or target of e.
    Vertex find_endpoint(Edge e) {
        Edge next = e.next;
        Edge prev = g.previous_edge(e);
        Vertex endp;
        if ( next.type == EdgeType.NULLEDGE ) {
            endp = next.target;
            assert(endp.type == VertexType.ENDPOINT ) : "endp.type == VertexType.ENDPOINT ";
        } else if (prev.type == EdgeType.NULLEDGE ) {
            endp = prev.source;
            assert(endp.type == VertexType.ENDPOINT ) : "endp.type == VertexType.ENDPOINT ";
        } else {
            throw new RuntimeException("Failed to find endpoint");
        }
        return endp;
    }

};
