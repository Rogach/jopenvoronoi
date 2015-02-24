package org.rogach.jopenvoronoi;

import org.junit.*;

public class RunTest {

    @Test
    public void parallelyPolygon() {
        VoronoiDiagram vd = new VoronoiDiagram();

        Vertex v1 = vd.insert_point_site(new Point(0.15907424869091413, -0.22755592000227737));
        Vertex v2 = vd.insert_point_site(new Point(-0.158774429631718, -0.22755592000227737));
        Vertex v3 = vd.insert_point_site(new Point(-0.158774429631718, 0.5000000000000007));
        Vertex v4 = vd.insert_point_site(new Point(-0.44085019690616734, 0.5000000000000007));
        Vertex v5 = vd.insert_point_site(new Point(-0.44085019690616734, -0.4999999999999993));
        Vertex v6 = vd.insert_point_site(new Point(0.44085019690616595, -0.4999999999999993));
        Vertex v7 = vd.insert_point_site(new Point(0.44085019690616595, 0.4999999999999993));
        Vertex v8 = vd.insert_point_site(new Point(0.15907424869091413, 0.4999999999999993));

        vd.insert_line_site(v1, v2);
        vd.insert_line_site(v2, v3);
        vd.insert_line_site(v3, v4);
        vd.insert_line_site(v4, v5);
        vd.insert_line_site(v5, v6);
        vd.insert_line_site(v6, v7);
        vd.insert_line_site(v7, v8);
        vd.insert_line_site(v8, v1);

        vd.check();
    }

    @Test
    public void separatorPositioningMinimized() {
        VoronoiDiagram vd = new VoronoiDiagram();

        Vertex v1 = vd.insert_point_site(new Point(-0.2567719874411157,-0.4983049800651602));
        Vertex v3 = vd.insert_point_site(new Point(-0.25972854724944455,-0.5143879072702902));
        Vertex v4 = vd.insert_point_site(new Point(-0.34168692840153536,-0.6418861147966213));
        Vertex v5 = vd.insert_point_site(new Point(-0.5288215108461576,0.18480346369654843));
        Vertex v6 = vd.insert_point_site(new Point(-0.35263585687204546,-0.50735692278175));

        vd.insert_line_site(v3, v4);
        vd.insert_line_site(v5, v6);

        vd.check();
    }

    @Test
    public void separatorPositioningFull() {
        VoronoiDiagram vd = new VoronoiDiagram();
        Vertex v1 = vd.insert_point_site(new Point(-0.2567719874411157,-0.4983049800651602));
        Vertex v2 = vd.insert_point_site(new Point(0.12205285479992212,-0.640371712930281));
        Vertex v3 = vd.insert_point_site(new Point(-0.25972854724944455,-0.5143879072702902));
        Vertex v4 = vd.insert_point_site(new Point(-0.34168692840153536,-0.6418861147966213));
        Vertex v5 = vd.insert_point_site(new Point(-0.5288215108461576,0.18480346369654843));
        Vertex v6 = vd.insert_point_site(new Point(-0.35263585687204546,-0.50735692278175));
        Vertex v7 = vd.insert_point_site(new Point(-0.4821854389417177,0.46463421861462373));

        vd.insert_line_site(v1, v2);
        vd.insert_line_site(v2, v3);
        vd.insert_line_site(v3, v4);
        vd.insert_line_site(v4, v5);
        vd.insert_line_site(v5, v6);
        vd.insert_line_site(v6, v7);
        vd.insert_line_site(v7, v1);

        vd.check();
    }

    @Test
    public void emerge1() {
        VoronoiDiagram vd = new VoronoiDiagram();
        Vertex v1 = vd.insert_point_site(new Point(-0.0255809378746924,-0.40506709848555283));
        Vertex v2 = vd.insert_point_site(new Point(-0.1070304600334181,-0.32199887924165504));
        Vertex v3 = vd.insert_point_site(new Point(-0.08412200854010599,-0.4091862019580794));
        Vertex v4 = vd.insert_point_site(new Point(-0.018610976572355775,-0.44924671559984736));

        vd.insert_line_site(v1, v2);

        vd.check();
    }

    @Test
    public void lubed1() {
        VoronoiDiagram vd = new VoronoiDiagram();
        Vertex v1 = vd.insert_point_site(new Point(-0.4569868959788259,-0.6901889429096152));
        Vertex v2 = vd.insert_point_site(new Point(-0.4777558843163665,-0.6741982953078733));
        Vertex v3 = vd.insert_point_site(new Point(-0.5828928817215249,-0.6506785938772909));
        Vertex v4 = vd.insert_point_site(new Point(-0.6509162259830449,-0.6905569065947034));

        vd.insert_line_site(v1, v2);

        vd.check();
    }

    @Test
    public void lubed2() {
        VoronoiDiagram vd = new VoronoiDiagram();
        Vertex v1 = vd.insert_point_site(new Point(-0.26416290615203275,-0.6167731946769498));
        Vertex v2 = vd.insert_point_site(new Point(-0.49700490843397843,-0.642139619517225));
        Vertex v3 = vd.insert_point_site(new Point(-0.4540183254084518,-0.6092924695453144));
        Vertex v4 = vd.insert_point_site(new Point(-0.5513893549317856,-0.5712639908621272));

        vd.insert_line_site(v1, v2);

        vd.check();
    }

    @Test
    public void lubed3() {
        VoronoiDiagram vd = new VoronoiDiagram();
        Vertex v1 = vd.insert_point_site(new Point(0.41164403323558574,-0.6789386939364543));
        Vertex v2 = vd.insert_point_site(new Point(0.17427452375132169,-0.6522148409835533));
        Vertex v3 = vd.insert_point_site(new Point(0.15951277675921582,-0.6538785084950686));
        Vertex v4 = vd.insert_point_site(new Point(0.3890141779152667,-0.6928288255549844));
        Vertex v5 = vd.insert_point_site(new Point(0.3860913593953006,-0.6919949537598736));
        Vertex v6 = vd.insert_point_site(new Point(0.42532972342719244,-0.6914003202947938));
        Vertex v7 = vd.insert_point_site(new Point(-0.16212113598677202,-0.6733320323817963));

        vd.insert_line_site(v4, v5);
        vd.insert_line_site(v1, v2);
        vd.insert_line_site(v6, v7);

        vd.check();
    }

    @Test
    public void acted1() {
        VoronoiDiagram vd = new VoronoiDiagram();
        Vertex v1 = vd.insert_point_site(new Point(0.3862126318449599, 0.5264600856031032));
        Vertex v2 = vd.insert_point_site(new Point(0.6865969842890696, -0.21517998694176943));
        Vertex v3 = vd.insert_point_site(new Point(0.6346354715467906, -0.08747150829990835));
        Vertex v4 = vd.insert_point_site(new Point(0.4441427359131531, 0.38013360292792653));

        vd.insert_line_site(v1, v2);
        vd.insert_line_site(v2, v3);
        vd.insert_line_site(v3, v4);

        vd.check();
    }

    @Test
    public void acted2() {
        VoronoiDiagram vd = new VoronoiDiagram();

        Vertex v1 = vd.insert_point_site(new Point(0.5262779849041228,-0.18953355654322246));
        Vertex v2 = vd.insert_point_site(new Point(0.19507597140208333,0.30486762117317756));
        Vertex v3 = vd.insert_point_site(new Point(0.2112261870707135,0.280197231985694));
        Vertex v4 = vd.insert_point_site(new Point(0.1821522008347376,0.30546312177727897));
        Vertex v5 = vd.insert_point_site(new Point(0.19684192998991756, 0.2762259925193069));

        vd.insert_line_site(v1, v2);
        vd.insert_line_site(v3, v4);

        vd.check();
    }

    @Test
    public void khazar1() {
        VoronoiDiagram vd = new VoronoiDiagram();

        Vertex v1 = vd.insert_point_site(new Point( 0.5,-0.25000096153846174));
        Vertex v2 = vd.insert_point_site(new Point(-0.5,-0.25000096153846174));
        Vertex v3 = vd.insert_point_site(new Point(0.4896403846153846,0.23890288461538464));
        Vertex v4 = vd.insert_point_site(new Point(-0.36254423076923076,0.23890288461538464));
        Vertex v5 = vd.insert_point_site(new Point(0.4896365384615385,-0.23891442307692334));

        vd.insert_line_site(v1, v2);
        vd.insert_line_site(v3, v4);
        vd.insert_line_site(v5, v3);

        vd.check();
    }
}
