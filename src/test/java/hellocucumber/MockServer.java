package hellocucumber;

import org.mockserver.client.MockServerClient;
import org.mockserver.client.initialize.PluginExpectationInitializer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.StringBody;

public class MockServer implements PluginExpectationInitializer {

	@Override
	public void initializeExpectations(MockServerClient mockServerClient) {
		mockServerClient.when(HttpRequest.request()
				.withMethod("POST")
				.withPath("/adresse")
				.withBody(StringBody.subString("active=true"))
		).respond(HttpResponse.response()
				.withStatusCode(200)
				.withBody("{\"mvt_cree\" : \"true\", \"contrat_update\" : \"true\"}"));

		mockServerClient.when(HttpRequest.request()
				.withMethod("POST")
				.withPath("/adresse")
				.withBody(StringBody.subString("active=false"))
		).respond(HttpResponse.response()
				.withStatusCode(200)
				.withBody("{\"mvt_cree\" : \"true\", \"contrat_update\" : \"false\"}"));
	}
}