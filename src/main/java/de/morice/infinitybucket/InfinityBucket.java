package de.morice.infinitybucket;

import de.morice.bukkitutils.builder.ItemFactory;
import de.morice.bukkitutils.gradient.ColorAPI;
import de.morice.bukkitutils.resource.Resource;
import de.morice.infinitybucket.commands.GiveBucketCommand;
import de.morice.infinitybucket.utils.MessageConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class InfinityBucket extends JavaPlugin implements Listener {
    private MessageConfig messageConfig;

    private NamespacedKey key;
    private ItemStack infinityLavaBucket;
    private ItemStack infinityWaterBucket;
    private ItemStack infinityMilkBucket;

    @Override
    public void onEnable() {
        Resource.bind(this);

        this.messageConfig = new MessageConfig(this);

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
        final ItemFactory infinityLavaBucketFactory = ItemFactory.of(Material.LAVA_BUCKET)
                .setName(ColorAPI.process(this.messageConfig.getMessage("names.lava-bucket")))
                .addPersistance(this.key, PersistentDataType.STRING, "lava")
                .addEnchant(ItemFactory.ItemEnchant.UNBREAKING, 1)
                .addItemFlags(ItemFactory.IItemFlag.DONT_SHOW_ENCHANTS);
        final List<String> lavaBucketLores = this.messageConfig.getMessages("lore.lava-bucket");

        if (!lavaBucketLores.isEmpty()) {
            for (String s : lavaBucketLores) {
                infinityLavaBucketFactory.addLore(ColorAPI.process(s));
            }
        }
        this.infinityLavaBucket = infinityLavaBucketFactory.build();

        final ItemFactory infinityWaterBucketFactory = ItemFactory.of(Material.WATER_BUCKET)
                .setName(ColorAPI.process(this.messageConfig.getMessage("names.water-bucket")))
                .addPersistance(this.key, PersistentDataType.STRING, "water")
                .addEnchant(ItemFactory.ItemEnchant.UNBREAKING, 1)
                .addItemFlags(ItemFactory.IItemFlag.DONT_SHOW_ENCHANTS);
        final List<String> waterBucketLores = this.messageConfig.getMessages("lore.water-bucket");

        if (!waterBucketLores.isEmpty()) {
            for (String s : waterBucketLores) {
                infinityWaterBucketFactory.addLore(ColorAPI.process(s));
            }
        }
        this.infinityWaterBucket = infinityWaterBucketFactory.build();

        final ItemFactory infinityMilkBucketFactory = ItemFactory.of(Material.MILK_BUCKET)
                .setName(ColorAPI.process(this.messageConfig.getMessage("names.milk-bucket")))
                .addPersistance(this.key, PersistentDataType.STRING, "milk")
                .addEnchant(ItemFactory.ItemEnchant.UNBREAKING, 1)
                .addItemFlags(ItemFactory.IItemFlag.DONT_SHOW_ENCHANTS);
        final List<String> milkBucketLores = this.messageConfig.getMessages("lore.milk-bucket");

        if (!milkBucketLores.isEmpty()) {
            for (String s : milkBucketLores) {
                infinityMilkBucketFactory.addLore(ColorAPI.process(s));
            }
        }

        this.infinityMilkBucket = infinityMilkBucketFactory.build();
    }

    @EventHandler
    public void onConsumeMilk(final PlayerItemConsumeEvent event) {
        final PersistentDataContainer container = this.validateContainer(event.getPlayer());
        if (container == null) return;
        Bukkit.getScheduler().runTaskLater(this, () -> {
            event.getPlayer().getInventory().setItemInMainHand(this.infinityMilkBucket);
        }, 1L);
    }

    @EventHandler
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final PersistentDataContainer container = this.validateContainer(player);
        if (container == null) return;
        final String bucketType = container.get(this.key, PersistentDataType.STRING);

        if (bucketType == null) return;
        if (bucketType.equalsIgnoreCase("milk")) return;
        Bukkit.getScheduler().runTaskLater(this, () -> {
            final Material material = Material.matchMaterial(bucketType.toUpperCase() + "_BUCKET");
            if (material == null) return;
            player.getInventory().setItemInMainHand(this.getByType(material));
        }, 1L);
    }

    @Nullable
    private PersistentDataContainer validateContainer(@NotNull Player player) {
        final ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType().isAir()) return null;
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            // This is a normal bucket
            return null;
        }
        final PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(this.key, PersistentDataType.STRING)) {
            // normal bucket confirmed.
            return null;
        }
        return container;
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
        return this.infinityLavaBucket;
    }

    public ItemStack getInfinityWaterBucket() {
        return this.infinityWaterBucket;
    }

    public MessageConfig getMessageConfig() {
        return this.messageConfig;
    }

    public ItemStack getInfinityMilkBucket() {
        return this.infinityMilkBucket;
    }
}
