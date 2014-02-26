package netgroups.multigraph;

import netgroups.NetworkData;
import mc3kit.MC3KitException;
import static java.lang.Math.*;
import static mc3kit.util.Math.*;
import mc3kit.model.*;
import mc3kit.util.IterableBitSet;

public class GroupPairLikelihood extends Variable {
	MultigraphModel model;
	NetworkData data;
	
	int r;
	int s;
	
	public GroupPairLikelihood(MultigraphModel model, NetworkData data, int r, int s) throws MC3KitException {
		super(model);

		this.model = model;
		this.data = data;
		
		assert r <= s;
		this.r = r;
		this.s = s;
		
		model.addEdge(this, model.groupFuncs[r]);
		if(r == s) {
			model.addEdge(this, model.groupPairShapeWithin);
			model.addEdge(this, model.groupPairMeanWithin);
		}
		else {
			model.addEdge(this, model.groupFuncs[s]);
			model.addEdge(this, model.groupPairShapeBetween);
			model.addEdge(this, model.groupPairMeanBetween);
		}
	}

	@Override
	public boolean update() throws MC3KitException {
		IterableBitSet rg = model.groupFuncs[r].getGroup();
		IterableBitSet sg = model.groupFuncs[s].getGroup();
		
		double alpha;
		double mean;
		if(r == s) {
			alpha = model.groupPairShapeWithin.getValue();
			mean = model.groupPairMeanWithin.getValue();
		}
		else {
//			System.err.printf("rg: %s, sg: %s\n", rg, sg);
			alpha = model.groupPairShapeBetween.getValue();
			mean = model.groupPairMeanBetween.getValue();
		}
		int nr = rg.cardinality();
		int ns = sg.cardinality();
		double beta = alpha / (mean * nr * ns);
		
		// Calculate link count for this pair of groups
		int mrs = 0;
		for(int i : rg) {
			for(int j : sg) {
				mrs += data.get(i, j);
			}
		}
		
		double logP;
		if(r == s) {
			// Almost gamma-Poisson, but with factor of 1/2 in exponent
			logP = alpha * log(beta) - (alpha + mrs / 2.0) * log(beta + 0.5)
				+ logGamma(alpha + mrs / 2.0) - logGamma(alpha);
		}
		else {
			// Simple gamma-Poisson marginal likelihood
			logP = alpha * log(beta) - (alpha + mrs) * log(beta + 1.0)
					+ logGamma(alpha + mrs) - logGamma(alpha);
		} 
		setLogP(logP);
		
		return true;
	}
}
