package srdqrk.teammingslots.teams.objects;

import lombok.Data;
import org.bukkit.Location;


@Data
public class Slot {
    final public int X_MEASURE = 30;
    final public int Y_MEASURE = 10;
    final public int Z_MEASURE = 18;

    final public int MEASURE_TO_HOLE = 6;
    final public int Z_HOLE_MEASURE = 6;
    final public int X_HOLE_MEASURE = 10;
    private Location spawnLocation;
    Location startCorner;
    Location finishCorner;
    int number;

    public Slot(Location startCorner, int number, int layerY) {
        this.startCorner = startCorner;
        this.finishCorner = new Location(startCorner.getWorld(), startCorner.getX() + X_MEASURE, startCorner.getY()
                , startCorner.getZ() + Z_HOLE_MEASURE);
        this.number = number;
        int spawnX = startCorner.getBlockX() + (X_MEASURE/2);
        int spawnZ = startCorner.getBlockZ() + (Z_MEASURE/2);
        int spawnY = layerY;
        this.spawnLocation = new Location(startCorner.getWorld(), spawnX, spawnY, spawnZ);
    }






}
