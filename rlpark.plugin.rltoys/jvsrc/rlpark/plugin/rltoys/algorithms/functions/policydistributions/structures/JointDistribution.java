package rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.rltoys.algorithms.functions.policydistributions.BoundedPdf;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class JointDistribution implements PolicyDistribution, BoundedPdf {
  private static final long serialVersionUID = -7545331400083047916L;
  protected final PolicyDistribution[] distributions;
  @IgnoreMonitor
  private int[] weightsToAction;

  public JointDistribution(PolicyDistribution[] distributions) {
    this.distributions = distributions;
  }

  @Override
  public double pi(RealVector s, Action a) {
    double product = 1.0;
    for (int i = 0; i < distributions.length; i++)
      product *= distributions[i].pi(s, ActionArray.getDim(a, i));
    return product;
  }

  @Override
  public ActionArray decide(RealVector s) {
    List<ActionArray> actions = new ArrayList<ActionArray>();
    int nbDimension = 0;
    for (PolicyDistribution distribution : distributions) {
      ActionArray a = (ActionArray) distribution.decide(s);
      nbDimension += a.actions.length;
      actions.add(a);
    }
    double[] result = new double[nbDimension];
    int currentPosition = 0;
    for (ActionArray a : actions) {
      System.arraycopy(a.actions, 0, result, currentPosition, a.actions.length);
      currentPosition += a.actions.length;
    }
    return new ActionArray(result);
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    List<PVector> parameters = new ArrayList<PVector>();
    List<Integer> parametersToAction = new ArrayList<Integer>();
    for (int i = 0; i < distributions.length; i++)
      for (PVector parameterVector : distributions[i].createParameters(nbFeatures)) {
        parameters.add(parameterVector);
        parametersToAction.add(i);
      }
    PVector[] result = new PVector[parameters.size()];
    parameters.toArray(result);
    weightsToAction = new int[parameters.size()];
    for (int i = 0; i < weightsToAction.length; i++)
      weightsToAction[i] = parametersToAction.get(i);
    return result;
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    List<RealVector> gradLogs = new ArrayList<RealVector>();
    for (int i = 0; i < distributions.length; i++) {
      PolicyDistribution distribution = distributions[i];
      RealVector[] gradLog = distribution.getGradLog(x_t, ActionArray.getDim(a_t, i));
      for (RealVector parameterVector : gradLog)
        gradLogs.add(parameterVector);
    }
    RealVector[] result = new RealVector[gradLogs.size()];
    gradLogs.toArray(result);
    return result;
  }

  public int weightsIndexToActionIndex(int i) {
    return weightsToAction[i];
  }

  public PolicyDistribution policy(int actionIndex) {
    return distributions[actionIndex];
  }

  @Override
  public int nbParameterVectors() {
    int result = 0;
    for (PolicyDistribution distribution : distributions)
      result += distribution.nbParameterVectors();
    return result;
  }

  @Override
  public double piMax(RealVector s) {
    double result = 1;
    for (PolicyDistribution distribution : distributions)
      result *= ((BoundedPdf) distribution).piMax(s);
    return result;
  }
}
