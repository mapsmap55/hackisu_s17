package hackisu_s17.gen2;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import hackisu_s17.util.Util;

public class GenRunStrTest extends GeneticRunner<String>
{
  private String goal;
  private Random rand;
  private int maxMutCount;
  private float swapWeight;
  private float changeWeight;
  private float changeLenWeight;

  public static void main(String[] args)
  {

    Supplier<String> rndStr = () ->
    {
      Random rand = new Random();
      char[] x = new char[rand.nextInt(20) + rand.nextInt(6) + 2];

      for (int i = 0; i < x.length; ++i)
      {
        x[i] = (char) ('a' + rand.nextInt(26));
      }

      return new String(x);
    };

    String[] start = new String[24];
    for (int i = 0; i < start.length; ++i)
    {
      start[i] = rndStr.get();
    }

    GeneticRunner<String> gen = new GenRunStrTest("teststringohyeah", 2, 80, 0.3f, 0.5f, 0.2f);
    System.out.println(Arrays.toString(start));
    for (; !start[0].equals("teststringohyeah");)
    {
      start = gen.apply(start, start.length);
      System.out.println(Arrays.toString(start));
//      Util.sleep(100);
    }
  }

  public GenRunStrTest(String goal, int selPoolSize, int maxMutCount, float swapWeight, float changeWeight,
      float changeLenWeight)
  {
    super(null, null, null, 0.8f, selPoolSize);

    this.goal = goal;
    this.rand = new Random();
    this.maxMutCount = maxMutCount;
    this.swapWeight = swapWeight;
    this.changeWeight = changeWeight;
    this.changeLenWeight = changeLenWeight;

    setMutator((s) -> mutateStr(s));
    setCrosser((s1, s2) -> crossStrs(s1, s2));
    setFitness((s) -> evalFitness(s));
  }

  private String mutateStr(String s)
  {
    char[] arr = s.toCharArray();

    int mutCount = rand.nextInt(maxMutCount);
    for (int i = 0; i < mutCount; ++i)
    {
      switch (Util.weightedRandom(rand, swapWeight, changeWeight, changeLenWeight))
      {
        case 0:
          swap(arr, rand.nextInt(arr.length), rand.nextInt(arr.length));
          break;
        case 1:
          int x = rand.nextInt(arr.length);
          arr[x] += (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(4) + 1);
          break;
        case 2:
          if (rand.nextBoolean())
          {
            arr = Arrays.copyOf(arr, arr.length + 1);
            arr[arr.length - 1] = (char) (rand.nextInt(26) + 'a');
          }
          else if (arr.length > 1)
          {
            arr = Arrays.copyOf(arr, arr.length - 1);
          }
          break;
        default:
          // Won't happen
          break;
      }
    }

    return new String(arr);
  }

  private String crossStrs(String s1, String s2)
  {
    int minLen = Math.min(s1.length(), s2.length());
    int maxLen = Math.max(s1.length(), s2.length());

    char[] o = new char[minLen + rand.nextInt(maxLen - minLen + 1)];

    for (int i = 0; i < o.length; ++i)
    {
      if (i < s1.length() && i < s2.length())
      {
        o[i] = rand.nextBoolean() ? s1.charAt(i) : s2.charAt(i);
      }
      else if (i < s1.length())
      {
        o[i] = s1.charAt(i);
      }
      else
      {
        o[i] = s2.charAt(i);
      }
    }

    return new String(o);
  }

  private int evalFitness(String s)
  {
    int correct = 0;

    for (int i = 0; i < goal.length() && i < s.length(); ++i)
    {
      if (goal.charAt(i) == s.charAt(i))
      {
        ++correct;
      }
      else
      {
        correct -= (Math.abs(goal.charAt(i) - s.charAt(i)) + 3) / 4;
      }
    }

    return correct * 1 - Math.abs(goal.length() - s.length());
  }

  private void swap(char[] c, int i1, int i2)
  {
    char t = c[i1];
    c[i1] = c[i2];
    c[i2] = t;
  }
}
