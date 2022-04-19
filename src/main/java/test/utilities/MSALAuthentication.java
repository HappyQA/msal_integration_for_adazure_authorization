package test.utilities;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCredential;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.SilentParameters;

public class MSALAuthentication {
    private static String applicationId;
    // Set authority to allow only organizational accounts
    // Device code flow only supports organizational accounts
    private static String authority;
    private static String clientSecret;

    public static void initialize(String applicationId, String authority, String clientSecret) {
        MSALAuthentication.applicationId = applicationId;
        MSALAuthentication.authority = authority;
        MSALAuthentication.clientSecret = clientSecret;
    }

    public static String getUserAccessToken(String[] scopes) {
        if (applicationId == null) {
            System.out.println("You must initialize Authentication before calling getUserAccessToken");
            return null;
        }

        Set<String> scopeSet = new HashSet<>();

        Collections.addAll(scopeSet, scopes);

        IClientCredential cred = ClientCredentialFactory.createFromSecret(clientSecret);
        ConfidentialClientApplication app;
        try {
            // Build the MSAL application object for a client credential flow
            app = ConfidentialClientApplication.builder(applicationId, cred).authority(authority).build();
        } catch (MalformedURLException e) {
            System.out.println("Error creating confidential client: " + e.getMessage());
            return null;
        }

        IAuthenticationResult result;
        try {
            SilentParameters silentParameters = SilentParameters.builder(scopeSet).build();
            result = app.acquireTokenSilently(silentParameters).join();
        } catch (Exception ex){
            if (ex.getCause() instanceof MsalException) {

                ClientCredentialParameters parameters =
                        ClientCredentialParameters
                                .builder(scopeSet)
                                .build();
                // Try to acquire a token. If successful, you should see
                // the token information printed out to console
                result = app.acquireToken(parameters).join();
            } else {
                // Handle other exceptions accordingly
                System.out.println("Unable to authenticate = " + ex.getMessage());
                return null;
            }
        }

        if (result != null) {
            System.out.println("Access Token - " + result.accessToken());
            return result.accessToken();
        }
        return null;
    }
}
