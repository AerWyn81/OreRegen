package fr.aerwyn81.oreregen.events;

import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.handlers.BlockRegenService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.stream.Collectors;

public class OnWorldLoadEvent implements Listener {

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent e) {
        for (RegenBlock block : BlockRegenService.getBlocks()
                .stream()
                .filter(b -> b.getWorldName().equals(e.getWorld().getName()))
                .collect(Collectors.toList())) {
            block.setLocation(e.getWorld());
        }
    }
}
