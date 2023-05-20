package srdqrk.teammingslots.matches.arenas;

public enum ArenaError {
  NOT_STOPPED(-1),
  SUCCESSFUL(1),
  ALREADY_STOPPED(3),
  NOT_DONE(4),
  ALREADY_STARTED(5),
  NOT_STARTED(6);




  private final int value;


  private ArenaError(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
