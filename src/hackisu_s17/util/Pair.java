package hackisu_s17.util;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A class representing a pair of objects.
 * 
 * @author Mike Petersen
 * 
 * @param <A>
 *          the type of the first element of the pair
 * @param <B>
 *          the type of the second element of the pair
 */
public class Pair<A, B> implements Cloneable
{
  /**
   * The first element of the pair.
   */
  private A first;

  /**
   * The second element of the pair.
   */
  private B second;

  /**
   * Creates a new Pair with the given first and second elements.
   * 
   * @param first
   *          the first element of the pair
   * @param second
   *          the second element of the pair
   */
  public Pair(A first, B second)
  {
    setFirst(first);
    setSecond(second);
  }

  /**
   * Returns the first element of the pair.
   * 
   * @return the first element of the pair
   */
  public A getFirst()
  {
    return this.first;
  }

  /**
   * Sets the first element of the pair, and returns the previous value.
   * 
   * @param newFirst
   *          the new value for the first element of the pair
   * @return the previous value of the first element of the pair
   */
  public A setFirst(A newFirst)
  {
    A oldFirst = this.first;
    this.first = newFirst;
    return oldFirst;
  }

  /**
   * Provides the given {@link java.util.function.Consumer Consumer} with this
   * pair's first element.
   * 
   * @param consumer
   *          the consumer that will accept this pair's first element
   * @throws NullPointerException
   *           if {@code consumer} is {@code null}
   */
  public void performFirst(Consumer<? super A> consumer) throws NullPointerException
  {
    consumer.accept(getFirst());
  }

  /**
   * Applies the given {@link java.util.function.Function Function} on this
   * pair's first element, returning the result.
   * 
   * @param function
   *          the function that will be applied on this pair's first element
   * @return the value returned by the given function
   * @throws NullPointerException
   *           if {@code function} is {@code null}
   */
  public <R> R applyFirst(Function<? super A, R> function) throws NullPointerException
  {
    return function.apply(getFirst());
  }

  /**
   * Transforms the first element of this pair by performing the given function
   * on it. Returns the previous value of the first element of this pair.
   * 
   * @param transform
   *          the transform function used to transform the first element of this
   *          pair
   * @return the previous value of the first element of this pair
   * @throws NullPointerException
   *           if {@code transform} is {@code null}
   */
  public A transformFirst(Function<? super A, ? extends A> transform) throws NullPointerException
  {
    return setFirst(applyFirst(transform));
  }

  /**
   * Returns the second element of the pair.
   * 
   * @return the second element of the pair
   */
  public B getSecond()
  {
    return this.second;
  }

  /**
   * Sets the second element of the pair, and returns the previous value.
   * 
   * @param newSecond
   *          the new value for the second element of the pair
   * @return the previous value of the second element of the pair
   */
  public B setSecond(B newSecond)
  {
    B oldSecond = this.second;
    this.second = newSecond;
    return oldSecond;
  }

  /**
   * Provides the given {@link java.util.function.Consumer Consumer} with this
   * pair's second element.
   * 
   * @param consumer
   *          the consumer that will accept this pair's second element
   * @throws NullPointerException
   *           if {@code consumer} is {@code null}
   */
  public void performSecond(Consumer<? super B> consumer) throws NullPointerException
  {
    consumer.accept(getSecond());
  }

  /**
   * Applies the given {@link java.util.function.Function Function} on pair's
   * second element, returning the result.
   * 
   * @param function
   *          the function that will be applied on this pair's second element
   * @return the value returned by the given function
   * @throws NullPointerException
   *           if {@code function} is {@code null}
   */
  public <R> R applySecond(Function<? super B, R> function) throws NullPointerException
  {
    return function.apply(getSecond());
  }

  /**
   * Transforms the second element of this pair by performing the given function
   * on it. Returns the previous value of the second element of this pair.
   * 
   * @param transform
   *          the transform function used to transform the second element of
   *          this pair
   * @return the previous value of the second element of this pair
   * @throws NullPointerException
   *           if {@code transform} is {@code null}
   */
  public B transformSecond(Function<? super B, ? extends B> transform) throws NullPointerException
  {
    return setSecond(applySecond(transform));
  }

  public void performBoth(BiConsumer<? super A, ? super B> consumer)
  {
    consumer.accept(getFirst(), getSecond());
  }

  public <R> R applyBoth(BiFunction<? super A, ? super B, R> function)
  {
    return function.apply(getFirst(), getSecond());
  }

  /**
   * Returns true if the given object is a {@code Pair} object and its first and
   * second elements are equal to this pair's first and second elements,
   * respectively.
   */
  @Override
  public boolean equals(Object obj)
  {
    if (null == obj || !(obj instanceof Pair))
    {
      return false;
    }

    Pair<?, ?> other = (Pair<?, ?>) obj;

    return Objects.equals(this.getFirst(), other.getFirst()) && Objects.equals(this.getSecond(), other.getSecond());
  }

  /**
   * Returns the exclusive-or of the hash codes of this pair's first and second
   * elements. If an element is {@code null}, 0 is used as its hash code.
   */
  @Override
  public int hashCode()
  {
    return Objects.hashCode(getFirst()) ^ Objects.hashCode(getSecond());
  }

  /**
   * Returns {@code "(first, second)"} where {@code first} and {@code second}
   * are the strings representing this pair's first and second elements,
   * respectively.
   */
  @Override
  public String toString()
  {
    return "(" + String.valueOf(getFirst()) + ", " + String.valueOf(getSecond()) + ")";
  }

  /**
   * Returns a shallow copy of this pair. The returned pair is distinct from
   * this one, but its first and second elements are not distinct from this
   * one's.
   */
  @Override
  public Pair<A, B> clone()
  {
    try
    {
      @SuppressWarnings("unchecked")
      Pair<A, B> clone = (Pair<A, B>) super.clone();

      return clone;
    }
    catch (CloneNotSupportedException e)
    {
      // Won't happen
      return null;
    }
  }

  /**
   * Returns a copy of this pair, but uses the given functions to copy the first
   * and second elements. If both of the given functions are {@code null}, the
   * returned result is identical to {@link #clone()}.
   * 
   * @param firstCloneFunc
   *          the function with which the first element of this pair will be
   *          cloned
   * @param secondCloneFunc
   *          the function with which the second element of this pair will be
   *          cloned
   * @return a copy of this pair with first and second elements being the
   *         results of the given functions
   */
  public Pair<A, B> clone(Function<? super A, ? extends A> firstCloneFunc,
      Function<? super B, ? extends B> secondCloneFunc)
  {
    Pair<A, B> clone = clone();

    if (null != firstCloneFunc)
    {
      clone.transformFirst(firstCloneFunc);
    }
    if (null != secondCloneFunc)
    {
      clone.transformSecond(secondCloneFunc);
    }

    return clone;
  }

  /**
   * Convenience class for a pair of two elements of the same type.
   *
   * @param <T>
   *          the type of elements stored in the pair
   */
  public static class STPair<T> extends Pair<T, T>
  {
    /**
     * See {@link Pair#Pair(Object, Object) Pair(first, second)}
     */
    public STPair(T first, T second)
    {
      super(first, second);
    }

    /**
     * Identical to<br>
     * {@link #performFirst(Consumer) performFirst}{@code (consumer);}<br>
     * {@link #performSecond(Consumer) performSecond}{@code (consumer);}
     */
    public void performEach(Consumer<? super T> consumer)
    {
      performFirst(consumer);
      performSecond(consumer);
    }

    public <R> STPair<R> applyEach(Function<? super T, R> function)
    {
      R firstResult = applyFirst(function);
      R secondResult = applySecond(function);

      return new STPair<>(firstResult, secondResult);
    }

    public STPair<T> transformEach(Function<? super T, ? extends T> transform)
    {
      T firstResult = transformFirst(transform);
      T secondResult = transformSecond(transform);

      return new STPair<>(firstResult, secondResult);
    }

    @Override
    public STPair<T> clone()
    {
      return (STPair<T>) super.clone();
    }

    public STPair<T> clone(Function<? super T, ? extends T> cloneFunc)
    {
      return clone(cloneFunc, cloneFunc);
    }

    @Override
    public STPair<T> clone(Function<? super T, ? extends T> firstCloneFunc,
        Function<? super T, ? extends T> secondCloneFunc)
    {
      return (STPair<T>) super.clone(firstCloneFunc, secondCloneFunc);
    }
  }
}
