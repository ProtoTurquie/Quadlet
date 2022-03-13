package mainPackage

import kotlin.math.max


class Piece {
    var inner: Grid<Boolean>
    var reference: MutableMap<Int, ()->Grid<Boolean>>? = null
    var col: Int = 0
    var row: Int = 0
    var currentState = 0


    val cols get() = inner.width
    val rows get() = inner.height

    constructor(reference: MutableMap<Int, ()->Grid<Boolean>>, col:Int, row: Int, currentState : Int = 0)
    {
        this.reference = reference
        this.col = col
        this.row = row
        this.currentState = currentState
        inner = reference[currentState]!!()
    }
    constructor(template: Grid<Boolean>, col: Int, row: Int) {
        this.inner = template
        this.col = col
        this.row = row
    }

    /**
     * @return whether it was removed successfully
     */
    fun removeColumnIfEmpty(column: Int) : Boolean
    {
        if(!inner.grid.IsColumnEmpty(column, false)) {
            return false
        }
        for(rowI in rows.intIndices())
        {
            inner.grid[rowI].removeAt(column)
        }

        return true
    }
    fun removeRowIfEmpty(row1: Int) : Boolean
    {
        if(!inner.grid.IsRowEmpty(row1, false)) {
            return false
        }
        inner.grid.removeAt(row1)

        return true
    }

    /**
     * @return false if failed
     */
    fun changeState(grid: Grid<Boolean>, factor: Int = 1) : Boolean
    {
        if(reference == null) throw IllegalStateException("You need to input a template to be able to use this")
        var before = currentState
        if(currentState + factor < 0)
        {
            currentState = reference!!.keys.maxOrNull()!!.coerceAtLeast(1) + currentState + factor
        }
        else if(currentState + factor >  reference!!.keys.maxOrNull()!!) // ❓ unsure about this
            currentState  =  (currentState+factor) % (reference!!.keys.maxOrNull()!!+1)
        else
            currentState += factor

        var _c = 0; var _r = 0
        var _reference= reference!![currentState]!!().apply {
            if(this.removeColumnIfEmpty(0, false))_c++; if(this.removeColumnIfEmpty(this.width-1, false))_r--
            if(this.removeRowIfEmpty(0, false))_c--;      if(this.removeRowIfEmpty( this.height-1, false))_r++
        }

        if(col +_c <0) _c=0
        if(row +_r <0) _r=0
        if(!grid.isOvershoot(col + _reference.width+_c, row + _reference.height+_r))
        {
            if(grid.checkConflicts(_reference, row+_r, col+_c))
            {
                currentState = before
                return false
            }
            else inner = _reference
            col += _c
            row += _r
            return true
        }
        else
        {
            currentState = before
            return false
        }
    }

    /**
     * returns whether the operation has succeeded (true) or failed (false)
     * ✅ tested
     */
    fun moveLeft(game: Quadlet) : Boolean
    {
        if(removeColumnIfEmpty(0)) col++

        var leftMostToUse = inner.grid.GetLatestCols(false, false)

        for(i in leftMostToUse.indices.step(2))
        {
            if(leftMostToUse[i+1] != -1 && col + leftMostToUse[i+1]-1 >= 0)
            {
                if(game.statics.getElementAt_colrow(col + leftMostToUse[i+1]-1, row + leftMostToUse[i]))
                    return false
            }
            else if(!(col + leftMostToUse[i+1] >=0))
                return false
        }

        this.col--
        this.col = max(col, 0) // I was bored of debugging code and just slapped
                                  // this here

        return true

    }
    /**
     * returns whether the operation has succeeded (true) or failed (false)
     * ✅ tested
     */
    fun moveRight(game: Quadlet) : Boolean
    {
        // the reason why you cant move L's to the rightmost edge is because it doesn't only consist of active areas
        // but also areas, either fuoll columns or rows that are completely empty
        // edit: this is also a problem in O's so i dunno
        // also, for some reason, the safeToFall doesn't work properly as in staticifiying when diagonal to the empty
        // side of L
        removeColumnIfEmpty(cols-1)
        var rightMostToUse = inner.grid.GetLatestCols(false, true)

        for(i in rightMostToUse.indices.step(2))
        {
            if(rightMostToUse[i+1] != -1 && col + rightMostToUse[i+1]+1 < game.width)
            {
                if(game.statics.getElementAt_colrow(col + rightMostToUse[i+1]+1, row + rightMostToUse[i]))
                    return false
            }
            else if(!(col + rightMostToUse[i+1]+1 < game.width)) {
                return false
            }
        }
        this.col++
        this.col = col.coerceAtMost(game.width-1)
        return true
    }

    fun will_piece_staticify_next_tick(game: Quadlet) : Boolean
    {

        removeRowIfEmpty(rows-1)
        row = row.coerceAtMost(game.height-1)
        var lastRow = inner.grid.GetLatestRows(false, true)

        for(i in lastRow.indices.step(2))
        {
            if(lastRow[i] != -1 && row + lastRow[i]+1 < game.height)
            {
                if(game.statics.getElementAt_colrow(col + lastRow[i+1], row + lastRow[i]+1))
                {
                    return true
                }
            }
            else if(!(row + lastRow[i]+1 < game.height))
            {
                return true
            }
        }
        return false

    }
    /**
     * @return whether the piece should be removed from the game
     */
    fun moveDown(game: Quadlet) : Boolean
    {
        // TODO: for the move ones, like say, for the down one, this one, check the last row of the piece that
        //  isnt fully empty, instead of taking the last one, same for the other ones
        var safeToFall = true
        removeRowIfEmpty(rows-1)
        row = row.coerceAtMost(game.height-1)
        var lastRow = inner.grid.GetLatestRows(false, true)

        for(i in lastRow.indices.step(2))
        {
            if(lastRow[i] != -1 && row + lastRow[i]+1 < game.height)
            {
                if(game.statics.getElementAt_colrow(col + lastRow[i+1], row + lastRow[i]+1))
                {
                    safeToFall = false
                    break
                }
            }
            else if(!(row + lastRow[i]+1 < game.height))
            {
                safeToFall = false
                break
            }
        }
        if(safeToFall)
        {
            this.row++
            return false
        }
        else
        {
            for(rowI in inner.grid.size.intIndices())
            {
                for(colI in inner.grid[rowI].size.intIndices())
                {
                    if(!game.statics.isOvershoot(this.col + colI, this.row + rowI))
                        if(inner.getElementAt_colrow(colI, rowI) == true) // cuz we dont want to override existing ones
                            game.statics.setElementAt_colrow(colI + this.col, rowI + this.row, true)

                }
            }
            return true
        }
        return false

/*
        var rowToUse = -1
        //  this will fail if the empty ones are at the left or the top, but it shouldn't even be a case in this game
        //  also if there are more than 1 empty ones, in which case it will get the last-1, or if there are more than
        //  1 empty ones, but for god’s sake just don't have them
        for(checkRows in rows.intIndices())
        {
            if(inner.grid.IsRowEmpty(checkRows, false)) rowToUse = checkRows-1
        }
        if(rowToUse == -1) rowToUse = rows-1
        for(colI in inner.grid.last().indices)
        {
               // index of the row, posY in future (1 row lower)
            if(rowToUse +row +1 < game.height)
            {
                if(this.inner.grid[rowToUse][colI] == true) // cuz we want to only check if it exists
                    if(game.statics.getElementAt_colrow(this.col + colI, row + rowToUse +1))
                        safeToFall = false //✅
            }
            else // at the last index
            {
                safeToFall = false
            }
        }
        if(safeToFall)
            row++ // ✅
        else
        {
            for(rowI in rows.intIndices())
                for(colI in cols.intIndices())
                {
                    // the problematic part
                    if(!game.statics.isOvershoot(this.col + colI, this.row + rowI))
                        if(inner.getElementAt_colrow(colI, rowI) == true) // cuz we dont want to override existing ones
                            game.statics.setElementAt_colrow(colI + this.col, rowI + this.row, true)
                }
            return true
        }
        return false

*/
    }
}