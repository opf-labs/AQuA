package aqua;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Hello {
	
    protected final Log logger = LogFactory.getLog(getClass());

    // display the upload form as the home page
    @RequestMapping({"/", "/home"})
    public ModelAndView showHomePage(Model m) {
            HashMap<String, String> model = new HashMap<String, String>();
            return new ModelAndView("home", model);
    }
}
