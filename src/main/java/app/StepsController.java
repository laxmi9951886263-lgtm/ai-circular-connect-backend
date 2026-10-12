
package app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StepsController {

    private final StepsService stepsService;

    public StepsController(StepsService stepsService) {
        this.stepsService = stepsService;
    }

    @GetMapping("/steps/{n}/{k}")
    public int getWinner(
            @PathVariable int n,
            @PathVariable int k) {
        return stepsService.getWinner(n, k);
    }
}