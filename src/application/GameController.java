package application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameController implements Initializable{
	private final int GRID_SIZE = 9;
	private final int BOX_SIZE =  3;
	public static final int STAGE_WIDTH = 880;
	public static final int STAGE_HEIGHT = 620;
	@FXML
	private Pane pane;
	@FXML
	private AnchorPane root;
	@FXML
	private GridPane gameGrid;
	@FXML
	private GridPane numbersGrid;
	@FXML
	private Label messageLabel, createModeLabel;
	@FXML
	private Button eraseButton, saveButton, playButton, backButton, sudokuSolverButton;
	@FXML
	private ComboBox<String> difficultyComboBox;
	
	private static String difficulty;
	public static HashSet<Integer> allNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
	
	private String newGameDifficulty;
	private FileIOController fileIOController = new FileIOController();
	
	private GridPane[] gridPanes = new GridPane[GRID_SIZE];
	
	private Button[] numberButtonsArr = new Button[GRID_SIZE];
	private StackPane selectedPane;
	private boolean isHighlighted = false;
	
	public static boolean isCreateMode; 
	
	
	public GridPane getGameGrid() {
		return gameGrid;
	}
	
	public void setDifficulty(String diff) {
		difficulty = diff;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		pane.setOnMouseClicked(e -> {
			if(isHighlighted) {
				dehighlightCells();
			}
		});

		saveButton.setVisible(isCreateMode); 
		saveButton.setDisable(!isCreateMode);
		saveButton.setOnAction(e -> {
			if(difficultyComboBox.getValue() == null) {
				createModeLabel.setText("Set a difficulty tag before saving!");
			}
			else {
				newGameDifficulty = difficultyComboBox.getValue();
				saveGame();
			}
		});
		
		playButton.setVisible(isCreateMode);
		
		difficultyComboBox.setVisible(isCreateMode);
		difficultyComboBox.setDisable(!isCreateMode);
		difficultyComboBox.setItems(FXCollections.observableArrayList(new String[] {"easy", "medium", "hard"}));
		
		int j = 0;
		for(int row = 0; row < BOX_SIZE; row++) {
			for(int col = 0; col < BOX_SIZE; col++) {
				GridPane grid = (GridPane) gameGrid.getChildren().get(j);
				grid.setGridLinesVisible(false);
				GridPane.setRowIndex(grid, row);
				GridPane.setColumnIndex(grid, col);
				j++;
			}
		}			
			
		for(int i = 0; i < gameGrid.getChildren().size(); i++) {
			if(gameGrid.getChildren().get(i) instanceof GridPane) {
				GridPane box = (GridPane) gameGrid.getChildren().get(i);
				gridPanes[i] = box;
				for(int row = 0; row < BOX_SIZE; row++) {
					for(int col = 0; col < BOX_SIZE; col++) {
						StackPane pane = new StackPane();
						
						Label numberLabel = new Label(null);
						pane.getChildren().add(numberLabel);
						numberLabel.getStyleClass().add("number-label");
						StackPane.setAlignment(numberLabel, Pos.CENTER);
						
						pane.getStyleClass().add("game-grid-cell");
						
						pane.setOnMouseClicked(e ->{
							dehighlightCells();
							selectCell(pane);
							highlightCells(pane);
						});
						
						box.add(pane, col, row);
						GridPane.setColumnIndex(pane, col);
						GridPane.setRowIndex(pane, row);
						
						setCellStyle(pane);
					}
				}		
			}
		}
		
		int index = 0;
		for(Node node : numbersGrid.getChildren()) {
			if(node instanceof Button) {
				Button button = (Button) node;
				numberButtonsArr[index] = button;
				button.setOnKeyPressed(e ->{	
					if(e.getText().equals(button.getText())) {
						button.fire();
					}
				});
				button.setOnAction(e -> {
					placeNumber(button);
				});
				index++;
			}
		}
		
		if(!isCreateMode) {
			try {
				fileIOController.loadGame(gameGrid, difficulty, false, true);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	
	public void setCellStyle(Pane pane) {
		int row = GridPane.getColumnIndex(pane);
		int col = GridPane.getRowIndex(pane);
		
		if(pane.getStyleClass().size() > 1) {
			pane.getStyleClass().clear();
			pane.getStyleClass().add("game-grid-cell");
		}
		
		if(row == 0) {
			pane.getStyleClass().add("first-column");
		}
		if(col == 0) {
			pane.getStyleClass().add("first-row");
		}
	}
	
	
	public void highlightCells(Pane pane) {
		isHighlighted = true;
		
		if(pane.getParent() instanceof GridPane) {
			GridPane parentGrid = (GridPane) pane.getParent();
			
			//Highlighting the cells in the same grid
			for(Node node : parentGrid.getChildren()) {
				if(node instanceof StackPane) {
					Pane pane2 = (Pane) node;
					pane2.getStyleClass().clear();
					pane2.getStyleClass().add("game-grid-cell");
					pane2.getStyleClass().add("highlighted");
				}
			}
			
			//Highlighting the cells in the same row or column
			int row = GridPane.getRowIndex(parentGrid);
			int col = GridPane.getColumnIndex(parentGrid);
			
			for(Node gridNode : gameGrid.getChildren()) {
				if(gridNode instanceof GridPane) {
					GridPane grid = (GridPane) gridNode;
					if(GridPane.getRowIndex(grid) == row) {
						for(Node cell : grid.getChildren()) {
							if(cell instanceof StackPane) {
								StackPane pane2 = (StackPane) cell;
								if(GridPane.getRowIndex(pane2) == GridPane.getRowIndex(pane)) {
									pane2.getStyleClass().clear();
									pane2.getStyleClass().add("game-grid-cell");
									pane2.getStyleClass().add("highlighted");
								}
							}
						}
					}
				}
			}
		
			//Highlighting the cells in the same column
			for(Node gridNode : gameGrid.getChildren()) {
				if(gridNode instanceof GridPane) {
					GridPane grid = (GridPane) gridNode;
					if(GridPane.getColumnIndex(grid) == col) {
						for(Node cell : grid.getChildren()) {
							if(cell instanceof StackPane) {
								StackPane pane2 = (StackPane) cell;
								if(GridPane.getColumnIndex(pane2) == GridPane.getColumnIndex(pane)) {
									pane2.getStyleClass().clear();
									pane2.getStyleClass().add("game-grid-cell");
									pane2.getStyleClass().add("highlighted");
								}
							}
						}
					}
				}
			}
		}
		
		pane.getStyleClass().clear();
		pane.getStyleClass().add("game-grid-cell");
		pane.getStyleClass().add("selected");
	}

	
	public void dehighlightCells() {
		isHighlighted = false;
		
		for(Node gridNode : gameGrid.getChildren()) {
			if(gridNode instanceof GridPane) {
				GridPane grid = (GridPane) gridNode;
				for(Node node : grid.getChildren()) {
					if(node instanceof StackPane) {
						StackPane pane2 = (StackPane) node;
						if(pane2.getStyleClass().contains("highlighted") || pane2.getStyleClass().contains("selected")) {
							setCellStyle(pane2);
						}
					}
				}
			}
		}
	}
		
	
	public void selectCell(StackPane pane) {
		selectedPane = pane;
		messageLabel.setText("Select a number to place\non the selected cell."); 
		messageLabel.setStyle("-fx-font-size: 18; -fx-text-fill: green"); 
		
		Set<Integer> neighboringNumsSet = new HashSet<>();
		GridPane selectedGrid = (GridPane) pane.getParent();
		
		//Controlling duplicates in the same 3x3 grid
		for(Node node : selectedGrid.getChildren()) {
			if(node instanceof StackPane) {
				StackPane cell = (StackPane) node;
				if(cell.getChildren().get(0) instanceof Label) {
					Label numLabel = (Label) cell.getChildren().get(0);
					if(numLabel.getText() != null) {
						neighboringNumsSet.add(Integer.parseInt(numLabel.getText()));
					}
				}
			}
		}
		
		//Controlling duplicates in the same row on the main grid		
		for(GridPane grid : gridPanes) {
			if(GridPane.getRowIndex(grid) == GridPane.getRowIndex(selectedGrid)) {
				for(Node node : grid.getChildren()) {
					if(GridPane.getRowIndex(node) == GridPane.getRowIndex(pane)) {
						if(node instanceof StackPane) {
							StackPane cell = (StackPane) node;
							if(cell.getChildren().get(0) instanceof Label) {
								Label numLabel = (Label) cell.getChildren().get(0);
								if(numLabel.getText() != null) {
									neighboringNumsSet.add(Integer.parseInt(numLabel.getText()));
								}
							} 
						}
					}
				}
			}
		}
		
		//Controlling duplicates in the same column on the main grid	
		for(GridPane grid : gridPanes) {
			if(GridPane.getColumnIndex(grid) == GridPane.getColumnIndex(selectedGrid)) {
				for(Node node : grid.getChildren()) {
					if(GridPane.getColumnIndex(node) == GridPane.getColumnIndex(pane)) {
						if(node instanceof StackPane) {
							StackPane cell = (StackPane) node;
							if(cell.getChildren().get(0) instanceof Label) {
								Label numLabel = (Label) cell.getChildren().get(0);
								if(numLabel.getText() != null) {
									neighboringNumsSet.add(Integer.parseInt(numLabel.getText()));
								}
							} 
						}
					}
				}
			}
		}
		
		for(Button button : numberButtonsArr) {
			if(neighboringNumsSet.contains(Integer.parseInt(button.getText()))) {
				button.setDisable(true);
			}
			else {
				if(button.isDisable()) {
					button.setDisable(false);
				}
			}
		}
	}
	
	
	public void placeNumber(Button button) {
		if(selectedPane != null) {
			for(Node node : selectedPane.getChildren()) {
				if(node instanceof Label) {
					Label label = (Label) node;
					label.getStyleClass().add("user-placed");
					label.setText(button.getText());
				}
			}
			messageLabel.setText(String.format("You placed %s at\nrow %d, column %d", button.getText(), GridPane.getRowIndex(selectedPane) + 1, GridPane.getColumnIndex(selectedPane) + 1));
			messageLabel.setStyle("-fx-font-size: 18; -fx-text-fill: green"); 
			if(isGameOver()) {
				checkGameSuccess();
			}
		}
		else {
			messageLabel.setText("You need to first select a cell\nto place a number.");
		}
	}
	
	
	public void eraseNumber() {
		Label label = (Label) selectedPane.getChildren().get(0);
		
		if(label.getStyleClass().contains("user-placed")) {
			String labelText = label.getText();
			for(Button button : numberButtonsArr) {
				if(button.getText().equals(labelText)){
					button.setDisable(false);
				}
			}
			
			if(selectedPane != null) {
				for(Node node : selectedPane.getChildren()) {
					if(node instanceof Label) {
						((Label) node).setText(null);
						messageLabel.setText("Selected number is erased.");
					}
				}
			}	
		}
		else {
			messageLabel.setText("You cannot erase a preplaced\nnumber.");
		}
	}
	
	
	public boolean isGameOver() {
		for(GridPane grid : gridPanes) {
			for(Node node : grid.getChildren()) {
				if(node instanceof StackPane) {
					StackPane pane = (StackPane) node;
					Label label = (Label) pane.getChildren().get(0);
					if(label.getText() == null) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	
	public boolean checkGameSuccess() {
		//Checking the individual 3x3 boxes
		for(GridPane box : gridPanes) {
			HashSet<Integer> neighboringNums = new HashSet<>();
					
			for(Node cellNode : box.getChildren()) {
				if(cellNode instanceof StackPane) {
					StackPane cell = (StackPane) cellNode;
					neighboringNums.add(Integer.parseInt(((Label) cell.getChildren().get(0)).getText()));
				}
			}
			if(!neighboringNums.equals(allNumbers)) {
				return false; 
			}
			
		}
		
		//Checking the rows
		for(int row = 0; row < 3; row++) {
			HashSet<Integer> neighboringNums1 = new HashSet<>();
			HashSet<Integer> neighboringNums2 = new HashSet<>();
			HashSet<Integer> neighboringNums3 = new HashSet<>();
			
			for(GridPane grid : gridPanes) {
				if(GridPane.getRowIndex(grid) == row) {
					for(Node node : grid.getChildren()) {
						if(node instanceof StackPane) {
							StackPane pane = (StackPane) node;
							int num = Integer.parseInt(((Label) pane.getChildren().get(0)).getText());
							switch(GridPane.getRowIndex(pane)) {
							case 0:
								neighboringNums1.add(num);
								break;
							case 1:
								neighboringNums2.add(num);
								break;
							case 2:
								neighboringNums3.add(num);
								break;
							}
						}
					}
				}
			}	
			if(neighboringNums1.equals(allNumbers) && neighboringNums2.equals(allNumbers) && neighboringNums3.equals(allNumbers)) {
				continue;
			}
			else {
				return false;
			}
		}
		

		//Checking the columns
		for(int col = 0; col < 3; col++) {
			HashSet<Integer> neighboringNums1 = new HashSet<>();
			HashSet<Integer> neighboringNums2 = new HashSet<>();
			HashSet<Integer> neighboringNums3 = new HashSet<>();
			
			for(GridPane grid : gridPanes) {
				if(GridPane.getColumnIndex(grid) == col) {
					for(Node node : grid.getChildren()) {
						if(node instanceof StackPane) {
							StackPane pane = (StackPane) node;
							int num = Integer.parseInt(((Label) pane.getChildren().get(0)).getText());
							switch(GridPane.getColumnIndex(pane)) {
							case 0:
								neighboringNums1.add(num);
								break;
							case 1:
								neighboringNums2.add(num);
								break;
							case 2:
								neighboringNums3.add(num);
								break;
							}
						}
					}
				}
			}	
			if(neighboringNums1.equals(allNumbers) && neighboringNums2.equals(allNumbers) && neighboringNums3.equals(allNumbers)) {
				continue;
			}
			else {
				return false;
			}
		}
		return true;
	}
	
	
	public void backToPreviousPage() {
		Scene scene = backButton.getScene();
		Stage stage = (Stage) scene.getWindow();
		
		Stage popup = new Stage();
		popup.initModality(Modality.NONE);
		popup.initOwner(stage);
		VBox warningBox = new VBox(new Text("WARNING: All progress of the current game will be lost if you go back."));
		VBox.setMargin(warningBox, new Insets(10, 10, 20, 10));
		
		
		Button confirmButton = new Button("Confirm");
		Button cancelButton = new Button("Cancel");
		
		HBox buttonsBox = new HBox(50, cancelButton, confirmButton);
		buttonsBox.setAlignment(Pos.CENTER);
		
		Scene popupScene = new Scene(new VBox(warningBox, buttonsBox));
		popup.setTitle("WARNING");
		
		popup.setScene(popupScene);
		popup.show();
		
		confirmButton.setOnAction(e -> {
			try {
				isCreateMode = false;
				popup.close();
				stage.setWidth(486);
				stage.setHeight(377);
				stage.centerOnScreen();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));
				Parent root = loader.load();
				scene.setRoot(root);
			}
			catch(IOException exception) {
				exception.printStackTrace();
			}
			
		});
		cancelButton.setOnAction(e -> popup.close());
	}
	
	
	//Create mode related methods
	public void saveGame() {
		saveButton.setDisable(true);
		fileIOController.saveGame(ArrayOperations.getBoardAsArray(gameGrid), newGameDifficulty);
		messageLabel.setText("Game saved successfully!");
		createModeLabel.setText("");
	}
	
	
	public void playCreatedGame() throws FileNotFoundException {
		fileIOController.loadGame(gameGrid, newGameDifficulty, true, false);		
	}
	
	
	//Sudoku Solver related
	public void solveSudoku() {
		SudokuSolver solver = new SudokuSolver();
		int[][] gameGridArr = solver.getSolvedBoard(ArrayOperations.getBoardAsArray(gameGrid));
		
		if(gameGridArr != null) {
			ArrayOperations.setGameGridAccordingToArray(gameGrid, gameGridArr);
			messageLabel.setText("Sudoku solved successfully!");
			messageLabel.setStyle("-fx-font-size: 18; -fx-text-fill: green"); 
		}
		else {
			messageLabel.setText("Given Sudoku is unsolvable!");
			messageLabel.setStyle("-fx-font-size: 18; -fx-text-fill: red"); 
		}
		
		
	}
	

}
