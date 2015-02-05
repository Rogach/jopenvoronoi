package org.rogach.jopenvoronoi;

/// As we incrementally construct the diagram voronoi-vertices
/// can have one of these four different states.
public enum VertexStatus {
    OUT,          /*!< OUT-vertices will not be deleted */
    IN,           /*!< IN-vertices will be deleted */
    UNDECIDED,    /*!< UNDECIDED-vertices have not been examied yet */
    NEW           /*!< NEW-vertices are constructed on OUT-IN edges */
}
