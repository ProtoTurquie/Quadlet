package mainPackage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
// TODO: add did you know to the pause menu where u will get the random facts from a website
// TODO: try to integrate the "start scanning from" option of the getlatestCOLROW method into this game
//         i cant integrate it directly

class MainScreen : Screen
{
    var game = Quadlet(10, 20)
    var piece_staticify_delay = GDXTimer(0f, 0.3f, null, false, false,false )
    var tick_timer = GDXTimer(0f, tick_delay, {game.tick(piece_staticify_delay)}, true ).activate()
    var movement_timer = GDXTimer(0f, movement_delay, null, false, false, false ).activate()
    var spawn_piece_timer = GDXTimer(0f, spawn_piece_delay, {if(game.pieces.isEmpty()) game.addRandomPiece()}, false, false, false ).activate()
    var tile_1 = Texture("tile_3.png")
    var tile_2 = Texture("tile_3.png")
    override fun show() {
        println(Gdx.files.local("clear_row.wav").exists())
        happy_adventure.isLooping = true ; happy_adventure.play()

    }
    override fun render(delta: Float) {

        ScreenUtils.clear(Color.LIGHT_GRAY)

        draw(this, delta)

        updateTimers(this, delta)

        processInput(this, delta)


        uiViewport.apply()
        uiBatch.projectionMatrix = uiViewport.camera.combined
        uiBatch.begin()
        font.draw(uiBatch, """
            score: ${game.score}
            tickDelay: ${tick_timer.end}
            rowsCleared: ${game.rowsCleared}
        """.trimIndent(), 300f, 200f)
        if(paused)
            font.draw(uiBatch, "paused", 200f, 200f)
        uiBatch.end()
    }

    override fun dispose() {
        happy_adventure.dispose()
        clear_row_sound.dispose()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        uiViewport.update(width, height, true)
    }
    override fun hide() {
    }
}