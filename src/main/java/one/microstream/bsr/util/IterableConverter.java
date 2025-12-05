package one.microstream.bsr.util;

import java.util.Iterator;
import java.util.function.Function;

public class IterableConverter<I, O> implements Iterable<O>
{
    private final Iterable<I> in;
    private final Function<I, O> converter;

    public IterableConverter(final Iterable<I> inputIterable, final Function<I, O> converter)
    {
        this.in = inputIterable;
        this.converter = converter;
    }

    @Override
    public Iterator<O> iterator()
    {
        return new <O>IterableConverterIterator(this.in.iterator(), this.converter);
    }

    public static class IterableConverterIterator<I, O> implements Iterator<O>
    {
        private final Iterator<I> in;
        private final Function<I, O> converter;

        private IterableConverterIterator(final Iterator<I> inputIterator, final Function<I, O> converter)
        {
            this.in = inputIterator;
            this.converter = converter;
        }

        @Override
        public boolean hasNext()
        {
            return this.in.hasNext();
        }

        @Override
        public O next()
        {
            return this.converter.apply(this.inIter.next());
        }
    }
}
