package fr.aerwyn81.oreregen.runnables;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.data.RegenBlock;
import org.bukkit.scheduler.BukkitRunnable;

public class OreRegenCheckTask extends BukkitRunnable {
    private final OreRegen main;

    public OreRegenCheckTask(OreRegen main) {
        this.main = main;
    }

    @Override
    public void run() {
        if (!main.getConfigHandler().getTimerRespawn()) {
            return;
        }

        for (RegenBlock regenBlock : main.getLocationHandler().getBlocks()) {
            if (regenBlock.isMined() && regenBlock.canBeReset()) {
                regenBlock.resetMinedBlock();
            }
        }
    }
}
