package org.rogach.jopenvoronoi;

public class Face {
    public Edge edge;
    public Site site;
    public FaceStatus status;
    public boolean is_null_face;
    public Face() {}
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("F(");
        Edge current = edge;
        do {
            sb.append(current.source.position);
            sb.append(">");
            current = current.next;
        } while (current != edge);
        sb.append(")");
        return sb.toString();
    }
}
