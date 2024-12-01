package page.j5155.expressway.ftc

/**
 * This is an OpMode wrapper that lets you use a GamepadActionRunner (and its associated features) without dispersing
 * casts throughout your code.
 * @param runner a GamepadActionRunner (you don't need to override this, it handles that automatically)
 */
abstract class GamepadActionLinearOpMode(override val runner: GamepadActionRunner = GamepadActionRunner()): ActionLinearOpMode()