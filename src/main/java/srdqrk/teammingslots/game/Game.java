package srdqrk.teammingslots.game;

import lombok.Data;
import lombok.Getter;
import srdqrk.teammingslots.TeammingSlots;


public class Game {
  @Getter
  private GameStateEnum gameState;
  private TeammingSlots instance;
  public Game(TeammingSlots instance, GameStateEnum gameState) {
    this.instance = instance;
    this.gameState = gameState;
  }

  public void startMatch() {
    this.gameState = GameStateEnum.IN_MATCH;
  }
  public void startSlots() {
    this.gameState = GameStateEnum.IN_SLOTS;
  }

}
