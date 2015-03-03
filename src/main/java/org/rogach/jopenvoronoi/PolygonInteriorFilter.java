package org.rogach.jopenvoronoi;

/// \brief Filter for retaining voronoi-diagram inside a polygon
///
/// this filter sets the valid-property of edges
/// all interior edges are marked valid=true
/// all exterior edges are marked valid=false
///
/// a polygon/pocket boundary shoud be specified in CW order
/// islands within the polygon should be specified in CCW order
public class PolygonInteriorFilter extends Filter {

    private boolean side;

    /// \brief create a polygon interior Filter with given \a side
    /// \param side set true (false) for polygons inserted in CW (CCW) order and islands inserted in CCW (CW) order.
    public PolygonInteriorFilter(boolean side) {
        this.side = side;
    }
    
    /// determine if an edge is valid or not
    public boolean apply(Edge e) {
        
        if (e.type == EdgeType.LINESITE || e.type == EdgeType.NULLEDGE) {
            return true;
        }

        // if polygon inserted ccw  as (id1->id2), then the linesite should occur on valid faces as id1->id2
        // for islands and the outside the edge is id2->id1
        
        Face f = e.face;
        Site s = f.site;
        if (s.isLine() && linesite_ccw(f)) {
            return true;
        } else if (s.isPoint() ) {
            // we need to search for an adjacent linesite.
            // (? can we have a situation where this fails?)
            Edge linetwin = find_adjacent_linesite(f);
            if (linetwin != null) {
                Edge twin = linetwin.twin;
                Face twin_face = twin.face;
                if (linesite_ccw(twin_face)) {
                    return true;
                }
            } else {
                return false;
            }
        } 
        return false;
    }
    /// on the face f, find the adjacent linesite
    private Edge find_adjacent_linesite(Face f) {
        Edge current = f.edge;
        Edge start = current;
        
        do {
            Edge twin = current.twin;
            if (twin != null) {
                Face twf = twin.face;
                if (twf.site.isLine()) {
                    return current;
                }
            }
            current = current.next;
        } while (current!=start);
        
        return null;
    }
    /// return true if linesite was inserted in the direction indicated by _side
    private boolean linesite_ccw(Face f) {
        Edge current = f.edge;
        Edge start = current;
        do {
            if ((side && current.type == EdgeType.LINESITE && current.inserted_direction) ||
                  (!side && current.type == EdgeType.LINESITE && !current.inserted_direction)) {
                return true;
            }
            current = current.next;
        } while (current!=start);
        return false;
    }
}
