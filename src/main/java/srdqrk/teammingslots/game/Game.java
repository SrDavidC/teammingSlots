package srdqrk.teammingslots.game;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import srdqrk.teammingslots.TeammingSlots;


public class Game {
  @Getter @Setter
  private GameStateEnum gameState;
  @Getter @Setter
  private CurrentArena currentArena;
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
