package dbtest;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import dbtest.DbTestConfiguration;
import dbtest.resources.DbTestResource;
import dbtest.health.TemplateHealthCheck;
import com.github.arteam.jdbi3.JdbiFactory;
import org.jdbi.v3.core.Jdbi;
import dbtest.DatabaseMetrics;

public class DbTestApplication extends Application<DbTestConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DbTestApplication().run(args);
    }

    @Override
    public String getName() {
        return "DbTest";
    }

    @Override
    public void initialize(final Bootstrap<DbTestConfiguration> bootstrap) {
        DatabaseMetrics.setRegistry(bootstrap.getMetricRegistry());
    }

    @Override
    public void run(final DbTestConfiguration configuration,
                    final Environment environment) {
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment,
                configuration.getDatabase(),
                "postgresql");
        environment.healthChecks().register("template", healthCheck);
        final DbTestResource resource = new DbTestResource(
                configuration.getTemplate(),
                configuration.getDefaultName(),
                jdbi
        );
        environment.jersey().register(resource);
    }

}
