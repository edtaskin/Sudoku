package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WelcomePageController implements Initializable{
	
	@FXML
	private Label SUlabel, DOlabel, KUlabel;
	@FXML
	private Button createButton, playButton, rulesButton, generateButton;
	@FXML
	private ComboBox<String> difficultyComboBox;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		 
		difficultyComboBox.setItems(FXCollections.observableArrayList(new String[]{"easy", "medium", "hard"}));
		difficultyComboBox.getSelectionModel().selectFirst();
		
		rulesButton.setOnAction(e -> displayRules());
		
		TranslateTransition translateSU = new TranslateTransition();
		TranslateTransition translateDO = new TranslateTransition();
		TranslateTransition translateKU = new TranslateTransition();
		translateSU.setNode(SUlabel);
		translateDO.setNode(DOlabel);
		translateKU.setNode(KUlabel);
		
		translateSU.setDuration(Duration.seconds(1));
		translateDO.setDuration(Duration.seconds(1));
		translateKU.setDuration(Duration.seconds(1));
		
		translateSU.setByX(160); translateSU.setByY(0);
		translateDO.setByX(0); translateDO.setByY(79);
		translateKU.setByX(-162); translateKU.setByY(-206);
		 
		translateSU.play();
		translateSU.setOnFinished(e -> translateDO.play());
		translateDO.setOnFinished(e -> translateKU.play());
		
		RotateTransition rotateSU = new RotateTransition();
		RotateTransition rotateDO = new RotateTransition();
		RotateTransition rotateKU = new RotateTransition();
		rotateSU.setNode(SUlabel);
		rotateDO.setNode(DOlabel);
		rotateKU.setNode(KUlabel);
		
		rotateSU.setDuration(Duration.seconds(1));
		rotateDO.setDuration(Duration.seconds(1));
		rotateKU.setDuration(Duration.seconds(1));
		
		rotateSU.setByAngle(360);
		rotateDO.setByAngle(360);
		rotateKU.setByAngle(360);
		
		rotateSU.play();
		rotateSU.setOnFinished(e -> rotateDO.play());
		rotateDO.setOnFinished(e -> rotateKU.play());
		
	}
	
	public void createGame() throws IOException {
		Stage stage = (Stage) SUlabel.getScene().getWindow();
		stage.setWidth(880);
		stage.setHeight(620);
		stage.centerOnScreen();
		
		GameController.isCreateMode = true;
		Scene scene = SUlabel.getScene();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
		Parent root = loader.load();
		scene.setRoot(root);
	}
	
	public void playGame() throws IOException {
		Stage stage = (Stage) SUlabel.getScene().getWindow();
		stage.setWidth(880);
		stage.setHeight(620);
		stage.centerOnScreen();
		
		GameController gameController = new GameController();
		gameController.setDifficulty(difficultyComboBox.getValue());

		Scene scene = SUlabel.getScene();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
		Parent root = loader.load();
		scene.setRoot(root);
	}
	
	public void displayRules() {
		Stage stage = (Stage) SUlabel.getScene().getWindow();
		Stage popup = new Stage();
		popup.setWidth(500); 
		popup.setHeight(250);
		
		popup.initModality(Modality.NONE);
		popup.initOwner(stage);
		VBox titleBox = new VBox(new Text("SUDOKU RULES"));
		titleBox.setAlignment(Pos.CENTER);
		titleBox.getChildren().get(0).setStyle("-fx-font-size: 24");
		
		
		VBox rulesBox = new VBox();
		rulesBox.setAlignment(Pos.CENTER_LEFT);
		rulesBox.setSpacing(20);
		
		Scene popupScene = new Scene(new VBox(titleBox, rulesBox), 300, 200);
		
		popup.setScene(popupScene);
		popup.show();
		
		Scanner scanner;
		try {
			scanner = new Scanner(new File("resource\\howToPlay.txt"));
			popup.setTitle(scanner.nextLine());
			while(scanner.hasNextLine()) {
				Text text = new Text(scanner.nextLine());
				text.setStyle("-fx-font-size: 16");
				
				String str = text.getText();
				
				if(str.length() > 100) {
					str = str.substring(0, 99) + "\n" + str.substring(99);
				}
				text = new Text(str);
				
				rulesBox.getChildren().add(text);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public void generateGame() throws IOException {
		Scene scene = SUlabel.getScene();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Generator.fxml"));
		Parent root = loader.load();
		scene.setRoot(root);
	}

}
