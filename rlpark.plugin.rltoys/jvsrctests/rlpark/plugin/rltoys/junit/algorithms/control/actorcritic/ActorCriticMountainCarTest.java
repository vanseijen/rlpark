package rlpark.plugin.rltoys.junit.algorithms.control.actorcritic;


import java.util.Random;

import org.junit.Test;

import rlpark.plugin.rltoys.agents.rl.LearnerAgentFA;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.ActorCritic;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.onpolicy.ActorLambda;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.BoltzmannDistribution;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.TabularAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDLambda;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDLambdaAutostep;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.junit.algorithms.control.mountaincar.MountainCarOnPolicyTest;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;


public class ActorCriticMountainCarTest extends MountainCarOnPolicyTest {
  static class MountainCarActorCriticControlFactory implements MountainCarAgentFactory {
    @Override
    public RLAgent createAgent(MountainCar mountainCar, Projector projector) {
      final double lambda = .3;
      final double gamma = .99;
      OnPolicyTD critic = createCritic(projector, lambda, gamma);
      StateToStateAction toStateAction = new TabularAction(mountainCar.actions(), projector.vectorNorm(),
                                                           projector.vectorSize());
      PolicyDistribution distribution = new BoltzmannDistribution(new Random(0), mountainCar.actions(), toStateAction);
      ActorLambda actor = new ActorLambda(lambda, gamma, distribution, .01 / projector.vectorNorm(), projector.vectorSize());
      return new LearnerAgentFA(new ActorCritic(critic, actor), projector);
    }

    protected OnPolicyTD createCritic(Projector projector, final double lambda, final double gamma) {
      return new TDLambda(lambda, gamma, .1 / projector.vectorNorm(), projector.vectorSize(),
                          new ATraces((int) (projector.vectorNorm() * 100), 0.05));
    }
  }

  @Test
  public void testDiscreteActorCriticOnMountainCar() {
    runTestOnOnMountainCar(new MountainCarActorCriticControlFactory());
  }

  @Test
  public void testDiscreteAutostepActorCriticOnMountainCar() {
    runTestOnOnMountainCar(new MountainCarActorCriticControlFactory() {
      @Override
      protected OnPolicyTD createCritic(Projector projector, final double lambda, final double gamma) {
        return new TDLambdaAutostep(lambda, gamma, projector.vectorSize());
      }
    });
  }
}
