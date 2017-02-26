package hackisu_s17.gen_neur;

import java.util.Random;
import java.util.function.ToIntFunction;

import org.json.JSONArray;
import org.json.JSONObject;

import hackisu_s17.gen2.GeneticRunner;
import hackisu_s17.neur.NeuralNet;
import hackisu_s17.util.Util;

public class GeneticNeuralNetwork extends GeneticRunner<NeuralNet>
{
  private int maxMutations;
  private float adjustWeightWeight;
  private float addNeuronWeight;
  private float removeNeuronWeight;
  private float addLayerWeight;
  private float removeLayerWeight;
  private float adjustWeightMaxPercent;

  public GeneticNeuralNetwork(ToIntFunction<NeuralNet> fitness, float mutatePortion, int selectionPoolSize,
      int maxMutations)
  {
    this(fitness, mutatePortion, selectionPoolSize, maxMutations, 600, 200, 50, 100, 20, 0.4f);
  }

  public GeneticNeuralNetwork(ToIntFunction<NeuralNet> fitness, float mutatePortion, int selectionPoolSize,
      int maxMutations, float adjustWeightWeight, float addNeuronWeight, float removeNeuronWeight, float addLayerWeight,
      float removeLayerWeight, float adjustWeightMaxPercent)
  {
    super(null, null, fitness, mutatePortion, selectionPoolSize);

    setMutator((nn) -> mutateNetwork(nn));
    setCrosser((nn1, nn2) -> crossNetworks(nn1, nn2));
    this.maxMutations = maxMutations;
    this.adjustWeightWeight = adjustWeightWeight;
    this.addNeuronWeight = addNeuronWeight;
    this.removeNeuronWeight = addNeuronWeight;
    this.addLayerWeight = addLayerWeight;
    this.removeLayerWeight = removeLayerWeight;
    this.adjustWeightMaxPercent = adjustWeightMaxPercent;
  }

  private NeuralNet mutateNetwork(NeuralNet nn)
  {
    Random rand = new Random();
    JSONObject neurNetObj = new JSONObject(nn.toString());
    JSONArray neurLayerArr = neurNetObj.getJSONArray("layerData");

    int mutCount = rand.nextInt(maxMutations);
    for (int i = 0; i < mutCount; ++i)
    {
      // get a layer index, ignoring input and output layers
      int layerIdx = rand.nextInt(neurLayerArr.length() - 2) + 1;
      JSONObject layerObj = neurLayerArr.getJSONObject(layerIdx);
      switch (Util.weightedRandom(rand, adjustWeightWeight, addNeuronWeight, removeNeuronWeight, addLayerWeight,
          removeLayerWeight))
      {
        case 0:// adjust weight of some neuron
          int neurIdx = rand.nextInt(layerObj.getInt("neuronCount"));
          break;
        case 1:// add a neuron in some layer
          break;
        case 2:// remove neuron from some layer
          break;
        case 3:// add a layer somewhere
          break;
        case 4:// remove a layer somewhere
          break;
        default:
          break;
      }
    }

    NeuralNet resultNN = nn;
    // TODO init resultNN
    return resultNN;
  }

  private NeuralNet crossNetworks(NeuralNet nn1, NeuralNet nn2)
  {

    NeuralNet resultNN = nn1;
    // TODO init resultNN
    return resultNN;
  }
}
