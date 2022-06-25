package qa.load.scenario;

import us.abstracta.jmeter.javadsl.core.DslJmeterEngine;
import us.abstracta.jmeter.javadsl.core.DslTestPlan;
import us.abstracta.jmeter.javadsl.core.engines.EmbeddedJmeterEngine;
import us.abstracta.jmeter.javadsl.core.engines.JmeterEnvironment;
import us.abstracta.jmeter.javadsl.core.threadgroups.DslDefaultThreadGroup;
import us.abstracta.jmeter.javadsl.core.threadgroups.RpsThreadGroup;

import java.time.Duration;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

public class WebTestScenario {

    DslDefaultThreadGroup getTG_openWeb(int linksCount, String nameOfSampler) {
        return threadGroup()
                .children(
                        forLoopController(100,
                                httpSampler(nameOfSampler, "/")
                                        .method("GET")
                                        .host("web")
                                        .port(80)
                                        .protocol("http")
                                        .param("id", "${id}")
                                        .children(
                                                jsr223PreProcessor(String.format(
                                                        "var r = new java.util.Random(); vars.put('id', String.valueOf(r.nextInt(%d)));",
                                                        linksCount))
                                        )
                        )
                );
    }


    public DslTestPlan sendHttp(String hostname, String jmeterDbName, String testId, int linksCount, String nameOfSampler) {
        var plan =  testPlan(
                getTG_openWeb(linksCount, nameOfSampler)
                        .rampToAndHold(100, Duration.ofMinutes(10), Duration.ofMinutes(10)),
                influxDbListener("http://influxdb:8086/write?db=" + jmeterDbName)
                        .tag("testId", testId)
                        .tag("hostname", hostname)
        );
        return plan;
    }
}
