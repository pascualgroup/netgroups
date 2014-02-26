#!/usr/bin/env python

import os
import sys
import json
from jsons import *
from collections import OrderedDict
import csv
import numpy as np


if __name__ == '__main__':
	if len(sys.argv) != 3:
		print 'Usage: {0} [network-filename] [samples-filename]'.format(sys.argv[0])
		sys.exit(1)
	
	netFilename = sys.argv[1]
	with open(netFilename) as f:
		network = json.load(f, object_pairs_hook=OrderedDict)
	nodeNames = network['nodes']
	
	sampsFilename = sys.argv[2]
	csvFilename = os.path.splitext(sampsFilename)[0] + '.csv'
	colNames = None
	with open(csvFilename, 'w') as outFile:
		cw = csv.writer(outFile)
		with open(sampsFilename) as inFile:
			for obj in JsonsReader(inFile):
				if colNames is None:
					colNames = ['iteration', 'logPrior', 'logLikelihood']
					for colName in obj:
						if colName != 'partition' and not (colName in colNames):
							colNames.append(colName)
					
					headerRow = colNames + nodeNames
					cw.writerow(headerRow)
				
				row = list()
				for colName in colNames:
					row.append(obj[colName])
				row += obj['partition']
								
				cw.writerow(row)
