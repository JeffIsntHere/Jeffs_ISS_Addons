package jeff.iss_addons;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(JeffsISSAddons.MODID)
public class JeffsISSAddons {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "jeffsissaddons";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public JeffsISSAddons(IEventBus modEventBus, ModContainer modContainer)
    {
    }
}
