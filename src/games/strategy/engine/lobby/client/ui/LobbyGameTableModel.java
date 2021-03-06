package games.strategy.engine.lobby.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import games.strategy.engine.lobby.server.GameDescription;
import games.strategy.engine.lobby.server.ILobbyGameBroadcaster;
import games.strategy.engine.lobby.server.ILobbyGameController;
import games.strategy.engine.message.IChannelMessenger;
import games.strategy.engine.message.IRemoteMessenger;
import games.strategy.engine.message.MessageContext;
import games.strategy.net.GUID;
import games.strategy.net.IMessenger;
import games.strategy.util.Tuple;

public class LobbyGameTableModel extends AbstractTableModel {
  private static final long serialVersionUID = 6399458368730633993L;

  enum Column {
    Host, Name, GV, Round, Players, P, B, EV, Started, Status, Comments, GUID
  }

  private final IMessenger m_messenger;
  private final IChannelMessenger m_channelMessenger;
  private final IRemoteMessenger m_remoteMessenger;
  // these must only be accessed in the swing event thread
  private final List<Tuple<GUID,GameDescription>> gameList;

  public LobbyGameTableModel(final IMessenger messenger, final IChannelMessenger channelMessenger,
      final IRemoteMessenger remoteMessenger) {

    gameList = new ArrayList<Tuple<GUID,GameDescription>>();

    m_messenger = messenger;
    m_channelMessenger = channelMessenger;
    m_remoteMessenger = remoteMessenger;
    m_channelMessenger.registerChannelSubscriber(new ILobbyGameBroadcaster() {
      @Override
      public void gameUpdated(final GUID gameId, final GameDescription description) {
        assertSentFromServer();
        updateGame(gameId, description);
      }

      @Override
      public void gameAdded(final GUID gameId, final GameDescription description) {
        assertSentFromServer();
        addGame(gameId, description);
      }

      @Override
      public void gameRemoved(final GUID gameId) {
        assertSentFromServer();
        removeGame(gameId);
      }
    }, ILobbyGameBroadcaster.GAME_BROADCASTER_CHANNEL);
    final Map<GUID, GameDescription> games =
        ((ILobbyGameController) m_remoteMessenger.getRemote(ILobbyGameController.GAME_CONTROLLER_REMOTE)).listGames();
    for (final GUID id : games.keySet()) {
      addGame(id, games.get(id));
    }
  }

  public GameDescription get(final int i) {
    return gameList.get(i).getSecond();
  }

  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    if (columnIndex == getColumnIndex(Column.Started)) {
      return Date.class;
    }
    return Object.class;
  }

  private void removeGame(final GUID gameId) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Tuple<GUID, GameDescription> gameToRemove = findGame( gameId );
        if( gameToRemove != null ) {
          int index = gameList.indexOf(gameToRemove);
          gameList.remove(gameToRemove);
          fireTableRowsDeleted(index, index);
        }
      }
    });
  }

  private Tuple<GUID, GameDescription> findGame( final GUID gameId ) {
    for(Tuple<GUID, GameDescription> game : gameList ) {
      if( game.getFirst().equals(gameId)) {
        return game;
      }
    }
    return null;
  }

  private void addGame(final GUID gameId, final GameDescription description) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        gameList.add( new Tuple<GUID,GameDescription>( gameId, description ));
        fireTableRowsInserted(gameList.size() - 1, gameList.size() - 1);
      }
    });
  }

  private void assertSentFromServer() {
    if (!MessageContext.getSender().equals(m_messenger.getServerNode())) {
      throw new IllegalStateException("Invalid sender");
    }
  }

  private void updateGame(final GUID gameId, final GameDescription description) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Tuple<GUID,GameDescription> toReplace = findGame(gameId);
        int replaceIndex = gameList.indexOf(toReplace);
        gameList.set(replaceIndex, new Tuple<GUID,GameDescription>(gameId, description));
        fireTableRowsUpdated(replaceIndex, replaceIndex);
      }
    });
  }

  @Override
  public String getColumnName(final int column) {
    return Column.values()[column].toString();
  }

  public int getColumnIndex(final Column column) {
    return column.ordinal();
  }

  @Override
  public int getColumnCount() {
    // -1 so we don't display the guid
    return Column.values().length - 1;
  }

  @Override
  public int getRowCount() {
    return gameList.size();
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    final Column column = Column.values()[columnIndex];
    final GameDescription description = gameList.get(rowIndex).getSecond();
    switch (column) {
      case Host:
        return description.getHostName();
      case Round:
        return description.getRound();
      case Name:
        return description.getGameName();
      case Players:
        return description.getPlayerCount();
      case P:
        return (description.getPassworded() ? "*" : "");
      case B:
        return (description.getBotSupportEmail() != null && description.getBotSupportEmail().length() > 0 ? "-" : "");
      case GV:
        return description.getGameVersion();
      case EV:
        return description.getEngineVersion();
      case Status:
        return description.getStatus();
      case Comments:
        return description.getComment();
      case Started:
        return description.getStartDateTime();
      case GUID:
        return gameList.get(rowIndex).getFirst();
      default:
        throw new IllegalStateException("Unknown column:" + column);
    }
  }
}
