package com.gabriel.motionapp.gesture.use_cases

import android.util.Log
import com.gabriel.motionapp.gesture.use_cases.gestures.GestureEnum


// Tolerance is the number of times each gesture must be detected in a row to trigger the action
class TriggerActionOnGestureUseCase(val tolerance: Int) {
    val actions = mutableMapOf<GestureEnum, () -> Unit>()
    var lastTriggeredActions = mutableListOf<GestureEnum>()

    companion object {
        const val TAG = "RegisterGestureActionUseCase"
    }

    fun registerAction(gesture: GestureEnum, action: () -> Unit): TriggerActionOnGestureUseCase {
        actions[gesture] = action
        return this
    }

    fun execute(gesture: GestureEnum) {
        if (!actions.containsKey(gesture)) {
            Log.w(TAG, "Gesture not registered: $gesture")
        }

        // Registering the action
        lastTriggeredActions.add(gesture)

        // If reach the tolerance, trigger the action
        if (lastTriggeredActions.count { it == gesture } >= tolerance) {
            actions[gesture]?.invoke()
            lastTriggeredActions = mutableListOf()
        }
    }
}