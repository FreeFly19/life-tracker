import java.sql.Timestamp
import java.time.LocalDateTime

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.HttpClient

object TestEl extends App {
  import com.sksamuel.elastic4s.http.ElasticDsl._

  val client = HttpClient(ElasticsearchClientUri("localhost", 9200))

  client.execute(deleteIndex("life-tracker")).await

  client.execute{
    createIndex("life-tracker").mappings(
      mapping("span").fields(
        textField("title"),
        textField("description"),
        objectField("at").fields (
          dateField("start"),
          dateField("finish")
        ),
        keywordField("tags")
      )
    )
  }.await

  val startAt = System.currentTimeMillis

  var time = Timestamp.valueOf(LocalDateTime.now).getTime

  val docs = for(i <- 1 until 1000000) yield indexInto("life-tracker" / "span") fields(
      "title" -> ("TextItem" + i),
      "description" -> "Some desc",
      "at" -> Map(
        "start" -> time,
        "finish" -> (time + (math.random()*10000).toLong + i * 10000)
      ),
      "tags" -> "work"
    )

  docs.grouped(10000).foreach(g => client.execute(bulk(g)).await)

  println(System.currentTimeMillis - startAt)

  client.close


  val res = client.execute{
    search("life-tracker" / "span") query "textitem1"
  }.await

  println(res.hits.hits.head.sourceAsString)
}

