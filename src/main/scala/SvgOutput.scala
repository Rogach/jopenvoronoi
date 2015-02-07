package org.rogach.jopenvoronoi

import java.io._
import java.awt.Color
import scala.collection.JavaConverters._

object SvgOutput {
  def output(vd: VoronoiDiagram, fname: String) {
    val w = new PrintWriter(fname)
    val g = vd.get_graph_reference()

    // write header
    w.println("""<svg width="1024px" height="1024px">""")

    g.edges.asScala.foreach { e =>
      writeEdge(w, e)
    }

    // write footer
    w.println("""</svg>""")

    w.close()
  }

  def writeEdge(w: PrintWriter, e: Edge) {
    val src = e.source
    val trg = e.target
    val src_p = scale(src.position)
    val trg_p = scale(trg.position)

    val col = edge_color_string(e)
    val points =
      if (e.`type` == EdgeType.SEPARATOR ||
          e.`type` == EdgeType.LINE ||
          e.`type` == EdgeType.LINESITE ||
          e.`type` == EdgeType.OUTEDGE ||
          e.`type` == EdgeType.LINELINE ||
          e.`type` == EdgeType.PARA_LINELINE) {
        // edge drawn as two points
        List(src_p, trg_p)
      } else if (e.`type` == EdgeType.PARABOLA) {
        val t_src = src.dist
        val t_trg = trg.dist
        val t_min = math.min(t_src, t_trg)
        val t_max = math.max(t_src, t_trg)
        val nmax = 40
        (0 until nmax).map { n =>
          val t = t_min + ((t_max - t_min)/((nmax - 1)*(nmax - 1)))*n*n
          scale(e.point(t))
        }.toList
      } else {
        sys.error(s"Unexpected edge type: ${e.`type`}")
      }
    val pointsStr = points.map { p =>
      f"${p.x}%.3f,${p.y}%.3f"
    }.mkString(" ")
    w.println(s"""<polyline points="$pointsStr" fill="transparent" stroke-width="1" stroke="$col" />""")
  }

  def scale(p: Point) = {
    val s = 512d
    new Point(p.x * s + s, -p.y * s + s)
  }

  def edge_color_string(e: Edge) = color_string(edge_color(e))

  def color_string(c: Color) = s"rgb(${c.getRed},${c.getGreen},${c.getBlue})"

  def edge_color(e: Edge): Color = {
    e.`type` match {
      case EdgeType.LINESITE => Color.YELLOW
      case EdgeType.PARABOLA => Color.CYAN
      case EdgeType.SEPARATOR => Color.MAGENTA
      case EdgeType.LINELINE => new Color(0, 128, 0)
      case EdgeType.PARA_LINELINE => new Color(0, 255, 0)
      case EdgeType.OUTEDGE => Color.ORANGE
      case _ => Color.BLUE
    }
  }
}
