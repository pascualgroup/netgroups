package netgroups.graph;

import static mc3kit.util.Math.*;
import mc3kit.MC3KitException;
import mc3kit.model.*;

public class LikelihoodNormalization extends Variable {
	GraphModel model;
	int groupCount;
	int betweenGroupPairCount;
	
	public LikelihoodNormalization(GraphModel model) throws MC3KitException {
		super(model);
		
		model.addEdge(this, model.pPriorAlphaWithin);
		model.addEdge(this, model.pPriorBetaWithin);
		if(model.config.differentWithinGroupPrior) {
			model.addEdge(this, model.pPriorAlphaBetween);
			model.addEdge(this, model.pPriorBetaBetween);
		}
		
		this.model = model;
		this.groupCount = model.config.groupCount;
		if(model.data.isDirected()) {
			betweenGroupPairCount = groupCount * (groupCount - 1);
		}
		else {
			betweenGroupPairCount = groupCount * (groupCount - 1) / 2;
		}
	}
	
	@Override
	public boolean update() {
		double logP = 0.0;
		
		// Within-group normalizations
		double alphaWithin = model.pPriorAlphaWithin.getValue();
		double betaWithin = model.pPriorBetaWithin.getValue();
		logP -= groupCount * logBeta(alphaWithin, betaWithin);
		
		// Between-group normalizations
		double alphaBetween = model.pPriorAlphaBetween.getValue();
		double betaBetween = model.pPriorBetaBetween.getValue();
		logP -= betweenGroupPairCount * logBeta(alphaBetween, betaBetween);
		
		setLogP(logP);
		return true;
	}
}
