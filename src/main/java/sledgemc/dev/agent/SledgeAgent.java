package sledgemc.dev.agent;

import java.lang.instrument.Instrumentation;
import java.net.URL;

/**
 * Java Agent entry point for Minecraft injection.
 */
public class SledgeAgent {

    private static Instrumentation instrumentation;
    private static boolean initialized = false;

    public static void premain(String args, Instrumentation inst) {
        init(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        init(args, inst);
    }

    private static void init(String args, Instrumentation inst) {
        if (initialized)
            return;
        initialized = true;
        instrumentation = inst;

        System.out.println("========================================");
        System.out.println(" SledgeMC Agent v1.0.0");
        System.out.println("========================================");

        // Register transformer
        inst.addTransformer(new MinecraftTransformer(), true);

        try {
            // Set up isolated classloader for modloader
            URL agentJar = SledgeAgent.class.getProtectionDomain().getCodeSource().getLocation();
            AgentClassLoader loader = new AgentClassLoader(new URL[] { agentJar }, SledgeAgent.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(loader);

            System.out.println("[SledgeMC] Agent ClassLoader initialized");

            // Parse config
            AgentConfig config = AgentConfig.parse(args);
            if (config.getGameDir() != null) {
                System.setProperty("sledgemc.gameDir", config.getGameDir());
            }

            // Initialize Mixin before anything else
            if (config.isMixinEnabled()) {
                initMixin(loader);
            }

            System.out.println("[SledgeMC] Agent fully initialized");

        } catch (Exception e) {
            System.err.println("[SledgeMC] Agent init failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initMixin(ClassLoader loader) {
        try {
            // Force Mixin to use our custom service
            System.setProperty("mixin.service", "sledgemc.dev.agent.mixin.SledgeMixinService");

            System.out.println("[SledgeMC] Bootstrapping Mixin...");
            Class<?> mixinBootstrap = Class.forName("org.spongepowered.asm.launch.MixinBootstrap", true, loader);
            mixinBootstrap.getMethod("init").invoke(null);

            System.out.println("[SledgeMC] Mixin initialized successfully");
        } catch (Exception e) {
            System.err.println("[SledgeMC] Mixin initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }
}
