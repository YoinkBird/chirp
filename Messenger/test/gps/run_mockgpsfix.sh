#!/bin/bash -xu
git_repo=$(git rev-parse --show-toplevel)
# assume everything cloned to the same place
mockgeofix_path="../mockgeofix"
ip=192.168.10.179
${mockgeofix_path}/helper_scripts/run_sim.py -i ${ip} -g "${git_repo}/Messenger/test/gps/seton_to_dell.gpx" -I localhost -P 8080 -s 1000

