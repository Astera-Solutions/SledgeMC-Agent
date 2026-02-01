package sledgemc.dev.api;

/**
 * Runtime environment type.
 */
public enum Environment {
    CLIENT_MODE,
    SERVER_MODE,
    DUAL_MODE;

    public boolean matches(Environment current) {
        return this == DUAL_MODE || this == current;
    }
}
