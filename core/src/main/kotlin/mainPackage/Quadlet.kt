package mainPackage

import kotlin.math.ceil

class Quadlet {
    var score = 0
    var rowsCleared = 0
    val width: Int
    get() = statics.width
    val height: Int
    get() = statics.height

    var pieces = mutableListOf<Piece>()
    var statics: Grid<Boolean>


    constructor(width: Int, height: Int) {
        statics =  Grid(width, height, false)
    }
    // insert the dynamic pieces at say like y:10 instead of y:0 as the standart will be bottom down
    private var _piece_staticify_next_tick = false
    fun tick(timer: GDXTimer)
    {
        if(paused) return
        for(piece in pieces.indices)
        {
            if(pieces[piece].will_piece_staticify_next_tick(this))
            {
                if(timer.active)
                {
                    if(timer.surpassed())
                    {
                        pieces[piece].moveDown(this)
                        toRemove.add(pieces[piece])
                        timer.reset().disable()
                    }
                }
                else
                {
                    timer.activate()
                }
            }
            else
            {  // ✅
                pieces[piece].moveDown(this)
                timer.reset().disable() // it is afloat so the timer is reset
            }
        }
        for(piece in toRemove)
            pieces.remove(piece)
        toRemove.clear()
        for(row in height.intIndices().reversed())
        {
            if(CheckRowCompletion(row))
            {                                   // cuz when you remove the second from a list the third becomes second
                _completedRows.add(row)
                ClearsThisTick++
            }
        }
        for(row in _completedRows.indices.reversed())
        {
            ClearRowAndPullAboveUnder(_completedRows[row])
        }
        _completedRows.clear()
        if(ClearsThisTick>=1)score += ceil((ClearsThisTick*100 * 1+1f/ ClearsThisTick)/100f).toInt()*100
        if(ClearsThisTick>=1) clear_row_sound.play(1f, 0.5f + ClearsThisTick/8f, 1f)
        rowsCleared+= ClearsThisTick
        ClearsThisTick = 0

    }

}
var _completedRows = mutableListOf<Int>()
var toRemove = mutableListOf<Piece>()
private var ClearsThisTick = 0

fun Quadlet.reset()
{
    dead = false
    score = 0
    rowsCleared = 0
    pieces.clear()
    statics.clear()
    happy_adventure.play()
}

fun Quadlet.addRandomPiece()
{
    if(this.addPiece_colrow(Piece(piecesList.random()(), 2, 0)))
    {
        var dead = true
        paused = true
        println("you lost")
        die.play(1f)
        happy_adventure.stop()
    }
}
/**
 * tip: the row doesn't need to be completed algorithm wise 🥳
 */
fun Quadlet.ClearRowAndPullAboveUnder(row: Int)
{
    if(row < 0 || row >= height) throw IllegalArgumentException("row is out of bounds")
    else if(row==0)
        for(colI in width.intIndices())
        {
            statics.grid[0][colI] = false
            return
        }
    // the row is already in index units rather than size units so I didn't use
    // to IntIndices which is 0..row-1
    for(rowI in (0..row).reversed())
    {
        for(colI in width.intIndices())
        {
            if(rowI == 0)
            {
                statics.grid[rowI][colI] = false
            }
            else{
                this.statics.grid[rowI][colI] = this.statics.grid[rowI-1][colI]

            }
        }
    }
    return
}
fun Quadlet.CheckRowCompletion(row: Int, wanted: Boolean = true) : Boolean
{
    for(col in this.statics.grid[0].size.intIndices())
        if(!this.statics.grid[row][col])
            return false
    return true
}

/**
 * @return true if the piece has overridden other STATIC blocks when creating
 */
fun Quadlet.addPiece_colrow(piece: Piece) : Boolean
{
    pieces.add(piece)
    piece.apply {
        removeColumnIfEmpty(0); removeColumnIfEmpty(cols-1)
             removeRowIfEmpty(0);      removeRowIfEmpty( rows-1)
    }
    return this.statics.checkConflicts(piece.inner, piece.row, piece.col)

}


fun Quadlet.draw()
{
    var merged = Grid(statics.grid.copy(), false)
    for(piece in pieces)
    {   // ❌ BROKEN
        merged = Grid(merged.getOverridden_colrow(piece.inner, piece.col, piece.row), false)
    }

    print("\u001b[H")
    for(row in merged.height.intIndices())
    {
        for(col in merged.width.intIndices())
        {
            if(merged.getElementAt_colrow(col,row))
            {
                print("X")
            }
            else
            {
                print("_")
            }
        }
        println()
    }

}
