package com.putri.post8

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.putri.post8.databinding.ItemTaskBinding

class TaskAdapter(
    private val tasks: List<Task>,
    private val listener: Listener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    interface Listener {
        fun onEdit(task: Task)
        fun onDelete(task: Task)
        fun onToggle(task: Task, isCompleted: Boolean)
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.tvTaskTitle.text = task.title
            binding.tvTaskDescription.text = task.description
            binding.tvTaskDeadline.text = task.deadline
            binding.cbTaskComplete.isChecked = task.completed

            // Styling selesai
            if (task.completed) {
                binding.tvTaskTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvTaskTitle.alpha = 0.5f
            } else {
                binding.tvTaskTitle.paintFlags = 0
                binding.tvTaskTitle.alpha = 1f
            }

            binding.ivTaskDelete.setOnClickListener { listener.onDelete(task) }
            binding.root.setOnClickListener { listener.onEdit(task) }

            binding.cbTaskComplete.setOnCheckedChangeListener { _, isChecked ->
                if (binding.cbTaskComplete.isPressed)
                    listener.onToggle(task, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) = holder.bind(tasks[position])
    override fun getItemCount() = tasks.size
}
