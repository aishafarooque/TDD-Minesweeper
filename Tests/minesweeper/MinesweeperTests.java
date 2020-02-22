package minesweeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class MinesweeperTests {
  private Minesweeper minesweeper;

  @BeforeEach
  void init() {
    minesweeper = new Minesweeper();
  }

  @Test
  void canary() {
    assertTrue(true);
  }

  @Test
  void exposeUnexposedCell() {
    minesweeper.exposeCell(0, 1);

    assertEquals(CellState.EXPOSED, minesweeper.getCellState(0, 1));
  }

  @Test
  void exposeAnExposedCell() {
    minesweeper.exposeCell(0, 1);
    minesweeper.exposeCell(0, 1);

    assertEquals(CellState.EXPOSED, minesweeper.getCellState(0, 1));
  }

  @Test
  void exposeCellOutOfRange() {
    assertAll(
      () -> assertThrows(IndexOutOfBoundsException.class,
        () -> minesweeper.exposeCell(-1, 2)),
      () -> assertThrows(IndexOutOfBoundsException.class,
        () -> minesweeper.exposeCell(10, 2)),
      () -> assertThrows(IndexOutOfBoundsException.class,
        () -> minesweeper.exposeCell(1, -2)),
      () -> assertThrows(IndexOutOfBoundsException.class,
        () -> minesweeper.exposeCell(2, 12))
    );
  }

  @Test
  void initialCellStateIsUnExposed() {
    assertEquals(CellState.UNEXPOSED, minesweeper.getCellState(2, 3));
  }

  @Test
  void sealUnexposedCell() {
    minesweeper.toggleSeal(5, 4);
    
    assertEquals(CellState.SEALED, minesweeper.getCellState(5, 4));
  }

  @Test
  void unsealSealedCell() {
    minesweeper.toggleSeal(4, 5);
    minesweeper.toggleSeal(4, 5);

    assertEquals(CellState.UNEXPOSED, minesweeper.getCellState(4, 5));
  }

  @Test
  void sealExposedCell() {
    minesweeper.exposeCell(3, 4);
    minesweeper.toggleSeal(3, 4);

    assertEquals(CellState.EXPOSED, minesweeper.getCellState(3, 4));
  }

  @Test
  void exposeSealedCell() {
    minesweeper.toggleSeal(2, 5);
    minesweeper.exposeCell(2, 5);

    assertEquals(CellState.SEALED, minesweeper.getCellState(2, 5));
  }

  @Test
  void exposeOnUnexposedCallsExposeNeighbors() {
    boolean[] exposeNeighborsCalled = new boolean[1];

    Minesweeper minesweeper = new Minesweeper() {
      protected void exposeNeighbors(int row, int column) {
        exposeNeighborsCalled[0] = true;
      }
    };

    minesweeper.exposeCell(4, 2);

    assertTrue(exposeNeighborsCalled[0]);
  }

  @Test
  void exposeOnExposedDoesNotCallExposeNeighbors() {
    boolean[] exposeNeighborsCalled = new boolean[1];

    Minesweeper minesweeper = new Minesweeper() {
      protected void exposeNeighbors(int row, int column) {
        exposeNeighborsCalled[0] = true;
      }
    };

    minesweeper.exposeCell(3, 5);

    exposeNeighborsCalled[0] = false;

    minesweeper.exposeCell(3, 5);

    assertFalse(exposeNeighborsCalled[0]);
  }

  @Test
  void exposeSealedCellDoesNotCallExposeNeighbors() {
    boolean[] exposeNeighborsCalled = new boolean[1];

    Minesweeper minesweeper = new Minesweeper() {
      protected void exposeNeighbors(int row, int column) {
        exposeNeighborsCalled[0] = true;
      }
    };

    minesweeper.toggleSeal(2, 5);
    minesweeper.exposeCell(2, 5);

    assertFalse(exposeNeighborsCalled[0]);
  }

  @Test
  void exposeNeighborsExposesEightNeighbors() {
    var neighbors = new ArrayList<String>();
    
    Minesweeper minesweeper = new Minesweeper() {
      public void exposeCell(int row, int column) {
        neighbors.add(row + "-" + column);
      }
    };
    
    minesweeper.exposeNeighbors(3, 4);
    
    assertEquals(List.of("2-3", "2-4", "2-5", "3-3", "3-4", "3-5",
      "4-3", "4-4", "4-5"), neighbors);
  }

  @Test
  void exposeNeighborsTopLeftOnlyExposesExisting() {
    var neighbors = new ArrayList<String>();

    Minesweeper minesweeper = new Minesweeper() {
      public void exposeCell(int row, int column) {
        neighbors.add(row + "-" + column);
      }
    };

    minesweeper.exposeNeighbors(0, 0);

    assertEquals(List.of("0-0", "0-1", "1-0", "1-1"), neighbors);
  }

  @Test
  void exposeNeighborsBottomRightOnlyExposesExisting() {
    var neighbors = new ArrayList<String>();

    Minesweeper minesweeper = new Minesweeper() {
      public void exposeCell(int row, int column) {
        neighbors.add(row + "-" + column);
      }
    };

    minesweeper.exposeNeighbors(9, 9);

    assertEquals(List.of("8-8", "8-9", "9-8", "9-9"), neighbors);
  }

  @Test
  void exposeNeighborBorderCellOnlyExposesExisting() {
    var neighbors = new ArrayList<String>();

    Minesweeper minesweeper = new Minesweeper() {
      public void exposeCell(int row, int column) {
        neighbors.add(row + "-" + column);
      }
    };

    minesweeper.exposeNeighbors(0, 3);

    assertEquals(List.of("0-2", "0-3", "0-4", "1-2", "1-3", "1-4"),
      neighbors);
  }

  @Test
  void isMineAt() {
    assertFalse(minesweeper.isMineAt(3, 2));
  }

  @Test
  void setMine() {
    minesweeper.setMine(3, 2);
    assertTrue(minesweeper.isMineAt(3, 2));
  }

  @Test
  void isMineAtOutsideBoundaries() {

    assertAll(
      () -> assertFalse(minesweeper.isMineAt(-1, 4)),
      () -> assertFalse(minesweeper.isMineAt(10, 5)),
      () -> assertFalse(minesweeper.isMineAt(5, -1)),
      () -> assertFalse(minesweeper.isMineAt(7, 10))
    );
  }

  @Test
  void exposeAdjacentDoesNotCallExposeNeighbors() {
    boolean[] exposeNeighborsCalled = new boolean[1];

    Minesweeper minesweeper = new Minesweeper() {
      protected void exposeNeighbors(int row, int column) {
        exposeNeighborsCalled[0] = true;
      }
    };

    minesweeper.setMine(2, 2);
    minesweeper.exposeCell(1, 2);
    assertFalse(exposeNeighborsCalled[0]);
  }

  @Test
  void adjacentCountIsZeroNextToNoMines() {
    assertEquals(0, minesweeper.adjacentMinesCountAt(4, 6));
  }

  @Test
  void adjacentCountOfMineCellIsZero() {
    minesweeper.setMine(3, 4);

    assertEquals(0, minesweeper.adjacentMinesCountAt(3, 4));
  }

  @Test
  void adjacentCountOfCellNextToOneMine() {
    minesweeper.setMine(3, 4);

    assertEquals(1, minesweeper.adjacentMinesCountAt(3, 5));
  }

  @Test
  void adjacentCountOfCellNextToTwoMines() {
    minesweeper.setMine(3, 4);
    minesweeper.setMine(2, 6);

    assertEquals(2, minesweeper.adjacentMinesCountAt(3, 5));
  }

  @Test
  void adjacentCountOfTopLeftCellNextToMine() {
    minesweeper.setMine(0, 1);

    assertEquals(1, minesweeper.adjacentMinesCountAt(0, 0));
  }

  @Test
  void adjacentCountOfTopRightCellNotNextToMine() {
    assertEquals(0, minesweeper.adjacentMinesCountAt(0, 9));
  }

  @Test
  void adjacentCountOfBottomRightCellNextToMine() {
    minesweeper.setMine(9, 8);

    assertEquals(1, minesweeper.adjacentMinesCountAt(9, 9));
  }

  @Test
  void adjacentCountOfBottomLeftNotNextToMine() {
    assertEquals(0, minesweeper.adjacentMinesCountAt(9, 0));
  }

  @Test
  void getGameStatusWhenGameInProgress() {
    assertEquals(GameStatus.INPROGRESS, minesweeper.getGameStatus());
  }

  @Test
  void exposeMineMakesGameStatusLOST() {
    minesweeper.setMine(3, 3);
    minesweeper.exposeCell(3, 3);

    assertEquals(GameStatus.LOST, minesweeper.getGameStatus());
  }

  @Test
  void gameInProgressAllMinesSealedCellsNotAllExposed() {
    minesweeper.setMine(2, 2);
    minesweeper.toggleSeal(2, 2);

    assertEquals(GameStatus.INPROGRESS, minesweeper.getGameStatus());
  }

  @Test
  void gameInProgressAllMinesSealedEmptyCellSealed() {
    minesweeper.setMine(6, 7);
    minesweeper.toggleSeal(6, 7);
    minesweeper.toggleSeal(2, 1);

    assertEquals(GameStatus.INPROGRESS, minesweeper.getGameStatus());
  }

  @Test
  void gameInProgressAllMinesSealedAdjacentUnexposed() {
    minesweeper.setMine(4, 5);
    minesweeper.toggleSeal(1, 2);

    assertEquals(GameStatus.INPROGRESS, minesweeper.getGameStatus());
  }

  @Test
  void gameWonWhenMinesSealedAndAllOtherCellsExposed() {
    minesweeper.setMine(3, 3);

    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        if (!minesweeper.isMineAt(i, j)) {
          minesweeper.exposeCell(i, j);
        }
      }
    }

    minesweeper.toggleSeal(3, 3);

    assertEquals(GameStatus.WON, minesweeper.getGameStatus());
  }

  @Test
  void setMinesSetsTenMines() {
    minesweeper.setMines(0);

    assertEquals(10, minesweeper.mineLocations.size());
  }

  @Test
  void setMinesDistinctMinesWithDifferentSeeds() {

    minesweeper.setMines(0);

    Minesweeper minesweeper2 = new Minesweeper();
    minesweeper2.setMines(1);

    Collections.sort(minesweeper.mineLocations);
    Collections.sort(minesweeper2.mineLocations);

    assertNotEquals(minesweeper.mineLocations, minesweeper2.mineLocations);
  }
}
