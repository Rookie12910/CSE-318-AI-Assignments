import java.util.ArrayList;
import java.util.Collections;


public class MancalaBoard {
    ArrayList<Integer> board;
    Boolean p1Turn;
    int totalGemsCaptured, extraMovesWon;

    final int P1StartingPit = 0;
    final int P2StartingPit = 7;
    final int P1StoragePit = 6; //Index of player1's storage pit
    final int P2StoragePit = 13; //Index of player2's storage pit
    final int P1EndingPit = P1StoragePit-1;
    final int P2EndingPit = P2StoragePit - 1;

    final int pitsOnEachSide = 6;
    final int boardSize = 14;
    final int initialGems = 4;



    public MancalaBoard()
    {
        this.board = new ArrayList<>(Collections.nCopies(boardSize, initialGems));
        this.board.set(P1StoragePit, 0); // storage of player1 initially 0
        this.board.set(P2StoragePit, 0); // storage of player2 initially 0
        this.p1Turn = true;              // Player1 makes a move first
        this.totalGemsCaptured = 0;
        this.extraMovesWon = 0;
    }

    public MancalaBoard(MancalaBoard other)
    {
        this.board = new ArrayList<>(other.board);
        this.p1Turn = other.p1Turn;
        this.totalGemsCaptured = other.totalGemsCaptured;
        this.extraMovesWon = other.extraMovesWon;
    }

    void distributeGems()
    {
        int p1Side = 0, p2Side = 0;
        for(int i = 0; i<pitsOnEachSide; i++)
        {
            p1Side += board.get(P1StartingPit+i);
            p2Side += board.get(P2StartingPit+i);
            board.set(P1StartingPit+i,0);
            board.set(P2StartingPit+i,0);
        }
        board.set(P1StoragePit,(board.get(P1StoragePit)+p1Side));
        board.set(P2StoragePit,(board.get(P2StoragePit)+p2Side));
    }

    boolean isGameOver()
    {
        boolean p1SideEmpty = true;
        boolean p2SideEmpty = true;
        for(int i = 0;i<pitsOnEachSide;i++)
        {
            if (board.get(P1StartingPit+i) > 0) p1SideEmpty = false;
            if (board.get(P2StartingPit+i) > 0) p2SideEmpty = false;
        }
        if(p1SideEmpty || p2SideEmpty) distributeGems();
        return p1SideEmpty || p2SideEmpty;
    }

    ArrayList<Integer> getNextMoves()
    {
        ArrayList<Integer> moves = new ArrayList<>();
        int startIdx = p1Turn ? P1StartingPit : P2StartingPit;
        for(int i = startIdx; i< (startIdx+pitsOnEachSide); i++)
        {
            if(board.get(i) > 0) moves.add(i);
        }
        Collections.shuffle(moves);
        return moves;
    }

    Boolean executeMove(int index)
    {
        int stones = board.get(index);
        board.set(index,0);
        index++;
        while(stones > 0)
        {
            if(p1Turn && index==P2StoragePit)
            {
                index = 0;
                continue;
            }
            if(!p1Turn && index==P1StoragePit)
            {
                index++;
                continue;
            }
            board.set(index, board.get(index)+1);
            stones--;
            index++;
            index %= boardSize;
        }
        int lastPit = (index == P1StartingPit ? P2StoragePit : index-1);
        if(p1Turn)
        {
            if(lastPit < pitsOnEachSide && board.get(lastPit)==1 && board.get(P2EndingPit-lastPit) > 0)
            {
                int capturedGems = board.get(lastPit) + board.get(12-lastPit);
                board.set(lastPit,0);
                board.set(P2EndingPit-lastPit,0);
                board.set(P1StoragePit,board.get(P1StoragePit)+capturedGems);
                totalGemsCaptured += capturedGems;
            }
        }
        else
        {
            if(lastPit > pitsOnEachSide && lastPit < P2StoragePit && board.get(lastPit)==1 && board.get(P2EndingPit-lastPit) > 0)
            {
                int capturedGems = board.get(lastPit) + board.get(P2EndingPit-lastPit);
                board.set(lastPit,0);
                board.set(P2EndingPit-lastPit,0);
                board.set(P2StoragePit,board.get(P2StoragePit)+capturedGems);
                totalGemsCaptured -= capturedGems;
            }
        }

        if((p1Turn && lastPit==P1StoragePit) || (!p1Turn && lastPit==P2StoragePit))
        {
            extraMovesWon+= p1Turn ? 1 : -1;
            return true;
        }
        else return false;
    }

    Double calculateHeuristics(int choice)
    {
        final int w1 = 3;
        final int w2 = 4;
        final int w3 = 3;
        final int w4 = 4;
        int p1Storage, p1Side, p2Storage, p2Side;
        double heuristicsValue = 0;
        p1Storage  = board.get(P1StoragePit);
        p2Storage = board.get(P2StoragePit);
        p1Side = p2Side = 0;
        for(int i = 0; i<pitsOnEachSide; i++)
        {
            p1Side += board.get(P1StartingPit+i);
            p2Side += board.get(P2StartingPit+i);
        }
        switch (choice)
        {
            case 1:
                heuristicsValue = p1Storage - p2Storage;
                break;
            case 2:
                heuristicsValue = w1 * (p1Storage - p2Storage) + w2 * (p1Side - p2Side);
                break;
            case 3:
                heuristicsValue = w1 * (p1Storage - p2Storage) + w2 * (p1Side - p2Side) + w3 * extraMovesWon;
                break;
            case 4:
                heuristicsValue = w1 * (p1Storage - p2Storage) + w2 * (p1Side - p2Side) + w3 * extraMovesWon + w4 * totalGemsCaptured;
                break;
            default:
                break;
        }
        return heuristicsValue;
    }

    void printBoard()
    {
        System.out.println("****************************************************************");
        if(Game.currMode== Game.gameMode.AIVsAI) System.out.println("Best move for player"+ (p1Turn ? "1" : "2")+" : " + (Game.bestMove+1));
        System.out.println("\n\t\t\t\tPlayer2\n");
        System.out.println("\tPit:\t13\t12\t11\t10\t9\t8\t");
        System.out.println("\t     \t--\t--\t--\t--\t--\t--\t");
        System.out.print("\tGems: \t");
        for(int i = 12;i>=7;i--) System.out.print(board.get(i)+"\t");
        System.out.println("\n");
        System.out.println("P2("+board.get(P2StoragePit)+")"+"\t\t\t\t\t\t\t\t"+ "P1("+board.get(P1StoragePit)+")"+"\n");
        System.out.print("\tGems: \t");
        for(int i = 0;i<6;i++) System.out.print(board.get(i)+"\t");
        System.out.println();
        System.out.println("\t     \t--\t--\t--\t--\t--\t--\t");
        System.out.println("\tPit:\t1\t2\t3\t4\t5\t6\t");
        System.out.println("\n\t\t\t\tPlayer1\n");
        System.out.println("****************************************************************");
    }
}
