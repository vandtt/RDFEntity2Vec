package sequenceGenerator

import scalax.collection.Graph
import scalax.collection.edge.LkDiEdge
import util.Model.SequenceGenerator

import scala.annotation.tailrec
import scala.collection.mutable.{HashMap, ListBuffer}
import scala.util.Random


class LatentSequencesGenerationWalker(val graph: Graph[String, LkDiEdge], val depth: Int, val numWalks: Int) extends SequenceGenerator {

  val random = new Random

  val threshold = 0.7

  override def get: Seq[Seq[String]] = {
    /*for {
      node <- graph.nodes.toList
      i <- 0 until numWalks
    } yield randomWalk(node)
    */
    var features = new ListBuffer[String]()
    features += "http://www.aifb.uni-karlsruhe.de/Forschungsgruppen/viewForschungsgruppeOWL/id1instance"

    var latentSeqs = new ListBuffer[Seq[String]]()

    var seqs = new ListBuffer[Seq[String]]()
    for(i<-0 until numWalks) {
      for (node <- graph.nodes.toList) {
        val s = randomWalk(node)
        println(s"s: $s and features $features")
        if(s.intersect(features.seq).length>=1)
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

  def mergeSequence(seq1: Seq[String], seq2: Seq[String], features: Seq[String]): Seq[String]={
    if(seq1.intersect(seq2).length >= 1)
      return seq1.union(seq2).distinct
    return null
  }

  def randomWalk(node: Graph[String, LkDiEdge]#NodeT): Seq[String] = {
    randomWalkRec(node, List()).reverse
  }


  def getRandomOutEdge(node: Graph[String, LkDiEdge]#NodeT): Option[Graph[String, LkDiEdge]#EdgeT] = {
    if(node.outDegree > 0) {
      //Some(node.outgoing.toList(random.nextInt(node.outDegree)))
      Some(sample(getProbabilityToWalk(node)))
    }
    else
      None
  }


  def getProbabilityToWalk(node1: Graph[String, LkDiEdge]#NodeT): HashMap[Graph[String, LkDiEdge]#EdgeT,Float] = {
    val probabilityMap: HashMap[Graph[String, LkDiEdge]#EdgeT,Float] = HashMap.empty[Graph[String, LkDiEdge]#EdgeT,Float]
    var oedgeLabelIds = new ListBuffer[String]()
    var oedgeIds = new ListBuffer[String]()
    val candidates = getSimilarCandidate(node1)


    for(edge<-node1.outgoing){
      oedgeIds+= edge.to.toString
      oedgeLabelIds+= edge.label.toString
      if(candidates.size > 0) {
        val m = getProbabilityToWalkToJ(node1, edge.to, candidates)
        val pr = m.toFloat / (candidates.size *2)
        probabilityMap += (edge -> pr.toFloat)
      }
      else
        probabilityMap += (edge -> (1/(node1.outDegree.toFloat)))
    }

    return probabilityMap
  }

  def getProbabilityToWalkToJ(node1: Graph[String, LkDiEdge]#NodeT, nodeJ: Graph[String, LkDiEdge]#NodeT, candidates: HashMap[Graph[String, LkDiEdge]#NodeT,Float]): Float = {
    var edges = node1.outgoing
    var edgeLabel = ""
    for(edge<- edges)
      if(edge.to == nodeJ)
        edgeLabel = edge.label.toString

    var m1 = 0
    for ((k,v) <- candidates)
    {
     // m1 = m1 + getEdgeByFromTo(k,nodeJ).length
     // m1 = m1 + getEdgeByFromLabel(k,edgeLabel).length
      if(getEdgeByFromTo(k,nodeJ).length >=1)
        m1=m1+1
      if(getEdgeByFromLabel(k,edgeLabel).length >=1)
        m1=m1+1
    }

    val pr = (m1).toFloat
    return pr
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

  // sampling one edges from candidates dist
  def sample (dist: HashMap[Graph[String, LkDiEdge]#EdgeT, Float]): Graph[String, LkDiEdge]#EdgeT = {
    val it = dist.iterator
    var sum = 0.0
    while (it.hasNext) {
      val (item, itemProb) = it.next
      sum += itemProb
    }
    var p = scala.util.Random.nextFloat()
    p = p*(sum.toFloat)

    val map = dist.iterator
    var accum = 0.0
    while (map.hasNext) {
      val (item, itemProb) = map.next
      accum += itemProb
      if (accum.toFloat >= p) {
        return item // return so that we don't have to search through the whole distribution
      }
    }
    val (item, itemProb) = dist.head
    return item
    //sys.error(f"this should never happen")  // needed so it will compile
  }


}


