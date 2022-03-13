package mainPackage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEmitter
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.Particle
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport


var clear_row_sound = Gdx.audio.newSound(Gdx.files.internal("clear_row.ogg")) // https://sfxr.me/
var happy_adventure = Gdx.audio.newMusic(Gdx.files.internal("happy_adveture.mp3"))
var die = Gdx.audio.newSound(Gdx.files.internal("die.ogg")) // https://sfxr.me/
// from https://opengameart.org/content/happy-adventure-loop
var uiViewport = FillViewport(400f, 400f)
var uiBatch = SpriteBatch()
var renderer = ShapeRenderer()
var batch = SpriteBatch()
var font = BitmapFont()
lateinit var centralScreen : MainScreen
var viewport = FitViewport(10f, 20f)

var gravX = 0; var gravY = 1
var tick_delay = 0.5f
var drop_down_multiplier = 8f
var movement_delay = 0.1f
var spawn_piece_delay = 0.2f
var paused = false
var dead = false
/*
PROBLEM, these are flipped in some direction, for instance the l is 4TALL and 1WIDE
THERE REALLY ISNT A STANDART IN THOSE KINDS OF THINGS SADLY
*/
var piecesList = mutableListOf(Pieces.L, Pieces.I, Pieces.S, Pieces.T, Pieces.Z, Pieces.O, Pieces.J)

enum class Pieces(val innerMap: MutableMap<Int, ()->Grid<Boolean>>)
{
    I(
        mutableMapOf
            (
            0 to {"""
                0000
                1111
                0000
                0000
            """.toGrid()},
            1 to {"""
                0010
                0010
                0010
                0010
                """.toGrid()},
            2 to {"""
                0000
                0000
                1111
                0000
            """.toGrid()},
            3 to {"""
                0100
                0100
                0100
                0100
            """.toGrid()}
            )),
    J(
        mutableMapOf
            (
            0 to {"""
                100
                111
                000
            """.toGrid()},
            1 to {"""
                011
                010
                010
                """.toGrid()},
            2 to {"""
                000
                111
                001
            """.toGrid()},
            3 to {"""
                010
                010
                110
                """.toGrid()}
            )),

    L(
        mutableMapOf
            (
            0 to {"""
                001
                111
                000
            """.toGrid()},
            1 to {"""
                010
                010
                011
                """.toGrid()},
            2 to {"""
                000
                111
                100
            """.toGrid()},
            3 to {"""
                110
                010
                010
                """.toGrid()}
            )),
    O(
        mutableMapOf(
            0 to { """
                11
                11
            """.toGrid()}
        )),
    S(
        mutableMapOf
            (
            0 to {"""
                011
                110
                000
            """.toGrid()},
            1 to {"""
                010
                011
                001
                """.toGrid()},
            2 to {"""
                000
                011
                110
            """.toGrid()},
            3 to {"""
                100
                110
                010
                """.toGrid()}
            )),
    T(
        mutableMapOf
            (
            0 to {"""
                010
                111
                000
            """.toGrid()},
            1 to {"""
                010
                011
                010
                """.toGrid()},
            2 to {"""
                000
                111
                010
            """.toGrid()},
            3 to {"""
                010
                110
                010
                """.toGrid()}
            )),
    Z(
        mutableMapOf
            (
            0 to {"""
                110
                011
                000
            """.toGrid()},
            1 to {"""
                001
                011
                010
                """.toGrid()},
            2 to {"""
                000
                110
                011
            """.toGrid()},
            3 to {"""
                010
                110
                100
                """.toGrid()}
    ));
    operator fun invoke() : MutableMap<Int, ()->Grid<Boolean>>
    {
        return this.innerMap
    }

}