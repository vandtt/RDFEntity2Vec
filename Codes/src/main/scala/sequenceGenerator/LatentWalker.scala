package sequenceGenerator

import java.io.{BufferedWriter, File}
import java.io._
import java.io.DataInputStream

import util.Model.SequenceGenerator

import scala.annotation.tailrec
import scala.util.Random
import scalax.collection.Graph
import scalax.collection.edge.LkDiEdge
import util.Writer.WalksWriter

import scala.collection.mutable.ListBuffer

/**
  * Created by Van 2019.04
  */
class LatentWalker (val graph: Graph[String, LkDiEdge], val depth: Int, val numWalks: Int, val features: ListBuffer[String], val sourceFile: String) extends SequenceGenerator {

  val random = new Random

  override def get: Seq[Seq[String]] = {

    /*
    for {
      node <- graph.nodes.toList
      i <- 0 until numWalks
    } yield randomWalk(node)
*/

    val os  = new DataOutputStream(new BufferedOutputStream(new FileOutputStream( sourceFile.substring( sourceFile.lastIndexOf("/") + 1)+ "_latent.txt")))



    /*var features = new ListBuffer[String]()
    //features += "http://dl-learner.org/carcinogenesis#Bond-1"// carcinogenesis

    */

    var latentSeqs = new ListBuffer[Seq[String]]()

    var seqs = new ListBuffer[Seq[String]]()
    for(i<-0 until numWalks) {
      for (node <- graph.nodes.toList) {
        val s = randomWalk(node)
        println(s"s: $s and features $features")
        if(s.intersect(features.seq).length>=1)
          latentSeqs += s
        seqs +=s

        /// for binary writing
        if(s!=null) {
          var w = ""
          for(ss<-s)
          {
            w = w + ss +" "
          }
          //os.writeBytes(w +"\n")

        }
        /// end binary writing

      }
    }



   // val outputFile = new File(sourceFile).getParent  + sourceFile.substring( sourceFile.lastIndexOf("/") + 8) + "_new.txt"

    val outputFile = new File(sourceFile).getParent + "/embedding_" + numWalks +"_"+depth+ "_"+
      sourceFile.substring( sourceFile.lastIndexOf("/") + 1) + ".txt"

    println("Writing on file " + outputFile + " . . .")

    val Writer = new WalksWriter(outputFile)
    Writer(seqs)

    println("Done")
    print(outputFile)


    val outputFile1 = new File(sourceFile).getParent + "/embedding_latent_" + numWalks +"_"+depth+ "_"+
      sourceFile.substring( sourceFile.lastIndexOf("/") + 1) + ".txt"

   // val outputFile1 = new File(sourceFile).getParent  + sourceFile.substring( sourceFile.lastIndexOf("/") + 8) + "_latent_new.txt"

    print(s"output: $outputFile1")

    val bw = new BufferedWriter(new FileWriter(outputFile1))

    println(s"latentSequences size ${latentSeqs.size}")

    for(i<-0 until latentSeqs.length -2 )
    {
      for(j<-1 until latentSeqs.length -1)
      {
        //println(s"ms ${latentSeqs.toList(i)} and ${latentSeqs.toList(j)}")
        val seq = mergeSequence(latentSeqs.toList(i),latentSeqs.toList(j), features)
        if(seq!=null) {
          var w = ""
          for(s<-seq)
            {

              w = w + s +" "
            }
          if(!w.isEmpty())
           bw.write(w + "\n")
           //os.writeBytes(w +"\n")

        }
      }
    }
    bw.close()
    os.close

    return seqs
  }

  def mergeSequence(seq1: Seq[String], seq2: Seq[String], features: Seq[String]): Seq[String]={
    if(seq1.intersect(seq2).intersect(features).length >=1)
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

object LatentWalker {

}
