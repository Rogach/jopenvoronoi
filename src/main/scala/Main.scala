package org.rogach.jopenvoronoi

import scala.collection.JavaConverters._
import java.io._

object Main extends App {
  try {
    val vd = new VoronoiDiagram()

    val p0 = new Point(-0.1, -0.2)
    val p1 = new Point(0.2, 0.1)
    val p2 = new Point(0.4, 0.2)
    val p3 = new Point(0.6, 0.6)
    val p4 = new Point(-0.6, 0.3)

    val v0 = vd.insert_point_site(p0)
    val v1 = vd.insert_point_site(p1)
    val v2 = vd.insert_point_site(p2)
    val v3 = vd.insert_point_site(p3)
    val v4 = vd.insert_point_site(p4)

    vd.insert_line_site(v0, v1)
    vd.insert_line_site(v1, v2)
    vd.insert_line_site(v2, v3)
    vd.insert_line_site(v3, v4)
    vd.insert_line_site(v4, v0)

    vd.check()

    vd.filter(new MedialAxisFilter())

    // // benchmark :)
    // val stt = System.currentTimeMillis
    // (1 to 10000).foreach { _ =>
    //   val x = scala.util.Random.nextDouble * 1.4 - 0.7
    //   val y = scala.util.Random.nextDouble * 1.4 - 0.7
    //   vd.insert_point_site(new Point(x,y))
    // }
    // val end = System.currentTimeMillis
    // printf("elapsed %d ms\n", end - stt)

    SvgOutput.output(vd, "medial_axis_test.svg")
  } catch { case e: Throwable =>
    // println(e.getMessage)
    val baos = new ByteArrayOutputStream()
    e.printStackTrace(new PrintStream(baos))
    println(baos.toString.split("\n").dropRight(29).mkString("\n"))
  }
}
