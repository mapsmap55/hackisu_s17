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
  private int minNewNeursInLayer;
  private int maxNewNeursInLayer;

  public GeneticNeuralNetwork(ToIntFunction<NeuralNet> fitness, float mutatePortion, int selectionPoolSize,
      int maxMutations)
  {
    this(fitness, mutatePortion, selectionPoolSize, maxMutations, 600, 200, 50, 100, 20, 0.4f, 3, 8);
  }

  public GeneticNeuralNetwork(ToIntFunction<NeuralNet> fitness, float mutatePortion, int selectionPoolSize,
      int maxMutations, float adjustWeightWeight, float addNeuronWeight, float removeNeuronWeight, float addLayerWeight,
      float removeLayerWeight, float adjustWeightMaxPercent, int minNewNeursInLayer, int maxNewNeursInLayer)
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
    this.minNewNeursInLayer = minNewNeursInLayer;
    this.maxNewNeursInLayer = maxNewNeursInLayer;
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
      // get the nearby layer objects for easy access
      JSONObject prevLayerObj = neurLayerArr.getJSONObject(layerIdx - 1);
      JSONObject layerObj = neurLayerArr.getJSONObject(layerIdx);
      JSONObject nextLayerObj = neurLayerArr.getJSONObject(layerIdx + 1);

      switch (Util.weightedRandom(rand, adjustWeightWeight, addNeuronWeight, removeNeuronWeight, addLayerWeight,
          removeLayerWeight))
      {
        case 0:// adjust weight of some neuron
          adjustNeurWeight(layerObj, rand);
          break;
        case 1:// add a neuron in some layer
          addNeurToLayer(prevLayerObj, layerObj, nextLayerObj, rand);
          break;
        case 2:// remove neuron from some layer
          removeNeurFromLayer(layerObj, nextLayerObj, rand);
          break;
        case 3:// add a layer somewhere
        {
          if (rand.nextBoolean())
          {
            // add between prev and selected
            nextLayerObj = layerObj;
          }
          else
          {
            // add between selected and next
            prevLayerObj = layerObj;
          }
          addLayerBetween(neurNetObj, prevLayerObj, nextLayerObj, rand);
          break;
        }
        case 4:// remove a layer somewhere
        {
          break;
        }
        default:
          break;
      }
    }

    System.out.println(neurNetObj.toString(2));

    NeuralNet resultNN = nn;
    // TODO init resultNN
    return resultNN;
  }

  private void adjustNeurWeight(JSONObject layerObj, Random rand)
  {
    int neurIdx = rand.nextInt(layerObj.getInt("neuronCount"));
    JSONObject neur = layerObj.getJSONArray("neuronData").getJSONObject(neurIdx);
    int wtIdx = rand.nextInt(neur.getInt("inputCount"));
    float val = (float) neur.getJSONArray("inputWeights").getDouble(wtIdx);
    if (Math.abs(val) < 1e-9)
    {
      val += (rand.nextFloat() * adjustWeightMaxPercent) * (rand.nextBoolean() ? 1 : -1);
    }
    else
    {
      val += val * (rand.nextFloat() * adjustWeightMaxPercent) * (rand.nextBoolean() ? 1 : -1);
    }
    neur.getJSONArray("inputWeights").put(wtIdx, val);
  }

  private void addNeurToLayer(JSONObject prevLayerObj, JSONObject layerObj, JSONObject nextLayerObj, Random rand)
  {
    // create neuron object
    JSONObject newNeur = new JSONObject();
    newNeur.put("inputCount", prevLayerObj.getInt("neuronCount"));
    newNeur.put("bias", rand.nextGaussian());
    JSONArray newNeurInputWeights = new JSONArray();
    newNeur.put("inputWeights", newNeurInputWeights);
    for (int j = 0; j < newNeur.getInt("inputCount"); ++j)
    {
      newNeurInputWeights.put(rand.nextGaussian());
    }
    // add to layer
    layerObj.getJSONArray("neuronData").put(newNeur);
    // give next layer's neurons an input weight for it
    for (int j = 0; j < nextLayerObj.getJSONArray("neuronData").length(); ++j)
    {
      JSONObject neur = nextLayerObj.getJSONArray("neuronData").getJSONObject(j);
      neur.put("inputCount", neur.getInt("inputCount") + 1);
      neur.getJSONArray("inputWeights").put(rand.nextGaussian());
    }
  }

  private void removeNeurFromLayer(JSONObject layerObj, JSONObject nextLayerObj, Random rand)
  {
    int neurIdx = layerObj.getInt("neuronCount");
    if (neurIdx <= 1)
    {
      // don't remove the last neuron
      return;
    }
    // Remove the neuron from this layer
    neurIdx = rand.nextInt(neurIdx);
    JSONArray arr = layerObj.getJSONArray("neuronData");
    arr.remove(neurIdx);
    layerObj.put("neuronCount", layerObj.getInt("neuronCount") - 1);
    // update next layer's inputs
    arr = nextLayerObj.getJSONArray("neuronData");
    for (int j = 0; j < arr.length(); ++j)
    {
      JSONObject neur = arr.getJSONObject(j);
      neur.put("inputCount", neur.getInt("inputCount") - 1);
      neur.getJSONArray("inputWeights").remove(neurIdx);
    }
  }

  private void addLayerBetween(JSONObject neurNetObj, JSONObject prevLayerObj, JSONObject nextLayerObj, Random rand)
  {
    neurNetObj.put("layerCount", neurNetObj.getInt("layerCount") + 1);
    JSONObject layerObj = new JSONObject();
    int newNeurCount = minNewNeursInLayer + rand.nextInt(maxNewNeursInLayer - minNewNeursInLayer + 1);
    layerObj.put("neuronCount", newNeurCount);
    JSONArray newNeurData = new JSONArray();
    layerObj.put("neuronData", newNeurData);
    for (int j = 0; j < newNeurCount; ++j)
    {
      JSONObject neur = new JSONObject();
      neur.put("bias", rand.nextGaussian());
      neur.put("inputCount", prevLayerObj.getInt("neuronCount"));
      JSONArray neurInputWeights = new JSONArray();
      neur.put("inputWeights", neurInputWeights);
      for (int k = 0; k < neur.getInt("inputCount"); ++k)
      {
        neurInputWeights.put(rand.nextGaussian());
      }
    }
    // set weights & num inputs of next layer
    JSONArray nextLayerNeurData = nextLayerObj.getJSONArray("neuronData");
    for (int j = 0; j < nextLayerNeurData.length(); ++j)
    {

    }
  }

  private NeuralNet crossNetworks(NeuralNet nn1, NeuralNet nn2)
  {

    NeuralNet resultNN = nn1;
    // TODO init resultNN
    return resultNN;
  }
}
