package com.comunidadedevspace.taskbeats.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comunidadedevspace.taskbeats.TaskBeatsApplication
import com.comunidadedevspace.taskbeats.data.Task
import com.comunidadedevspace.taskbeats.data.TaskDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val taskDAO: TaskDAO
) : ViewModel() {

    val taskListLiveData: LiveData<List<Task>> = taskDAO.getAllTasks()

    fun execute(taskAction: TaskAction) {
        when(taskAction.actionType) {
            ActionType.CREATE.name -> insertToDatabase(taskAction.task!!)
            ActionType.DELETE.name -> deleteTaskById(taskAction.task!!.id)
            ActionType.UPDATE.name -> updateTask(taskAction.task!!)
            ActionType.DELETEALL.name -> deleteAllTasks()
        }
    }

    private fun deleteAllTasks() {
       viewModelScope.launch(Dispatchers.IO) {
            taskDAO.deleteAllTasks()
        }
    }

    private fun deleteTaskById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDAO.deleteTaskById(id)
        }
    }

    private fun insertToDatabase(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDAO.insert(task)
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDAO.updateTask(task)
        }
    }

    companion object {
        fun create(application: Application) : TaskListViewModel {
            val databaseInstance = (application as TaskBeatsApplication).getDatabase()
            val dao = databaseInstance.taskDao()
            return TaskListViewModel(dao)
        }
    }

}