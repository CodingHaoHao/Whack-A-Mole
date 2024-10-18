package ch.makery.address.view

import scalafx.scene.control.{Alert, TextField}
import scalafx.scene.layout.{AnchorPane, Pane}
import scalafxml.core.macros.sfxml
import ch.makery.address.MainApp
import ch.makery.address.util.ScoreDatabase
import scalafx.scene.control.Alert.AlertType

@sfxml
class StartGameController(
     private val rulePane: AnchorPane,
     private val userName: TextField
                         ) {
  rulePane.setVisible(false)

  def startRule(): Unit = {
    rulePane.setVisible(true)
  }

  def startGame(): Unit = {
    val userNameText = userName.text.value.trim
    if (userNameText.isEmpty) {
      val alert = new Alert(AlertType.Warning) {
        initOwner(MainApp.stage)
        title = "Empty Input"
        headerText = "No Username Is Entered"
        contentText = "Please enter a name before starting the game."
      }
      alert.showAndWait()
    } else {
      ScoreDatabase.userName = userNameText
      MainApp.showGame()
    }
  }
}