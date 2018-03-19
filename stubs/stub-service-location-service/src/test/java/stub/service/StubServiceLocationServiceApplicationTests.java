package stub.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.restdocs.RestDocumentation.documentationConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class StubServiceLocationServiceApplicationTests {

	@Autowired
	WebApplicationContext context;

	@Value("${org.springframework.restdocs.outputDir:target/generated-snippets}")
	private String restdocsOutputDir;

	private MockMvc mockMvc;

	@Before
	public void init() {
		System.setProperty("org.springframework.restdocs.outputDir",
				this.restdocsOutputDir);
		this.mockMvc = ForwardAwareMockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration()).build();
	}

	@Test
	public void getHome() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/"))
		.andExpect(MockMvcResultMatchers.content()
				.string(containsString("locations")))
		.andDo(document("home"));
	}

	@Test
	public void getLocations() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/serviceLocations"))
		.andExpect(MockMvcResultMatchers.content()
				.string(containsString("_embedded")))
		.andDo(document("serviceLocations"));
	}

	static class ForwardAwareMockMvcBuilders {

		public static DefaultMockMvcBuilder webAppContextSetup(
				WebApplicationContext context) {
			DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(context);
			ForwardAwareResultHandler forwarder = new ForwardAwareResultHandler();
			builder.alwaysDo(forwarder);
			forwarder.setMockMvc(builder.build());
			return builder;
		}

		private static class ForwardAwareResultHandler implements ResultHandler {

			private MockMvc mockMvc;

			@Override
			public void handle(MvcResult result) throws Exception {
				MockHttpServletRequest request = result.getRequest();
				String uri = request.getRequestURI();
				MockHttpServletResponse response = result.getResponse();
				String forward = response.getForwardedUrl();
				if (StringUtils.hasText(forward)) {
					request.setRequestURI(forward);
					response.setForwardedUrl(null);
					MvcResult forwarded = this.mockMvc.perform(servletContext -> request)
							.andReturn();
					// Hack response into result so it can be asserted as normal
					ReflectionTestUtils.setField(result, "mockResponse",
							forwarded.getResponse());
					// Reset request to original uri
					request.setRequestURI(uri);
				}
			}

			public void setMockMvc(MockMvc mockMvc) {
				this.mockMvc = mockMvc;
			}
		}

	}

}
