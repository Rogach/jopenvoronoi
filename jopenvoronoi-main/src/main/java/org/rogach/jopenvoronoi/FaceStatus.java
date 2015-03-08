package org.rogach.jopenvoronoi;

public enum FaceStatus {
    INCIDENT,    /*!< INCIDENT faces contain one or more IN-vertex */
    NONINCIDENT  /*!< NONINCIDENT faces contain only OUT/UNDECIDED-vertices */
}
