package netgroups.graph;

import java.util.Map;

import netgroups.*;
import mc3kit.*;
import mc3kit.mcmc.Chain;
import mc3kit.model.*;
import mc3kit.types.doublevalue.DoubleVariable;
import mc3kit.types.doublevalue.distributions.UniformDistribution;
import mc3kit.types.partition.GroupFunction;
import mc3kit.types.partition.PartitionVariable;
import mc3kit.types.partition.distributions.UniformPartitionDistribution;

public class GraphModel extends Model {
	Config config;
	NetworkData data;
	
	// Parameters
	PartitionVariable partition;
	DoubleVariable pPriorAlphaWithin;
	DoubleVariable pPriorBetaWithin;
	DoubleVariable pPriorAlphaBetween;
	DoubleVariable pPriorBetaBetween;
	
	GroupFunction[] groupFuncs;
	
	public GraphModel(Chain initialChain, Config config, NetworkData data) throws MC3KitException {
		super(initialChain);
		
		this.config = config;
		this.data = data;
		
		beginConstruction();
		
		// Partition
		partition = new PartitionVariable(this, "partition", data.nodeCount(), config.groupCount, false, true);
		partition.setDistribution(new UniformPartitionDistribution(this));
		groupFuncs = new GroupFunction[config.groupCount];
		for(int r = 0; r < config.groupCount; r++) {
			groupFuncs[r] = new GroupFunction(this, partition, r);
		}
		
		// Beta prior parameters for within- and between-group link probabilities
		UniformDistribution alphaBetaPrior = new UniformDistribution(this, config.pPriorMinAlphaBeta, config.pPriorMaxAlphaBeta);
		pPriorAlphaWithin = new DoubleVariable(this, "pPriorAlphaWithin", alphaBetaPrior);
		pPriorBetaWithin = new DoubleVariable(this, "pPriorBetaWithin", alphaBetaPrior);
		if(config.differentWithinGroupPrior) {
			pPriorAlphaBetween = new DoubleVariable(this, "pPriorAlphaBetween", alphaBetaPrior);
			pPriorBetaBetween = new DoubleVariable(this, "pPriorBetaBetween", alphaBetaPrior);
		}
		else {
			pPriorAlphaBetween = pPriorAlphaWithin;
			pPriorBetaBetween = pPriorBetaWithin;
		}
		
		// Group-pair marginal likelihood functions
		for(int r = 0; r < config.groupCount; r++) {
			for(int s = data.isDirected() ? 0 : r; s < config.groupCount; s++) {
				new GroupPairLikelihood(this, data, r, s);
			}
		}
		
		// Marginal likelihood normalization
		new LikelihoodNormalization(this);
		
		endConstruction();
	}
	
	public GraphModel(Chain initialChain, Map<String, Object> sample, Config config, NetworkData data) throws MC3KitException {
		super(initialChain);
		throw new MC3KitException("Load from database unimplemented.");
	}
}
