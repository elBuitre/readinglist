package readinglist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import readinglist.ReadinglistApplication;
import readinglist.Reader;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
		classes = ReadinglistApplication.class)
@WebAppConfiguration
public class MockMvcWebTests {

	@Autowired
	private WebApplicationContext webContext;
	
	private MockMvc mockMvc;
	
	@Before
	public void setupMockMvc() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webContext)
				.apply(springSecurity())
				.build();
	}
	
	@Test
	public void homePage_unauthenticatedUser() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string("Location", "http://localhost/login"));
	}
	
	@Test
	@WithUserDetails("craig")
	public void homepage_authenticatedUser() throws Exception {
		
		Reader expectedReader = new Reader();
		expectedReader.setUsername("craig");
		expectedReader.setPassword("password");
		expectedReader.setFullname("Craig Walls");
		
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(view().name("readingList"))
			.andExpect(model().attribute("reader",
					samePropertyValuesAs(expectedReader)))
			.andExpect(model().attribute("books", hasSize(0)));
			
	}
}
