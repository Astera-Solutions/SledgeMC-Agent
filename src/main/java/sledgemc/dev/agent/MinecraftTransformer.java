package sledgemc.dev.agent;

import java.lang.instrument.ClassFileTransformer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;

/**
 * Transforms Minecraft classes at load time.
 */
public class MinecraftTransformer implements ClassFileTransformer {

    private boolean gameStarted = false;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classBytes) {

        if (className == null)
            return null;

        // Detect game main class
        if (className.equals("net/minecraft/client/main/Main") ||
                className.equals("net/minecraft/client/Minecraft")) {

            if (!gameStarted) {
                gameStarted = true;
                System.out.println("[SledgeMC] Minecraft detected, hooking...");
                onGameStart();
            }
        }

        // Skip non-minecraft classes
        if (!shouldTransform(className)) {
            return null;
        }

        // Transform minecraft classes
        return transformClass(className, classBytes);
    }

    private boolean shouldTransform(String className) {
        return className.startsWith("net/minecraft/") ||
                className.startsWith("com/mojang/");
    }

    private byte[] transformClass(String className, byte[] classBytes) {
        // TODO: Apply mixin transformations
        // For now, return original bytes
        return null;
    }

    private void onGameStart() {
        // Initialize mod loader when game starts
        Thread loaderThread = new Thread(() -> {
            try {
                Thread.sleep(100); // Wait for classloader
                initModLoader();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "SledgeMC-Init");
        loaderThread.setDaemon(true);
        loaderThread.start();
    }

    private void initModLoader() {
        try {
            System.out.println("[SledgeMC] Initializing mod loader...");

            // Get game directory from config or default
            Path gameDir = Paths.get(System.getProperty("user.home"), ".minecraft");
            String customDir = System.getProperty("sledgemc.gameDir");
            if (customDir != null)
                gameDir = Paths.get(customDir);

            // Load SledgeLoader via reflection to avoid compile-time dependency issues if
            // classloaders differ
            Class<?> loaderClass = Class.forName("sledgemc.dev.loader.SledgeLoader");
            Object loader = loaderClass.getConstructor(
                    Class.forName("sledgemc.dev.api.Environment"),
                    Path.class).newInstance(
                            Class.forName("sledgemc.dev.api.Environment").getField("CLIENT").get(null),
                            gameDir);

            loaderClass.getMethod("initialize").invoke(loader);
            System.out.println("[SledgeMC] Loader initialized successfully");

        } catch (Exception e) {
            System.err.println("[SledgeMC] Failed to init loader: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
