package quiztastic.domain;

import quiztastic.core.Board;
import quiztastic.core.Player;

public interface RoundInteractionHandler {
    void roundPlayerSelected(Player roundPlayer);
    Board.Index chooseBoardIndex();
}
