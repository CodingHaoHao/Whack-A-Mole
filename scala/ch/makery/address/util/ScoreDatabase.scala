package ch.makery.address.util

import java.io.{BufferedReader, BufferedWriter, FileReader, FileWriter, IOException}
import scala.util.Try

object ScoreDatabase {

  private val filePath = "src\\main\\resources\\Database\\Ranking.txt"
  private var _userName: String = ""

  def userName: String = {
    _userName
  }

  def userName_=(name: String): Unit = {
    _userName = name
    println("Check List:-")
    println(s"UserName set to: $userName") // debug
  }

  def addScore(score: Int): Unit = {
    println(s"Score Get: $score")

    Try {
      val writer = new BufferedWriter(new FileWriter(filePath, true))
      try {
        writer.write(s"$userName,$score\n")
        println(s"Written to file: $userName,$score")
      } finally {
        writer.close()
      }
    }.recover {
      case e: IOException =>
        println(s"Error writing to the scores file: ${e.getMessage}")
    }
  }

  def getTopScores(limit: Int = 5): List[(String, Int)] = {
    var scores = List[(String, Int)]()

    Try {
      val reader = new BufferedReader(new FileReader(filePath))
      try {
        var line: String = null
        while ({ line = reader.readLine(); line != null }) {
          val parts = line.split(",")
          if (parts.length == 2) {
            val playerName = parts(0).trim
            val score = parts(1).trim.toInt
            scores = (playerName, score) :: scores
          }
        }
      } finally {
        reader.close()
      }
    }.recover {
      case _: IOException =>
        println("Error reading the scores file.")
    }
    scores.sortBy(-_._2).take(limit)
  }
}