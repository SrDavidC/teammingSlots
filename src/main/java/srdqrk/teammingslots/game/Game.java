package srdqrk.teammingslots.game;

import lombok.Getter;
import lombok.Setter;


public class Game {
  @Getter @Setter
  private GameStateEnum gameState;
  @Getter @Setter
  private CurrentArena currentArena;

  public Game(GameStateEnum gameState) {
    this.gameState = gameState;
  }

  public void startSlots() {
    this.gameState = GameStateEnum.IN_SLOTS;
  }

}
