package minesweeper;

import java.util.HashMap;
import java.util.Map;


public class Minesweeper {
  private static int BOUNDS = 10;
  private CellState[][] cellStates = new CellState[BOUNDS][BOUNDS];
  private Map<Integer, Integer> mineLocations = new HashMap<Integer, Integer>();

  public Minesweeper() {
    for(int i = 0; i < BOUNDS; i++) {
      for(int j = 0; j < BOUNDS; j++) {
        cellStates[i][j] = CellState.UNEXPOSED;
      }
    }
  }

  public void exposeCell(int row, int column) {
    if (cellStates[row][column] == CellState.UNEXPOSED) {
      cellStates[row][column] = CellState.EXPOSED;

      if (adjacentMinesCountAt(row, column) == 0) {
        exposeNeighbors(row, column);
      }
    }
  }

  protected void exposeNeighbors(int row, int column) {
    for (int i = Math.max(0, row - 1); i <= Math.min(row + 1, BOUNDS - 1); i++) {
      for (int j = Math.max(0, column - 1); j <= Math.min(column + 1, BOUNDS - 1); j++) {
          exposeCell(i, j);
      }
    }
  }

  public void toggleSeal(int row, int column) {
    if (cellStates[row][column] != CellState.EXPOSED) {
      cellStates[row][column] =
        cellStates[row][column] == CellState.SEALED ?
          CellState.UNEXPOSED : CellState.SEALED;
    }
  }

  public CellState getCellState(int row, int column) {
    return cellStates[row][column];
  }

  public boolean isMineAt(int row, int column) {
    return mineLocations.get(row) != null &&
              mineLocations.get(row).equals(column);
  }

  public void setMine(int row, int column) {
    mineLocations.put(row, column);
  }

  public int adjacentMinesCountAt(int row, int column) {
    if (isMineAt(row, column)) {
      return 0;  // No reason to count if cell is a mine
    }

    int numberOfMines = 0;

    for (int i = Math.max(0, row - 1); i <= Math.min(row + 1, BOUNDS - 1); i++) {
      for (int j = Math.max(0, column - 1); j <= Math.min(column + 1, BOUNDS - 1); j++) {
        numberOfMines += checkForMine(i, j);
      }
    }

    return numberOfMines;
  }

  // Needed this to reduce cyclo of adjacentMinesCountAt
  private int checkForMine(int row, int column) {
    return isMineAt(row, column) ? 1 : 0;
  }
}
