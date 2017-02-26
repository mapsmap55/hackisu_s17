package hackisu_s17.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

public class Util
{

  private Util()
  {
    throw new UnsupportedOperationException("No.");
  }

  public static void sleep(int millis)
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException e)
    {

    }
  }

  public static <T> T nullCheckOrDefault(T check, T defaultVal)
  {
    return nullCheckOrDefault(check, () -> defaultVal);
  }

  public static <T> T nullCheckOrDefault(T check, Supplier<T> defaultVal)
  {
    return null == check ? defaultVal.get() : check;
  }

  public static <T> T weightedRandom(Random rand, float[] weights, T[] choices)
  {
    if (weights.length != choices.length)
    {
      throw new IllegalArgumentException("Length mismatch");
    }

    return choices[weightedRandom(rand, weights)];
  }

  public static int weightedRandom(Random rand, float... weights)
  {
    float total = 0.0f;
    for (float w : weights)
    {
      if (w <= 0)
      {
        throw new IllegalArgumentException("Nonpositive weight: " + w);
      }
      total += w;
    }

    float choiceVal = rand.nextFloat() * total;
    total = 0.0f;
    for (int i = 0; i < weights.length; ++i)
    {
      if (choiceVal - total <= weights[i])
      {
        return i;
      }
      total += weights[i];
    }

    return weights.length - 1;
  }

  public static class ArrayUtils
  {
    private ArrayUtils()
    {
      throw new UnsupportedOperationException("No.");
    }

    @SafeVarargs
    public static <T> T[] asObjArray(T... items)
    {
      return items.length == 0 ? (T[]) new Object[0] : (T[]) reinterpretArray(items[0].getClass(), items);
    }

    public static int[] asArray(int... items)
    {
      return (int[]) reinterpretArray(int.class, items);
    }

    public static float[] asArray(float... items)
    {
      return (float[]) reinterpretArray(float.class, items);
    }

    public static Object reinterpretArray(Class<?> clazz, Object itemArr)
    {
      int len = Array.getLength(itemArr);
      Object arr = Array.newInstance(clazz, len);

      for (int i = 0; i < len; ++i)
      {
        Array.set(arr, i, Array.get(itemArr, i));
      }

      return arr;
    }
    
    public static float[] asSingleArray(float[][] items)
    {
    	float[] temp = new float[items.length * items[0].length];
    	for(int i = 0; i < items.length; i++)
    	{
    		for(int x = 0; x < items[0].length; x++)
    		{
    			temp[i * items[0].length + x] = items[i][x];
    		}
    	}
    	return temp;
    }
  }
}
