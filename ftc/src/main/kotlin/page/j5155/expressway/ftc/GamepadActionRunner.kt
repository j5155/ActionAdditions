package page.j5155.expressway.ftc

import com.qualcomm.robotcore.hardware.Gamepad

/**
 * This is an ActionRunner that runs actions automatically when certain buttons are pressed on the gamepads.
 */
class GamepadActionRunner: UniqueActionRunner() {
    /**
     * A GamepadEx reference used for mapping actions to buttons/joysticks/triggers
     */
    lateinit var gamepad1: GamepadEx

    /**
     * A GamepadEx reference used for mapping actions to buttons/joysticks/triggers
     */
    lateinit var gamepad2: GamepadEx

    /**
     * This function initializes the gamepads. It MUST be called prior to the first time update() is called. Ideally is
     * called as the first thing in the OpMode.
     */
    fun initialize(gamepad1: Gamepad, gamepad2: Gamepad) {
        this.gamepad1 = GamepadEx(gamepad1)
        this.gamepad2 = GamepadEx(gamepad2)
    }

    /**
     * This function should be called every loop, as it handles updating the gamepads and running their mapped actions
     */
    fun update() {
        gamepad1.update().forEach {
            runAsync(it)
        }
        gamepad2.update().forEach {
            runAsync(it)
        }
    }
}