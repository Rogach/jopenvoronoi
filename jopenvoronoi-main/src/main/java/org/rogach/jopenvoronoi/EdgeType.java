package org.rogach.jopenvoronoi;

public enum EdgeType {
    LINE,          /*!< Line edge between PointSite and PointSite */
    LINELINE,      /*!< Line edge between LineSite and LineSite */
    PARA_LINELINE, /*!< Line edge between LineSite and LineSite (parallel case) */
    OUTEDGE,       /*!< special outer edge set by initialize() */
    PARABOLA,      /*!< Parabolic edge between PointSite and LineSite */
    ELLIPSE,
    HYPERBOLA,
    SEPARATOR,     /*!< Separator edge between PointSite (endpoint) and LineSite or ArcSite */
    NULLEDGE,      /*!< zero-length null-edge around a PointSite which is and endpoint */
    LINESITE,       /*!< pseudo-edge corresponding to a LineSite */
    ARCSITE       /*!< pseudo-edge corresponding to a LineSite */
}
