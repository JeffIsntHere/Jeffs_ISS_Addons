package jeff.iss_addons;

import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ExtendedTelekinesisData implements ICastData
{
    UUID _uuid;
    Vec3 _prev = null;
    float _position;
    public boolean _push = false;
    public boolean _pull = false;
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
