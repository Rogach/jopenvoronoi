JOpenVoronoi
============

A quick-and-dirty port of [openvoronoi](https://github.com/aewallin/openvoronoi) library from C++ to Java.
Capable of creating 2D Voronoi segment diagrams and medial axes.

Code is in a mess - the port was done in 4 days, don't have the time to clean it up, but it works and is fast -
approximately a second to create diagram from 20000 points.

Building
========
Currently the project is built using Maven, but since it's just a bunch
of java files and a single jar dependency, you can quickly build it with whatever is at hand.

Source files are located in `jopenvoronoi-main/src` directory, jar dependency - `lib/commons-math3-3.4.1.jar`.

You can build a jar of the project with:
```
cd jopenjoronoi-main
mvn package
```

and run benchmarks:
```
./run.sh BenchmarkRandomPoints
./run.sh BenchmarkRandomPolygon
```

Example code
============

```java
import org.rogach.jopenvoronoi.*;

VoronoiDiagram vd = new VoronoiDiagram();
Vertex v1 = vd.insert_point_site(new Point(-0.4,-0.2));
Vertex v2 = vd.insert_point_site(new Point(0,0.4));
Vertex v3 = vd.insert_point_site(new Point(0.4,-0.2));
vd.insert_line_site(v1, v2);
vd.insert_line_site(v2, v3);
vd.insert_line_site(v3, v1);
SvgOutput.output(vd, "test.svg");
```

License
=======
JOpenVoronoi is released under GPLv3 (see COPYING), same as it's parent
 [openvoronoi](https://github.com/aewallin/openvoronoi) project.