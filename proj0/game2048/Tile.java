package game2048;

/** Represents the image of a numbered tile on a 2048 board.
 *  @author P. N. Hilfinger.
 */
public class Tile {


    /** 一个新的方块，其 VALUE 为其值，位于 (ROW, COL) 处。
     * 此构造函数是私有的，因此所有方块都通过工厂方法 create、move 和 merge 创建。
     */
    private Tile(int value, int col, int row) {
        this.value = value;
        this.row = row;
        this.col = col;
        this.next = null;
    }

    /** Return my current row. */
    public int row() {
        return row;
    }

    /** Return my current column. */
    public int col() {
        return col;
    }

    /** Return the value supplied to my constructor. */
    public int value() {
        return value;
    }

    /** 返回我的下一个状态。在我被移动或合并之前，我就是我自己的后继者。 */
    public Tile next() {
        return next == null ? this : next;
    }

    /** 返回一个在 (ROW, COL) 处，值为 VALUE 的新方块。
     * 这是一个静态工厂方法，用于创建新的 Tile 实例。
     */
    public static Tile create(int value, int col, int row) {
        return new Tile(value, col, row);
    }

    /** Return the result of moving me to (COL, ROW). */
    public Tile move(int col, int row) {
        Tile result = new Tile(value, col, row);
        next = result;
        return result;
    }

    /** 返回将 OTHERTILE 与我合并后，移动到 (COL, ROW) 的结果。
     * 此方法创建一个新的 Tile 对象代表合并后的状态（值翻倍），并将其设置为
     * 当前 Tile 和 otherTile 的后继。
     */
    public Tile merge(int col, int row, Tile otherTile) {
        assert value == otherTile.value();
        next = otherTile.next = new Tile(2 * value, col, row);
        return next;
    }

    /** Return the distance in rows or columns between me and my successor
     *  tile (0 if I have no successor). */
    public int distToNext() {
        if (next == null) {
            return 0;
        } else {
            return Math.max(Math.abs(row - next.row()),
                            Math.abs(col - next.col()));
        }
    }

    @Override
    public String toString() {
        return String.format("%d@(%d, %d)", value(), col(), row());
    }

    /** My value. */
    private final int value;

    /** My last position on the board. */
    private final int row, col;

    /** Successor tile: one I am moved to or merged with. */
    private Tile next;
}
