package com.putri.post8

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.putri.post8.databinding.UploadDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskDialog(
    private val context: Context,
    private val tasksRef: DatabaseReference
) {

    fun show(taskToEdit: Task? = null, onSaved: (() -> Unit)? = null) {

        val binding = UploadDialogBinding.inflate(LayoutInflater.from(context))

        // Jika Edit task
        if (taskToEdit != null) {
            binding.editTextTaskTitle.setText(taskToEdit.title)
            binding.editTextTaskDescription.setText(taskToEdit.description)
            binding.editTextTaskDeadline.setText(taskToEdit.deadline)
        }

        // === FIX: Buat date picker bisa muncul dari 2 tempat ===
        binding.editTextTaskDeadline.setOnClickListener {
            openDatePicker(binding)
        }

        binding.textInputDeadline.setEndIconOnClickListener {
            openDatePicker(binding)
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(if (taskToEdit == null) "Tambah Tugas Baru" else "Edit Tugas")
            .setView(binding.root)
            .setPositiveButton("Simpan") { _, _ ->

                val title = binding.editTextTaskTitle.text.toString().trim()
                val desc = binding.editTextTaskDescription.text.toString().trim()
                val ddl = binding.editTextTaskDeadline.text.toString().trim()

                if (title.isEmpty() || ddl.isEmpty()) {
                    Toast.makeText(context, "Judul dan Deadline wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (taskToEdit == null) {
                    // Menambahkan task
                    val id = tasksRef.push().key
                    val newTask = Task(id, title, desc, ddl, false)
                    tasksRef.child(id!!).setValue(newTask)
                        .addOnSuccessListener { onSaved?.invoke() }
                } else {
                    // Mengedit task
                    val updated = taskToEdit.copy(
                        title = title,
                        description = desc,
                        deadline = ddl
                    )
                    tasksRef.child(taskToEdit.id!!).setValue(updated)
                        .addOnSuccessListener { onSaved?.invoke() }
                }
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // === FIX: Fungsi DatePicker terpisah ===
    private fun openDatePicker(binding: UploadDialogBinding) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            context,
            { _, y, m, d ->
                val df = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                val selected = Calendar.getInstance().apply { set(y, m, d) }.time
                binding.editTextTaskDeadline.setText(df.format(selected))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
}
