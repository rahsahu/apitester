package de.devbliss.apitester;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import de.devbliss.apitester.factory.GetFactory;

/**
 * Contains static methods to perform GET requests. If you want to make more requests in a series
 * sharing the same {@link TestState} and using the same {@link GetFactory}, consider using
 * {@link ApiTest} which is wrapping that stuff for you.
 * 
 * @author hschuetz
 * 
 */
public class Getter {

    public static Context get(URI uri) throws IOException {
        return get(uri, null, null);
    }

    public static Context get(URI uri, GetFactory getFactory) throws IOException {
        return get(uri, null, getFactory);
    }

    public static Context get(URI uri, TestState testState) throws IOException {
        return get(uri, testState, null);
    }

    public static Context get(URI uri, TestState testState, GetFactory getFactory)
            throws IOException {

        if (getFactory == null) {
            getFactory = ApiTesterModule.createGetFactory();
        }

        if (testState == null) {
            testState = ApiTesterModule.createTestState();
        }

        HttpGet request = getFactory.createGetRequest(uri);

        // IMPORTANT: we have to get the cookies from the testState before making the request
        // because this request could add some cookie to the testState (e.g: the response could have
        // a Set-Cookie header)
        ApiRequest apiRequest =
                ApiTestUtil.convertToApiRequest(uri, request, testState.getCookies());

        HttpResponse response = testState.client.execute(request);
        ApiResponse apiResponse = ApiTestUtil.convertToApiResponse(response);
        return new Context(apiResponse, apiRequest);
    }
}
