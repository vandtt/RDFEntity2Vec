import java.io.File
import java.util.Date

import graphGenerator.GraphFromRDF
import scalax.collection.edge.WDiEdge
import scalax.collection.edge.Implicits._
import scalax.collection.Graph
import scalax.collection.edge
import java.util.zip._

import sequenceGenerator.LatentSequencesGenerationWalker
import util.Writer.WalksWriter

object TestGraph {
  def main(args: Array[String]) {
    val sourceFile =  args(0)
    val GraphGen = new GraphFromRDF(args(0))
    val graph = GraphGen.get
    /*
    val node1 = graph.get("http://qstest.org/node2")
    val node2 = graph.get("http://qstest.org/node12")
    val node11 = graph.get("http://qstest.org/node11")
*/

    val RanWalker = new LatentSequencesGenerationWalker(graph, 7, 4)

    val startTime = new Date().getTime
    println("Starting Random Walks generation . . .")


    val walks = RanWalker.get

    val endTime = new Date().getTime
    val totSec = (endTime - startTime) / 1000.0
    println("Completed in " + totSec + " seconds")

    val outputFile = new File(sourceFile).getParent + "/7_embedding_4_latent" +
      sourceFile.substring( sourceFile.lastIndexOf("/") + 1) + ".txt"

    println("Writing on file " + outputFile + " . . .")
    val Writer = new WalksWriter(outputFile)
    Writer(walks)
    println("Done")


    //println(RanWalker.getSimilarity(node1,node2))

    //println(RanWalker.getSimilarCandidateWithPre(node2,node2.incoming.toList(0)))
    //println(RanWalker.getSimilarCandidate(node2))
    //println(RanWalker.getProbabilityToWalkWithPre(node2,node2.incoming.toList(0)))

    /*
    println(s"outdegree of node is ${node.outDegree}") // node.outDegree: count outgoing of node
    for (i <- 0 until node.outDegree) {
      val edge = node.outgoing.toList(i)
      println(s"$i is ${node.outgoing.toList(i)}")  // node.outgoing.toList(i): get edge i-th
      println(s"To ${edge.to} and label ${edge.label.toString}")
    }

    println("outgoing test")
    val edges = node.outgoing
    for (edge <- edges)
      {
        println(s"From ${edge.from} and label ${edge.label.toString}")
        println(s"To ${edge.to} and label ${edge.label.toString}")
      }
    println("incoming test")
    val inedges = node.incoming
    for (edge <- inedges)
    {
      println(s"From ${edge.from} and label ${edge.label.toString}")
      println(s"To ${edge.to} and label ${edge.label.toString}")
    }

    */

  }

}
