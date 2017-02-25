package hackisu_s17.neur;

import java.util.Arrays;

public class NeuralNet
{
  private final NeuronLayer[] layers;

  public NeuralNet(int... layerCounts)
  {
    if (layerCounts.length < 2)
    {
      throw new IllegalArgumentException("Must be at least 2 layers: was " + layerCounts.length);
    }
    layers = new NeuronLayer[layerCounts.length];
  }

  public float[] calculate(float... inputs)
  {
    // TODO
    return null;
  }

  private class NeuronLayer
  {
    private final Neuron[] neurons;
    private final float[] lastOutputs;

    public NeuronLayer(int count, int numInputs)
    {
      this(count, numInputs, null, null);
    }

    public NeuronLayer(int count, int numInputs, float[] biases, float[][] weights)
    {
      this.neurons = new Neuron[count];
      this.lastOutputs = new float[count];

      if (null == biases && null == weights)
      {
        for (int i = 0; i < count; ++i)
        {
          this.neurons[i] = new Neuron(numInputs);
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
    }

    public int numInputs()
    {
      return neurons[0].numInputs();
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

    public float[] getLastOutputs()
    {
      return Arrays.copyOf(lastOutputs, lastOutputs.length);
    }
  }
}
