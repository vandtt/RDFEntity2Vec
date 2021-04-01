package sequenceGenerator

import java.io.File
import java.util.Date

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import scala.util.control._
import scala.collection.immutable.ListMap
import graphGenerator.GraphFromRDF
import util.Model.SequenceGenerator

import scala.util.Random
import scalax.collection.Graph
import scalax.collection.edge.LkDiEdge

class biasRandomWalker(val graph: Graph[String, LkDiEdge], val depth: Int, val numWalks: Int) extends SequenceGenerator {

  val random = new Random

  val threshold = 0.7

  override def get: Seq[Seq[String]] = {
    for {
      node <- graph.nodes.toList
      i <- 0 until numWalks
    } yield biasRandomWalk(node)
  }


  private def randomWalkRec(node: Graph[String, LkDiEdge]#NodeT, acc: List[String]): Seq[String] = {
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

  def biasRandomWalk(node: Graph[String, LkDiEdge]#NodeT): Seq[String] = {
    println(s"begin biasRandomWalk for node: ${node.value}")

    val list = biasRandomWalkRec(node, List()).reverse
    println(s"end of biasRandomWalk for node: ${node.value} and list: $list")
    return list
  }


  def getRandomOutEdge(node: Graph[String, LkDiEdge]#NodeT): Option[Graph[String, LkDiEdge]#EdgeT] = {
    if(node.outDegree > 0)
      Some(node.outgoing.toList( random.nextInt(node.outDegree) ))
    else
      None
  }

  // TODO: modify this function
  private def biasRandomWalkRec(node: Graph[String, LkDiEdge]#NodeT, acc: List[String]): Seq[String] = {
   // println(s"node: ${node.value}, acc: ${acc.size.toString}, $acc")
    if (acc.size == 0 )
    {
    //  println(s"TH acc=0 : acc.size = ${acc.size.toString}")
      getBiasRandomOutEdge(node) match {
        case Some(edge) =>
     //     println(s"edge from: ${edge.from.value}")
     //     println(s"edge to: ${edge.to.value}")
          biasRandomWalkRec(edge.to, edge.label.toString :: node.value :: acc) //node is the next node
        case _ =>
          node.value :: acc
      }
    }
    else if (acc.size < depth) {
    //  println(s"TH acc.size = ${acc.size.toString}")
    //  println(s"TH acc : acc.size = ${acc.size.toString}")
      getBiasRandomOutEdge(node) match {
        case Some(edge) =>
     //     println(s"edge from: ${edge.from.value}")
     //     println(s"edge to: ${edge.to.value}")
          biasRandomWalkRecWithPre(edge.to,edge, edge.label.toString :: node.value :: acc )
        case _ =>
          node.value :: acc
      }
    }
    else {
     // print(s"end biasRandomWalkRec $acc")
      acc
    }
  }
  private def biasRandomWalkRecWithPre(node: Graph[String,  LkDiEdge]#NodeT,preEdge: Graph[String,  LkDiEdge]#EdgeT, acc: List[String]): Seq[String] = {
    //println(s"biasRandomWalkRecWithPre node: ${node.value}, acc: ${acc.size.toString}, $acc")
    if (acc.size < depth) {
      //println(s"biasRandomWalkRecWithPre TH acc.size = ${acc.size.toString}")
      getBiasRandomOutEdgeWithPre(node,preEdge ) match {
        case Some(edge) =>
          //println(s"edge from: ${edge.from.value}")
          //println(s"edge to: ${edge.to.value}")
          biasRandomWalkRecWithPre(edge.to,edge, edge.label.toString :: node.value :: acc )
        case _ =>
          node.value :: acc
      }
      biasRandomWalkRec(node, acc)
    }
    else {
      //acc
      //println(" end ")
      //println(s"acc ${acc}")
      biasRandomWalkRec(node, acc)
    }
  }

  private def biasRandomWalkRec_old(node: Graph[String, LkDiEdge]#NodeT, acc: List[String]): Seq[String] = {
   // println(s"node: ${node.value}, acc: ${acc.size.toString}, $acc")
    if (acc.size == 0 )
      {
   //     println(s"TH acc=0 : acc.size = ${acc.size.toString}")
        getRandomOutEdge(node) match {
          case Some(edge) =>
            biasRandomWalkRec(node, edge.label.toString :: node.value :: acc) //node is the next node
          case _ =>
            node.value :: acc
        }

      }
    else if(acc.size == 2)
      {
   //     println(s"TH acc=1 : acc.size = ${acc.size.toString}")
        getBiasRandomOutEdge(node) match {
          case Some(edge) =>
            biasRandomWalkRec( edge.to, edge.label.toString :: node.value :: acc )
          case _ =>
            node.value :: acc
        }
      }
    else if (acc.size < depth) {
   //   println(s"TH acc>=3 : acc.size = ${acc.size.toString}")
      var preNodeName = acc.toList(acc.size-1)
      val loop = new Breaks
      loop.breakable {
        for (edge <- node.incoming) {
          if (edge.from.value == preNodeName) {
            getBiasRandomOutEdgeWithPre(node,edge ) match {
              case Some(edge) =>
                biasRandomWalkRec( edge.to, edge.label.toString :: node.value :: acc )
              case _ =>
                node.value :: acc
            }
            loop.break()
          }
        }
      }
      acc

    }
    else {
      acc
    }
  }


  // to get one out edge with previous edge
  def getBiasRandomOutEdgeWithPre(node: Graph[String, LkDiEdge]#NodeT, preEdge: Graph[String, LkDiEdge]#EdgeT): Option[Graph[String, LkDiEdge]#EdgeT]={
    //println("getBiasRandomOutEdgeWithPre")
    if(node.outDegree > 0) {
      val candidates =getProbabilityToWalkWithPre(node, preEdge)

      return Some(sample(candidates))
      /*
      for ((k, v) <- candidates) {
        if (v >= 0.4) {
          return Some(k)
        }
      }
      */
    }
    else
      None
  }


  // to get one out edge with out previous edge
  def getBiasRandomOutEdge(node: Graph[String, LkDiEdge]#NodeT): Option[Graph[String, LkDiEdge]#EdgeT]={

    if(node.outDegree > 0) {
      val candidates =getProbabilityToWalk(node)
      return Some(sample(candidates))
      /*
      for ((k, v) <- candidates) {
        if (v >= 0.4) {
          return Some(k)
        }
      }
      */

    }
    else
      None
  }

  def getSimilarity(node1: Graph[String, LkDiEdge]#NodeT, node2:Graph[String,LkDiEdge]#NodeT): Float ={

    if(node1.value == node2.value)
      return 1
    var oedgeLabelIds1 = new ListBuffer[String]()
    var oedgeLabelIds2 = new ListBuffer[String]()
    var iedgeLabelIds1 = new ListBuffer[String]()
    var iedgeLabelIds2 = new ListBuffer[String]()

    var oedgeIds1 = new ListBuffer[String]()
    var oedgeIds2 = new ListBuffer[String]()
    var iedgeIds1 = new ListBuffer[String]()
    var iedgeIds2 = new ListBuffer[String]()

    val outEdge1 = node1.outgoing
    for(edge<-outEdge1){
      oedgeIds1+= edge.to.toString
      oedgeLabelIds1+= edge.label.toString
    }
    val outEdge2 = node2.outgoing
    for(edge<-outEdge2){
      oedgeIds2+= edge.to.toString
      oedgeLabelIds2+= edge.label.toString
    }

    val inEdge1 = node1.incoming
    for(edge<-inEdge1){
      iedgeIds1+= edge.from.toString
      iedgeLabelIds1+= edge.label.toString
    }
    val inEdge2 = node2.incoming
    for(edge<-inEdge2){
      iedgeIds2+= edge.from.toString
      iedgeLabelIds2+= edge.label.toString
    }

    // get common edge label Ids of 2 nodes
    val commonOEdgeLabelIds = oedgeLabelIds1.intersect(oedgeLabelIds2)
    //println(s"commonOEdgeLabelIds: ${commonOEdgeLabelIds.toList}")
    val commonIEdgeLabelIds = iedgeLabelIds1.intersect(iedgeLabelIds2)
    //println(s"commonIEdgeLabelIds: ${commonIEdgeLabelIds.toList}")
    // get total edge Label ids
    val totalOEdgeLabelIds = oedgeLabelIds1.union(oedgeLabelIds2).distinct
    //println(s"totalOEdgeLabelIds: ${totalOEdgeLabelIds.toList}")
    val totalIEdgeLabelIds = iedgeLabelIds1.union(iedgeLabelIds2).distinct
    //println(s"totalIEdgeLabelIds: ${totalIEdgeLabelIds.toList}")

    // get common edges
    val commonOEdgeIds = oedgeIds1.intersect(oedgeIds2)
    //println(s"commonOEdgeIds: ${commonOEdgeIds.toList}")
    val commonIEdgeIds = iedgeIds1.intersect(iedgeIds2)
    //println(s"commonIEdgeIds: ${commonIEdgeIds.toList}")

    // get total edges
    val totalOEdgeIds = oedgeIds1.union(oedgeIds2).distinct
    //println(s"totalOEdgeIds: ${totalOEdgeIds.toList}")
    val totalIEdgeIds = iedgeIds1.union(iedgeIds2).distinct
    //println(s"totalIEdgeIds: ${totalIEdgeIds.toList}")

    val s = ((commonIEdgeLabelIds.length.toFloat)/(totalIEdgeLabelIds.length.toFloat) + (commonOEdgeLabelIds.length.toFloat)/(totalOEdgeLabelIds.length.toFloat)+
      (commonOEdgeIds.length.toFloat)/(totalOEdgeIds.length.toFloat)+ (commonIEdgeIds.length.toFloat)/(totalIEdgeIds.length.toFloat))/5

    return s
  }

  def getSimilarityWithPreNode(node1: Graph[String, LkDiEdge]#NodeT, node2:Graph[String,LkDiEdge]#NodeT,prNode1: Graph[String, LkDiEdge]#NodeT, prNode2:Graph[String,LkDiEdge]#NodeT ): Float ={

    if(prNode1.value.toString != prNode2.value.toString){
      return (getSimilarity(node1,node2) + getSimilarity(prNode1,prNode2))/2
    }
    else
      return (getSimilarity(node1,node2) + 1)/2

  }

  def getSimilarCandidate(node1: Graph[String, LkDiEdge]#NodeT): HashMap[Graph[String, LkDiEdge]#NodeT,Float] ={
    val similarMap: HashMap[Graph[String, LkDiEdge]#NodeT,Float] = HashMap.empty[Graph[String, LkDiEdge]#NodeT,Float]

    for (node <- graph.nodes.toList) {
      if (node != node1) {
        var t = getSimilarity(node1, node)
        if(t>=threshold)
          similarMap += (node -> t)
      }
    }
    return similarMap
  }

  // this function to get similarCandidates of node1 given previous edge = similarity of (node1,anyNode) + similarity of (preNode1, preAnyNode)
  def getSimilarCandidateWithPre(node1: Graph[String, LkDiEdge]#NodeT, preEdge:Graph[String,LkDiEdge]#EdgeT): HashMap[Graph[String, LkDiEdge]#NodeT,Float] ={
    val similarMap: HashMap[Graph[String, LkDiEdge]#NodeT,Float] = HashMap.empty[Graph[String, LkDiEdge]#NodeT,Float]
    val loop = new Breaks

    val edgeLabel = preEdge.label.toString

    for (node <- graph.nodes.toList) {
      if (node != node1) {
        loop.breakable {
          for (edge <- node.incoming.toList) {
            if (edge.label.toString == preEdge.label.toString) {
              val s1 = getSimilarity(preEdge.from, edge.from)

              if(s1>=threshold) {
                val s2 = getSimilarity(node1, node)
                similarMap += (node -> (s1 + s2))
                loop.break()
              }
            }
          }
        }
      }
    }

    return similarMap

  }

  def getProbabilityToWalkToJWithPre(node1: Graph[String, LkDiEdge]#NodeT, nodeJ: Graph[String, LkDiEdge]#NodeT, preEdge:Graph[String,LkDiEdge]#EdgeT): Float = {
    var edges = node1.outgoing
    var edgeLabel = ""
    for(edge<- edges)
      if(edge.to == nodeJ)
        edgeLabel = edge.label.toString

    var m1 = 0
    val candidates = getSimilarCandidateWithPre(node1, preEdge)
    //println(s"candidates of node: $node1, given $preEdge.label.toString: $candidates")
    for ((k,v) <- candidates)
      {
        //m1 = m1 + getEdgeByFromTo(k,nodeJ).length
        //m1 = m1 + getEdgeByFromLabel(k,edgeLabel).length
        if(getEdgeByFromTo(k,nodeJ).length >=1)
          m1=m1+1
        if(getEdgeByFromLabel(k,edgeLabel).length >=1)
          m1=m1+1
      }

    val pr = m1.toFloat
    return pr
  }

  def getProbabilityToWalkToJ(node1: Graph[String, LkDiEdge]#NodeT, nodeJ: Graph[String, LkDiEdge]#NodeT): Float = {
    var edges = node1.outgoing
    var edgeLabel = ""
    for(edge<- edges)
      if(edge.to == nodeJ)
        edgeLabel = edge.label.toString

    var m1 = 0
    val candidates = getSimilarCandidate(node1)
    for ((k,v) <- candidates)
    {
      //m1 = m1 + getEdgeByFromTo(k,nodeJ).length
      //m1 = m1 + getEdgeByFromLabel(k,edgeLabel).length
      if(getEdgeByFromTo(k,nodeJ).length >=1)
        m1=m1+1
      if(getEdgeByFromLabel(k,edgeLabel).length >=1)
        m1=m1+1
    }

    val pr = (m1).toFloat
    return pr
  }

  // this function to get probability of any pair edge(node1,anyNode) given previous edge
  def getProbabilityToWalkWithPre(node1: Graph[String, LkDiEdge]#NodeT, preEdge:Graph[String,LkDiEdge]#EdgeT): HashMap[Graph[String, LkDiEdge]#EdgeT,Float] = {
    val probabilityMap: HashMap[Graph[String, LkDiEdge]#EdgeT,Float] = HashMap.empty[Graph[String, LkDiEdge]#EdgeT,Float]
    var oedgeLabelIds = new ListBuffer[String]()
    var oedgeIds = new ListBuffer[String]()

    val candidates = getSimilarCandidateWithPre(node1,preEdge)
    /*
    var totalEdgeOfCandidates = 0
    for((k,v)<-candidates)
      {
        totalEdgeOfCandidates = totalEdgeOfCandidates + k.outDegree
      }
    */
    val total = candidates.size
    //println(s"total candidates: $total")

    var sumTotal = 0

    for(edge<-node1.outgoing){
      oedgeIds+= edge.to.toString
      oedgeLabelIds+= edge.label.toString
      if(total > 0 ) {
        val m = getProbabilityToWalkToJWithPre(node1, edge.to, preEdge)
        //println(s" m: $m") // New total candidates = 0, m bat buoc phai bang 0

        //sumTotal = oedgeLabelIds.length + oedgeIds.length
        sumTotal = total*2
        //sumTotal = totalEdgeOfCandidates*2
        //println(s"total o: $sumTotal")
        val pr = m.toFloat / sumTotal

        //println(s"pr: $pr")
        probabilityMap += (edge -> pr.toFloat)
      }
      else
        {
          //println("no candidates")
          probabilityMap += (edge -> 1/((node1.outDegree.toFloat)*2))
          //println(s"pr: ${1/(node1.outDegree.toFloat)}")
        }

    }

    return probabilityMap
  }

  def getProbabilityToWalk(node1: Graph[String, LkDiEdge]#NodeT): HashMap[Graph[String, LkDiEdge]#EdgeT,Float] = {
    val probabilityMap: HashMap[Graph[String, LkDiEdge]#EdgeT,Float] = HashMap.empty[Graph[String, LkDiEdge]#EdgeT,Float]
    var oedgeLabelIds = new ListBuffer[String]()
    var oedgeIds = new ListBuffer[String]()
    val candidates = getSimilarCandidate(node1)
    val total = candidates.size

    for(edge<-node1.outgoing){
      oedgeIds+= edge.to.toString
      oedgeLabelIds+= edge.label.toString
      if( total >0) {
        val m = getProbabilityToWalkToJ(node1, edge.to)
        var sumTotal = total * 2
        //sumTotal = totalEdgeOfCandidates*2
        //println(s"total o: $sumTotal")
        val pr = m.toFloat / sumTotal
        probabilityMap += (edge -> pr.toFloat)
      }
      else
      {
        //println("no candidates")
        probabilityMap += (edge -> 1/(node1.outDegree.toFloat))
      }
    }

    return probabilityMap
  }

  def getEdgeByFromTo(node1: Graph[String, LkDiEdge]#NodeT, node2: Graph[String, LkDiEdge]#NodeT):ListBuffer[Graph[String, LkDiEdge]#EdgeT]={
    var edgeIds = new ListBuffer[Graph[String, LkDiEdge]#EdgeT]()
    val edges = node1.outgoing
    for(edge <- edges)
      {
        if(edge.to == node2)
          {
            edgeIds += edge
          }
      }
   // println(s"getEdgeByFromTo: ${edgeIds.length}")
    return edgeIds
  }

  def getEdgeByFromLabel(node1: Graph[String, LkDiEdge]#NodeT, edgeLabel: String):ListBuffer[Graph[String, LkDiEdge]#EdgeT]={
    var edgeIds = new ListBuffer[Graph[String, LkDiEdge]#EdgeT]()
    val edges = node1.outgoing
    for(edge <- edges)
    {
      if(edge.label.toString == edgeLabel)
      {
        edgeIds += edge
      }
    }
   // println(s"getEdgeByFromLabel: ${edgeIds.length}")
    return edgeIds
  }

  def sample (dist: HashMap[Graph[String, LkDiEdge]#EdgeT, Float]): Graph[String, LkDiEdge]#EdgeT = {
    //println(s"dist: $dist")
    val it = dist.iterator
    var sum = 0.0
    while (it.hasNext) {
      val (item, itemProb) = it.next
      sum += itemProb
    }
    var p = scala.util.Random.nextFloat()
    p = p*(sum.toFloat)

    //println(s"p $p")
    //println(s"sum $sum")
    val map = dist.iterator
    var accum = 0.0
    while (map.hasNext) {
      val (item, itemProb) = map.next
      accum += itemProb
      //println(s"accum: $accum")
      if (accum.toFloat >= p) {
        return item // return so that we don't have to search through the whole distribution
      }
    }
    val (item, itemProb) = dist.head //.iterator.next
    //println(s"item: $item")
    return item
    //sys.error(f"this should never happen")  // needed so it will compile
  }


}


