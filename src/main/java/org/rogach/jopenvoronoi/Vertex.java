package org.rogach.jopenvoronoi;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/// \brief A vertex in the voronoi diagram
///
/// an object of this type is held in the BGL-graph for each vertex.
public class Vertex {
    public List<Edge> out_edges = new ArrayList<>();
    public List<Edge> in_edges = new ArrayList<>();

    public Vertex() {}

    public int degree() {
        return out_edges.size() + in_edges.size();
    }
    public static int count = 0; ///< global vertex count \todo hold this in hedigraph instead?

    /// A map of this type is used by VoronoiDiagramChecker to check that all vertices
    /// have the expected (correct) degree (i.e. number of edges)
    /// map for checking topology correctness
    public static Map<VertexType, Integer> expected_degree = new HashMap<>();
    static {
        expected_degree.put(VertexType.OUTER, 4);     // special outer vertices
        expected_degree.put(VertexType.NORMAL, 6);    // normal vertex in the graph
        expected_degree.put(VertexType.POINTSITE, 0); // point site
        expected_degree.put(VertexType.ENDPOINT, 6);  // end-point of line or arc
        expected_degree.put(VertexType.SEPPOINT, 6);  // end-point of separator
        expected_degree.put(VertexType.SPLIT, 4);     // split point, to avoid loops in delete-tree
        expected_degree.put(VertexType.APEX, 4);      // apex point on quadratic bisector
    }

    public VertexStatus status; ///< vertex status. updated/changed during an incremental graph update
    public VertexType type; ///< The type of the vertex. Never(?) changes
    public double max_error; ///< \todo what is this? remove?
    public boolean in_queue; ///< flag for indicating wether vertex is in the vertexQueue
    public Point position; ///< the position of the vertex.
    public double k3;  ///< the offset-direction {-1,+1} of this vertex to the newly inserted site.
    public double alfa; ///< diangle for a null-vertex. only for debug-drawing
    public Face null_face; ///< if this is a null-face, a handle to the null-face
    public Face face; ///< the face of this vertex, if the vertex is a point-site
    public double r; ///< clearance-disk radius, i.e. the closest Site is at this distance

    /// ctor with given status and type
    public Vertex( Point p, VertexStatus st, VertexType t) {
        init(p,st,t);
    }

    /// ctor with initial apex Point
    public Vertex( Point p, VertexStatus st, VertexType t, Point initDist) {
        init(p,st,t,initDist);
    }

    /// ctor with initial k3-value
    public Vertex( Point p, VertexStatus st, VertexType t, Point initDist, double lk3) {
        init(p,st,t,initDist,lk3);
    }

    /// ctor with initial clearance-disk radius
    public Vertex( Point p, VertexStatus st, VertexType t, double init_radius) {
        init(p,st,t);
        r = init_radius;
    }

    /// set index, increase count, initialize in_queue to false.
    void init() {
        count++;
        in_queue = false;
        alfa=-1; // invalid/non-initialized alfa value
        null_face = null;
        type = VertexType.NORMAL;
        face = null;
        max_error = 0;
    }

    /// set position and status
    void init(Point p, VertexStatus st) {
        init();
        position=p;
        status=st;
    }

    /// set position, status and type
    void init(Point p, VertexStatus st, VertexType t) {
        init(p,st);
        type = t;
    }

    /// set position, status, type, and clearance-disk through givem apex-point
    void init(Point p, VertexStatus st, VertexType t, Point initDist) {
        init(p,st,t);
        init_dist(initDist);
    }

    /// set position, status, type, clerance-disk radius, and k3-side
    void init(Point p, VertexStatus st, VertexType t, Point initDist, double lk3) {
        init(p,st,t,initDist);
        k3 = lk3;
    }

    /// set in_queue false, and status to ::UNDECIDED
    void reset_status() {
        in_queue = false;
        status = VertexStatus.UNDECIDED;
    }

    void set_alfa(Point dir) {
        alfa = Numeric.diangle(dir.x,dir.y);
    }

    /// initialize clerance-disk
    void init_dist(Point p) {
        r = dist(p);
    }

    /// return distance to a point from this vertex
    public double dist(Point p) {
        return position.sub(p).norm();
    }

    /// set clearance-disk to zero
    void zero_dist() {
        r=0;
    }

    /// return clearance disk-radius
    public double dist() {
        return r;
    }

    /// in-circle predicate
    double in_circle(Point p) {
        return dist(p) - r;
    }

    /// reset the index count
    public static void reset_count() {
        count = 0;
    }
}
