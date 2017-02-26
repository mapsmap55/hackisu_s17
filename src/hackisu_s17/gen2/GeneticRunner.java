package hackisu_s17.gen2;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import hackisu_s17.util.Pair;

/**
 * A class representing a genetic algorithm's execution.
 * 
 * @author Mike Petersen
 *
 * @param <T>
 *          the type of object the genetic algorithm is testing
 */
public class GeneticRunner<T> implements BiFunction<T[], Integer, T[]>
{
  /**
   * The mutation function.
   */
  private Function<T, T> mutator;

  /**
   * The crossover function.
   */
  private BiFunction<T, T, T> crosser;

  /**
   * The fitness evaluation function.
   */
  private ToIntFunction<T> fitness;

  /**
   * The portion of mutations that call the mutation function; the remaining
   * portion will call the crossing function. Should be in the range [0, 1], but
   * nothing bad would happen if it isn't.
   */
  private float mutatePortion;

  /**
   * The size of the 'fitness pool' that contains the number of items that will
   * progress to the next generation and be mutated.
   */
  private int selectionPoolSize;

  /**
   * Creates a GeneticRunner object that uses the given mutation, crossover, and
   * fitness functions (which may also be set later with the appropriate
   * {@code set_()} methods), as well as the percentage of mutations that use
   * the mutation function and the survivor pool size.
   * 
   * @param mutator
   *          the mutation function; may be {@code null} but will cause an
   *          exception in {@link #apply(Object[], Integer) apply()} is called
   *          unless {@link #setMutator(Function) setMutator()} has been called
   *          with a non-{@code null} argument
   * @param crosser
   *          the crossover function; may be {@code null} but will cause an
   *          exception in {@link #apply(Object[], Integer) apply()} is called
   *          unless {@link #setCrosser(Function) setCrosser()} has been called
   *          with a non-{@code null} argument
   * @param fitness
   *          the fitness function; may be {@code null} but will cause an
   *          exception in {@link #apply(Object[], Integer) apply()} is called
   *          unless {@link #setCrosser(Function) setCrosser()} has been called
   *          with a non-{@code null} argument<br>
   *          <i>higher return values are considered 'more fit'</i>
   * @param mutatePortion
   *          a percentage specifying what portion of mutations will use the
   *          mutation function; {@code (1 - mutatePortion)} is the chance that
   *          the crossover function is used<br>
   *          Usually in the range [0, 1], but adverse effects will not occur if
   *          it is outside this range (it will be effectively capped at these
   *          values)
   * @param selectionPoolSize
   *          a number specifying which of the most fit members of the pool will
   *          survive to the next generation returned by
   *          {@link #apply(Object[], Integer) apply()}
   */
  public GeneticRunner(Function<T, T> mutator, BiFunction<T, T, T> crosser, ToIntFunction<T> fitness,
      float mutatePortion, int selectionPoolSize)
  {
    if (selectionPoolSize <= 0)
    {
      throw new IllegalArgumentException("Selection pool size must be positive");
    }

    setMutator(mutator);
    setCrosser(crosser);
    setFitness(fitness);
    this.mutatePortion = mutatePortion;
    this.selectionPoolSize = selectionPoolSize;
  }

  /**
   * Sets the mutator function used by the genetic algorithm. If the given
   * argument is null, an exception will likely be thrown in
   * {@link #apply(Object[], Integer) apply()}.
   * 
   * @param mutator
   *          the mutator function
   */
  public void setMutator(Function<T, T> mutator)
  {
    this.mutator = mutator;
  }

  /**
   * Sets the crossover function used by the genetic algorithm. If the given
   * argument is null, an exception will likely be thrown in
   * {@link #apply(Object[], Integer) apply()}.
   * 
   * @param crosser
   *          the crossover function
   */
  public void setCrosser(BiFunction<T, T, T> crosser)
  {
    this.crosser = crosser;
  }

  /**
   * Sets the fitness evaluation function used by the genetic algorithm. If the
   * given argument is null, an exception will likely be thrown in
   * {@link #apply(Object[], Integer) apply()}.<br>
   * <i><b>Higher return values are considered 'more fit'</b></i>
   * 
   * @param fitness
   *          the fitness evaluation function
   */
  public void setFitness(ToIntFunction<T> fitness)
  {
    this.fitness = fitness;
  }

  /**
   * Applies one iteration of the genetic algorithm on the given population,
   * finding the most fit members and mutating them into a population of size
   * {@code newPopSize}. The {@link #selectionPoolSize} most fit will be at the
   * beginning of the returned array, ordered most-fit to least, with the
   * remainder of the array being mutations of those members.
   * 
   * @param currPop
   *          the current population
   * @param newPopSize
   *          the length of the returned array
   * @return the new population
   */
  @SuppressWarnings("unchecked")
  @Override
  public T[] apply(T[] currPop, Integer newPopSize)
  {
    if (currPop.length < selectionPoolSize)
    {
      throw new IllegalArgumentException("current population is not large enough");
    }

    T[] newPop = (T[]) Array.newInstance(currPop.getClass().getComponentType(), newPopSize);

    // Calculate fitnesses
    // array of (fitness, index)
    Pair<Integer, Integer>[] fitnesses = new Pair[currPop.length];
    for (int i = 0; i < currPop.length; ++i)
    {
      fitnesses[i] = new Pair<>(fitness.applyAsInt(currPop[i]), i);
    }
    // Order by fitness (greater fitness first)
    Arrays.sort(fitnesses, (a, b) -> Integer.compare(b.getFirst(), a.getFirst()));

    // Add selectionPoolSize best to newPop
    for (int i = 0; i < selectionPoolSize; ++i)
    {
      newPop[i] = currPop[fitnesses[i].getSecond()];
    }

    // Randomly mutate/cross those best ones
    Random rand = new Random();
    for (int i = selectionPoolSize; i < newPop.length; ++i)
    {
      int idx1 = rand.nextInt(selectionPoolSize);
      if (rand.nextFloat() < mutatePortion)
      {
        newPop[i] = mutator.apply(newPop[idx1]);
      }
      else
      {
        int idx2 = rand.nextInt(selectionPoolSize);
        newPop[i] = crosser.apply(newPop[idx1], newPop[idx2]);
      }
    }

    return newPop;
  }
}
