package org.isoron.uhabits.activities.habits.edit

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.util.Log
import org.isoron.androidbase.activities.ActivityContextModule
import org.isoron.androidbase.activities.BaseActivity
import org.isoron.androidbase.activities.BaseActivityModule
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.activities.DaggerHabitsActivityComponent
import org.isoron.uhabits.activities.HabitModule
import org.isoron.uhabits.activities.habits.list.views.CheckmarkPanelViewFactory
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.utils.getActiveColor
import javax.inject.Inject

class AddHabitRepetitionDialog: AppCompatDialogFragment() {

    @Inject internal lateinit var checkmarkPanelViewFactory: CheckmarkPanelViewFactory

    private lateinit var habit: Habit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val appComponent = (requireActivity().applicationContext as HabitsApplication).component

        val habitId = checkNotNull(arguments).getLong(HABIT_ID, -1)
        val habitList = appComponent.habitList
        habit = checkNotNull(habitList.getById(habitId)) {
            "error getting habit for repetition from habit list. habit id: $habitId"
        }

        //todo: make header view and check panel into one view

        val activityComponent = DaggerHabitsActivityComponent
                .builder()
                .activityContextModule(ActivityContextModule(requireContext()))
                .baseActivityModule(BaseActivityModule(requireActivity() as BaseActivity))
                .habitModule(HabitModule(habit))
                .habitsApplicationComponent(appComponent)
                .build()
        activityComponent.inject(this)

        val cache = appComponent.habitCardListCache

        val view = checkmarkPanelViewFactory.create().apply {
            val checkmarks = cache.getCheckmarks(habitId)
            color = getActiveColor(habit)
            values = checkmarks
            buttonCount = 5 //todo: make this dynamic
            onToggle = {
                Log.i("AddHabitDialog", it.toString())
            }
        }

        return AlertDialog.Builder(requireContext())
                .setView(view)
                .create()
    }

    companion object {
        private const val HABIT_ID: String = "habit_id"

        @JvmStatic fun newInstance(habitId: Long) = AddHabitRepetitionDialog().apply {
            arguments = Bundle().apply {
                putLong(HABIT_ID, habitId)
            }
        }
    }
}