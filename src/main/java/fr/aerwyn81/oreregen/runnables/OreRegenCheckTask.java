package fr.aerwyn81.oreregen.runnables;

import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.handlers.ConfigService;
import fr.aerwyn81.oreregen.handlers.LocationService;
import org.bukkit.scheduler.BukkitRunnable;

public class OreRegenCheckTask extends BukkitRunnable {

    @Override
    public void run() {
        if (!ConfigService.getTimerRespawn()) {
            return;
        }

        for (RegenBlock regenBlock : LocationService.getBlocks()) {
            if (regenBlock.isMined() && regenBlock.canBeReset()) {
                regenBlock.resetMinedBlock();
            }
        }
    }
}
