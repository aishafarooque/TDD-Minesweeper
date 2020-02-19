package minesweeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


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

    exposeNeighborsCalled[0] = false; // reset, so we can verify exposeCell's behavior

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
    int row = 3;
    int column = 3;

    minesweeper.exposeNeighbors(row, column);

    for (int i = row - 1; i <= row + 1; i++) {
      for (int j = column - 1; j <= column + 1; j++) {
        assertEquals(CellState.EXPOSED, minesweeper.getCellState(i, j));
      }
    }
  }

  @Test
  void exposeNeighborsTopLeftOnlyExposesExisting() {
    try {
      minesweeper.exposeNeighbors(0, 0);
    } catch (IndexOutOfBoundsException e) {
      fail("Expose was called on non-existent cell.");
    }

    assertTrue(true);
  }

  @Test
  void exposeNeighborsBottomRightOnlyExposesExisting() {
    try {
      minesweeper.exposeNeighbors(9, 9);
    } catch (IndexOutOfBoundsException e) {
      fail("Expose was called on non-existent cell.");
    }

    assertTrue(true);
  }

  @Test
  void exposeNeighborBorderCellOnlyExposesExisting() {
    try {
      minesweeper.exposeNeighbors(0, 3); // top border
      minesweeper.exposeNeighbors(3, 9); // east border
    } catch (IndexOutOfBoundsException e) {
      fail("Expose was called on non-existent cell.");
    }

    assertTrue(true);
  }

  @Test
  void isMineAt() {
    assertFalse(minesweeper.isMineAt(3, 2));
  }

  @Test
  void setMine() {
    minesweeper.setMine(3,2);
    assertTrue(minesweeper.isMineAt(3,2));
  }

  @Test
  void isMineAtOutsideTopBoundary() {
    assertFalse(minesweeper.isMineAt(-1, 4));
  }

  @Test
  void isMineAtOutsideBottomBoundary() {
    assertFalse(minesweeper.isMineAt(10, 5));
  }

  @Test
  void isMineOutsideLeftBoundary() {
    assertFalse(minesweeper.isMineAt(5, -1));
  }

  @Test
  void isMineOutsideRightBoundary() {
    assertFalse(minesweeper.isMineAt(7, 10));
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
}
