package page.j5155.expressway.ftc

import com.acmerobotics.roadrunner.Action
import com.qualcomm.robotcore.hardware.Gamepad
import kotlin.math.abs

/**
 * This is a wrapper class to map gamepad buttons to actions upon various events.
 *
 * @param gamepad the Qualcomm gamepad to observe. It should be `gamepad1` or `gamepad2`, unless you're doing something funky.
 */
class GamepadEx(gamepad: Gamepad) {
    val a = Button { gamepad.a }
    val b = Button { gamepad.b }
    val x = Button { gamepad.x }
    val y = Button { gamepad.y }

    val dpadUp = Button { gamepad.dpad_up }
    val dpadDown = Button { gamepad.dpad_down }
    val dpadLeft = Button { gamepad.dpad_left }
    val dpadRight = Button { gamepad.dpad_right }

    val leftBumper = Button { gamepad.left_bumper }
    val rightBumper = Button { gamepad.right_bumper }

    val leftTrigger = Trigger { gamepad.left_trigger }
    val rightTrigger = Trigger { gamepad.right_trigger }

    val leftStick = JoyStick ({ gamepad.left_stick_x }, { gamepad.left_stick_y }, Button { gamepad.left_stick_button })
    val rightStick = JoyStick ({ gamepad.right_stick_x }, { gamepad.right_stick_y }, Button { gamepad.right_stick_button })

    val controls = listOf(a, b, x, y, dpadUp, dpadDown, dpadLeft, dpadRight, leftBumper, rightBumper,
        leftTrigger, rightTrigger, leftStick, rightStick)

    fun setTriggerThresholds(threshold: Float) {
        leftTrigger.threshold = threshold
        rightTrigger.threshold = threshold
    }

    fun setJoyStickThresholds(threshold: Float) {
        leftStick.deadzone = threshold
        rightStick.deadzone = threshold
    }

    fun update(): Array<Action> {
        val actionsArray = mutableListOf<Action>()

        controls.forEach {
            actionsArray.addAll(it.update())
        }

        return actionsArray.toTypedArray()
    }

    /**
     * Wrapper for an individual button on a gamepad.
     * @param name the (human-readable) name of the button
     * @param controlToWatch a lambda for the control to watch. This will be re-invoked every time update is called, so
     *          that it will automatically match the gamepad's value
     */
    inner class Button(private val controlToWatch: () -> Boolean):
        Control {
        /**
         * Whether this button is currently pushed down
         */
        @JvmField
        var down = false

        /**
         * Whether this button was pressed between the last call to update and this one
         */
        @JvmField
        var justPressed = false

        /**
         * Whether this button was released between the last call to update and this one
         */
        @JvmField
        var justReleased = false

        /**
         * Map an action to the onPress event. It will be run once the button is pressed
         */
        var onPressActionMap: (() -> Action)? = null

        /**
         * Map an action to the onRelease event. It will be run once the button is released
         */
        var onReleaseActionMap: (() -> Action)? = null

        /**
         * This one is a little bit special. It runs the action EVERY TIME the button is pressed (and update gets
         * called).
         */
        var heldActionMap: (() -> Action)? = null

        /**
         * Updates the various values, and adds mapped actions if applicable
         */
        override fun update(): List<Action> {

            val actionList = mutableListOf<Action>()

            justPressed = controlToWatch.invoke() && !down
            justReleased = !controlToWatch.invoke() && down
            down = controlToWatch.invoke()

            // If there are mapped actions, request to run them
            if (justPressed && onPressActionMap != null) {
                actionList += onPressActionMap!!.invoke()
            }
            if (justReleased && onReleaseActionMap != null) {
                actionList += onReleaseActionMap!!.invoke()
            }

            if (down && heldActionMap != null) {
                actionList += heldActionMap!!.invoke()
            }

            return actionList
        }
    }

    /**
     * Wrapper for an individual trigger on a gamepad.
     * @param name the (human-readable) name of the trigger
     * @param controlToWatch a lambda for the control to watch. This will be re-invoked every time update is called, so
     *          that it will automatically match the gamepad's value
     */
    inner class Trigger(private val controlToWatch: () -> Float):
        Control {
        /**
         * How far off of default the trigger should move before it is considered "down"
         */
        var threshold: Float = 0f

        /**
         * Whether this button is currently pushed down
         */
        val down: Boolean
            get() = amount > threshold

        /**
         * Whether this button was pressed between the last call to update and this one
         */
        @JvmField
        var justPressed = false

        /**
         * Whether this button was released between the last call to update and this one
         */
        @JvmField
        var justReleased = false

        /**
         * The amount the trigger is pressed down
         */
        @JvmField
        var amount = 0f

        /**
         * Map an action to the onPress event. It will be run once the button is pressed
         */
        var onPressActionMap: (() -> Action)? = null

        /**
         * Map an action to the onRelease event. It will be run once the button is released
         */
        var onReleaseActionMap: (() -> Action)? = null

        /**
         * This one is a little bit special. It runs the action EVERY TIME the button is pressed (and update gets
         * called).
         */
        var heldActionMap: (() -> Action)? = null

        /**
         * Updates the various values, and adds mapped actions if applicable
         */
        override fun update(): List<Action> {
            val actionList = mutableListOf<Action>()
            justPressed = controlToWatch.invoke() > threshold && !down
            justReleased = controlToWatch.invoke() <= threshold && down
            amount = controlToWatch.invoke()

            // If there are mapped actions, request to run them
            if (justPressed && onPressActionMap != null) {
                actionList += onPressActionMap!!.invoke()
            }
            if (justReleased && onReleaseActionMap != null) {
                actionList += onReleaseActionMap!!.invoke()
            }

            if (down && heldActionMap != null) {
                actionList += heldActionMap!!.invoke()
            }

            return actionList
        }
    }

    /**
     * Wrapper for an individual joystick on a gamepad
     * @param name the (human-readable) name of the joystick
     * @param xToWatch the x value to watch
     * @param yToWatch the y value to watch
     * @param button the button to watch
     */
    inner class JoyStick(private val xToWatch: () -> Float, private val yToWatch: () -> Float, val button: Button):
        Control {
        /**
         * How much of the joystick range to round to 0
         * Compensates for slight inaccuracy and miscentering of joystick
         */
        var deadzone: Float = 0f

        /**
         * Whether the joystick is out of the deadzone
         */
        val moved: Boolean
            get() = abs(x) > deadzone || abs(y) > deadzone

        /**
         * Whether the joystick just exited the deadzone
         * or in other words, was just moved
         */
        @JvmField
        var justMoved = false

        /**
         * Whether the joystick just returned to the deadzone
         * or in other words, was just released
         */
        @JvmField
        var justCentered = false

        /**
         * The current x-value of the joystick
         */
        @JvmField
        var x = 0.0f

        /**
         * The current y-value of the joystick
         */
        @JvmField
        var y = 0.0f

        /**
         * Map an action to run when the joystick first moves off center
         */
        var onMoveActionMap: (() -> Action)? = null

        /**
         * Map an action to run when the joystick first moves back to the center position
         */
        var onCenterActionMap: (() -> Action)? = null

        /**
         * Map an action to run whenever the joystick is off of the center position
         */
        var offCenterActionMap: (() -> Action)? = null

        /**
         * Updates the various values, and adds mapped actions if applicable
         */
        override fun update(): List<Action> {
            val actionList = mutableListOf<Action>()

            button.update()

            justMoved = (abs(xToWatch.invoke()) > deadzone || abs(yToWatch.invoke()) > deadzone) && !moved
            justCentered = (abs(xToWatch.invoke()) <= deadzone && abs(yToWatch.invoke()) <= deadzone) && moved

            x = xToWatch.invoke()
            y = yToWatch.invoke()

            // If there are mapped actions, request to run them
            if (justMoved && onMoveActionMap != null) {
                actionList += onMoveActionMap!!.invoke()
            }
            if (justCentered && onCenterActionMap != null) {
                actionList += onCenterActionMap!!.invoke()
            }

            if (moved && offCenterActionMap != null) {
                actionList += offCenterActionMap!!.invoke()
            }

            return actionList
        }
    }

    interface Control {
        /**
         * Updates the various values, and adds mapped actions if applicable
         */
        fun update(): List<Action>
    }

}