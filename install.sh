#!/bin/bash

echo "Installing Plugin"

#TODO
#installation dir as var
#local install thing as var, basically its a copy and delete.i

if [ -d "~/Library/Application\ Support/IntelliJIdea2016.2" ]; then
    	echo "Plugin Directory Found"
	echo "Copying File"
	cp ./adobe-aem-plugin.jar ~/Library/Application\ Support/IntelliJIdea2016.2/
	echo "File copied, please restart IntelliJ"
fi
