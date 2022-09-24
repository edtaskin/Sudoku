package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SudokuGeneratorController implements Initializable{
	@FXML
	private Button generateButton, playButton, backButton;
	@FXML
	private ComboBox<String> difficultyComboBox;
	@FXML
	private TextField missingNumsField;
	@FXML
	private Label difficultyLabel, messageLabel;
	
	private static final int GRID_SIZE = 9;
	private static final int BOX_SIZE = 3; 
	private static final Integer[] numbers = {1, 2, 3 ,4 ,5 ,6 ,7 ,8 ,9};
	
	private String difficulty;
	private HashMap<String, Integer[]> defaultMissingNumsMap = new HashMap<>();
	
	private int[][] board;
	private SudokuSolver solver = new SudokuSolver();
	private FileIOController fileIOController = new FileIOController();
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		defaultMissingNumsMap.put("easy", new Integer[] {35, 36, 37, 38, 39});
		defaultMissingNumsMap.put("medium", new Integer[] {43, 44, 45, 46, 47});
		defaultMissingNumsMap.put("hard", new Integer[] {50, 51, 52, 53, 54});
		
		
		difficultyComboBox.setItems(FXCollections.observableArrayList(new String[] {"easy", "medium", "hard"}));
		difficultyLabel.setVisible(false);
		
		playButton.setDisable(true);
		
		generateButton.setOnAction(e -> {
			if(difficultyComboBox.getValue() == null) {
				difficultyLabel.setVisible(true);
			}
			else {
				difficulty = difficultyComboBox.getValue();
				generateGame();
				generateButton.setDisable(true);
				playButton.setDisable(false);
				messageLabel.setText("Now you can play the newly generated game");
			}
		});
	}


	private int[][] getFilledBoard() {
		int[][] board = new int[GRID_SIZE][GRID_SIZE];

		
		//Filling diagonal 3x3 boxes
		for(int boxTopLeft = 0; boxTopLeft < GRID_SIZE; boxTopLeft+=3) {
			ArrayList<Integer> numsAvailableForBox = new ArrayList<>();
			Collections.addAll(numsAvailableForBox, numbers);

			for(int row = boxTopLeft; row < boxTopLeft + BOX_SIZE; row++) {
				for(int col = boxTopLeft; col < boxTopLeft + BOX_SIZE; col++) {
					int randomIndex = ThreadLocalRandom.current().nextInt(numsAvailableForBox.size());
					board[row][col] = numsAvailableForBox.get(randomIndex);
					numsAvailableForBox.remove(randomIndex);
				}
			}	
		}
		
		//Filling the rest of the board
		for(int row = 0; row < GRID_SIZE; row++) {
			for(int col = 0; col < GRID_SIZE; col++) {
				if(board[row][col] == 0) {
					List<Integer> shuffledNums = new ArrayList<>();
					Collections.addAll(shuffledNums, numbers);
					Collections.shuffle(shuffledNums);
					for(int num : shuffledNums) {
						if(solver.isValidPlacement(board, num, row, col)) {
							board[row][col] = num;
							if(!solver.solveSudoku(board)) {
								board[row][col] = 0;
							}
						}
					}
				}
				
			}
		}
		return board;
	}
	
	public void deleteNums(int[][] board) {
		int numbersToDelete = 0;
		Integer[] missingNumsArr = defaultMissingNumsMap.get(difficulty);
		if(missingNumsField.getText().isEmpty()) {
			numbersToDelete = missingNumsArr[ThreadLocalRandom.current().nextInt(missingNumsArr.length)];
			
		}
		else {
			try {
				numbersToDelete = Integer.parseInt(missingNumsField.getText());
				if(numbersToDelete <= 0 || numbersToDelete > 81) {
					throw new Exception();
				}
			}
			catch(Exception e) {
				numbersToDelete = missingNumsArr[ThreadLocalRandom.current().nextInt(missingNumsArr.length)];
			}
		}
		
		//Checking if the board is solvable after each number deletion
		List<Integer> fullCells = new ArrayList<>();
		
		for(int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
			fullCells.add(i);
		}
		
		for(int i = 0; i < numbersToDelete; i++) {
			boolean numDeleted = false;
			while(numDeleted == false) {
				int randomIndex = fullCells.get(ThreadLocalRandom.current().nextInt(fullCells.size()));
				int row = (randomIndex - (randomIndex % GRID_SIZE)) / GRID_SIZE;
				int col = randomIndex % GRID_SIZE;
				
				while(board[row][col] == 0) {
					randomIndex = ThreadLocalRandom.current().nextInt(fullCells.size());
					row = (randomIndex - (randomIndex % GRID_SIZE)) / GRID_SIZE;
					col = randomIndex % GRID_SIZE;
				}
				
				int previousValue = board[row][col];
				board[row][col] = 0;
				
				int[][] boardClone = new int[9][9];
				for(int k = 0; k < GRID_SIZE; k++) {
					for(int j = 0; j < GRID_SIZE; j++) {
						boardClone[k][j] = board[k][j];
					}
				}
				
				if(solver.solveSudoku(boardClone)) {
					for(int index = 0; index < fullCells.size(); index++) {
						if(fullCells.get(index) == randomIndex) {
							fullCells.remove(index);
						}
					}
					
					numDeleted = true;
				}
				else {
					board[row][col] = previousValue;
				}
			}
		}		
	}
	
	
	public void generateGame() {
		board = getFilledBoard();
		deleteNums(board);
		fileIOController.saveGame(board, difficulty);
	}
	
	
	public void backToPrevPage() throws IOException {
		Scene scene = backButton.getScene();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));
		Parent root = loader.load();
		scene.setRoot(root);
	}
	
	
	public void playGame() throws IOException {
		GameController gameController = new GameController();
		gameController.setDifficulty(difficulty);
		
		Scene scene = playButton.getScene();
		Stage stage = (Stage) scene.getWindow();
		stage.setWidth(GameController.STAGE_WIDTH);
		stage.setHeight(GameController.STAGE_HEIGHT);
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
		Parent root = loader.load();
		
		scene.setRoot(root);
	}
	
	/*
	 * Helper function for debugging
	 */
	public void printBoard(int[][] board) {
		for(int r = 0; r < GRID_SIZE; r++) {
			if(r % 3 == 0 && r != 0) {
				System.out.println("---------------");
			}
			for(int c = 0; c < GRID_SIZE; c++) {
				if(c % 3 == 0 && c != 0) {
					System.out.print(" | ");
				}
				
				System.out.print(board[r][c]);
			}
			System.out.println();
		}
		
	}
}
