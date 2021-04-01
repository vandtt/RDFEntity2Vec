package graphGenerator

import java.io.File

import org.apache.jena.rdf.model._
import org.apache.jena.util.FileManager
import scalax.collection.Graph
import scalax.collection.edge.LkDiEdge
import util.Model.GraphGenerator
import java.io._

import scala.collection.JavaConverters._



class GraphFromRDFId (val source: String) extends GraphGenerator {

  var SOMap = collection.mutable.Map[String, String]()
  var PMap = collection.mutable.Map[String, String]()

  val dictionaryFile = new File(source).getParent + "/dictionary_" +
    source.substring( source.lastIndexOf("/") + 1) + ".txt"

  val groundTruthFile = new File(source).getParent + "/groundTruth_" +
    source.substring( source.lastIndexOf("/") + 1) + ".tsv"

  val bw = new BufferedWriter(new FileWriter(dictionaryFile))


  override def get: Graph[String, LkDiEdge] = {
    val in = FileManager.get().open(source)
    val modelWithLiteral = ModelFactory.createDefaultModel()
    val readerType = getExtension(source) match {
      case Some("ttl") => "TTL"
      case Some("nt") => "NT"
      case _ => "RDF/XML"
    }



    modelWithLiteral.read(in, null, readerType)

    val model = ModelFactory.createDefaultModel()
    modelWithLiteral.listStatements().asScala.foreach(stmt => {
      if (!stmt.getObject.isInstanceOf[Literal])
        model.add(stmt)
    })

/*    val edges = model.listStatements().asScala.map(stmt =>
      LkDiEdge(stmt.getSubject.toString, stmt.getObject.toString) (stmt.getPredicate.toString)
    ).toList
    */

    val edges = model.listStatements().asScala.map(stmt =>
      LkDiEdge(mapStringtoId(stmt.getSubject.toString, SOMap, false), mapStringtoId(stmt.getObject.toString, SOMap,false)) (mapStringtoId(stmt.getPredicate.toString, PMap, true))
    ).toList
    bw.close()

    Graph.from(List(), edges)

  }

  def getExtension(filename: String): Option[String] = {
    if (filename contains ".")
      Some(filename.substring( filename.lastIndexOf(".") + 1))
    else
      None
  }

  def mapStringtoId(str: String, map: collection.mutable.Map[String, String], isP: Boolean): String =
  {
    var id = map.get(str)
    //print("str", str)
   // print("id", id)

    if(id == None) {
      var newid = map.size.toString()
      if(isP)
        newid = "p" + newid
      map += (str -> newid)

      bw.write(str +" " + newid +"\n")
      return newid
    }
    return id.get
  }






}




