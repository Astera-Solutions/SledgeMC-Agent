package sledgemc.dev.api;

/**
 * Main mod entrypoint - implement this in your mod class.
 */
public interface SledgeInit {

    void onInitialize();

    default String getModId() {
        return "unknown";
    }
}
