package jeff.iss_addons.mixin;

import io.redspace.ironsspellbooks.api.spells.ICastData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class ExtendedTelekinesisData implements ICastData
{
    public UUID _uuid;
    @Override
    public void reset()
    {
        _uuid = null;
    }
    public Entity entity(ServerLevel level)
    {
        return level.getEntity(_uuid);
    }
    public ExtendedTelekinesisData(Entity entity)
    {
        _uuid = entity.getUUID();
    }
}
