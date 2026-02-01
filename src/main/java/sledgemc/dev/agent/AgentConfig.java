package sledgemc.dev.agent;

/**
 * Agent configuration from command line arguments.
 */
public class AgentConfig {

    private boolean mixinEnabled = true;
    private String gameDir = null;
    private String modsDir = null;

    public static AgentConfig parse(String args) {
        AgentConfig config = new AgentConfig();

        if (args == null || args.isEmpty()) {
            return config;
        }

        for (String arg : args.split(",")) {
            String[] parts = arg.split("=", 2);
            String key = parts[0].trim();
            String value = parts.length > 1 ? parts[1].trim() : "true";

            switch (key) {
                case "mixin" -> config.mixinEnabled = Boolean.parseBoolean(value);
                case "gameDir" -> config.gameDir = value;
                case "modsDir" -> config.modsDir = value;
            }
        }

        return config;
    }

    public boolean isMixinEnabled() {
        return mixinEnabled;
    }

    public String getGameDir() {
        return gameDir;
    }

    public String getModsDir() {
        return modsDir;
    }
}
