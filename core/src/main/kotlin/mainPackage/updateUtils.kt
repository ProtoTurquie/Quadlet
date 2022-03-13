package mainPackage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

fun processInput(screen: MainScreen, delta: Float)
{
    screen.apply {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.reset()
        }
        if(paused)
        {
            if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
                paused = false
        }
        else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                paused = true

            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && game.pieces.isEmpty()) {
                game.addRandomPiece()
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && game.pieces.isNotEmpty()) {
                game.pieces.first().changeState(game.statics, 1)
                piece_staticify_delay.currentTime = (piece_staticify_delay.currentTime - 0.0f)
                    .coerceAtLeast(-0f)
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.X) && game.pieces.isNotEmpty()) {
                game.pieces.first().changeState(game.statics, -1)
                piece_staticify_delay.currentTime = (piece_staticify_delay.currentTime - 0.04f) // add a bit of grace period
                    .coerceAtLeast(-0f)
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && game.pieces.isNotEmpty()) {
                if (movement_timer.surpassed()) {
                    game.pieces.first().moveRight(game)
                    movement_timer.reset()
                }

                //            piece_staticify_delay.currentTime = (piece_staticify_delay.currentTime-0.5f).coerceAtLeast(-.5f)
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && game.pieces.isNotEmpty()) {
                if (movement_timer.surpassed()) {
                    game.pieces.first().moveLeft(game)
                    movement_timer.reset()
                }//            piece_staticify_delay.currentTime = (piece_staticify_delay.currentTime-0.5f).coerceAtLeast(-.5f)
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && game.pieces.isNotEmpty()) {
                tick_timer.end = tick_delay * 1 / drop_down_multiplier
                piece_staticify_delay.currentTime += piece_staticify_delay.end / 2
            } else {
                tick_timer.end = tick_delay
            }


            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                tick_timer.currentTime = -1f
            }
        }
    }

}

fun draw(screen: MainScreen, delta: Float)
{
    screen.apply{

        viewport.camera.update()
        viewport.apply()
        renderer.projectionMatrix = viewport.camera.combined
        batch.projectionMatrix = viewport.camera.combined

        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.DARK_GRAY
        renderer.rect(0f, 0f, game.width*1f, game.height*1f)
        renderer.end()

        batch.begin()
        for(row in game.statics.grid.indices)
        {
            for(col in game.statics.grid[row].indices)
            {
                if(game.statics.grid[row][col]){
                    batch.color = Color.YELLOW
                    batch.draw(tile_2, col*1f, (row reverseIndex 20 )*1f, 1f, 1f)

                }
            }
        }
        for(piece in game.pieces)
        {
            renderer.color = Color.RED
            for(row in piece.rows.intIndices())
            {
                for(col in piece.cols.intIndices())
                {
                    if(piece.inner.grid[row][col])
                    {
                        batch.color = Color.RED
                        batch.draw(tile_1, piece.col + col*1f, (piece.row + row reverseIndex 20)*1f, 1f, 1f)
                    }
                }
            }

        }
        batch.end()

    }
}

fun updateTimers(screen: MainScreen, delta: Float) {
    screen.apply {
        if (!paused) {

            tick_timer.update(delta)
            piece_staticify_delay.update(delta)
            movement_timer.update(delta)
            spawn_piece_timer.update(delta)
            if (game.pieces.isNotEmpty()) spawn_piece_timer.reset()
        }
    }
}
