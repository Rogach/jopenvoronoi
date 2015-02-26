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
        int c = 0;
        do {
            if (current == null) break;
            sb.append(current.source.position);
            sb.append(">");
            current = current.next;
            c++;
        } while (current != edge && c < 100);
        if (c >= 100) {
            sb.append("...");
        }
        sb.append(")");
        return sb.toString();
    }
}
