#!/bin/sh -x

echo "START"
/backup/gatling/init.gatling.sh
/backup/jmeter/init.jmeter.sh
/backup/jmeter-2022-06-22-2022-06-22/init.jmeter.sh
/backup/telegraf/init.telegraf.sh
echo "COMPLETE"
