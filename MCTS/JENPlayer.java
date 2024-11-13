import java.util.*;
import java.io.*;

public class JENPlayer {

    static Random random=new Random();

    static void setupBoardState(JENState state, int player, char[][] board)
    {
        /* Set up the current state */
        state.player = player;
        JENPlayerHelper.memcpy(state.board,board);

        /* Find the legal moves for the current state */
        JENPlayerHelper.FindLegalMoves(state);
    }

    
    static void PerformMove(JENState state, int moveIndex)
    {
        JENPlayerHelper.PerformMove(state.board, state.movelist[moveIndex], JENPlayerHelper.MoveLength(state.movelist[moveIndex]));
        state.player = state.player%2+1;
        JENPlayerHelper.FindLegalMoves(state);
    }

    static class Node{
        JENState state;
        Node[] children;
        Node parent;
        double won=0.0;
        int played=0;

        public Node(JENState state, Node parent){
            this.parent = parent;
            this.state = state;
            this.children = new Node[state.numLegalMoves];
        }
    }

    public static void FindBestMove(int player, char[][] board, char[] bestmove, long end) {
        
        JENState state = new JENState(); // , nextstate;
        setupBoardState(state, player, board);

        Node root = new Node(state, null);

        while (System.currentTimeMillis() < end){
            MCTS(root, end);
        }

        int bestChoice = root.children[0].played;// - root.children[0].won)/root.played; // proportion won out of total played
        int choiceIndex = 0;
        for (int x = 1; x < root.children.length; x++) {
            int temp = root.children[x].played;// - root.children[x].won)/root.played;
            // System.err.println("CHILD " + x + " WON: " + (root.children[x].played - root.children[x].won) + " PLAYED: " + root.children[x].played);
            if (temp > bestChoice){
                bestChoice = temp;
                choiceIndex = x;
            }
        }
        // System.err.println("ROOT," + root.won + ", " + root.played);
        // System.err.println("CHOSE CHILD " + choiceIndex);// + " WON: " + (root.children[choiceIndex].played - root.children[choiceIndex].won) + " PLAYED: " + root.children[choiceIndex].played);
        JENPlayerHelper.memcpy(bestmove, state.movelist[choiceIndex], JENPlayerHelper.MoveLength(state.movelist[choiceIndex]));
    }

    // search the current tree to find the node to expand based on utility function 
    static void MCTS(Node node, long end){
        if (System.currentTimeMillis() > end) return;
        if (node.state.numLegalMoves <= 0){

            node.played++;
            backprop(node.parent, 1.0, 1);
            return;
        }
        if (node.children[0] == null){

            expand(node, end);
            return;
        }

        Double best = utility(node.children[0], node);
        Node selected = node.children[0];
        for(int i=1; i<node.children.length;i++){
            Double temp = utility(node.children[i], node);
            if (temp>best){
                best = temp;
                selected=node.children[i];
            }

        }
        MCTS(selected, end);
    }

    static double simulate(JENState state, int moveLimit){
        if (state.numLegalMoves <= 0) return 1.0;
        if (moveLimit <= 0) return 0.0;

        int index = random.nextInt(state.numLegalMoves);
        JENState nextState = new JENState(state);
        PerformMove(nextState, index);
        return -simulate(nextState, --moveLimit);
    }

    //backpropogate played values and won(flipping each time) to parent node from expansion of leaf

    static void backprop(Node node, double won, int played){
        if (node == null) return;
        node.won+=won;
        node.played+=played;
        backprop(node.parent, played-won, played);
    }

    //expand children of leaf node and do a simulationg for each
    static void expand(Node node, long end){

        for (int i=0; i<node.state.numLegalMoves; i++){
            JENState nextState = new JENState(node.state);
            PerformMove(nextState, i);
            node.children[i] = new Node(nextState, node);
            double temp = simulate(nextState, 100);

            if (temp > 0) node.won += 1.0;
            else if (temp == 0) {
                node.won +=0.5;
                node.children[i].won += 0.5;
            }
            else node.children[i].won += 1.0;
            node.played++;
            node.children[i].played++;

            if (System.currentTimeMillis() > end) break;
        }
        backprop(node.parent, node.played - node.won, node.played);
    }

    static double utility(Node child, Node node){
        return (double)((child.played - child.won)/child.played + 1.4*Math.sqrt(Math.log(node.played)/child.played));
    }
}
