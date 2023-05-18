package com.comunidadedevspace.taskbeats.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.comunidadedevspace.taskbeats.R
import com.comunidadedevspace.taskbeats.data.Task
import com.google.android.material.snackbar.Snackbar

class TaskDetailActivity : AppCompatActivity() {

    private var task: Task? = null
    companion object {
        const val TASK_DETAIL_EXTRA = "task.detail.extra"

        fun start(context: Context, task: Task?) : Intent {
            val intent = Intent(context, TaskDetailActivity::class.java).apply {
                putExtra(TASK_DETAIL_EXTRA, task)
            }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        task = intent.getSerializableExtra(TASK_DETAIL_EXTRA) as Task?

        val edtTaskTitle: EditText = findViewById(R.id.edt_task_title)
        val edtTaskDescription: EditText = findViewById(R.id.edt_task_description)
        val buttonAdd: Button = findViewById(R.id.button_add_task)

        if(task != null) {
            edtTaskTitle.setText(task!!.title)
            edtTaskDescription.setText(task!!.description)
        }

        buttonAdd.setOnClickListener {
            val title = edtTaskTitle.text.toString()
            val description = edtTaskDescription.text.toString()

            if(task == null) {
                addOrUpdateTask(0, title, description, ActionType.CREATE)
            }

            task?.let {
                addOrUpdateTask(it.id, title, description, ActionType.UPDATE)
            }
        }
    }

    private fun addOrUpdateTask(
        id: Int,
        title: String,
        description: String,
        actionType: ActionType
    ) {
        val task = Task(id, title, description)
        returnAction(task, actionType)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater : MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_task_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.delete_task -> {
                if(task != null) {
                    returnAction(task!!, ActionType.DELETE)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun returnAction(task: Task, actionType: ActionType) {
        val intent = Intent().apply {
            val taskAction = TaskAction(task, actionType.name)
            putExtra(TASK_ACTION_RESULT, taskAction)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }
}