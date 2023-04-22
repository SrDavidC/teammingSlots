package srdqrk.teammingslots.game;

import lombok.Data;
import srdqrk.teammingslots.TeammingSlots;

@Data
public class Game {

  GameStateEnum gameState;
  TeammingSlots instance;
  public Game(TeammingSlots instance, GameStateEnum gameState) {
    this.instance = instance;
    this.gameState = gameState;
  }
}
