package dbtest;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import dbtest.DbTestConfiguration;
import dbtest.resources.DbTestResource;
import dbtest.health.TemplateHealthCheck;

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
        // TODO: application initialization
    }

    @Override
    public void run(final DbTestConfiguration configuration,
                    final Environment environment) {
      final DbTestResource resource = new DbTestResource(
          configuration.getTemplate(),
          configuration.getDefaultName()
        );
      final TemplateHealthCheck healthCheck =
        new TemplateHealthCheck(configuration.getTemplate());
      environment.healthChecks().register("template", healthCheck);
      environment.jersey().register(resource);
    }

}
