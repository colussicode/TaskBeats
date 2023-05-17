package com.comunidadedevspace.taskbeats.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.comunidadedevspace.taskbeats.R
import com.comunidadedevspace.taskbeats.data.Task

class TaskListAdapter(
    private val openTaskDetailScreen : (Task) -> Unit
) : ListAdapter<Task, TaskListViewHolder>(TaskListAdapter) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_task, parent, false)

        return TaskListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, openTaskDetailScreen)
    }

    companion object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.title == newItem.title && oldItem.description == newItem.description
        }

    }
}

class TaskListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val textViewTitle : TextView = view.findViewById(R.id.textview_task_title)
    private val textViewDescription  : TextView = view.findViewById(R.id.textview_task_description)
    private val linearLayoutDetailActivity: LinearLayout = view.findViewById(R.id.linear_detail_activity)

    fun bind(
        task: Task,
        openTaskDetailScreen: (Task) -> Unit
    ) {
        textViewTitle.text = task.title
        textViewDescription.text = task.description
        linearLayoutDetailActivity.setOnClickListener {
            openTaskDetailScreen(task)
        }
    }
}