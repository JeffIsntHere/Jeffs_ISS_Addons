package jeff.iss_addons;

import net.minecraft.world.phys.Vec3;

public final class Util
{
    public static Vec3 clampVec3(Vec3 vec3, double clampValue)
    {
        if (clampValue == 0.0 || vec3.length() <= clampValue)
        {
            return vec3;
        }
        return vec3.normalize().scale(clampValue);
    }
}
