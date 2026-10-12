package app.dto;

import java.util.List;

public class StepsResponse {

    private int n;
    private List<String> steps;

    public StepsResponse(int n, List<String> steps) {
        this.n = n;
        this.steps = steps;
    }

    public int getN() {
        return n;
    }

    public List<String> getSteps() {
        return steps;
    }
}