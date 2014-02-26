package netgroups;

import mc3kit.mcmc.LogLevel;

public class Config {
	// Model/data parameters
	public ModelType modelType = ModelType.DEGREE_CORRECTED_MULTIGRAPH;
	
	public boolean differentWithinGroupPrior = true;
	
	public int groupCount = 2;
	
	// Internal tweaks
	public double pPriorMinAlphaBeta = 0.01;
	public double pPriorMaxAlphaBeta = 10;
	
	// General program parameters
	public String dataFilename;
	public LogLevel logLevel = LogLevel.INFO;
	public String logFilename = "debug.log";
	public String sampleFilename = "samples.jsons";
	public String bestSampleFilename = "samples_best.jsons";
	
	// MCMC parameters
	public long thin = 1;
	public long randomSeed = 0;
	public int chainCount = 1;
	public boolean logAllChains = false;
	public double heatPower = 3.0;
	public double minHeatExponent = 0.5;
	public double targetAcceptanceRate = 0.25;
	public long runFor = 1000000;
	public long tuneFor = 100000;
	public long tuneEvery = 100;
	public long verifyEvery = 100;
	public double tolerance = 1e-4;
}
