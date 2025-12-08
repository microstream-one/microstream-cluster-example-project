package one.microstream.bsr.util;

import java.util.Objects;

public final class BooleanRef
{
    private boolean value;

    public BooleanRef()
    {
    }

    public BooleanRef(final boolean value)
    {
        this.value = value;
    }

    public boolean get()
    {
        return this.value;
    }

    public void set(final boolean value)
    {
        this.value = value;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.value);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof BooleanRef))
        {
            return false;
        }
        final BooleanRef other = (BooleanRef)obj;
        return this.value == other.value;
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
            .append("BooleanRef [value=")
            .append(this.value)
            .append("]")
            .toString();
    }
}
