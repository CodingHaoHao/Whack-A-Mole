package ch.makery.address

import ch.makery.address.view.MediaManager
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.scene.image.Image

object MainApp extends JFXApp {
  private val mediaManager = new MediaManager()

  stage = new PrimaryStage {
    icons += new Image(getClass.getResourceAsStream("/images/Mole.png"))
    title = "Whack A Mole"
    scene = new Scene {
      root = loadView("view/StartGame.fxml")
    }
    onCloseRequest = _ => stopBackgroundMusic()
  }

  private def loadView(fxmlPath: String): jfxs.layout.AnchorPane = {
    val resource = getClass.getResource(fxmlPath)
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    loader.getRoot[jfxs.layout.AnchorPane]
  }

  private def updateScene(fxmlPath: String): Unit = {
    stage.scene().setRoot(loadView(fxmlPath))
  }

  def showGame(): Unit = {
    updateScene("view/Game.fxml")
  }

  def showStart(): Unit = {
    updateScene("view/StartGame.fxml")
  }

  private def playBackgroundMusic(): Unit = {
    mediaManager.backgroundMusic(0.7)
  }

  private def stopBackgroundMusic(): Unit = {
    mediaManager.stopBackgroundMusic()
  }
  playBackgroundMusic()
}
