package test.common;

import test.config.ConfigFile;
import test.steps.BudgetStructureSteps;
import test.utilities.MSALAuthentication;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Disabled;

public class BaseClass {
    public static Logger logger = LogManager.getLogger(SomeClass.class);

    public static ConfigFile cfg = ConfigFactory.create(ConfigFile.class);

    @BeforeClass
    public void getAuthenticationInAD() {
        final String appId = cfg.applicationId();

        final String authority = cfg.applicationAuthority();

        final String clientSecret = cfg.applicationClientSecret();

        final String appScopes = cfg.applicationScopes();

        MSALAuthentication.initialize(appId, authority, clientSecret);
        final String accessToken = MSALAuthentication.getUserAccessToken(new String[]{appScopes});
        logger.info("Get Access Token " + accessToken);

        RestAssured
                .given()
                .auth()
                .oauth2(accessToken);
        logger.info("Accepted access token from Active Directory");
    }
}
