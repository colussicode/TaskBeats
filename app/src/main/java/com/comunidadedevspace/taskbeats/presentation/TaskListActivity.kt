package com.comunidadedevspace.taskbeats.presentation

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.comunidadedevspace.taskbeats.R
import com.comunidadedevspace.taskbeats.data.AppDatabase
import com.comunidadedevspace.taskbeats.data.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "task_beats"
        )
            .build()
    }

    private val dao by lazy {
        database.taskDao()
    }

    private lateinit var ctnContent: LinearLayout

    private val taskListAdapter = TaskListAdapter(::onListItemClicked)

    private val resultActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val taskAction = result.data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction?

            when(taskAction!!.actionType) {
                ActionType.DELETE.name -> deleteTaskById(taskAction.task.id)
                ActionType.CREATE.name -> insertToDatabase(taskAction.task)
                ActionType.UPDATE.name -> updateTask(taskAction.task)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        listFromDatabase()

        val taskListRecyclerView: RecyclerView = findViewById(R.id.rv_task_list)
        val fab: FloatingActionButton = findViewById(R.id.fab_create_task)

        taskListRecyclerView.adapter = taskListAdapter

        taskListRecyclerView.layoutManager = LinearLayoutManager(this)

        ctnContent = findViewById(R.id.ctn_content)

        fab.setOnClickListener {
            openTaskListDetail()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_task_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_tasks -> {
                deleteAllTasks()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteTaskById(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteTaskById(id)
            listFromDatabase()
        }
    }

    private fun deleteAllTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteAllTasks()
            listFromDatabase()
        }
    }

    private fun updateTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.updateTask(task)
            listFromDatabase()
        }
    }

    private fun insertToDatabase(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(task)
            listFromDatabase()
        }
    }

    private fun listFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val myDatabaseList = dao.getAllTasks()
            taskListAdapter.submitList(myDatabaseList)

//            with(Dispatchers.Main) {
//                if (myDatabaseList.isEmpty()) {
//                    ctnContent.visibility = View.VISIBLE
//                } else {
//                    ctnContent.visibility = View.GONE
//                }
//            }
        }
    }

    private fun onListItemClicked(task: Task) {
        openTaskListDetail(task)
    }

    private fun openTaskListDetail(task: Task? = null) {
        val intent = TaskDetailActivity.start(this, task)
        resultActivity.launch(intent)
    }

    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }
}

enum class ActionType {
    DELETE,
    UPDATE,
    CREATE
}

data class TaskAction(
    val task: Task,
    val actionType: String
) : Serializable

const val TASK_ACTION_RESULT = "TASK_ACTION_RESULT"