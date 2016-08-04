package readinglist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=ReadinglistApplication.class)
@WebIntegrationTest(randomPort=true)
public class ServerWebTests {
	
	//private static FirefoxDriver browser;
	private static ChromeDriver browser;
	
	@Value("${local.server.port}")
	private int port;

	@BeforeClass
	public static void openBrowser() {
		System.setProperty("webdriver.chrome.driver",
				"c:/var/drivers/chromedriver.exe");
		browser = new ChromeDriver();
		browser.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	
	@AfterClass
	public static void closeBrowser() {
		browser.quit();
	}

	@Test
	public void failingAuthentication() {
		// Check that login page is loaded
		String baseUrl = "http://localhost:" + port;
		browser.get(baseUrl);
		assertEquals("Login", browser.getTitle().toString());
		
		// Send invalid credentials
		browser.findElementByName("username").sendKeys("John");
		browser.findElementByName("password").sendKeys("wrong");
		browser.findElementByTagName("form").submit();
		
		// Check that incorrect username and password message appear
		WebElement div = browser.findElementByCssSelector("div.error");
		assertTrue(div.getText().contains("Incorrect username"));
	}

	@Test
	public void LoginAndLogout() {
		// Check that login page is loaded
		String baseUrl = "http://localhost:" + port;
		browser.get(baseUrl);
		assertEquals("Login", browser.getTitle().toString());
		
		// Send valid credentials
		browser.findElementByName("username").sendKeys("craig");
		browser.findElementByName("password").sendKeys("password");
		browser.findElementByTagName("form").submit();
		
		// Check that reading list page is loaded
		assertEquals("Reading List", browser.getTitle().toString());
		
		// Perform logout
		browser.findElementByName("logout").submit();
		assertTrue(browser.findElementByTagName("div").getText().contains("logged out"));
	}

	@Test
	public void addBookToEmptyList() {
		String baseUrl = "http://localhost:" + port;
		
		browser.get(baseUrl);
		
		assertEquals("Login", browser.getTitle().toString());
		
		// Send valid credentials
		browser.findElementByName("username").sendKeys("craig");
		browser.findElementByName("password").sendKeys("password");
		browser.findElementByTagName("form").submit();

		assertEquals("You have no books in your book list",
				browser.findElementByTagName("div").getText());
		
		browser.findElementByName("title").sendKeys("BOOK TITLE");
		browser.findElementByName("author").sendKeys("BOOK AUTHOR");
		browser.findElementByName("isbn").sendKeys("1234567890");
		browser.findElementByName("description").sendKeys("DESCRIPTION");
		browser.findElementByTagName("form").submit();
		
		WebElement dl =
				browser.findElementByCssSelector("dt.bookHeadline");
		assertEquals("BOOK TITLE by BOOK AUTHOR (ISBN: 1234567890)",
				dl.getText());
		
		WebElement dt =
				browser.findElementByCssSelector("dd.bookDescription");
		assertEquals("DESCRIPTION", dt.getText());

		// Perform logout
		browser.findElementByName("logout").submit();
		assertTrue(browser.findElementByTagName("div").getText().contains("logged out"));
	}
}
