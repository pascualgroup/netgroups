netgroups
=========
Ed Baskerville  
updated 26 February 2014


## Overview

Finds groups in networks (e.g., food webs) using Bayesian/empirical-Bayes stochastic block models and Metropolis-coupled Markov-chain Monte Carlo (MC3).

Caveat: this code is not fully vetted for general use, and is not very convenient in its current form. I plan to make it more convenient; definitely send me a message if you have particular needs. This documentation obviously needs substantial improvement as well.

Includes the Bayesan group model used in Baskerville et al. 2011 (PLOS Computational Biology):

http://dx.doi.org/10.1371/journal.pcbi.1002321

which is based on the maximum-likelihood version in Allesina and Pascual 2009 (Ecology Letters):

http://dx.doi.org/10.1111/j.1461-0248.2009.01321.x

Still in testing: a Bayesian implementation of the degree-corrected stochastic block model of Karrer 2011 (Physical Review E):

http://dx.doi.org/10.1103/PhysRevE.83.016107

Currently only implemented for undirected multigraphs; directed support planned.

## Requirements

* Core code requires Java (JDK 7 recommended).
* Scripts require Python (2.7.x recommended).
* Only tested on Mac OS X; Linux should work. Windows support should be possible by modifying scripts but is not currently present.

## Installation

Download the latest release distribution from the GitHub page:

https://github.com/pascualgroup/netgroups

Then download CERN Colt and Google GSON and place their `.jar` files in the `jar` directory of the distribution:

http://acs.lbl.gov/software/colt/colt-download/releases/colt-1.2.0.zip

https://code.google.com/p/google-gson/

## Setting up an analysis

Data must currently be in JSON format, like so:

```{json}
{
  "directed" : true,
  "nodes" : ["Alice", "Bob", "Carol"],
  "edges" : [
    {
      "from" : "Alice",
      "to" : "Bob"
    },
    {
      "from" : "Bob",
      "to" : "Carol"
    },
    {
      "from" : "Carol",
      "to" : "Alice"
    },
    {
      "from" : "Carol",
      "to" : "Bob",
      "count" : 10
    }
  ]
}
```

`count` is optional. If nonzero, it is only used for multigraph models.

Configuration files take the following form:

```{json}
{
	"dataFilename" : "serengeti-network.json",
	"modelType" : "SIMPLE_GRAPH",
	"sampleFilename" : "samples.jsons",
	"bestSampleFilename" : "samples_best.jsons",
	"groupCount" : 3,
	"chainCount" : 1,
	"thin" : 1,
	"memory" : 512,
	"differentWithinGroupPrior" : true,
	"tuneFor" : 1000,
	"runFor" : 10000
}
```

Summary of configuration options:

<dl>
  <dt>`dataFilename`</dt>
  <dd>Filename of the JSON-formatted network file. This is absolute or relative to the working directory.</dd>
  <dt>`modelType`</dt>
  <dd>Model type, either `SIMPLE_GRAPH` or `DEGREE_CORRECTED_MULTIGRAPH` (the latter is in development and should not be used yet).</dd>
  <dt>`sampleFilename`</dt>
  <dd>Output for periodically generated samples, in JSON-formatted chunks separated by lines containing `---`.</dd>
  <dt>`bestSampleFilename`</dt>
  <dd>Output for maximum-posterior sample so far. The last entry in this file is the best sample identified so far.</dd>
  <dt>`groupCount`</dt>
  <dd>Number of groups to identify.</dd>
  <dt>`chainCount`</dt>
  <dd>Number of heated chains to use in order to avoid getting stuck at local maxima. The hottest chain is currently set to an exponent of 0.5, so marginal likelihood estimation cannot be currently performed across multiple chains.</dd>
  <dt>`thin`</dt>
  <dd>Number of samples to skip between output. Note that each iteration samples from all variables and allows swaps between chains.</dd>
  <dt>`tuneFor`</dt>
  <dd>Tuning period for finding good proposals to hyperparameters; chain is not Markov until after this point.</dd>
  <dt>`runFor`</dt>
  <dd>Number of iterations to run for.</dd>
  <dt>`memory`</dt>
  <dd>Maximum memory to use, in megabytes.</dd>
	<dt>`differentWithinGroupPrior`</dt>
	<dd>Whether to use a different prior for within-group parameters vs. between-group parameters.</dd>
</dl>

## Running the program

Create a working directory with your network data and configuration file. Then run the program using:

```{sh}
cd path/to/working-dir
path/to/netgroups/scripts/netgroups_run.py config.json
```

You can monitor the progress of the samples and best-samples files using `monitor_progress.sh` in another terminal:

```{sh}
path/to/netgroups/scripts/monitor_progress.sh path/to/working-dir/samples.jsons
```

When complete, you can extract samples files into CSV format using `samples2csv.py`, e.g.,

```{sh}
path/to/netgroups/scripts/samples2csv.py path/to/working-dir/samples.jsons
```

which will create `samples.csv`.

The files in the `db` directory will allow the software to start where it left off in future versions, but for now you can delete these files once the program has finished.

## Differences from the code used in the paper

TODO: expand this.

* For simplicity and computational predictability, each run explores partitions with exactly `groupCount` groups (no empty groups).
* Hyperparameters are given bounded uniform priors, and for simplicity I recommend using an empirical-Bayes approach, where the maximum-marginal-likelihood combination of hyperparameters and partition is identified across runs with different numbers of groups.
* By default, within- and between-group probability parameters are given different priors (set `differentWithinGroupPrior` to `false` to change this).
