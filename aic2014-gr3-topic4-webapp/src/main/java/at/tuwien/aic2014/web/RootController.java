package at.tuwien.aic2014.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RootController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public String getRoot() {
		return "hello world";
	}
}
