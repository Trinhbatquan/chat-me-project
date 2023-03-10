package com.trinhviet.chatme.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.trinhviet.chatme.R
import com.trinhviet.chatme.adapter.RecyclerItemTouchHelper
import com.trinhviet.chatme.adapter.SearchUserAdapter
import com.trinhviet.chatme.databinding.ActivitySearchBinding
import com.trinhviet.chatme.viewModel.User
import java.util.ArrayList

class SearchActivity : AppCompatActivity() {

    private lateinit var searchUserAdapter: SearchUserAdapter
    lateinit var binding: ActivitySearchBinding

    lateinit var users: List<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        binding.searchList.setHasFixedSize(true)
        binding.searchList.layoutManager = LinearLayoutManager(applicationContext) //context

        users = ArrayList()
        retrieveAllUser()

         binding.search.addTextChangedListener(object : TextWatcher {
             override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

             }

             override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                 searchForUser(s.toString().toLowerCase())
             }

             override fun afterTextChanged(s: Editable?) {

             }
         })
    }

    private fun retrieveAllUser() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser.uid

        val referenceUser = FirebaseDatabase.getInstance().reference.child("User")

        referenceUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (users as ArrayList<User>).clear()
                if (binding.search.text.toString().isEmpty()) {
                    for (snapshot in p0.children) {
                        val user: User? = snapshot.getValue(User::class.java)
                        if (!(user!!.getUid()).equals(firebaseUserID)) {
                            (users as ArrayList<User>).add(user)
                        }
                    }

                    searchUserAdapter = SearchUserAdapter(applicationContext, users, false) // khong chac context
                    val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(searchUserAdapter, this@SearchActivity))
                    itemTouchHelper.attachToRecyclerView(binding.searchList)
                    binding.searchList.adapter = searchUserAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun searchForUser(str: String) {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser.uid
        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("User").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (users as ArrayList<User>).clear()

                for(snapshot in p0.children) {
                    val user: User? = snapshot.getValue(User::class.java)
                    if (!(user!!.getUid()).equals(firebaseUserID)) {
                        (users as ArrayList<User>).add(user)
                    }
                }
                searchUserAdapter = SearchUserAdapter(applicationContext, users, false) // khong chac context

                val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(searchUserAdapter, this@SearchActivity))
                itemTouchHelper.attachToRecyclerView(binding.searchList)

                binding.searchList.adapter = searchUserAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun navigateToChatActivity(user: User) {
        val intent = Intent(this@SearchActivity, SingleChatLogActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("user", user)
        startActivity(intent)
        finish()
    }

    fun navigateToInfoActivity(user: User) {
        val intent = Intent(this@SearchActivity, InfoActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("user", user)
        startActivity(intent)
        finish()
    }
}