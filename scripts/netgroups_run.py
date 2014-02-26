#!/usr/bin/env python

import os
import sys
import json

if __name__ == '__main__':
	if len(sys.argv) != 2:
		print('Usage: {0} [configuration-file]'.format(sys.argv[0]))
		sys.exit(1)
	
	configFilename = sys.argv[1]
	jarDirname = os.path.join(os.path.dirname(__file__), '../jar')
	
	memory = 512
	with open(configFilename) as f:
		configObj = json.load(f)
		if 'memory' in configObj:
			memory = configObj['memory']
	
	command = 'java -ea -Xmx{0}M -cp "{1}/*" netgroups.Main {2}'.format(memory, jarDirname, configFilename)
	os.system(command)

