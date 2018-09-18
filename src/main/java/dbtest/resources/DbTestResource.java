package dbtest.resources;

import dbtest.api.Saying;
import com.codahale.metrics.annotation.Timed;
import dbtest.DatabaseMetrics;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;
import org.jdbi.v3.core.Jdbi;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class DbTestResource {
  private final String template;
  private final String defaultName;
  private final AtomicLong counter;
  private final Jdbi jdbi;

  public DbTestResource(String template, String defaultName, Jdbi jdbi) {
    this.template = template;
    this.defaultName = defaultName;
    this.counter = new AtomicLong();
    this.jdbi = jdbi;
  }

  @GET
  @Timed
  public Saying sayHello(@QueryParam("name") Optional<String> name) {
    final List<Integer> result = this.jdbi.withHandle(handle ->
            handle.select("Select 1;")
                    .mapTo(Integer.class)
                    .list()
    );
    final String value = String.format(template, result.get(0), DatabaseMetrics.getPoolState());
    return new Saying(counter.incrementAndGet(), value);
  }
}
