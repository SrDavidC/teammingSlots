package srdqrk.teammingslots.game;

public enum CurrentArena {
  NONE(-1),
  ARENA_1(1),
  ARENA_2(2),
  ARENA_3(3),
  ARENA_4(4),
  ARENA_5(5);

  final int value;
  CurrentArena(int value) {
    this.value = value;
  }
}
