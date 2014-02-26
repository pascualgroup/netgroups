package netgroups.multigraph;

import java.util.Map;

import netgroups.Config;
import netgroups.NetworkData;
import mc3kit.*;
import mc3kit.mcmc.Chain;
import mc3kit.model.*;
import mc3kit.types.doublevalue.DoubleVariable;
import mc3kit.types.doublevalue.distributions.UniformDistribution;
import mc3kit.types.partition.GroupFunction;
import mc3kit.types.partition.PartitionVariable;
import mc3kit.types.partition.distributions.UniformPartitionDistribution;

public class MultigraphModel extends Model {
	Config config;
	NetworkData data;
	
	// Parameters
	PartitionVariable partition;
	DoubleVariable dcDirichletAlpha;
	DoubleVariable groupPairShapeWithin;
	DoubleVariable groupPairShapeBetween;
	DoubleVariable groupPairMeanWithin;
	DoubleVariable groupPairMeanBetween;
	
	// "Variables" comprising likelihood calculation
	NormalizationLikelihood normalizationVar;
	GroupFunction[] groupFuncs;
//	GroupLikelihood[] groupVars;
//	GroupPairLikelihood[][] groupPairVars;
	
	public MultigraphModel(Chain initialChain, Config config, NetworkData data) throws MC3KitException {
		super(initialChain);

		assert !data.isDirected();
		
		this.config = config;
		this.data = data;
		
		beginConstruction();
		
		// Create partition
		partition = new PartitionVariable(this, "partition", data.nodeCount(), config.groupCount, false, true);
		partition.setDistribution(new UniformPartitionDistribution(this));
		groupFuncs = new GroupFunction[config.groupCount];
		for(int r = 0; r < config.groupCount; r++) {
			groupFuncs[r] = new GroupFunction(this, partition, r);
		}
		
		// Create parameters
		dcDirichletAlpha = new DoubleVariable(this, "dcDirichletAlpha", new UniformDistribution(this, 0.01, 10.0));
//		dcDirichletAlpha = new DoubleVariable(this, 1.0);
		
		double minShape = 0.01;
		double maxShape = 20.0;
		groupPairShapeWithin = new DoubleVariable(this, "groupPairShapeWithin", new UniformDistribution(this, minShape, maxShape));
//		groupPairShapeWithin = new DoubleVariable(this, 4.0);
		if(config.groupCount > 1) {
			groupPairShapeBetween = new DoubleVariable(this, "groupPairShapeBetween", new UniformDistribution(this, minShape, maxShape));
//			groupPairShapeBetween = new DoubleVariable(this, 1.0);
		}
		
		double minMean = (data.minCount() + 1.0);
		double maxMean = data.maxCount();
		System.err.printf("min mean: %f, max mean: %f\n", minMean, maxMean);
		
		groupPairMeanWithin = new DoubleVariable(this, "groupPairMeanWithin", new UniformDistribution(this, minMean, maxMean));
//		groupPairMeanWithin = new DoubleVariable(this, 20.0);
		if(config.groupCount > 1) {
			groupPairMeanBetween = new DoubleVariable(this, "groupPairMeanBetween", new UniformDistribution(this, minMean, maxMean));
//			groupPairMeanBetween = new DoubleVariable(this, 0.5);
		}
		
		// Create likelihood normalization
		normalizationVar = new NormalizationLikelihood(this, data);
		
		// Create per-group likelihood variables
//		groupVars = new GroupLikelihood[config.groupCount];
		for(int r = 0; r < config.groupCount; r++) {
			new GroupLikelihood(this, data, r);
		}
		
		// Create per-group-pair likelihood variables
//		groupPairVars = new GroupPairLikelihood[config.groupCount][config.groupCount];
		for(int r = 0; r < config.groupCount; r++) {
			for(int s = r; s < config.groupCount; s++) {
				new GroupPairLikelihood(this, data, r, s);
			}
		}
		
		endConstruction();
	}
	
	public MultigraphModel(Chain initialChain, Map<String, Object> sample, Config config, NetworkData data) throws MC3KitException {
		super(initialChain);
		throw new MC3KitException("Load from database unimplemented.");
	}
}
