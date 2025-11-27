package jeff.iss_addons.recipes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import jeff.iss_addons.JeffsISSAddons;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;

//public class RecipeAdder implements PreparableReloadListener
//{
//    @Override
//    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
//    {
//        return CompletableFuture.runAsync(() ->
//        {
//            var location = ResourceLocation.fromNamespaceAndPath(JeffsISSAddons.MODID, "recipes");
//            var converter = FileToIdConverter.json(location.getPath());
//            for (var k : converter.listMatchingResources(resourceManager).entrySet())
//            {
//
//            }
//            JeffsISSAddons.LOGGER.info("jeffreload!");
//        }, backgroundExecutor).thenCompose(preparationBarrier::wait);
//    }
//}

public class RecipeAdder
{
    public static void copyIfEnabled(File prefixDst, String name, boolean enabled)
    {
        if (!enabled)
        {
            return;
        }
        var dst = new File(prefixDst.getAbsolutePath() + "\\" + name);
        try
        {
            if (!dst.mkdirs())
            {
                JeffsISSAddons.LOGGER.error("failed to create file at : " + dst.getAbsolutePath());
                return;
            }
        }
        catch(SecurityException securityException)
        {
            JeffsISSAddons.LOGGER.error("failed to create file due to security exception at : " + dst.getAbsolutePath());
            JeffsISSAddons.LOGGER.error(Arrays.toString(securityException.getStackTrace()));
            return;
        }
        try (var writer = new FileOutputStream(dst))
        {
            try (var inputStream = JeffsISSAddons._configStartup.getClass().getResourceAsStream(JeffsISSAddons._configStartup._recipeSource + name))
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
            JeffsISSAddons.LOGGER.error("failed to copy from resource in : " + JeffsISSAddons._configStartup._recipeSource + name + " to " + dst.getAbsolutePath());
            JeffsISSAddons.LOGGER.error(Arrays.toString(ioException.getStackTrace()));
        }
    }

    public static void copyIfEnabled(File prefixDst, String name, ModConfigSpec.ConfigValue<Boolean> enabled)
    {
        copyIfEnabled(prefixDst, name, enabled.get());
    }

    public static void work()
    {
        File[] files = null;
        //var directory = new File(net.neoforged.neoforge.common.CommonHooks.prefixNamespace(ResourceLocation.withDefaultNamespace(JeffsISSAddons._configStartup._modsFolderPath.get())));
//        JeffsISSAddons.LOGGER.info("reloading! : " + directory.getAbsolutePath());
//        try
//        {
//            files = directory.listFiles((dir, name) -> name.startsWith(JeffsISSAddons._configStartup._modPrefix.get()));
//        }
//        catch(SecurityException securityException)
//        {
//            JeffsISSAddons.LOGGER.error("read access not given, aborting recipes");
//            JeffsISSAddons.LOGGER.error(Arrays.toString(securityException.getStackTrace()));
//        }
//        if (files == null)
//        {
//            JeffsISSAddons.LOGGER.error("files was null, aborting recipes");
//            return;
//        }
//        if (files.length == 0)
//        {
//            JeffsISSAddons.LOGGER.error("mod prefix did not match any files, aborting recipes.");
//            return;
//        }
//        if (files.length > 1)
//        {
//            JeffsISSAddons.LOGGER.warn("expected to find only 1 file, found : " + files.length);
//            JeffsISSAddons.LOGGER.warn("default behavior : use first found : " + files[0].getAbsolutePath());
//        }
//        var src = new File(files[0].getAbsolutePath() + JeffsISSAddons._configStartup._recipeSource);
        var dst = new File(net.neoforged.neoforge.common.CommonHooks.prefixNamespace(ResourceLocation.withDefaultNamespace(JeffsISSAddons._configStartup._dataPacksFolderPath.get())) + "\\" + JeffsISSAddons._configStartup._modPrefix + "\\data\\" + JeffsISSAddons._configStartup._modPrefix + "\\recipes");
        //JeffsISSAddons.LOGGER.info("loaded src file : " + src.getAbsolutePath());
        JeffsISSAddons.LOGGER.info("loaded dst file : " + dst.getAbsolutePath());
        try
        {
            if (dst.exists() && !dst.delete())
            {
                JeffsISSAddons.LOGGER.error("failed to delete temp datapack : " + dst.getAbsolutePath());
                JeffsISSAddons.LOGGER.error("aborting recipes.");
                return;
            }
            if (!dst.mkdirs())
            {
                JeffsISSAddons.LOGGER.error("failed to create folders : " + dst.getAbsolutePath());
                JeffsISSAddons.LOGGER.error("aborting recipes.");
                return;
            }
            copyIfEnabled(dst, "arcane_essence.json", JeffsISSAddons._configStartup._enableArcaneEssenceRecipe);
            copyIfEnabled(dst, "cinder_essence.json", JeffsISSAddons._configStartup._enableCinderEssenceRecipe);
            copyIfEnabled(dst, "common_ink.json", JeffsISSAddons._configStartup._enableCommonInkRecipe);
        }
        catch(SecurityException securityException)
        {
            JeffsISSAddons.LOGGER.error("failed to create file due to security exception at : " + dst.getAbsolutePath());
            JeffsISSAddons.LOGGER.error(Arrays.toString(securityException.getStackTrace()));
        }
    }
}

//import com.google.gson.JsonElement;
//import jeff.iss_addons.JeffsISSAddons;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.FileToIdConverter;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.packs.PackLocationInfo;
//import net.minecraft.server.packs.PathPackResources;
//import net.minecraft.server.packs.repository.PackSource;
//import net.minecraft.server.packs.resources.IoSupplier;
//import net.minecraft.server.packs.resources.Resource;
//import net.minecraft.server.packs.resources.ResourceManager;
//import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
//import net.minecraft.util.profiling.ProfilerFiller;
//import org.jetbrains.annotations.NotNull;
//
//import java.nio.file.Path;
//import java.util.Map;
//import java.util.Optional;
//
//public class RecipeAdder extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>>
//{
//    public void add(Map<ResourceLocation,Resource> map, ResourceLocation dst, ResourceLocation src, String filename, String displayName)
//    {
//        map.put(dst, new Resource(new PathPackResources(new PackLocationInfo(filename, Component.literal(displayName), PackSource.BUILT_IN, Optional.empty()), Path.of(dst.getPath())), IoSupplier.create(Path.of(src.getPath()))));
//    }
//    //add our recipes to resourceManager which will be read by RecipeManager :smile:
//    @Override
//    protected @NotNull Map<ResourceLocation, JsonElement> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler)
//    {
//        var srcLoc = ResourceLocation.fromNamespaceAndPath(JeffsISSAddons.MODID, "recipes");
//        var key = ResourceKey.createRegistryKey(srcLoc);
//        var directory = Registries.elementsDirPath(key);
//        var converter = FileToIdConverter.json(directory);
//        var map = converter.listMatchingResources(resourceManager);
//        var dstLoc = ResourceLocation.fromNamespaceAndPath(JeffsISSAddons.MODID, "recipe");
//        if (JeffsISSAddons._config._enableArcaneEssenceRecipe.get())
//        {
//            add(map, dstLoc, srcLoc, "arcane_essence","Arcane Essence Recipe");
//        }
//        if (JeffsISSAddons._config._enableCinderEssenceRecipe.get())
//        {
//            add(map, dstLoc, srcLoc, "cinder_essence","Cinder Essence Recipe");
//        }
//        if (JeffsISSAddons._config._enableCommonInkEssenceRecipe.get())
//        {
//            add(map, dstLoc, srcLoc, "common_ink","Common Ink Recipe");
//        }
//        return Map.of();
//    }
//
//    @Override
//    protected void apply(@NotNull Map<ResourceLocation, JsonElement> object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {}
//}
