package netgroups.graph;

import netgroups.NetworkData;
import mc3kit.MC3KitException;
import static mc3kit.util.Math.*;
import mc3kit.model.*;
import mc3kit.util.IterableBitSet;

public class GroupPairLikelihood extends Variable {
	GraphModel model;
	NetworkData data;
	
	int r;
	int s;
	
	public GroupPairLikelihood(GraphModel model, NetworkData data, int r, int s) throws MC3KitException {
		super(model);

		this.model = model;
		this.data = data;
		
		model.addEdge(this, model.groupFuncs[r]);
		if(r == s) {
			model.addEdge(this, model.pPriorAlphaWithin);
			model.addEdge(this, model.pPriorBetaWithin);
		}
		else {
			model.addEdge(this, model.groupFuncs[s]);
			model.addEdge(this, model.pPriorAlphaBetween);
			model.addEdge(this, model.pPriorBetaBetween);
		}
		
		this.r = r;
		this.s = s;
	}

	@Override
	public boolean update() throws MC3KitException {
		IterableBitSet rg = model.groupFuncs[r].getGroup();
		IterableBitSet sg = model.groupFuncs[s].getGroup();
		
		// Prior parameters
		double alpha;
		double beta;
		if(r == s) {
			alpha = model.pPriorAlphaWithin.getValue();
			beta = model.pPriorBetaWithin.getValue();
		}
		else {
			alpha = model.pPriorAlphaBetween.getValue();
			beta = model.pPriorBetaBetween.getValue();
		}
		
		// Group size
		int nr = rg.cardinality();
		int ns = sg.cardinality();
		
		// Calculate link count for this pair of groups,
		// ensuring that multiple links are ignored.
		int nLinks = 0;
		for(int i : rg) {
			for(int j : sg) {
				nLinks += data.get(i, j) > 0 ? 1 : 0;
			}
		}
		int nNonLinks = nr * ns - nLinks;
		assert(nNonLinks >= 0);
		
		// Calculate log-marginal likelihood for this pair of groups
		// (without normalization factor)
		double logP = logBeta(nLinks + alpha, nNonLinks + beta);
//		System.err.printf("alpha, beta: %f, %f\n", alpha, beta);
//		System.err.printf("logP %d(%d),%d(%d) (%d, %d): %f\n", r, nr, s, ns, nLinks, nNonLinks, logP);
		setLogP(logP);
		
		return true;
	}
}
