package org.rogach.jopenvoronoi;

import java.util.*;

/// \brief From a voronoi-diagram, generate offsets.
///
/// an offset is allways a closed loop.
/// the loop consists of offset-elements from each face that the loop visits.
/// each face is associated with a Site, and the offset element from
/// - a point-site is a circular arc
/// - a line-site is a line
/// - an arc is a circular arc
///
/// This class produces offsets at the given offset-distance on the entire
/// voronoi-diagram. To produce offsets only inside or outside a given geometry,
/// use a filter first. The filter sets the valid-property of edges, so that offsets
/// are not produced on faces with one or more invalid edge.
public class Offset {
    HalfEdgeDiagram g; ///< vd-graph
    Set<Face> remaining_faces = new HashSet<>();
    List<OffsetLoop> offset_list; ///< list of output offsets

    /// \param gi vd-graph
    public Offset(HalfEdgeDiagram g) {
        this.g = g;
    }

    /// create offsets at offset distance \a t
    public List<OffsetLoop> offset(double t) {
        offset_list = new ArrayList<OffsetLoop>();
        set_flags(t);
        Face start;
        while ((start = find_start_face()) != null) { // while there are faces that still require offsets
            offset_loop_walk(start, t); // start on the face, and do an offset loop
        }

        return offset_list;
    }

    /// find a suitable start face
    private Face find_start_face() {
        if (!remaining_faces.isEmpty()) {
            return remaining_faces.iterator().next();
        } else {
            return null;
        }
    }

    /// perform an offset walk at given distance \a t,
    /// starting at the given face
    private void offset_loop_walk(Face start, double t) {
        boolean out_in_mode = false;
        Edge start_edge = find_next_offset_edge(start.edge, t, out_in_mode); // the first edge on the start-face
        Edge current_edge = start_edge;
        OffsetLoop loop = new OffsetLoop(); // store the output in this loop
        loop.offset_distance = t;
        loop.add(new OffsetVertex(current_edge.point(t), current_edge));
        do {
            out_in_mode = edge_mode(current_edge, t);
            // find the next edge
            Edge next_edge = find_next_offset_edge(current_edge.next, t, out_in_mode);
            Face current_face = current_edge.face;
            loop.add(offset_element_from_face(current_face, current_edge, next_edge, t));
            remaining_faces.remove(current_face); // although we may revisit current_face (if it is non-convex), it seems safe to mark it "done" here.
            current_edge = next_edge.twin;
        } while (current_edge != start_edge);
        offset_list.add(loop); // append the created loop to the output
    }


    /// return an offset-element corresponding to the current face
    private OffsetVertex offset_element_from_face(Face current_face, Edge current_edge, Edge next_edge, double t) {
        Site s = current_face.site;
        Ofs o = s.offset(current_edge.point(t), next_edge.point(t)); // ask the Site for offset-geometry here.
        boolean cw = true;
        if (!s.isLine()) { // point and arc-sites produce arc-offsets, for which cw must be set.
            cw = find_cw(o.start(), o.center(), o.end() ); // figure out cw or ccw arcs?
        }
        // add offset to output
        return new OffsetVertex(next_edge.point(t), o.radius(), o.center(), cw, current_face, next_edge);
    }

    /// \brief figure out mode (?)
    private boolean edge_mode(Edge e, double t) {
        Vertex src = e.source;
        Vertex trg = e.target;
        double src_r = src.dist();
        double trg_r = trg.dist();
        if ((src_r<t) && (t<trg_r)) {
            return true;
        } else if ((trg_r<t) && (t<src_r)) {
            return false;
        } else {
            assert(false) : "failed to determine edge mode";
            return false;
        }
    }

    /// figure out cw or ccw for an arc
    private boolean find_cw(Point start, Point center, Point end) {
        return center.is_right(start,end); // NOTE: this only works for arcs smaller than a half-circle !
    }


    /// \brief starting at e, find the next edge on the face that brackets t
    ///
    /// we can be in one of two modes.
    /// if mode==false then we are looking for an edge where src_t < t < trg_t
    /// if mode==true we are looning for an edge where       trg_t < t < src_t
    private Edge find_next_offset_edge(Edge e, double t, boolean mode) {
        Edge start = e;
        Edge current = start;
        Edge ofs_edge = e;
        do {
            Vertex src = current.source;
            Vertex trg = current.target;
            double src_r = src.dist();
            double trg_r = trg.dist();
            if (!mode && (src_r<t) && (t<trg_r)) {
                ofs_edge = current;
                break;
            } else if (mode && (trg_r<t) && (t<src_r)) {
                ofs_edge = current;
                break;
            }
            current = current.next;
        } while (current!=start);
        return ofs_edge;
    }

    /// go through all faces and set flag=0 if the face requires an offset.
    private void set_flags(double t) {
        for (Face f : g.faces) {
            Edge start = f.edge;
            Edge current = start;
            do {
                Vertex src = current.source;
                Vertex trg = current.target;
                double src_r = src.dist();
                double trg_r = trg.dist();
                if (t_bracket(src_r,trg_r,t)) {
                    remaining_faces.add(f);
                }
                current = current.next;
            } while (current!=start);
        }

        // again go through faces again, and set flag=1 if any edge on the face is invalid
        // this is required because an upstream filter will set valid=false on some edges,
        // but not all, on a face where we do not want offsets.
        for (Face f : g.faces) {
            Edge start = f.edge;
            Edge current = start;
            do {
                if (!current.valid) {
                    remaining_faces.remove(f); // don't offset faces with invalid edges
                }
                current = current.next;
            } while (current!=start);
        }
    }


    /// is t in (a,b) ?
    private boolean t_bracket(double a, double b, double t) {
        double min_t = Math.min(a,b);
        double max_t = Math.max(a,b);
        return ((min_t<t) && (t<max_t));
    }

}
