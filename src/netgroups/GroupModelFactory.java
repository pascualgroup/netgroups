package netgroups;

import java.util.Map;

import netgroups.graph.GraphModel;
import netgroups.multigraph.MultigraphModel;
import mc3kit.*;
import mc3kit.mcmc.*;
import mc3kit.model.*;

public class GroupModelFactory implements ModelFactory {
	Config config;
	NetworkData data;
	
	public GroupModelFactory(Config config, NetworkData data) {
		this.config = config;
		this.data = data;
	}

	@Override
	public Model createModel(Chain initialChain) throws MC3KitException {
		switch(config.modelType) {
			case SIMPLE_GRAPH:
				return new GraphModel(initialChain, config, data);
			case DEGREE_CORRECTED_MULTIGRAPH:
				return new MultigraphModel(initialChain, config, data);
		}
		return null;
	}

	@Override
	public Model createModel(Chain initialChain, Map<String, Object> sample) throws MC3KitException {
		switch(config.modelType) {
			case SIMPLE_GRAPH:
				return new GraphModel(initialChain, sample,config, data);
			case DEGREE_CORRECTED_MULTIGRAPH:
				return new MultigraphModel(initialChain, sample, config, data);
		}
		return null;
		
	}
	
}
