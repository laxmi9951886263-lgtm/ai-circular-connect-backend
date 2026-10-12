package app;

public class Solution {

    public int findTheWinner(int n, int k) {
        int winner = 0;

        for (int i = 2; i <= n; i++) {
            winner = (winner + k) % i;
        }

        return winner + 1;
    }

    public static void main(String[] args) {
        Solution s = new Solution();
        System.out.println(s.findTheWinner(5, 2));
    }
}