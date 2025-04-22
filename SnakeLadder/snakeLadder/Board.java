import java.util.concurrent.ThreadLocalRandom;

public class Board {

    Cell[][] cells;

    Board(int boardSize, int numOfSnakes, int numOfLadder){
        intializeCells(boardSize);
        addSnakeLadders(cells, numOfSnakes,numOfLadder);
    }

    private void intializeCells(int boardSize){
        cells = new Cell[boardSize][boardSize];

        for(int i=0;i<boardSize;i++){
            for(int j=0;j<boardSize;j++){
                Cell cellObj = new Cell();
                cells[i][j] = cellObj;
            }
        }
    }

    private void addSnakeLadders(Cell[][] cells, int numOfSnakes, int numOfLadder){
        while(numOfSnakes >0){
            int snakeHead = ThreadLocalRandom.current().nextInt(1,cells.length * cells.length-1);
            int snakeTail = ThreadLocalRandom.current().nextInt(1,cells.length * cells.length-1);
            if(snakeTail > snakeHead){
                continue;
            }
            Jump sanakeObj = new Jump();
            sanakeObj.start = snakeHead;
            sanakeObj.end = snakeTail;

            Cell cell = getCell(snakeHead);
            cell.jump = sanakeObj;
            numOfSnakes--;
        }
        while(numOfLadder >0){
            int ladderStart = ThreadLocalRandom.current().nextInt(1,cells.length * cells.length-1);
            int ladderEnd = ThreadLocalRandom.current().nextInt(1,cells.length * cells.length-1);
            if(ladderStart > ladderEnd){
                continue;
            }
            Jump ladderObj = new Jump();
            ladderObj.start = ladderStart;
            ladderObj.end = ladderEnd;

            Cell cell = getCell(ladderStart);
            cell.jump = ladderObj;
            numOfLadder--;
        }
    }

    Cell getCell(int playerPosition){
        int boardRow = playerPosition/ cells.length;
        int boardColumn = (playerPosition % cells.length);
        return cells[boardRow][boardColumn];
    }
}
