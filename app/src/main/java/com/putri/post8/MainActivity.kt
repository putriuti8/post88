package com.putri.post8

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.putri.post8.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), TaskAdapter.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tasksRef: DatabaseReference
    private val tasks = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tasksRef = FirebaseDatabase.getInstance().getReference("tasks")

        adapter = TaskAdapter(tasks, this)
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter

        binding.fabAddTasks.setOnClickListener {
            AddTaskDialog(this, tasksRef).show(null) {
                snackbar("Tugas berhasil ditambahkan")
            }
        }

        fetchTasks()
    }

    private fun fetchTasks() {
        tasksRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                tasks.clear()

                // Ambil semua tugas
                for (child in snapshot.children) {
                    val task = child.getValue(Task::class.java)
                    val fixed = task?.copy(id = child.key) // pastikan ID ikut
                    if (fixed != null) tasks.add(fixed)
                }

                tasks.sortBy { it.completed }

                adapter.notifyDataSetChanged()

                // Atur empty state
                if (tasks.isEmpty()) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.rvTasks.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvTasks.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun snackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    // Listener dari adapter
    override fun onEdit(task: Task) {
        AddTaskDialog(this, tasksRef).show(task) {
            snackbar("Tugas berhasil diperbarui")
        }
    }

    override fun onDelete(task: Task) {
        tasksRef.child(task.id!!).removeValue().addOnSuccessListener {
            snackbar("Tugas dihapus")
        }
    }

    override fun onToggle(task: Task, isCompleted: Boolean) {
        val updated = task.copy(completed = isCompleted)
        tasksRef.child(task.id!!).setValue(updated)

        snackbar(
            if (isCompleted) "Tugas ditandai selesai"
            else "Tugas ditandai belum selesai"
        )
    }
}
