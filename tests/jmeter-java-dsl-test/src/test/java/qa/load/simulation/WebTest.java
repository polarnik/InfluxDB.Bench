package qa.load.simulation;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import qa.load.scenario.WebTestScenario;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WebTest {

    final WebTestScenario scenario = new WebTestScenario();

    String getTestId(int linksCount) {
        Long testNumber = System.currentTimeMillis();
        String testId = "jmeter_" + linksCount + "_" + testNumber;
        return testId;
    }

    String getInfluxDBName(int linksCount) {
        String jmeterDbName = "jmeter" + linksCount;
        return jmeterDbName;
    }

    String getHostName() {
        var host = System.getProperty("hostname");
        if (host != null) return host;

        try {
            var result = InetAddress.getLocalHost().getHostName();
            if (!result.isEmpty()) return result;
        } catch (UnknownHostException e) {
        }
        host = System.getenv("COMPUTERNAME");
        if (host != null) return host;
        host = System.getenv("HOSTNAME");
        if (host != null) return host;
        return "";
    }

    @ParameterizedTest
    @ValueSource (ints = {1, 1000, 10_000, 100_000})
    public void maxPerf(int linksCount) throws IOException {
        var plan =  scenario.sendHttp(
                getHostName(),
                getInfluxDBName(linksCount),
                getTestId(linksCount),
                linksCount,
                "web?id=${id} (GET)");
        plan.run();
    }

    @ParameterizedTest
    @ValueSource (ints = {1, 1000, 10_000, 100_000})
    public void maxPerfGood(int linksCount) throws IOException {
        var plan =  scenario.sendHttp(
                getHostName(),
                "jmeter",
                getTestId(linksCount),
                linksCount,
                "web?id={id} (GET)");
        plan.run();
    }

    @Test
    public void debug() {
        int linksCount = 10;
        var plan =  scenario.sendHttp(
                getHostName(),
                getInfluxDBName(linksCount),
                getTestId(linksCount),
                linksCount,
                "web?id=${id} (GET)");
        plan.showInGui();
    }
}
