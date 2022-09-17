package com.fgarcialainez.chatbotgpt3.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fgarcialainez.chatbotgpt3.Constants.SourceType
import com.fgarcialainez.chatbotgpt3.R
import com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel
import com.fgarcialainez.chatbotgpt3.models.MessagesListViewModelFactory
import com.fgarcialainez.chatbotgpt3.ui.activities.SettingsActivity
import com.fgarcialainez.chatbotgpt3.ui.adapters.MessagesListAdapter

abstract class BaseFragment(val sourceType: SourceType) : Fragment() {

    private lateinit var messagesListAdapter: MessagesListAdapter
    private lateinit var messagesListViewModel: com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get a reference to the root view
        val root = inflater.inflate(R.layout.fragment_base, container, false)

        // Find the view model store owner
        val owner = findNavController().getViewModelStoreOwner(R.id.mobile_navigation)

        // Create the view model
        messagesListViewModel =
            ViewModelProvider(owner, MessagesListViewModelFactory(sourceType, false)).get(
                getViewModelClass()
            )

        // Create the adapter
        messagesListAdapter =
            MessagesListAdapter(requireContext(), messagesListViewModel.messagesList)

        // Initialize the recycler view
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.adapter = messagesListAdapter

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        // Observe the model
        messagesListViewModel.messagesListLiveData.observe(
            requireActivity(),
            Observer { messagesList ->
                // Data bind the recycler view
                messagesListAdapter.notifyDataSetChanged()

                // Move focus on last message
                recyclerView.scrollToPosition(messagesList.size - 1)
            })

        // Send message action
        val editText: EditText = root.findViewById(R.id.editText)
        val sendMessageBtn: ImageButton = root.findViewById(R.id.sendMessageBtn)

        sendMessageBtn.setOnClickListener {
            // Get the message content
            val messageContent = editText.text.toString()

            if (messageContent.isNotEmpty()) {
                // Add the message to the view model
                messagesListViewModel.sendMessage(editText.text.toString())

                // Clear the edit text
                editText.setText("")

                // Hide the keyboard
                container?.hideKeyboard()
            }
        }

        return root
    }

    // Abstract method to return custom view models for each fragment
    abstract fun <T : com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel> getViewModelClass(): Class<T>

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable options menu in this fragment
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Get item id to handle item clicks
        val id = item.itemId

        // Handle item clicks
        if (id == R.id.action_restart) {
            // Show confirmation dialog
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(R.string.dialog_restart_conversation_title)
            builder.setMessage(R.string.dialog_restart_conversation_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_restart_conversation_yes) { _, _ ->
                    // Restart the conversation
                    messagesListViewModel.clearMessages()
                }
                .setNegativeButton(R.string.dialog_restart_conversation_no) { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
        else if (id == R.id.action_settings) {
            // Start the settings activity
            startActivity(Intent(requireActivity(), SettingsActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}