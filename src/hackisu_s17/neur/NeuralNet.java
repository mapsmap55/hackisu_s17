package hackisu_s17.neur;

import java.util.Arrays;

import hackisu_s17.util.Pair;

/**
 * A class representing a neural network. A neural network is a set of layers of
 * neurons, with each layer feeding into the next up to the output.
 * 
 * @author Mike Petersen
 *
 */
public class NeuralNet
{
  /**
   * The layers of neurons of this neural network.
   */
  private final NeuronLayer[] layers;

  /**
   * The last outputs of this neural network, calculated in
   * {@link #calculate(float...) calculate()}.
   */
  private final float[] lastOutputs;

  public static void main(String[] args)
  {
    @SuppressWarnings("unchecked")
    Pair<float[], float[][]>[] biasesAndWeights = (Pair<float[], float[][]>[]) new Pair[2];
    biasesAndWeights[0] = new Pair<>(new float[]
    { -10.0f, -10.0f }, new float[][]
    { new float[]
        { 10.0f, -20.0f }, new float[]
        { -20.0f, 10.0f } });
    biasesAndWeights[1] = new Pair<>(new float[]
    { -10.0f }, new float[][]
    { new float[]
        { 40.0f, 40.0f } });
    NeuralNet nn = new NeuralNet(2, biasesAndWeights);// new NeuralNet(2, 4, 1);
    System.out.println(nn.toString());
    System.out.println(Arrays.toString(nn.calculate(0.0f, 0.0f)));
    System.out.println(Arrays.toString(nn.calculate(0.0f, 1.0f)));
    System.out.println(Arrays.toString(nn.calculate(1.0f, 0.0f)));
    System.out.println(Arrays.toString(nn.calculate(1.0f, 1.0f)));
    System.out.println(nn.toString());
  }

  /**
   * Creates a neural network with randomized weights and biases. Each layer has
   * the given number of neurons.
   * 
   * @param layerCounts
   *          the number of neurons per layer; the first count is the number of
   *          inputs, the next is the number of neurons in the first hidden
   *          layer, and so on, with the last being the number of neurons in the
   *          output layer
   * @throws IllegalArgumentException
   *           if there was fewer than 2 layers given
   * @throws NullPointerException
   *           if {@code layerCounts == null}
   */
  public NeuralNet(int... layerCounts) throws IllegalArgumentException, NullPointerException
  {
    if (layerCounts.length < 2)
    {
      throw new IllegalArgumentException("Must be at least 2 layers: was " + layerCounts.length);
    }
    this.layers = new NeuronLayer[layerCounts.length];

    this.layers[0] = new NeuronLayer(layerCounts[0], 0);
    for (int i = 1; i < this.layers.length; ++i)
    {
      this.layers[i] = new NeuronLayer(layerCounts[i], layerCounts[i - 1]);
    }

    this.lastOutputs = new float[layerCounts[layerCounts.length - 1]];
  }

  /**
   * Creates a given neural network specified by the given layer information.
   * 
   * @param numInputs
   *          the number of inputs for the neural network
   * @param layerBiasesAndWeights
   *          Information about the biases and weights of the layers of the
   *          neural network. The input layer is omitted from this array. The
   *          arrays in each pair should be non-null and have the same length.
   *          The arrays contained in the second element of the pairs should
   *          have the same length, and be equal to the length of the previous
   *          layer's array (with the first layer's being the number of inputs).
   *          For example:<br>
   *          <code>
   *          new NeuralNet(2, { ({ -10.0f, -10.0f }, { { 10.0f, -20.0f }, { -20.0f, 10.0f } }), ({ -10.0f }, { 40.0f, 40.0f }) });<br>
   *          </code>
   */
  @SafeVarargs
  public NeuralNet(int numInputs, Pair<float[], float[][]>... layerBiasesAndWeights) throws NullPointerException
  {
    this.layers = new NeuronLayer[layerBiasesAndWeights.length + 1];

    this.layers[0] = new NeuronLayer(numInputs, 0);
    for (int i = 0; i < layerBiasesAndWeights.length; ++i)
    {
      if (layerBiasesAndWeights[i].applyBoth((a, b) -> null == a || null == b))
      {
        throw new NullPointerException("Cannot specify bias or weight arrays as null");
      }
      this.layers[i + 1] = new NeuronLayer(layerBiasesAndWeights[i].getFirst().length, this.layers[i].numNeurons(),
          layerBiasesAndWeights[i].getFirst(), layerBiasesAndWeights[i].getSecond());
    }

    this.lastOutputs = new float[layerBiasesAndWeights[layerBiasesAndWeights.length - 1].getSecond().length];
  }

  /**
   * Calculates the output of this neural network given the given input.
   * 
   * @param inputs
   *          the input values of the neural network
   * @return the resulting output of the neural network on the given inputs
   */
  public float[] calculate(float... inputs)
  {
    layers[0].setInputs(inputs);

    float[] tmp = new float[0];

    for (int i = 0; i < layers.length; ++i)
    {
      tmp = layers[i].calculate(tmp);
    }

    System.arraycopy(tmp, 0, lastOutputs, 0, lastOutputs.length);

    return tmp;
  }

  /**
   * Returns the last outputs returned by {@link #calculate(float...)
   * calculate()}. The returned array is a copy and may be modified by the
   * caller with no consequences.
   * 
   * @return the last outputs returned by {@code calculate()}
   */
  public float[] getLastOutputs()
  {
    return Arrays.copyOf(lastOutputs, lastOutputs.length);
  }

  @Override
  public String toString()
  {
    StringBuilder b = new StringBuilder();

    b.append("{\"layerCount\":").append(layers.length).append(",\"layerData\":[");

    for (NeuronLayer l : layers)
    {
      b.append(l.toString()).append(",");
    }

    if (b.charAt(b.length() - 1) == ',')
    {
      b.setLength(b.length() - 1);
    }
    b.append("]}");

    return b.toString();
  }

  private class NeuronLayer
  {
    private final Neuron[] neurons;

    public NeuronLayer(int count, int numInputs)
    {
      this(count, numInputs, null, null);
    }

    public NeuronLayer(int count, int numInputs, float[] biases, float[][] weights)
    {
      if (count <= 0)
      {
        throw new IllegalArgumentException("Must have positive number of neurons in a layer; was " + count);
      }
      this.neurons = new Neuron[count];

      if (null == biases && null == weights)
      {
        if (0 != numInputs)
        {
          for (int i = 0; i < count; ++i)
          {
            this.neurons[i] = new Neuron(numInputs);
          }
        }
        else
        {
          for (int i = 0; i < count; ++i)
          {
            this.neurons[i] = new Neuron.InputNeuron();
          }
        }
      }
      else if (null == biases || null == weights)
      {
        throw new IllegalArgumentException("Cannot specify only one of biases and weights");
      }
      else
      {
        if (biases.length != count || weights.length != count)
        {
          throw new IllegalArgumentException("Invalid length of biases or weights arrays, should each be " + count);
        }

        if (numInputs != 0)
        {
          for (int i = 0; i < count; ++i)
          {
            if (weights[i].length != numInputs)
            {
              throw new IllegalArgumentException(
                  "Invalid number of weights: " + weights[i].length + ", expected " + numInputs);
            }

            this.neurons[i] = new Neuron(biases[i], weights[i]);
          }
        }
        else
        {
          for (int i = 0; i < count; ++i)
          {
            this.neurons[i] = new Neuron.InputNeuron();
          }
        }
      }
    }

    public int numInputs()
    {
      return neurons[0].numInputs();
    }

    public int numNeurons()
    {
      return neurons.length;
    }

    public boolean isInputLayer()
    {
      return neurons[0] instanceof Neuron.InputNeuron;
    }

    public void setInputs(float... inputs)
    {
      if (!isInputLayer())
      {
        throw new IllegalStateException("Not an input layer");
      }
      if (inputs.length != neurons.length)
      {
        throw new IllegalArgumentException("Requires " + neurons.length + " inputs");
      }

      for (int i = 0; i < neurons.length; ++i)
      {
        ((Neuron.InputNeuron) neurons[i]).set(inputs[i]);
      }
    }

    public float[] calculate(float... inputs)
    {
      float[] result = new float[neurons.length];

      for (int i = 0; i < neurons.length; ++i)
      {
        result[i] = neurons[i].calculate(inputs);
      }

      System.arraycopy(result, 0, lastOutputs, 0, lastOutputs.length);

      return result;
    }

    @Override
    public String toString()
    {
      StringBuilder b = new StringBuilder();

      b.append("{\"neuronCount\":").append(neurons.length).append(",\"neuronData\":[");

      for (Neuron n : neurons)
      {
        b.append(n.toString()).append(",");
      }

      if (b.charAt(b.length() - 1) == ',')
      {
        b.setLength(b.length() - 1);
      }
      b.append("]}");

      return b.toString();
    }
  }
}
