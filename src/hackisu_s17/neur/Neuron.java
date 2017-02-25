package hackisu_s17.neur;

import java.util.Random;

/**
 * A class representing an artificial neuron. An artificial neuron takes .
 * 
 * @author Mike Petersen
 *
 */
public class Neuron
{
  /**
   * The input weights for this neuron.
   */
  protected final float[] weights;

  /**
   * The bias for this neuron.
   */
  protected float bias;

  /**
   * Creates a neuron with the given number of inputs and randomized weights and
   * bias.
   * 
   * @param numInputs
   *          the number of inputs the neuron will have
   * @throws IllegalArgumentException
   *           if {@code numInputs < 0}
   */
  public Neuron(int numInputs) throws IllegalArgumentException
  {
    this(numInputs, true);
  }

  /**
   * Creates a neuron with the given bias and input weights. The number of
   * inputs will be equal to the length of {@code weights}.
   * 
   * @param bias
   *          the bias for the neuron
   * @param weights
   *          the input weights for the neuron
   * @throws NullPointerException
   *           if {@code weights == null}
   */
  public Neuron(float bias, float... weights) throws NullPointerException
  {
    this(weights.length, false);

    System.arraycopy(weights, 0, this.weights, 0, weights.length);
    this.bias = bias;
  }

  /**
   * Creates a neuron with the given number of inputs. If {@code initWeights} is
   * {@code true}, then {@link #weights} and {@link #bias} are initialized with
   * {@linkplain java.util.Random#nextGaussian() random Gaussian values}.
   * 
   * @param numInputs
   *          the number of inputs of the neuron
   * @param initWeights
   *          if true, the weights and bias of the neuron will be set randomly;
   *          if false, these values will remain default (0.0f)
   * @throws IllegalArgumentException
   *           if {@code numInputs < 0}
   */
  private Neuron(int numInputs, boolean initWeights) throws IllegalArgumentException
  {
    if (0 > numInputs)
    {
      throw new IllegalArgumentException("" + numInputs);
    }

    this.weights = new float[numInputs];

    if (initWeights)
    {
      Random rand = new Random();
      for (int i = 0; i < this.weights.length; ++i)
      {
        this.weights[i] = (float) rand.nextGaussian();
      }
      this.bias = (float) rand.nextGaussian();
    }
  }

  /**
   * Returns the number of inputs this neuron has.
   * 
   * @return the number of inputs of this neuron
   */
  public int numInputs()
  {
    return weights.length;
  }

  /**
   * Calculates the output of this neuron. The returned value will be between
   * 0.0 and 1.0.
   * 
   * @param inputs
   *          the input values this neuron will operate on
   * @return the calculated output of this neuron
   * @throws IllegalArgumentException
   *           if the number of given inputs does not match the number of inputs
   *           of this neuron
   */
  public float calculate(float... inputs) throws IllegalArgumentException
  {
    if (numInputs() != inputs.length)
    {
      throw new IllegalArgumentException("Incorrect number of inputs: " + inputs.length);
    }

    float wtdAvg = bias;

    for (int i = 0; i < weights.length; ++i)
    {
      wtdAvg += weights[i] * inputs[i];
    }

    return sigmoid(wtdAvg);
  }

  /**
   * Calculates the sigmoid function of the given argument.
   * 
   * @param val
   *          the argument to the sigmoid function
   * @return sigmoid({@code val})
   */
  private static float sigmoid(float val)
  {
    return (float) (1.0 / (1 + Math.exp(-val)));
  }

  /**
   * An input neuron is a neuron which has a settable output. It achieves this
   * by setting the bias and having no inputs.
   *
   */
  public static class InputNeuron extends Neuron
  {
    public InputNeuron()
    {
      this(0.0f);
    }

    public InputNeuron(float value)
    {
      super(value);
    }

    public void set(float value)
    {
      this.bias = value;
    }

    public float get()
    {
      return this.bias;
    }
  }
}
