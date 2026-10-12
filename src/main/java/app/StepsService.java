
package app;

import app.dto.StepsResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StepsService {

    public Object getSteps(int n) {
        if (n < 1) {
            throw new IllegalArgumentException(
                    "n must be greater than 0"
            );
        }

        if (n <= 10) {
            List<String> steps = Arrays.asList(
                    "Step 1",
                    "Step 2",
                    "Step 3",
                    "Step 4",
                    "Step 5"
            );

            return new StepsResponse(n, steps);
        }

        return "n value is greater than 10";
    }

    public int getWinner(int n, int k) {
        if (n < 1 || k < 1) {
            throw new IllegalArgumentException(
                    "n and k must be greater than 0"
            );
        }

        int winner = 0;
        for (int i = 2; i <= n; i++) {
            winner = (winner + k) % i;
        }

        return winner + 1;
    }
}
