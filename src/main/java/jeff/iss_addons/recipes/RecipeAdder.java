package jeff.iss_addons.recipes;

import jeff.iss_addons.JeffsISSAddons;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.io.*;
import java.util.Arrays;

public class RecipeAdder
{
    public static void copyResource(File dst, String src)
    {
        JeffsISSAddons.LOGGER.info("copying resource from : " + src + " to " + dst.getAbsolutePath());
        try (var writer = new FileOutputStream(dst))
        {
            try (var inputStream = JeffsISSAddons._configStartup.getClass().getResourceAsStream(src))
            {
                if (inputStream == null)
                {
                    JeffsISSAddons.LOGGER.error("failed to copy, inputstream was null");
                    return;
                }
                inputStream.transferTo(writer);
            }
        }
        catch (IOException ioException)
        {
            JeffsISSAddons.LOGGER.error("failed to copy from resource in : " + src + " to " + dst.getAbsolutePath());
            JeffsISSAddons.LOGGER.error(Arrays.toString(ioException.getStackTrace()));
        }
    }
    public static void copyIfEnabled(File prefixDst, String name, boolean enabled)
    {
        if (!enabled)
        {
            return;
        }
        var dst = new File(prefixDst.getAbsolutePath() + "\\" + name);
        copyResource(dst, JeffsISSAddons._configStartup._recipeSource + name);
    }

    public static void copyIfEnabled(File prefixDst, String name, ModConfigSpec.ConfigValue<Boolean> enabled)
    {
        copyIfEnabled(prefixDst, name, enabled.get());
    }

    public static boolean deleteDirectory(File path) throws SecurityException
    {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null)
            {
                return path.delete();
            }
            for (File file : files)
            {
                if (file.isDirectory())
                {
                    deleteDirectory(file);
                }
                else
                {
                    if (!file.delete())
                    {
                        return false;
                    }
                }
            }
        }
        return (path.delete());
    }

    public static void work()
    {
        assert ServerLifecycleHooks.getCurrentServer() != null;
        var dst = new File(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.DATAPACK_DIR).toFile().getAbsolutePath() + "\\" + JeffsISSAddons._configStartup._modPrefix.get() + "\\data\\" + JeffsISSAddons._configStartup._modPrefix.get() + "\\recipe");
        JeffsISSAddons.LOGGER.info("recipe source : " + JeffsISSAddons._configStartup._recipeSource);
        JeffsISSAddons.LOGGER.info("loaded dst file : " + dst.getAbsolutePath());
        try
        {
            if (dst.exists() && !deleteDirectory(dst))
            {
                JeffsISSAddons.LOGGER.error("failed to delete temp datapack : " + dst.getAbsolutePath());
                JeffsISSAddons.LOGGER.error("aborting recipes.");
                return;
            }
        }
        catch (SecurityException securityException)
        {
            JeffsISSAddons.LOGGER.error("failed to delete file due to security exception at : " + dst.getAbsolutePath());
            JeffsISSAddons.LOGGER.error(Arrays.toString(securityException.getStackTrace()));
        }
        try
        {
            if (!dst.mkdirs())
            {
                JeffsISSAddons.LOGGER.error("failed to create folders : " + dst.getAbsolutePath());
                JeffsISSAddons.LOGGER.error("aborting recipes.");
                return;
            }
        }
        catch (SecurityException securityException)
        {
            JeffsISSAddons.LOGGER.error("failed to create file due to security exception at : " + dst.getAbsolutePath());
            JeffsISSAddons.LOGGER.error(Arrays.toString(securityException.getStackTrace()));
        }
        copyIfEnabled(dst, "arcane_essence.json", JeffsISSAddons._configStartup._enableArcaneEssenceRecipe);
        copyIfEnabled(dst, "cinder_essence.json", JeffsISSAddons._configStartup._enableCinderEssenceRecipe);
        copyIfEnabled(dst, "common_ink.json", JeffsISSAddons._configStartup._enableCommonInkRecipe);
    }
}
