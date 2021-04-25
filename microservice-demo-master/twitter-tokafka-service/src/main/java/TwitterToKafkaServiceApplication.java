import io.medalytics.microservice.demo.twitter.to.kafka.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"io.medalytics.microservice.*"})
@EnableAutoConfiguration
@SpringBootApplication
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
//    private final TwitterToKafkaServiceConfigData configData;
    private final StreamRunner streamRunner;

    public TwitterToKafkaServiceApplication(StreamRunner streamRunner) {
//        this.configData = configData;
        this.streamRunner = streamRunner;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class);
    }
//Application initialization
    @Override
    public void run(String... args) throws Exception {
    LOG.info("App starts...");
    streamRunner.start();
    }
}
