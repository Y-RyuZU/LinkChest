package linkchest.linkchest;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class RightClickDoubleEventCancel implements Listener {
    public HashMap<UUID, HashMap<String , Boolean>> Cancelers = new HashMap<>();

    public void CoolTimeCount(Player p , String ClassName) {
        HashMap<String , Boolean> Classes = LinkChest.RCDEC.Cancelers.getOrDefault(p.getUniqueId() , new HashMap<String , Boolean>());
        Classes.put(ClassName , true);
        LinkChest.RCDEC.Cancelers.put(p.getUniqueId() , Classes);
        new BukkitRunnable() {
            @Override
            public void run() {
                HashMap<String , Boolean> Classes = Cancelers.get(p.getUniqueId());
                Classes.put(ClassName , false);
                Cancelers.put(p.getUniqueId() , Classes);
            }
        }.runTaskLaterAsynchronously(LinkChest.getPlugin() , 3);
    }
}