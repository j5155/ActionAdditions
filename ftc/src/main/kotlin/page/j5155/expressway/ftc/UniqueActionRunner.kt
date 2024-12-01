package page.j5155.expressway.ftc

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.qualcomm.robotcore.util.RobotLog.a
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

open class UniqueActionRunner: ActionRunner() {
    private val uniqueActionsQueue = ArrayList<UniqueAction>()


    override fun updateAsync(packet: TelemetryPacket) {
        updateUniqueQueue()
        super.updateAsync(packet)
    }

    private fun updateUniqueQueue() {
        val oldActions = uniqueActionsQueue
        uniqueActionsQueue.clear()
        // running run on a UniqueAction will automatically re add it to the queue, or start running it
        oldActions.forEach { this.runAsync(it) }
    }

    override fun runAsync(a: Action) {
        if (duplicated(a)) {
            uniqueActionsQueue.add(a)
        } else {
            super.runAsync(a)
        }
    }

    fun runNoQueue(a: Action) {
        if (!duplicated(a)) {
            runningActions.add(a)
        }
    }

    @OptIn(ExperimentalContracts::class)
    fun duplicated(a: Action): Boolean {
        contract {
            // this allows the other function to add it to the uniqueActionsQueue without casting
            returns(true) implies (a is UniqueAction)
        }
        return a is UniqueAction && runningActions.stream().anyMatch {
            it is UniqueAction && it.key == a.key
        }
    }


    class UniqueAction(val action: Action, val key: String = "UniqueAction") : Action by action
}