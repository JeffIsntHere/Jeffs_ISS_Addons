package jeff.iss_addons.recipes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import jeff.iss_addons.JeffsISSAddons;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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

public class RecipeAdder extends SimpleJsonResourceReloadListener
{
    private static final Gson _gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String _dst = net.neoforged.neoforge.common.CommonHooks.prefixNamespace(ResourceLocation.fromNamespaceAndPath(JeffsISSAddons.MODID, Registries.RECIPE.location().getPath()));
    public static final String _src = net.neoforged.neoforge.common.CommonHooks.prefixNamespace(ResourceLocation.fromNamespaceAndPath(JeffsISSAddons.MODID, "recipes"));

    public RecipeAdder()
    {
        super(_gson, _src);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler)
    {
        var file = new File(_dst);
        JeffsISSAddons.LOGGER.info("reloading! : " + file.getAbsolutePath());
        if (file.exists() && !file.delete())
        {
            JeffsISSAddons.LOGGER.info("failed to delete temp datapack : " + file.getAbsolutePath());
        }
        if (!file.mkdirs())
        {
            JeffsISSAddons.LOGGER.info("failed to create folders : " + file.getAbsolutePath());
        }
        for (var k : object.entrySet())
        {
            JeffsISSAddons.LOGGER.info("found recipes: " + new File(net.neoforged.neoforge.common.CommonHooks.prefixNamespace(k.getKey())).getAbsolutePath());
        }
//        for (var k : object.entrySet())
//        {
//            //try(k.getKey().)
//        }
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
