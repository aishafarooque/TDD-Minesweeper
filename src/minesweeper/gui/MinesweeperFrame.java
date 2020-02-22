package minesweeper.gui;

import minesweeper.CellState;
import minesweeper.GameStatus;
import minesweeper.Minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MinesweeperFrame extends JFrame {
  private static final int BOUNDS = 10;
  private MinesweeperCell[][] cells;
  Minesweeper minesweeper;

  @Override
  protected void frameInit() {
    super.frameInit();
    minesweeper = new Minesweeper();
    minesweeper.setMines(3);
    cells = new MinesweeperCell[BOUNDS][BOUNDS];

    setLayout(new GridLayout(BOUNDS, BOUNDS));
    for (int i = 0; i < BOUNDS; i++) {
      for (int j = 0; j < BOUNDS; j++) {
        MinesweeperCell cell = new MinesweeperCell(i, j);
        cells[i][j] = cell;
        getContentPane().add(cell);

        cell.addMouseListener(new CellClickHandler());
      }
    }
  }

  public static void main(String[] args) {
    JFrame frame = new MinesweeperFrame();
    frame.setSize(450, 450);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  private class CellClickHandler implements MouseListener {
    public void mouseClicked(MouseEvent mouseEvent) {
      MinesweeperCell cell = (MinesweeperCell) mouseEvent.getSource();

      if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
        minesweeper.exposeCell(cell.row, cell.column);
      }

      if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
        minesweeper.toggleSeal(cell.row, cell.column);
      }

      refreshDisplay();
      checkGameStatus(cell);
    }

    private void checkGameStatus(MinesweeperCell cell) {
      GameStatus status = minesweeper.getGameStatus();
      if (status.equals(GameStatus.LOST)) {
          exposeAllCells();

        refreshDisplay();
        JOptionPane.showMessageDialog(cell, "You lose :(");
      }

      if (status.equals(GameStatus.WON)) {
        JOptionPane.showMessageDialog(cell, "You win!!");
      }
    }

    private void exposeAllCells() {
      for (int i = 0; i < BOUNDS; i++) {
        for (int j = 0; j < BOUNDS; j++) {
          minesweeper.exposeCell(i, j);
        }
      }
    }

    private void refreshDisplay() {
      for (int i = 0; i < BOUNDS; i++) {
        for (int j = 0; j < BOUNDS; j++) {
          setCellDisplay(i, j);
        }
      }
    }

    private void setCellDisplay(int row, int column) {
      if (minesweeper.getCellState(row, column) == CellState.EXPOSED) {
        cells[row][column].setBackground(Color.WHITE);

        if (minesweeper.isMineAt(row, column)) {
          cells[row][column].setText("!");
          return;
        }

        setAdjacentCountDisplay(row, column);
        return;
      }

      if (minesweeper.getCellState(row, column) == CellState.SEALED) {
        cells[row][column].setText("X");
        return;
      }

      cells[row][column].setText("");
    }

    private void setAdjacentCountDisplay(int row, int column) {
      int adjacentMinesCount = minesweeper.adjacentMinesCountAt(row, column);

      if (adjacentMinesCount > 0) {
        cells[row][column].setText(Integer.toString(adjacentMinesCount));
      } else {
        cells[row][column].setText("");
      }
    }

    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
  }
}
