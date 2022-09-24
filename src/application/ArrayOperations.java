package application;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class ArrayOperations {
	private static final int GRID_SIZE = 9;
	private static final int BOX_SIZE = 3;
	
	public static int[][] getBoardAsArray(GridPane gameGrid){
		int[][] gameGridArr = new int[GRID_SIZE][GRID_SIZE];
		
		for(Node node : gameGrid.getChildren()) {
			if(node instanceof GridPane) {
				GridPane box = (GridPane) node;
				for(Node node1 : box.getChildren()) {
					if(node1 instanceof StackPane) {
						StackPane pane = (StackPane) node1;
						if(pane.getChildren().get(0) instanceof Label) {
							Label label = (Label) pane.getChildren().get(0);
							String labelText = ((Label) pane.getChildren().get(0)).getText();

							int number = 0;
							if(labelText != null) {
								number = Integer.parseInt(labelText);
							}
							else {
								label.getStyleClass().add("user-placed");
							}
							//Calculating row and columns by the pane's location in local box and the location of that box in the board
							int row = GridPane.getRowIndex(box) * BOX_SIZE + GridPane.getRowIndex(pane);
							int column = GridPane.getColumnIndex(box) * BOX_SIZE + GridPane.getColumnIndex(pane);	
							gameGridArr[row][column] = number;
						}
					}
				}
			}
			
		}
		return gameGridArr;
	}
	
	
	public static void setGameGridAccordingToArray(GridPane gameGrid, int[][] board) {
		for(Node node : gameGrid.getChildren()) {
			GridPane box = (GridPane) node;
			for(Node node1 : box.getChildren()) {
				if(node1 instanceof StackPane) {
					StackPane pane = (StackPane) node1;
					
					int row = GridPane.getRowIndex(box) * BOX_SIZE + GridPane.getRowIndex(pane);
					int column = GridPane.getColumnIndex(box) * BOX_SIZE + GridPane.getColumnIndex(pane);
					
					if(pane.getChildren().get(0) instanceof Label) {
						Label numLabel = (Label) pane.getChildren().get(0);
						
						if(numLabel.getText() == null) {
							if(board[row][column] != 0) {
								numLabel.setText(String.valueOf(board[row][column]));
							}	
						}
					}
				}
			}
		}
	}
	
	
}
