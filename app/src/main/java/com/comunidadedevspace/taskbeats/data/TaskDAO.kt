package com.comunidadedevspace.taskbeats.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tasK: Task)

    @Query("SELECT * FROM task")
    fun getAllTasks() : LiveData<List<Task>>

    @Update
    fun updateTask(task: Task)

    @Query("DELETE from task")
    fun deleteAllTasks()

    @Query("DELETE from task WHERE id = :id")
    fun deleteTaskById(id: Int)
}