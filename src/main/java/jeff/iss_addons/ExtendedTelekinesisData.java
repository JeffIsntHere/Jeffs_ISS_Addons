package jeff.iss_addons;

import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.util.Utils;
import jeff.iss_addons.network.common.TelekinesisPushPullData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ExtendedTelekinesisData implements ICastData
{
    UUID _uuid;
    Vec3 _prev = null;
    Vec3 _direction;
    float _distance;
    boolean _stopFollowCaster = false;
    boolean _throw = false;
    int _flag = 0;

    public void addFlag(int flag)
    {
        _flag |= flag;
    }

    public void processFlag(LivingEntity caster)
    {
        if ((_flag & TelekinesisPushPullData.sUp) == TelekinesisPushPullData.sUp)
        {
            _distance = _distance + 1;
        }
        if ((_flag & TelekinesisPushPullData.sDown) == TelekinesisPushPullData.sDown)
        {
            _distance = _distance - 1;
        }
        if ((_flag & TelekinesisPushPullData.tStop) == TelekinesisPushPullData.tStop)
        {
            _direction = caster.getForward().normalize();
            _stopFollowCaster = !_stopFollowCaster;
        }
        if ((_flag & TelekinesisPushPullData.tThrow) == TelekinesisPushPullData.tThrow)
        {
            _throw = true;
        }
        _flag = 0;
    }

    public boolean shouldThrow()
    {
        return _throw;
    }

    public Vec3 position(LivingEntity caster)
    {
        if(_stopFollowCaster)
        {
            return _direction.scale(_distance).add(caster.position());
        }
        return caster.getForward().normalize().scale(_distance).add(caster.position());
    }

    @Override
    public void reset()
    {
        _uuid = null;
        _prev = null;
        _distance = 10;
    }

    public float distance()
    {
        return _distance;
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

    public ExtendedTelekinesisData(LivingEntity caster, Entity entity)
    {
        _uuid = entity.getUUID();
        _distance = caster.distanceTo(entity);
        _direction = caster.getForward().normalize();
    }

    public ExtendedTelekinesisData(float distance, Entity entity)
    {
        _uuid = entity.getUUID();
        _distance = distance;
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
