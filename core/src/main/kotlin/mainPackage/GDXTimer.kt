package mainPackage

import kotlin.math.max

/**
 * @param clampAtEnd basically tells the code to not loop back to 0 or do weird stuff it the tick_timer has rung, however it will cause problems with proper tracking of time as it will be clamped to a max.
 * needs to be manually activated after initialisation
 * TODO: add a onDisable_call ()->Unit
 */
data class GDXTimer(var startAt: Float = 0f, var end : Float,
                    var action: (()->Any?)?, var loop:Boolean = false,
                    var decrementWhileDisabled: Boolean = false,
                    var clampAtEnd: Boolean = false, var countDown: Boolean = false, var currentTime : Float = startAt)
{
    var decrementCoefficient = 1f
    init {
        if(countDown) currentTime = startAt
    }
    /**
     * get Progress in 0.0 to 1.0
     */
    fun getProgress() : Float
    {
        return if(!countDown)currentTime/end else 1-(currentTime/startAt)
    }
    var active = false
    var overflowing : Boolean = false
    var deActivateOnOverflow = clampAtEnd // this thing is dangerous, this is like passive clamping
    fun update(delta: Float): GDXTimer
    {
        if(!active && !decrementWhileDisabled) return this
        if(!countDown)
        {
            if (!active && decrementWhileDisabled) {
                currentTime -= delta * decrementCoefficient
                currentTime = max(0f, currentTime)
                return this
            }
            currentTime += delta
            if (currentTime >= end) {
                if (clampAtEnd && currentTime >= end) {
                    currentTime = end // to avoid occurrences where the program might think this is the normal case
                    return this
                }
                if (!loop && deActivateOnOverflow) {
                    active = false
                }
                else if(loop) currentTime %= end

                action?.invoke() // could be changed to repeat((currentTime / end).ToInt())
            }
        }
        else
        {
            // startAt > end
            if (!active && decrementWhileDisabled) {
                currentTime += delta * decrementCoefficient
                currentTime = currentTime.coerceAtMost(startAt)
                return this
            }
            currentTime -= delta
            if (currentTime <= end) {
                if (clampAtEnd && currentTime <= end) {
                    currentTime = startAt // to avoid occurrences where the program might think this is the normal case
                    return this
                }

                if (!loop && deActivateOnOverflow) {
                    active = false
                    currentTime = end
                }
                else if(loop) currentTime =  startAt - ( startAt % currentTime+0.01f)
                action?.invoke() // could be changed to repeat((currentTime / end).ToInt())
            }
        }
        return this
    }
    fun surpassed() : Boolean
    {
        require(!clampAtEnd && !countDown)
        return currentTime >= end
    }
    fun reset():GDXTimer
    {
        currentTime = startAt
        return this
    }
    fun decrease(amount: Float)
    {
        require(!countDown)
        if(currentTime - amount < startAt) currentTime = startAt
        else currentTime -= amount
    }
    fun increase(amount: Float)
    {
        require(!countDown)
        if(currentTime + amount > end) currentTime = end
        else currentTime += amount
    }
    fun activate():GDXTimer{active = true; return this}
    fun disable():GDXTimer{active = false; return this}
}
