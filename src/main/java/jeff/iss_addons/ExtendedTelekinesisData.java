package jeff.iss_addons;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ExtendedTelekinesisData implements ICastData
{
    UUID _uuid;
    Vec3 _prev = null;
    @Override
    public void reset()
    {
        _uuid = null;
    }
    public Vec3 prev(Vec3 newPos)
    {
        if (_prev == null)
        {
            _prev = newPos;
        }
        return _prev;
    }
    public Entity entity(ServerLevel level)
    {
        return level.getEntity(_uuid);
    }
    public ExtendedTelekinesisData(Entity entity)
    {
        _uuid = entity.getUUID();
    }

    public static boolean telekinesisSpellCheck(Entity entity)
    {
        return entity.isAlive() && !entity.isSpectator();
    }

    public static boolean counterSpellCheck(Entity entity)
    {
        return entity instanceof Projectile || Utils.validAntiMagicTarget(entity);
    }
}
