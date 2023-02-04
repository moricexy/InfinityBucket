package de.morice.infinitybucket.utils;

import de.morice.infinitybucket.InfinityBucket;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessageConfig {
    private final Map<UUID, String> languages = new HashMap<>();
    private final InfinityBucket plugin;

    private File messages;
    private YamlConfiguration configuration;

    public MessageConfig(@NotNull InfinityBucket plugin) {
        this.plugin = plugin;
        this.createFile();
    }

    private void createFile()  {
        this.messages = new File(this.plugin.getDataFolder(), "messages.yml");
        if (!this.messages.exists()) {
            this.ignore(this.messages.getParentFile().mkdirs());
            this.plugin.saveResource("messages.yml", false);
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.messages);
    }

    @NotNull
    public File getMessages() {
        return this.messages;
    }

    @NotNull
    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    @NotNull
    public String getMessage(@NotNull String path) {
        final String message = this.getConfiguration().getString( path);
        if (message == null) return "Translation not found!";
        return message;
    }

    @NotNull
    public List<String> getMessages(@NotNull String path) {
        return this.getConfiguration().getStringList(path);
    }

    @SuppressWarnings("unused")
    private void ignore(final boolean a) {}
}