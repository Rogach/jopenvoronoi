package org.rogach.jopenvoronoi;

public class Main {
    public static void main(String[] args) throws Exception {
        VoronoiDiagram vd = new VoronoiDiagram();

        Point p0 = new Point(-0.1, -0.2);
        Point p1 = new Point(0.2, 0.1);
        Point p2 = new Point(0.4, 0.2);
        Point p3 = new Point(0.6, 0.6);
        Point p4 = new Point(-0.6, 0.3);

        Vertex v0 = vd.insert_point_site(p0);
        Vertex v1 = vd.insert_point_site(p1);
        Vertex v2 = vd.insert_point_site(p2);
        Vertex v3 = vd.insert_point_site(p3);
        Vertex v4 = vd.insert_point_site(p4);

        vd.insert_line_site(v0, v1);
        vd.insert_line_site(v1, v2);
        vd.insert_line_site(v2, v3);
        vd.insert_line_site(v3, v4);
        vd.insert_line_site(v4, v0);

        vd.check();

        vd.filter(new MedialAxisFilter());

        // // benchmark :)
        // val stt = System.currentTimeMillis
        // (1 to 10000).foreach { _ =>
        //   val x = scala.util.Random.nextDouble * 1.4 - 0.7
        //   val y = scala.util.Random.nextDouble * 1.4 - 0.7
        //   vd.insert_point_site(new Point(x,y))
        // }
        // val end = System.currentTimeMillis
        // printf("elapsed %d ms\n", end - stt)

        SvgOutput.output(vd, "medial_axis_test.svg");
    }
}
