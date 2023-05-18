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
import com.comunidadedevspace.taskbeats.R
import com.comunidadedevspace.taskbeats.data.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private val viewModel : TaskListViewModel by lazy{
        TaskListViewModel.create(application)
    }

    private lateinit var ctnContent: LinearLayout

    private val taskListAdapter = TaskListAdapter(::onListItemClicked)

    private val resultActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK){
            val taskAction = result.data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction?

            viewModel.execute(taskAction!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
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
                deleteAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun listFromDatabase() {
        viewModel.taskListLiveData.observe(this@MainActivity) {
            if(it.isEmpty()) {
                ctnContent.visibility = View.VISIBLE
            } else {
                ctnContent.visibility = View.GONE
            }
            taskListAdapter.submitList(it)
        }
    }

    private fun onListItemClicked(task: Task) {
        openTaskListDetail(task)
    }

    private fun openTaskListDetail(task: Task? = null) {
        val intent = TaskDetailActivity.start(this, task)
        resultActivity.launch(intent)
    }

    private fun deleteAll() {
        val taskAction = TaskAction(null, ActionType.DELETEALL.name)
        viewModel.execute(taskAction)
    }
}

enum class ActionType {
    DELETE,
    UPDATE,
    CREATE,
    DELETEALL
}

data class TaskAction(
    val task: Task?,
    val actionType: String
) : Serializable

const val TASK_ACTION_RESULT = "TASK_ACTION_RESULT"