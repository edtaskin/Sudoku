package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class FileIOController {
	private ArrayList<Integer> playedGameIDs = new ArrayList<>();
	
	private final int GRID_SIZE = 9;
	

	public void saveGame(int[][] board, String difficulty) {
		String fileName = "resource\\" + difficulty + ".txt";
		File file = new File(fileName);
				
		try {
			List<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8));
			
			int gameCount = 0;
			for(int line = 0; line < fileContent.size(); line++) {
				if(fileContent.get(line).contains("Game Count")) {
					gameCount = Integer.parseInt(fileContent.get(line).split(":")[1].trim()) + 1;
					fileContent.set(line, String.format("Game Count : %d", gameCount));
					break;
				}
			}
			
			Files.write(Paths.get(fileName), fileContent, StandardCharsets.UTF_8);
			
			FileWriter fw = new FileWriter(file, true);
			fw.write(String.format("Game %d\n", gameCount));
			for(int[] arr : board) {
				for(int num : arr) {
					fw.write(String.valueOf(num));
				}
				fw.write("\n");
			}
			fw.write("END\n");
			fw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
		

	public void loadGame(GridPane gameGrid, String difficulty, boolean getFinalGame, boolean getRandomGame) throws FileNotFoundException {
		int[][] board = new int[GRID_SIZE][GRID_SIZE];
		String fileName = "resource\\" + difficulty + ".txt";
		
		Scanner scanner = new Scanner(new File(fileName));
		int gameCount = 0;
		
		while(scanner.hasNextLine()) {
			String currentLine = scanner.nextLine();
			
			if(currentLine.contains("Game Count")) {
				gameCount = Integer.parseInt(currentLine.split(":")[1].trim());
			}
		}	
		scanner = new Scanner(new File(fileName));
		
		int wantedGameID = 0;
		if(getFinalGame) {
			wantedGameID = gameCount;
		}
		else if(getRandomGame) {
			if(playedGameIDs.size() == gameCount) {
				playedGameIDs.clear();
			}
			else {
				while(playedGameIDs.contains(wantedGameID) || wantedGameID == 0) {
					wantedGameID = ThreadLocalRandom.current().nextInt(1, gameCount + 1);
				}
			}
			
		}
		
		while(scanner.hasNextLine()) {
			String currentLine = scanner.nextLine();
			
			if(currentLine.contains(String.format("Game %d", wantedGameID))) {
				for(int row = 0; row < GRID_SIZE; row++) {
					String[] splittedLineArr = scanner.nextLine().split("");
					for(int col = 0; col < GRID_SIZE; col++) {
						String str = splittedLineArr[col];
						if(str == " ") {
							System.out.println("ERROR");
							str = "0";
						}
						board[row][col] = Integer.parseInt(str);
					}
				}
				break;
			}
		}
		ArrayOperations.setGameGridAccordingToArray(gameGrid, board);
	}
	

}
