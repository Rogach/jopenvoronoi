package org.rogach.jopenvoronoi

import scala.collection.JavaConverters._

object Main extends App {
  val vd = new VoronoiDiagram()

  val p0 = new Point(-0.1, -0.2)
  val p1 = new Point(0.2, 0.1)

  val v0 = vd.insert_point_site(p0)
  val v1 = vd.insert_point_site(p1)

  // vd.insert_line_site(v0, v1)

  SvgOutput.output(vd, "medial_axis_test.svg")
}
