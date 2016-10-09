package photon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @RequestMapping(value = "/mfw", method = RequestMethod.GET)
    public String get(Model model) {
        return "mfw-home.html";
    }

}
