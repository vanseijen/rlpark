package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.internal;

import rlpark.plugin.rltoys.experiments.helpers.Runner;
import rlpark.plugin.rltoys.experiments.helpers.Runner.RunnerEvent;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.internal.AbstractEpisodeRewardMonitor;

public class OffPolicyEpisodeRewardMonitor extends AbstractEpisodeRewardMonitor {
  private final Runner runner;
  private int nextEvaluationIndex = 0;
  private final int nbEpisodePerEvaluation;

  public OffPolicyEpisodeRewardMonitor(Runner runner, int nbLearnerEvaluation, int nbTotalBehaviourEpisodes,
      int nbEpisodePerEvaluation) {
    super("Target", createStartingPoints(nbLearnerEvaluation, nbTotalBehaviourEpisodes));
    this.runner = runner;
    this.nbEpisodePerEvaluation = nbEpisodePerEvaluation;
  }

  static protected int[] createStartingPoints(int nbLearnerEvaluation, int nbTotalBehaviourEpisodes) {
    int[] starts = new int[nbLearnerEvaluation];
    double binSize = (double) nbTotalBehaviourEpisodes / (nbLearnerEvaluation - 1);
    for (int i = 0; i < starts.length; i++)
      starts[i] = (int) (i * binSize);
    starts[starts.length - 1] = nbTotalBehaviourEpisodes - 1;
    return starts;
  }

  public void runEvaluationIFN(int episodeIndex) {
    if (nextEvaluationIndex >= starts.length || starts[nextEvaluationIndex] > episodeIndex)
      return;
    for (int i = 0; i < nbEpisodePerEvaluation; i++) {
      runner.runEpisode();
      RunnerEvent runnerEvent = runner.runnerEvent();
      registerMeasurement(episodeIndex, runnerEvent.episodeReward, runnerEvent.step.time);
    }
    nextEvaluationIndex++;
  }
}
