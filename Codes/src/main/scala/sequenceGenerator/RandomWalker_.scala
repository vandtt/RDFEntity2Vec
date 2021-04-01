package sequenceGenerator


import util.Model.SequenceGenerator

import scala.annotation.tailrec
import scala.util.Random
import scalax.collection.Graph
import scalax.collection.edge.LkDiEdge


import scala.collection.mutable.ListBuffer

/**
  * Created by Van 2019.04
  */
class RandomWalker_ (val graph: Graph[String, LkDiEdge], val depth: Int, val numWalks: Int) extends SequenceGenerator {

  val random = new Random

  override def get: Seq[Seq[String]] = {
    /*
    for {
      node <- graph.nodes.toList
      i <- 0 until numWalks
    } yield randomWalk(node)
*/
    //Pass a parameter features that are list of entities.
    var features = new ListBuffer[String]()
   // features += "http://www.aifb.uni-karlsruhe.de/Forschungsgruppen/viewForschungsgruppeOWL/id1instance" // for aifb dataset
    features += "2500"  // for mutag  dataset
    features += "5661"
    features += "14"
    features += "297"
    features += "1196"

    var latentSeqs = new ListBuffer[Seq[String]]()

    var seqs = new ListBuffer[Seq[String]]()
    for(i<-0 until numWalks) {
      for (node <- graph.nodes.toList) {
        val s = randomWalk(node)
        println(s"s: $s and features $features")
        if(s.intersect(features.seq).length>=2)
          latentSeqs += s
        seqs +=s
      }
    }

    println(s"latentSequences size ${latentSeqs.size}")
    for(i<-0 until latentSeqs.length -2 )
    {
      for(j<-1 until latentSeqs.length -1)
      {
        //println(s"ms ${latentSeqs.toList(i)} and ${latentSeqs.toList(j)}")
        val s = mergeSequence(latentSeqs.toList(i),latentSeqs.toList(j), features)
        if(s!=null) {
          seqs += s
          //  println(s"ms $s")
        }
      }
    }
    return seqs
  }

  def mergeSequence(seq1: Seq[String], seq2: Seq[String], features: Seq[String]): Seq[String]={
    if(seq1.intersect(seq2).length >= 1)
      return seq1.union(seq2).distinct
    return null
  }

  @tailrec
  private def randomWalkRec(node: Graph[String, LkDiEdge]#NodeT, acc: List[String]): Seq[String] = {
    println(s"node: ${node.value}, acc: ${acc.size.toString}, $acc")
    if (acc.size < depth) {
      getRandomOutEdge(node) match {
        case Some(edge) =>
          randomWalkRec( edge.to, edge.label.toString :: node.value :: acc )
        case _ =>
          node.value :: acc
      }
    } else
      acc
  }

  def randomWalk(node: Graph[String, LkDiEdge]#NodeT): Seq[String] = {
    randomWalkRec(node, List()).reverse
  }


  def getRandomOutEdge(node: Graph[String, LkDiEdge]#NodeT): Option[Graph[String, LkDiEdge]#EdgeT] = {
    if(node.outDegree > 0)
      Some(node.outgoing.toList( random.nextInt(node.outDegree) ))
    else
      None
  }


}
object RandomWalker_ {

}
