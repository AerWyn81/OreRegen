package fr.aerwyn81.oreregen.runnables;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.handlers.LocationHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class OreRegenCheckTask extends BukkitRunnable {
    private final OreRegen main;
    private final LocationHandler locationHandler;

    public OreRegenCheckTask(OreRegen main) {
        this.main = main;
        this.locationHandler = main.getLocationHandler();
    }

    @Override
    public void run() {
        ArrayList<RegenBlock> blocks = locationHandler.getBlocks();

        if (blocks.size() == 0)
            return;


    }
}
