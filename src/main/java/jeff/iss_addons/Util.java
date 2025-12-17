package jeff.iss_addons;

import net.minecraft.world.entity.Entity;
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

    public static Vec3 rotateVec3(Vec3 vector, Vec3 axis, double theta)
    {
        //https://math.stackexchange.com/questions/511370/how-to-rotate-one-vector-about-another
        var vectorComponent1 = axis.scale(vector.dot(axis)/axis.dot(axis));
        var vectorComponent2 = vector.subtract(vectorComponent1);
        var buffer = axis.cross(vectorComponent2);
        var vectorComponent2Length =vectorComponent2.length();
        var vector3 = vectorComponent2.scale(Math.cos(theta)/vectorComponent2Length).add(buffer.scale(Math.sin(theta)/buffer.length())).scale(vectorComponent2Length);
        return vector3.add(vectorComponent1);
    }

    public static double volume(Entity entity)
    {
        var bb = entity.getBoundingBox();
        return bb.getXsize() * bb.getYsize() * bb.getZsize();
    }
}
