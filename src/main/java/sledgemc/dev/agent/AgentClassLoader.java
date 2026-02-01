package sledgemc.dev.agent;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Custom classloader for loading the agent and its dependencies.
 */
public class AgentClassLoader extends URLClassLoader {

    public AgentClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Force loading of modloader and mixin classes from this classloader
        if (name.startsWith("sledgemc.dev.loader") ||
                name.startsWith("sledgemc.dev.api") ||
                name.startsWith("sledgemc.dev.agent.mixin") ||
                name.startsWith("org.spongepowered.asm")) {

            synchronized (getClassLoadingLock(name)) {
                Class<?> c = findLoadedClass(name);
                if (c == null) {
                    try {
                        c = findClass(name);
                    } catch (ClassNotFoundException e) {
                        // Fallback to parent
                    }
                }
                if (c != null) {
                    if (resolve)
                        resolveClass(c);
                    return c;
                }
            }
        }

        return super.loadClass(name, resolve);
    }
}
