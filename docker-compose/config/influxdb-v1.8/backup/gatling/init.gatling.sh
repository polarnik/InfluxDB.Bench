#!/bin/sh -x

influx <<-EOSQL
CREATE DATABASE "gatling" WITH DURATION INF REPLICATION 1 SHARD DURATION 1h NAME "autogen";
EOSQL

