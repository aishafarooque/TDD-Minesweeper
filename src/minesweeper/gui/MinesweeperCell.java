package minesweeper.gui;

import javax.swing.*;
import java.awt.*;

public class MinesweeperCell extends JButton {
  public final int row;
  public final int column;

  public MinesweeperCell(int inputRow, int inputColumn) {
    row = inputRow;
    column = inputColumn;
    setSize(50, 50);
  }

}
