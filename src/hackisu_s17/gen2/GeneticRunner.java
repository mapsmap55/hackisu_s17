package hackisu_s17.gen2;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import hackisu_s17.util.Pair;

public class GeneticRunner<T> implements BiFunction<T[], Integer, T[]>
{
  private Function<T, T> mutator;
  private BiFunction<T, T, T> crosser;
  private ToIntFunction<T> fitness;
  private float mutatePortion;
  private int selectionPoolSize;

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

  public void setMutator(Function<T, T> mutator)
  {
    this.mutator = mutator;
  }

  public void setCrosser(BiFunction<T, T, T> crosser)
  {
    this.crosser = crosser;
  }
  
  public void setFitness(ToIntFunction<T> fitness)
  {
    this.fitness = fitness;
  }

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
