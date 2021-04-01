import java.io.File
import java.util.Date

import graphGenerator.{GraphFromRDF, GraphFromRDFId, GraphFromRDFId_LatentCase}
import sequenceGenerator.{LatentWalker, RandomWalker_}
import util.Writer.WalksWriter

import scala.collection.mutable.ListBuffer

object LatentWalk {
  def main(args: Array[String]) {

    args match {
      case Array(sourceFile, depth, numWalks) =>
        println(sourceFile)

        //val GraphGen = new GraphFromRDFId(sourceFile)
        var features = ListBuffer[String]()
        features += "http://qstest.org/prop1"

        val GraphGen = new GraphFromRDFId_LatentCase(sourceFile,features)

        val graph = GraphGen.get
        var features_object = ListBuffer[String]()
        features_object = GraphGen.feature_objects

        println("features_object", features_object.toList)

        //val RanWalker = new RandomWalker_(graph, depth.toInt, numWalks.toInt)
        val RanWalker = new LatentWalker(graph, depth.toInt, numWalks.toInt, features_object, sourceFile)
        //val RanWalker = new biasRandomWalker(graph, depth.toInt, numWalks.toInt, sourceFile)

        val startTime = new Date().getTime
        println("Starting Random Walks generation . . .")

        val walks = RanWalker.get

        val endTime = new Date().getTime
        val totSec = (endTime - startTime) / 1000.0
        println("Completed in " + totSec + " seconds")
/*
        val outputFile = new File(sourceFile).getParent + "/4_5_embedding_latent" +
          sourceFile.substring( sourceFile.lastIndexOf("/") + 1) + ".txt"

        println("Writing on file " + outputFile + " . . .")
        val Writer = new WalksWriter(outputFile)
        Writer(walks)
        println("Done")
*/
      case _ =>
        Console.err.println(s"wrong parameters for: ${args.mkString(" ")}")
        val string = """to run the jar do: java -cp name.jar Embed <sourceFile> <depth> <numWalks>
                       | where:
                       | <sourceFile> : file containing RDF triples
                       | <depth> : length of each random walk (e.g. 5)
                       | <numWalks> : number of walks generated for each node
                     """

        Console.err.println(string)
    }

  }
}
