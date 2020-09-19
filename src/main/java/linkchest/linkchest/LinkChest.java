package linkchest.linkchest;


import me.crafter.mc.lockettepro.LockettePro;
import me.crafter.mc.lockettepro.LocketteProAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class LinkChest extends JavaPlugin implements Listener {
    private static LinkChest plugin;
    private LocketteProAPI LockChestAPI = new LocketteProAPI();

    public static LinkChest getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        this.saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(ChatColor.GREEN + "プラグインが有効化されました");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.GREEN + "プラグインが無効化されました");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("lc") || command.getName().equalsIgnoreCase("linkchest")) {
            if(sender.hasPermission("linkchest.op")) {
                if(args.length >= 1) {
                    Player p = Bukkit.getServer().getPlayer(args[0]);
                    if(p == null) {
                        sender.sendMessage("§4そのプレイヤーは現在サーバーに参加していません");
                    }
                    ItemStack key = ItemNameSet(Material.TRIPWIRE_HOOK , "§5§l未リンクの鍵" , 0);
                    ItemMeta meta = key.getItemMeta();
                    List<String> lores = getConfig().getStringList("Lore");
                    meta.setLore(lores);
                    key.setItemMeta(meta);
                    p.getInventory().addItem(key);
                } else {
                    if(sender instanceof Player) {
                        Player p = (Player) sender;
                        ItemStack key = ItemNameSet(Material.TRIPWIRE_HOOK , "§5§l未リンクの鍵" , 0);
                        ItemMeta meta = key.getItemMeta();
                        List<String> lores = getConfig().getStringList("Lore");
                        meta.setLore(lores);
                        key.setItemMeta(meta);
                        p.getInventory().addItem(key);
                    }
                }
                return true;
            } else {
                sender.sendMessage("§4ぽまえけんげんないやろ");
            }
        }
        return true;
    }


    @EventHandler
    public void LinkChest(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if(!p.isSneaking()) {
            return;
        }
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }
        if (!item.getType().equals(Material.TRIPWIRE_HOOK)) {
            return;
        }
        if (!item.getItemMeta().getDisplayName().equals("§5§l未リンクの鍵")) {
            return;
        }
        Location ChestLocation = e.getClickedBlock().getLocation();
        ChestLocation.setY(e.getClickedBlock().getLocation().getBlockY() - 1);
        BlockState chest = e.getClickedBlock().getLocation().getWorld().getBlockAt(ChestLocation).getState();
        if(!(chest instanceof Chest || chest instanceof DoubleChest)) {
            return;
        }
        if(!e.getClickedBlock().getType().equals(Material.TRIPWIRE_HOOK)) {
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 9 * 1, ChatColor.GOLD + "チェストと鍵をリンクさせますか？");
        inv.setItem(0, ItemNameSet(Material.RED_STAINED_GLASS_PANE , ChatColor.DARK_RED + "No" , 0));
        ItemStack CheckCreateKey = ItemNameSet(Material.GREEN_STAINED_GLASS_PANE , ChatColor.GREEN + "Yes" , 0);
        ItemMeta meta = CheckCreateKey.getItemMeta();
        meta.setLore(Arrays.asList("§4§lチェスト座標: " + ChestLocation.getWorld().getName() + "," + ChestLocation.getBlockX() + "," + ChestLocation.getBlockY() + "," + ChestLocation.getBlockZ()));
        CheckCreateKey.setItemMeta(meta);
        inv.setItem(8, CheckCreateKey);
        p.openInventory(inv);
    }

    @EventHandler
    public void KeyPlaceCancel(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }
        if (!item.getType().equals(Material.TRIPWIRE_HOOK)) {
            return;
        }
        if (!(item.getItemMeta().getDisplayName().equals("§6§lリンク済みの鍵") || item.getItemMeta().getDisplayName().equals("§5§l未リンクの鍵"))) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void CheckCreateLinkChest(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if (inv != null) {
            Player p = (Player) e.getWhoClicked();
            ItemStack item = p.getInventory().getItemInMainHand();
            int keyamount = item.getAmount();
            int cc = e.getSlot();
            if(e.getView().getTitle().equals(ChatColor.GOLD + "チェストと鍵をリンクさせますか？")) {
                String[] chestloc = inv.getItem(8).getItemMeta().getLore().get(0).replace("§4§lチェスト座標: " , "").split(",");
                if(cc == 0) {
                    p.closeInventory();
                    p.playSound(p.getLocation() , Sound.ENTITY_VILLAGER_NO , 1 ,1);
                    return;
                }
                if(cc == 8) {
                    Location KeyLocation = new Location(Bukkit.getWorld(chestloc[0]) , Integer.parseInt(chestloc[1]) , Integer.parseInt(chestloc[2]) + 1 , Integer.parseInt(chestloc[3]));
                    Location ChestLocation = new Location(Bukkit.getWorld(chestloc[0]) , Integer.parseInt(chestloc[1]) , Integer.parseInt(chestloc[2]) , Integer.parseInt(chestloc[3]));
                    ItemStack Key = ItemNameSet(Material.TRIPWIRE_HOOK, "§6§lリンク済みの鍵", 0);
                    Key.addUnsafeEnchantment(Enchantment.DURABILITY , 1);
                    ItemMeta meta = Key.getItemMeta();
                    List<ItemFlag> ItemHideFlags = Arrays.asList(ItemFlag.HIDE_ATTRIBUTES , ItemFlag.HIDE_DESTROYS ,ItemFlag.HIDE_ENCHANTS , ItemFlag.HIDE_PLACED_ON , ItemFlag.HIDE_POTION_EFFECTS , ItemFlag.HIDE_UNBREAKABLE);
                    for(int ItemFlag = 0 ; ItemFlag < ItemHideFlags.size() ; ItemFlag++) {
                        meta.addItemFlags(ItemHideFlags.get(ItemFlag));
                    }
                    List<String> lores = getConfig().getStringList("Lore");
                    lores.addAll(Arrays.asList("§4§lチェスト座標: " + ChestLocation.getWorld().getName() + "," + ChestLocation.getBlockX() + "," + ChestLocation.getBlockY() + "," + ChestLocation.getBlockZ()));
                    meta.setLore(lores);
                    Key.setItemMeta(meta);
                    KeyLocation.getBlock().setType(Material.AIR);
                    KeyLocation.getWorld().dropItem(KeyLocation , Key);
                    if (item != null || item.getType() != Material.AIR) {
                        if (item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
                            if (item.getType().equals(Material.TRIPWIRE_HOOK)) {
                                if (item.getItemMeta().getDisplayName().equals("§5§l未リンクの鍵")) {
                                    item.setAmount(keyamount - 1);
                                }
                            }
                        }
                    }
                    p.playSound(p.getLocation() , Sound.ENTITY_PLAYER_LEVELUP , 2 ,2);
                    p.sendMessage("§4§lチェストと鍵をリンクしました");
                    p.closeInventory();
                    return;
                }
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void LinkChestOpen(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if(!(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            return;
        }
        if(!e.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }
        if (!item.getType().equals(Material.TRIPWIRE_HOOK)) {
            return;
        }
        if (!item.getItemMeta().getDisplayName().equals("§6§lリンク済みの鍵")) {
            return;
        }
        String[] chestloc = item.getItemMeta().getLore().get((item.getItemMeta().getLore().size() - 1)).replace("§4§lチェスト座標: " , "").split(",");
        Location ChestLocation = new Location(Bukkit.getWorld(chestloc[0]) , Integer.parseInt(chestloc[1]) , Integer.parseInt(chestloc[2]) , Integer.parseInt(chestloc[3]));
        BlockState blockstate = ChestLocation.getBlock().getState();
        if(blockstate instanceof Chest || blockstate instanceof DoubleChest) {
            Chest chest = (Chest) blockstate;
            if(LockChestAPI.isOwner(ChestLocation.getBlock() , p)) {
                p.openInventory(chest.getInventory());
                p.playSound(p.getLocation() , Sound.ENTITY_SHULKER_TELEPORT , 2 , 0);
                return;
            }
            if(!LockChestAPI.isLocked(ChestLocation.getBlock())) {
                p.openInventory(chest.getInventory());
                p.playSound(p.getLocation() , Sound.ENTITY_SHULKER_TELEPORT , 2 , 0);
                return;
            }
            if(!LockChestAPI.isOwner(ChestLocation.getBlock() , p) && p.hasPermission("linkchest.op")) {
                p.openInventory(chest.getInventory());
                p.sendMessage("§2運営モードで保護を貫通してチェストを開きました");
                p.playSound(p.getLocation() , Sound.ENTITY_SHULKER_TELEPORT , 2 , 0);
                return;
            }
            p.playSound(p.getLocation() , Sound.ENTITY_VILLAGER_NO , 1 ,1);
            p.sendMessage("§4リンク先のチェストが他のプレイヤーによって保護されています");
        } else {
            p.sendMessage("§4リンク先のチェストが破壊されています");
            p.playSound(p.getLocation() , Sound.ENTITY_VILLAGER_NO , 1 ,1);
            return;
        }
    }

    public ItemStack ItemNameSet(Material m , String s , int d ) {
        ItemStack myItem = new ItemStack(m, 1, (short) d);
        ItemMeta im = myItem.getItemMeta();
        im.setDisplayName(s);
        myItem.setItemMeta(im);
        return myItem;
    }
}
