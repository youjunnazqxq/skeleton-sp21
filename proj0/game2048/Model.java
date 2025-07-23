package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** 返回 (COL, ROW) 处的当前方块 (Tile)，其中 0 <= ROW < size()，0 <= COL < size()。
     * 如果该位置没有方块，则返回 null。用于测试。应该被废弃并移除。
     */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** 返回棋盘一侧的方格数量。用于测试。应该被废弃并移除。 */
    public int size() {
        return board.size();
    }

    /** 返回游戏是否结束（没有可移动的方块，或者棋盘上有一个值为 2048 的方块）。 */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** 返回当前最高游戏分数（在游戏结束时更新）。 */
    public int maxScore() {
        return maxScore;
    }

    /** 清空棋盘并重置得分。 */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** 将 TILE 添加到棋盘。同一位置不能有当前已存在的方块。 */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** 将棋盘向 SIDE 方向倾斜。如果棋盘发生变化，则返回 true。
     * 1. 如果两个方块在移动方向上相邻且值相同，则它们合并为一个新方块，
     * 新方块的值是原始值的两倍，并且这个新值会加到得分实例变量中。
     * 2. 合并产生的方块在同一次倾斜中不会再次合并。因此，在每一次移动中，
     * 每个方块最多只参与一次合并（可能为零次）。
     * 3. 当移动方向上有三个相邻的方块具有相同的值时，
     * 则移动方向上靠前的两个方块合并，而靠后的方块不合并。
     */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        this.board.setViewingPerspective(side);
        int size = this.board.size();
        boolean [][] merge_judge=new boolean[size][size];
        for (int i = 0; i < size; i++) {
            if(board_move(i,merge_judge)){
                changed = true;
            };
        }
        this.board.setViewingPerspective(Side.NORTH);
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }
    public int help_board_move(int col,int row,boolean [][] b) {
        int size = this.board.size();
       Tile current_tile=this.board.tile(col,row);
       int final_row=row;
       for (int r =row+1;r<size;r++){
           Tile next_tile=this.board.tile(col,r);
           if (next_tile==null){
               final_row=r;
           }else if(next_tile.value()==current_tile.value()&&!b[col][r]){
               final_row=r;
               break;
           }else{
               break;
           }
       }return final_row;
    }
    public boolean board_move(int col,boolean [][] b) {
        int size=this.board.size();
        boolean column_changed=false;
        for (int i=size-1;i>=0;i--){
            Tile current_tile=this.board.tile(col,i);
            if (current_tile==null){
                continue;
            }
            int dest_row=help_board_move(col,i,b);
            if (dest_row!=i){
                column_changed = true;
                int value=this.board.tile(col,i).value();
            if (this.board.move(col,dest_row,current_tile)) {
                this.score+=2*value;
                b[col][dest_row]=true;
            }
            }
        }return column_changed;
    }

    /** 检查游戏是否结束并适当地设置 gameOver 变量。
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** 判断游戏是否结束。 */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** 如果棋盘上至少有一个空格，则返回 true。
     * 空格存储为 null。
     */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function
        int size=b.size();
        for( int i=0;i<size;i++){
            for (int  j=0;j<size;j++){
                if (b.tile(i,j)==null){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 如果任何方块等于最大有效值，则返回 true。
     * 最大有效值由 MAX_PIECE 给出。请注意，
     * 给定一个 Tile 对象 t，我们可以通过 t.value() 获取其值。
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        int size=b.size();
        for( int i=0;i<size;i++){
            for (int  j=0;j<size;j++){
                if (b.tile(i,j)==null){
                    continue;
                }
                if (b.tile(i,j).value()==MAX_PIECE){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 如果棋盘上有任何有效的移动，则返回 true。
     * 存在有效移动有两种情况：
     * 1. 棋盘上至少有一个空位。
     * 2. 有两个相邻的方块具有相同的值。
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        int size=b.size();
        for( int i=0;i<size;i++){
            for (int  j=0;j<size;j++){
                if (equal_value(b,i,j) ||b.tile(i,j)==null){
                    return true;
                }
            }
        }
        return false;
    }
    public static  boolean side_judge(Board b,int i ,int j){
        if (i<0||j<0||i>=b.size()||j>=b.size()){
            return false;
        }else{
        return true;}
    }
    public static boolean equal_value(Board b, int i, int j) {
        Tile currentTile = b.tile(i, j);
        if (currentTile == null) {
            return false;
        }
        if (side_judge(b, i - 1, j)) {
            Tile leftTile = b.tile(i - 1, j);
            if (leftTile != null && currentTile.value() == leftTile.value()) {
                return true;
            }
        }
        if (side_judge(b, i + 1, j)) {
            Tile rightTile = b.tile(i + 1, j);
            if (rightTile != null && currentTile.value() == rightTile.value()) {
                return true;
            }
        }
        if (side_judge(b, i, j - 1)) {
            Tile upTile = b.tile(i, j - 1);
            if (upTile != null && currentTile.value() == upTile.value()) {
                return true;
            }
        }
        if (side_judge(b, i, j + 1)) {
            Tile downTile = b.tile(i, j + 1);
            if (downTile != null && currentTile.value() == downTile.value()) {
                return true;
            }
        }
        return false;
    }

    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
