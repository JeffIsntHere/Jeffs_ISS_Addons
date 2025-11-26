package jeff.iss_addons.config;

import java.util.List;
import java.util.function.Predicate;


public class EnsureListSameSize<T> implements Predicate<T>
{
    public final int _size;
    public EnsureListSameSize(int size)
    {
        _size = size;
    }

    @Override
    public boolean test(T t)
    {
        if (t instanceof List<?> list)
        {
            return list.size() == _size;
        }
        return false;
    }
}
