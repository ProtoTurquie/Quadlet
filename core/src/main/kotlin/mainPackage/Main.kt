package mainPackage

import com.badlogic.gdx.Game

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Main : Game() {
    override fun create() {
        var a = MainScreen()
        centralScreen = a
        setScreen(a)
    }
}