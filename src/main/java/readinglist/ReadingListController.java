package readinglist;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
@RequestMapping("/")
@ConfigurationProperties("amazon")
public class ReadingListController extends WebMvcConfigurerAdapter {

	private ReadingListRepository readingListRepository;
	private AmazonProperties amazonProperties;
	
	private CounterService counterService;
	private GaugeService gaugeService;
	
	@Autowired
	public ReadingListController(
			ReadingListRepository readingListRepository,
			AmazonProperties amazonProperties,
			CounterService counterService,
			GaugeService gaugeService) {
		this.readingListRepository = readingListRepository;
		this.amazonProperties = amazonProperties;
		this.counterService = counterService;
		this.gaugeService = gaugeService;
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String readersBooks(Reader reader, Model model) {
		List<Book> readingList =
				readingListRepository.findByReader(reader);
		if (readingList != null) {
			model.addAttribute("books", readingList);
			model.addAttribute("reader", reader);
			model.addAttribute("amazonID", amazonProperties.getAssociatedId());
		}
		return "readingList";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String addToReadingList(Reader reader, Book book) {
		book.setReader(reader);
		readingListRepository.save(book);
		counterService.increment("books.saved");
		gaugeService.submit("books.last.saved",  System.currentTimeMillis());
		return "redirect:/";
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}
	
	@Override
	public void addArgumentResolvers(
			List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new ReaderHandlerMethodArgumentResolver());
	}  
}