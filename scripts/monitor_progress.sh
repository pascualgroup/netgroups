#!/bin/sh

tail -n 1000 -f ${@} | grep -B 3 logLikelihood
