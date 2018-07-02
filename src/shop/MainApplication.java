package shop;

import io.dropwizard.jdbi3.JdbiFactory;
import org.eclipse.jetty.server.session.SessionHandler;
import org.jdbi.v3.core.Jdbi;
import shop.resources.*;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class MainApplication extends Application<MainConfiguration> {

    public static void main(String[] args) throws Exception{
        new MainApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap bootstrap) {

    }

    @Override
    public void run(MainConfiguration mainConfiguration, Environment environment) throws Exception {

        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, mainConfiguration.getDataSourceFactory(), "mysql");
        final HomeResource homeResource = new HomeResource();
        final DataResource dataResource = new DataResource(jdbi);
        final ShopResource shopResource = new ShopResource(jdbi);
        final CartResource cartResource = new CartResource(jdbi);

        environment.servlets().setSessionHandler(new SessionHandler());
        environment.jersey().register(homeResource);
        environment.jersey().register(dataResource);
        environment.jersey().register(shopResource);
        environment.jersey().register(cartResource);
    }
}
