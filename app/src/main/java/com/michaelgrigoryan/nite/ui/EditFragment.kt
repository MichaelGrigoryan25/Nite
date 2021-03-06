package com.michaelgrigoryan.nite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.michaelgrigoryan.nite.R
import com.michaelgrigoryan.nite.database.Database
import com.michaelgrigoryan.nite.models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment() {
    private val args by navArgs<EditFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_edit, container, false)

        val deleteButton: ImageView = view.findViewById(R.id.delete_note_button)
        val contentInput: TextInputEditText = view.findViewById(R.id.input_content)
        val saveButton: FloatingActionButton = view.findViewById(R.id.save_edited_note_button)

        contentInput.setText(args.noteToBeUpdated.note)
        contentInput.doOnTextChanged { text, _, _, _ ->
            if (text!!.isEmpty()) saveButton.hide()
            else saveButton.show()
        }

        deleteButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val db = Database.setup(requireContext()).noteDao()

                db.deleteNote(Note(
                    args.noteToBeUpdated.id,
                    args.noteToBeUpdated.note,
                    args.noteToBeUpdated.time
                ))

                withContext(Dispatchers.Main) {
                    view.findNavController().navigate(R.id.action_editFragment_to_homeFragment)
                }
            }
        }

        deleteButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val db = Database.setup(requireContext()).noteDao()
                db.deleteNote(
                        Note(
                                args.noteToBeUpdated.id,
                                args.noteToBeUpdated.note,
                                args.noteToBeUpdated.time
                        )
                )

                withContext(Dispatchers.Main) {
                    view.findNavController().popBackStack()
                    view.clearFocus()
                }
            }
        }

        saveButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val db = Database.setup(requireContext()).noteDao()
                val updatedNote = Note(
                    args.noteToBeUpdated.id,
                    contentInput.text.toString(),
                    SimpleDateFormat("MMMM dd hh:mm", Locale.getDefault()).format(Date())
                )
                db.updateNote(updatedNote)

                withContext(Dispatchers.Main) {
                    view.findNavController().popBackStack()
                }
            }
        }

        return view
    }
}
