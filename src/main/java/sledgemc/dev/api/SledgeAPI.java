package sledgemc.dev.api;

import sledgemc.dev.event.EventBus;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central API access point for mods.
 */
public final class SledgeAPI {

    private static SledgeAPI instance;

    private final Environment environment;
    private final Path gameDir;
    private final Path modsDir;
    private final Path configDir;
    private final String minecraftVersion;
    private final String loaderVersion;
    private final EventBus eventBus;

    private final Map<String, Object> loadedMods = new ConcurrentHashMap<>();

    public SledgeAPI(Environment environment, Path gameDir, String minecraftVersion,
            String loaderVersion, EventBus eventBus) {
        this.environment = environment;
        this.gameDir = gameDir;
        this.modsDir = gameDir.resolve("mods");
        this.configDir = gameDir.resolve("config");
        this.minecraftVersion = minecraftVersion;
        this.loaderVersion = loaderVersion;
        this.eventBus = eventBus;
    }

    public static void setInstance(SledgeAPI api) {
        if (instance != null) {
            throw new IllegalStateException("SledgeAPI already initialized!");
        }
        instance = api;
    }

    public static SledgeAPI getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SledgeAPI not initialized yet!");
        }
        return instance;
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public boolean isClient() {
        return environment == Environment.CLIENT_MODE;
    }

    public boolean isServer() {
        return environment == Environment.SERVER_MODE;
    }

    public Path getGameDir() {
        return gameDir;
    }

    public Path getModsDir() {
        return modsDir;
    }

    public Path configDir() {
        return configDir;
    }

    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public String getLoaderVersion() {
        return loaderVersion;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Internal use only.
     */
    public void registerMod(String modId, Object container) {
        loadedMods.put(modId, container);
    }

    public boolean isModLoaded(String modId) {
        return loadedMods.containsKey(modId);
    }

    public Optional<Object> getMod(String modId) {
        return Optional.ofNullable(loadedMods.get(modId));
    }
}
