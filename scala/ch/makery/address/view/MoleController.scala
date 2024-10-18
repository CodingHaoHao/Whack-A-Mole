package ch.makery.address.view

import ch.makery.address.MainApp
import ch.makery.address.util.ScoreDatabase
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.control.Label
import scalafx.application.Platform
import scalafx.scene.layout.AnchorPane
import java.util.{Timer, TimerTask}
import scalafxml.core.macros.sfxml
import scalafx.scene.media.{Media, MediaPlayer}
import scala.util.Random

@sfxml
class MoleController(
                      private val moleFigure1: ImageView, moleFigure2: ImageView, moleFigure3: ImageView, moleFigure4: ImageView, moleFigure5: ImageView,
                      moleFigure6: ImageView, moleFigure7: ImageView, moleFigure8: ImageView, moleFigure9: ImageView,
                      private val scoreLabel: Label,
                      private val timeLabel: Label,
                      private var resultLabel: Label,
                      private val boardPane: AnchorPane,
                      private val settingsPane: AnchorPane,
                      private val rankLabel1: Label, rankLabel2: Label, rankLabel3: Label, rankLabel4: Label, rankLabel5: Label,
                      private var highestScore: Label
                    ) {
  private var score: Int = 0
  private val moleFigures: List[ImageView] = List(moleFigure1, moleFigure2, moleFigure3, moleFigure4, moleFigure5, moleFigure6, moleFigure7, moleFigure8, moleFigure9)
  private val mole = new Image(getClass.getResourceAsStream("/images/Mole.png"))
  private var isPaused: Boolean = false
  private var userName: String = _
  private val rankLabels = List(rankLabel1, rankLabel2, rankLabel3, rankLabel4, rankLabel5)
  private val mediaManager = new MediaManager()
  private val countdownTimer = new countdownTimeSet(20, timeLabel, stopGame)

  for (v <- moleFigures) {
    v.image = mole
  }

  settingsPane.setVisible(false)
  boardPane.setVisible(false)

  private def startCountdown(): Unit = {
    updateHighestScore()
    countdownTimer.start()
  }

  private def updateHighestScore(): Unit = {
    val topScores = ScoreDatabase.getTopScores()
    highestScore.setText(
      if (topScores.nonEmpty) s"Highest Score: ${topScores.head._2}"
      else "Highest Score: 0"
    )
  }

  private def showRandomMole(): Unit = {
    moleFigures.foreach(_.setVisible(false))
    val randomIndex = Random.nextInt(moleFigures.length)
    val currentMoleFigure = moleFigures(randomIndex)
    currentMoleFigure.setVisible(true)
    currentMoleFigure.toFront()
  }

  // link to every mole's onMouseClicked function
  def handleMouseClick(): Unit = {
    moleFigures.filter(_ != null).foreach(_.setVisible(false))
    score += 1
    scoreLabel.setText(s"Score: $score")
    showRandomMole()
    mediaManager.hitSound()
  }

  private def stopGame(): Unit = {
    moleFigures.filter(_ != null).foreach(_.setVisible(false)) // Hide all moles
    countdownTimer.stop()
    timeLabel.setText("Time's Up!")

    ScoreDatabase.addScore(score)
    resultLabel.setText(s"$score")
    updateLeaderboard()
    boardPane.setVisible(true)
  }

  private def updateLeaderboard(): Unit = {
    val topScores = ScoreDatabase.getTopScores()
    rankLabels.zipWithIndex.foreach {case(label, index) =>
      label.setText(
        if (index < topScores.length)
          s"Player: ${topScores(index)._1}, Score: ${topScores(index)._2}"
        else
          "Waiting for you~"
      )
    }
  }

  // link to settings button onAction
  def showSettingsPane(): Unit = {
    isPaused = true
    countdownTimer.stop()
    moleFigures.filter(_ != null).foreach(_.setVisible(false)) // Hide all moles
    timeLabel.setText("Game Pause")
    settingsPane.setVisible(true)
  }

  // link to resumeButton onAction
  def resumeGame(): Unit = {
    isPaused = false
    settingsPane.setVisible(false)
    startCountdown()
    showRandomMole()
  }

  // link to restartButton onAction
  def restartGame(): Unit = {
    MainApp.showGame()
  }

  // link to exitButton onAction
  def exitGame(): Unit = {
    MainApp.showStart()
  }
  startCountdown()
  showRandomMole()
}

class countdownTimeSet(initialTime: Int, timeLabel: Label, timeUp: () => Unit) {
  private var timer: Timer = _
  private var timeRemaining: Int = initialTime

  def start(): Unit = {
    timer = new Timer()
    timer.scheduleAtFixedRate(new TimerTask {
      override def run(): Unit = Platform.runLater {
        if (timeRemaining > 0) {
          timeRemaining -= 1
          val minutes = timeRemaining / 60
          val seconds = timeRemaining % 60
          timeLabel.setText(f"Time: $minutes%02d:$seconds%02d")
        } else {
          stop()
          timeUp()
        }
      }
    }, 0, 1000)
  }

  def stop(): Unit = {
    if (timer != null) timer.cancel()
  }
}

class MediaManager() {
  private var mediaPlayer: MediaPlayer = _

  def backgroundMusic(volume:Double): Unit = {
    val path = getClass.getResource("/Audio/BackgroundMusic.mp3").toExternalForm
    mediaPlayer = new MediaPlayer(new Media(path))
    mediaPlayer.setCycleCount(MediaPlayer.Indefinite)
    mediaPlayer.setVolume(volume)
    mediaPlayer.play()
  }

  def stopBackgroundMusic(): Unit = {
    if (mediaPlayer != null) {
      mediaPlayer.stop()
    }
  }

  def hitSound(): Unit = {
    val path = getClass.getResource("/Audio/Hit.mp3").toExternalForm
    mediaPlayer = new MediaPlayer(new Media(path))
    mediaPlayer.play()
  }
}
