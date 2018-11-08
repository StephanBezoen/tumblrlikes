package nl.acidcats.tumblrlikes.util

/**
 * Created on 08/11/2018.
 */
fun Float.clamp(min: Float, max: Float): Float = Math.max(min, Math.min(this, max))
