package minesweeper;

import java.util.Random;
import java.util.stream.*;
import java.util.List;
import java.util.ArrayList;
import static java.lang.Math.*;

public class Minesweeper {
  private static int BOUNDS = 10;
  private static int NUMBER_OF_MINES = 10;
  private CellState[][] cellStates = new CellState[BOUNDS][BOUNDS];
  protected List<String> mineLocations = new ArrayList<>();

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
    for (int i = max(0, row - 1); i <= min(row + 1, BOUNDS - 1); i++) {
      for (int j = max(0, column - 1); j <= min(column + 1, BOUNDS - 1); j++) {
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
    return mineLocations.contains(row + "-" + column);
  }

  public void setMine(int row, int column) {
    mineLocations.add(row + "-" + column);
  }

  public int adjacentMinesCountAt(int row, int column) {
    if (isMineAt(row, column)) {
      return 0;
    }

    return IntStream.rangeClosed(row - 1, row + 1).flatMap(i ->
            IntStream.rangeClosed(column - 1, column + 1).filter( j ->
                    isMineAt(i, j) )
                    .map( j -> 1 )
    ).sum();
  }

  public GameStatus getGameStatus() {

    int exposedMineCount = IntStream.rangeClosed(0, BOUNDS -1 ).flatMap(i ->
      IntStream.rangeClosed(0, BOUNDS - 1).filter( j ->
        isMineAt(i, j) && cellStates[i][j] == CellState.EXPOSED)
        .map( j -> 1 )
    ).sum();

    if (exposedMineCount > 0) {
      return GameStatus.LOST;
    }

    int sealedMineCount = IntStream.rangeClosed(0, BOUNDS -1 ).flatMap(i ->
      IntStream.rangeClosed(0, BOUNDS - 1).filter( j ->
        isMineAt(i, j) && cellStates[i][j] == CellState.SEALED)
        .map( j -> 1 )
    ).sum();

    int exposedCount = IntStream.rangeClosed(0, BOUNDS -1 ).flatMap(i ->
            IntStream.rangeClosed(0, BOUNDS - 1).filter( j ->
                    cellStates[i][j] == CellState.EXPOSED)
                    .map( j -> 1 )
    ).sum();

    return (sealedMineCount == mineLocations.size() &&
            exposedCount == BOUNDS * BOUNDS - mineLocations.size()) ? GameStatus.WON:
            GameStatus.INPROGRESS;
  }

  public void setMines(int seedNumber) {
    Random rand = new Random(seedNumber);

    int row, column;

    for (int i = 0; i < NUMBER_OF_MINES; i++) {
      row = rand.nextInt(BOUNDS);
      column = rand.nextInt(BOUNDS);
      if (!isMineAt(row, column)) {
        setMine(row, column);
      } else {
        i--;
      }
    }
  }
}
