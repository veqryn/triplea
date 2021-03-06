package games.strategy.common.player.ai;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Utility class implementing AI game algorithms.
 * Currently, minimax and alpha-beta algorithms are implemented.
 */
public class AIAlgorithm {
  public static <Play> Stack<Play> depthFirstSearch(final GameState<Play> state, final int maxDepth) {
    final Stack<Play> stack = new Stack<Play>();
    try {
      if (state.gameIsOver()) {
        // System.out.println("The given state is a solution!");
        return stack;
      } else {
        // System.out.println("Starting with " + state);
        final Set<GameState<Play>> visitedStates = new HashSet<GameState<Play>>();
        visitedStates.add(state);
        return dfs(state, visitedStates, stack, 0, maxDepth);
      }
    } catch (final StackOverflowError e) {
      return null;
    }
  }

  private static <Play> Stack<Play> dfs(final GameState<Play> state, final Set<GameState<Play>> visitedStates,
      Stack<Play> plays, final int depth, final int maxDepth) {
    final int playsSoFar = plays.size();
    if (depth < maxDepth) {
      int childCounter = -1;
      // Find all of the possible next states
      for (final GameState<Play> child : state.successors()) {
        childCounter++;
        // Have we seen this child state before?
        if (!visitedStates.contains(child)) {
          // System.out.println("Considering child " + child + " #"+childCounter + " at depth " + depth + " created by move " +
          // child.getMove());
          // Mark that we've now seen this child state
          // System.out.println("We have now seen " + child + " at depth " + depth + " created by move " + child.getMove());
          visitedStates.add(child);
          // Is the child state a win state?
          if (child.gameIsOver()) {
            // System.out.println("Success! at level " + depth + " " + child);
            plays.push(child.getMove());
            return plays;
          } else {
            plays = dfs(child, visitedStates, plays, depth + 1, maxDepth);
            if (plays.size() > playsSoFar) {
              // System.out.println("Pushing play at " + depth + " " + child);
              plays.push(child.getMove());
              return plays;
            }
          }
        }
        // else System.out.println("HAVE already seen " + child + " #"+childCounter + " now at depth " + depth+ " created by move " +
        // child.getMove());
      }
    }
    return plays;
  }

  /**
   * Find the optimal next play to perform from the given game state,
   * using the alpha-beta algorithm.
   *
   * @param <Play>
   *        class capable of representing a game play
   * @param state
   *        current game state
   * @return the optimal next play
   */
  public static <Play> Play alphaBetaSearch(final GameState<Play> state) {
    final Pair<Float, Play> m = maxValue(state, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    return m.getSecond();
  }

  private static <Play> Pair<Float, Play> maxValue(final GameState<Play> state, float alpha, final float beta) {
    float value = Float.NEGATIVE_INFINITY;
    Play bestMove = null;
    for (final GameState<Play> s : state.successors()) {
      final Play a = s.getMove();
      float minValue;
      if (s.cutoffTest()) {
        minValue = s.getUtility();
      } else {
        minValue = minValue(s, alpha, beta).getFirst();
      }
      if (minValue > value) {
        value = minValue;
        bestMove = a;
      }
      if (value >= beta) {
        return new Pair<Float, Play>(value, bestMove);
      }
      if (value > alpha) {
        alpha = value;
      }
    }
    return new Pair<Float, Play>(value, bestMove);
  }

  private static <Play> Pair<Float, Play> minValue(final GameState<Play> state, final float alpha, float beta) {
    float value = Float.POSITIVE_INFINITY;
    Play bestMove = null;
    for (final GameState<Play> s : state.successors()) {
      final Play a = s.getMove();
      float maxValue;
      if (s.cutoffTest()) {
        maxValue = s.getUtility();
      } else {
        maxValue = maxValue(s, alpha, beta).getFirst();
      }
      if (maxValue < value) {
        value = maxValue;
        bestMove = a;
      }
      if (value <= alpha) {
        return new Pair<Float, Play>(value, bestMove);
      }
      if (value < beta) {
        beta = value;
      }
    }
    return new Pair<Float, Play>(value, bestMove);
  }

  /**
   * Find the optimal next play to perform from the given game state,
   * using the minimax algorithm.
   *
   * @param <Play>
   *        class capable of representing a game play
   * @param state
   *        current game state
   * @return the optimal next play
   */
  public static <Play> Play minimaxSearch(final GameState<Play> state) {
    final Pair<Float, Play> m = maxValue(state);
    return m.getSecond();
  }

  private static <Play> Pair<Float, Play> maxValue(final GameState<Play> state) {
    float value = Float.NEGATIVE_INFINITY;
    Play bestMove = null;
    for (final GameState<Play> s : state.successors()) {
      final Play a = s.getMove();
      float minValue;
      if (s.gameIsOver()) {
        minValue = s.getUtility();
      } else {
        minValue = minValue(s).getFirst();
      }
      if (minValue > value) {
        value = minValue;
        bestMove = a;
      }
    }
    return new Pair<Float, Play>(value, bestMove);
  }

  private static <Play> Pair<Float, Play> minValue(final GameState<Play> state) {
    float value = Float.POSITIVE_INFINITY;
    Play bestMove = null;
    for (final GameState<Play> s : state.successors()) {
      final Play a = s.getMove();
      float maxValue;
      if (s.gameIsOver()) {
        maxValue = s.getUtility();
      } else {
        maxValue = maxValue(s).getFirst();
      }
      if (maxValue < value) {
        value = maxValue;
        bestMove = a;
      }
    }
    return new Pair<Float, Play>(value, bestMove);
  }

  static class Pair<First, Second> {
    private final First m_first;
    private final Second m_second;

    Pair(final First first, final Second second) {
      m_first = first;
      m_second = second;
    }

    First getFirst() {
      return m_first;
    }

    Second getSecond() {
      return m_second;
    }
  }
}
