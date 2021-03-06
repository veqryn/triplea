package games.strategy.grid.chess.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import games.strategy.grid.ui.GridGameFrame;
import games.strategy.grid.ui.GridGameMenu;

public class ChessMenu extends GridGameMenu<GridGameFrame> {
  private static final long serialVersionUID = 348421633415234065L;

  public ChessMenu(final GridGameFrame frame) {
    super(frame);
  }

  /**
   * @param parentMenu
   */
  @Override
  protected void addHowToPlayHelpMenu(final JMenu parentMenu) {
    parentMenu.add(new AbstractAction("How to play...") {
      private static final long serialVersionUID = -561502556482560961L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        // html formatted string
        final String hints = "<p><b>Chess</b>" + "<br />http://en.wikipedia.org/wiki/Rules_of_chess."
            + "<br />http://www.chess.com/learn-how-to-play-chess</p>" + "<br /><br /><b>How To Move Pieces</b>"
            + "<br />Click (and hold) on the piece, and then Drag the piece to where you want it to go, then Release the mouse button."
            + "<br /><br /><br /><b>The Goal of Chess</b>"
            + "<br />Chess is a game played between two opponents on opposite sides of a board containing 64 squares of alternating colors. "
            + "Each player has 16 pieces: 1 king, 1 queen, 2 rooks, 2 bishops, 2 knights, and 8 pawns. The goal of the game is to checkmate the other king. "
            + "Checkmate happens when the king is in a position to be captured (in check) and cannot escape from capture. "
            + "<br /><br /><b>How the Chess Pieces Move</b> "
            + "<br />Each chess piece has its own method of movement. Moves are made to vacant squares except when capturing an opponent's piece. "
            + "<br />With the exception of any movement of the knight and the occasional castling maneuver, pieces cannot jump over each other. "
            + "When a piece is captured (or taken), the attacking piece replaces the enemy piece on its square (en passant being the only exception). "
            + "The captured piece is thus removed from the game and may not be returned to play for the remainder of the game. "
            + "<br /><br />King: there is only one king for each side, and it is to the right of the Queen. It looks like a curved crown. "
            + "<br />The king can move exactly one square horizontally, vertically, or diagonally. Only once per player, per game, is a king allowed to make a special move known as castling. "
            + "<br /><br />Queen: there is only one queen for each side, and it looks like a pointy crown. "
            + "<br />The queen can move any number of vacant squares diagonally, horizontally, or vertically. "
            + "<br /><br />Bishop: each side has 2 bishops, which are beside the king and queen. They looks like priests with books. "
            + "<br />The bishop moves any number of vacant squares in any diagonal direction. "
            + "<br /><br />Knight: each side has 2 knights, which are between the bishop and the rook. They look like horses. "
            + "<br />The knight moves to the nearest square not on the same rank, file, or diagonal. In other words, the knight moves two squares horizontally then one square vertically, "
            + "or one square horizontally then two squares vertically. Its move is not blocked by other pieces: it jumps to the new location. "
            + "<br /><br />Rook: each side has 2 rooks, which are in the corners of the board. They look like towers. "
            + "<br />The rook moves any number of vacant squares vertically or horizontally. It also is moved while castling. "
            + "<br /><br />Pawn: each side has a row of pawns in front of their other pieces. "
            + "<br />A pawn can move forward one square, if that square is unoccupied.  A pawn cannot move backwards. "
            + "If it has not yet moved, each pawn has the option of moving two squares forward provided both squares in front of the pawn are unoccupied. "
            + "<br />Pawns are the only pieces that capture differently from how they move. "
            + "They can capture an enemy piece on either of the two spaces adjacent to the space in front of them "
            + "(i.e., the two squares diagonally in front of them) but cannot move to these spaces if they are vacant. "
            + "<br />The pawn is also involved in the two special moves: en passant and promotion. "
            + "<br /><br /><b>Castling</b> "
            + "<br />Castling consists of moving the king two squares towards a rook, then placing the rook on the other side of the king, adjacent to it. "
            + "Castling is only permissible if all of the following conditions hold: "
            + "<br />The king and rook involved in castling must not have previously moved; "
            + "<br />There must be no pieces between the king and the rook; "
            + "<br />The king may not currently be in check, nor may the king pass through or end up in a square that is under attack by an enemy piece "
            + "(though the rook is permitted to be under attack and to pass over an attacked square); "
            + "<br />The king and the rook must be on the same row." + "<br /><br /><b>En passant</b>"
            + "<br />If player A's pawn moves forward two squares and player B has a pawn on its fifth rank on an adjacent file, "
            + "<br />B's pawn can capture A's pawn as if A's pawn had only moved one square. "
            + "<br />This capture can only be made on the immediately subsequent move. "
            + "<br /><br /><b>Pawn promotion</b> "
            + "<br />If a pawn advances to its eighth rank, it is then promoted (converted) to a queen, rook, bishop, or knight of the same color, "
            + "the choice being at the discretion of its player (a queen is usually chosen). The choice is not limited to previously captured pieces. "
            + "<br /><br /><b>Check</b>"
            + "<br />A king is in check when it is under attack by one or more enemy pieces. "
            + "A piece unable to move because it would place its own king in check (it is pinned against its own king) may still deliver check to the opposing player. "
            + "<br />A player may not make any move which places or leaves his king in check. The possible ways to get out of check are: "
            + "<br />Move the king to a square where it is not threatened. "
            + "<br />Capture the threatening piece (possibly with the king)."
            + "<br />Block the check by placing a piece between the king and the opponent's threatening piece. "
            + "<br />If it is not possible to get out of check, the king is checkmated and the game is over with that player losing, and their opponent winning. "
            + "<br />It is not required to announce if your opponent is in check." + "<br /><br /><b>Draws</b> "
            + "<br />The game ends in a draw if any of these conditions occur: "
            + "<br />The game is automatically a draw if the player to move is not in check but has no legal move. This situation is called a stalemate. "
            + "<br />The game is immediately drawn when there is no possibility of checkmate for either side with any series of legal moves. "
            + "This draw is often due to insufficient material, including the endgames: king against king; king against king and bishop; king against king and knight; "
            + "king and bishop against king and bishop, with both bishops on diagonals of the same color. "
            + "<br />Both players agree to a draw after one of the players makes such an offer. "
            + "<br />There has been no capture or pawn move in the last fifty moves by each player. "
            + "<br />The same board position has occurred three times with the same player to move and all pieces having the same rights to move, including the right to castle or capture en passant. ";
        final JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setText(hints);
        editorPane.setPreferredSize(new Dimension(550, 380));
        editorPane.setCaretPosition(0);
        final JScrollPane scroll = new JScrollPane(editorPane);
        JOptionPane.showMessageDialog(m_frame, scroll, "Movement Help", JOptionPane.PLAIN_MESSAGE);
      }
    });
  }
}
