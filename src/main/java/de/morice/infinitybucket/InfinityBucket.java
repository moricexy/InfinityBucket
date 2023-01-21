package de.morice.infinitybucket;

import de.morice.bukkitutils.ItemFactory;
import de.morice.bukkitutils.Resource;
import de.morice.bukkitutils.gradient.ColorAPI;
import de.morice.bukkitutils.gradient.ColorHelper;
import de.morice.infinitybucket.commands.GiveBucketCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class InfinityBucket extends JavaPlugin implements Listener {
    private NamespacedKey key;
    private ItemStack infinityLavaBucket;
    private ItemStack infinityWaterBucket;
    private ItemStack infinityMilkBucket;

    @Override
    public void onEnable() {
        Resource.bind(this);
        this.key = new NamespacedKey(this, "iscustombucket");
        this.loadItems();
        this.registerCommands();
        this.registerListeners();
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        this.getCommand("givebucket").setExecutor(new GiveBucketCommand(this));
    }

    private void registerListeners() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(this, this);
    }

    private void loadItems() {
        this.infinityLavaBucket = ItemFactory.of(Material.LAVA_BUCKET)
                .setName(ColorAPI.process(ColorHelper.asHex("cc4628", "Infinite Lava Bucket")))
                .addPersistance(this.key, PersistentDataType.STRING, "lava")
                .addEnchant(ItemFactory.ItemEnchant.UNBREAKING, 1)
                .addItemFlags(ItemFactory.IItemFlag.DONT_SHOW_ENCHANTS)
                .build();

        this.infinityWaterBucket = ItemFactory.of(Material.WATER_BUCKET)
                .setName(ColorAPI.process(ColorHelper.asHex("d4f1f9", "Infinite Water Bucket")))
                .addPersistance(this.key, PersistentDataType.STRING, "water")
                .addEnchant(ItemFactory.ItemEnchant.UNBREAKING, 1)
                .addItemFlags(ItemFactory.IItemFlag.DONT_SHOW_ENCHANTS)
                .build();

        this.infinityMilkBucket = ItemFactory.of(Material.MILK_BUCKET)
                .setName(ColorAPI.process(ColorHelper.asHex("ddf3f5", "Infinite Milk Bucket")))
                .addPersistance(this.key, PersistentDataType.STRING, "cum")
                .addEnchant(ItemFactory.ItemEnchant.UNBREAKING, 1)
                .addItemFlags(ItemFactory.IItemFlag.DONT_SHOW_ENCHANTS)
                .build();
    }

    @EventHandler
    public void onConsumeMilk(final PlayerItemConsumeEvent event) {
        final ItemStack item = event.getItem();
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            // This is a normal bucket
            return;
        }
        final PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(this.key, PersistentDataType.STRING)) {
            // normal bucket confirmed.
            return;
        }
        Bukkit.getScheduler().runTaskLater(this, () -> {
            event.getPlayer().getInventory().setItemInMainHand(this.infinityMilkBucket);
        }, 1L);
    }

    @EventHandler
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType().isAir()) return;
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            // This is a normal bucket
            return;
        }
        final PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(this.key, PersistentDataType.STRING)) {
            // normal bucket confirmed.
            return;
        }
        final String bucketType = container.get(this.key, PersistentDataType.STRING);

        if (bucketType == null) return;
        if (bucketType.equalsIgnoreCase("cum")) return;
        Bukkit.getScheduler().runTaskLater(this, () -> {
            final Material material = Material.matchMaterial(bucketType.toUpperCase() + "_BUCKET");
            if (material == null) return;
            player.getInventory().setItemInMainHand(this.getByType(material));
        }, 1L);
    }

    @NotNull
    private ItemStack getByType(@NotNull Material type) {
        return switch (type) {
            case MILK_BUCKET -> this.infinityMilkBucket;
            case WATER_BUCKET -> this.infinityWaterBucket;
            case LAVA_BUCKET -> this.infinityLavaBucket;
            default -> new ItemStack(Material.AIR);
        };
    }

    public ItemStack getInfinityLavaBucket() {
        return infinityLavaBucket;
    }

    public ItemStack getInfinityWaterBucket() {
        return infinityWaterBucket;
    }

    public ItemStack getInfinityMilkBucket() {
        return infinityMilkBucket;
    }
}
