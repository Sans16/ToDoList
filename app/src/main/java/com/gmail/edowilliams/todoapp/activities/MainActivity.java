package com.gmail.edowilliams.todoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.edowilliams.todoapp.R;
import com.gmail.edowilliams.todoapp.adapters.TodoItemAdapter;
import com.gmail.edowilliams.todoapp.fragments.EditItemFragment;
import com.gmail.edowilliams.todoapp.models.TodoItem;
import com.gmail.edowilliams.todoapp.utils.ItemClickSupport;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainActivity extends AppCompatActivity implements EditItemFragment.EditItemFragmentListener,TodoItemAdapter.Listener {

    private List<TodoItem> mTodoItems;
    private RecyclerView.Adapter mTodoItemsAdapter;
    private RecyclerView mRecyclerView;
    FirebaseFirestore db;
    FirebaseUser currentUser;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.features, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,AuthenticationPage.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            currentUser = (FirebaseUser) bundle.get("currentUser");
        }

        mTodoItems = TodoItem.getAll();
        mRecyclerView = (RecyclerView) findViewById(R.id.listViewItems);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());
        mTodoItemsAdapter = new TodoItemAdapter(this, mTodoItems,this);
        mRecyclerView.setAdapter(mTodoItemsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupListViewListener();
    }

    public void onAddItem(View view) {
        EditText editTextNewItem = (EditText) findViewById(R.id.editTextNewItem);
        String itemText = editTextNewItem.getText().toString();

        if (!itemText.isEmpty()) {
            TodoItem todoItem = new TodoItem(itemText);
            todoItem.save();
           // firebaseSave(todoItem);
            mTodoItems.add(todoItem);
            int newPosition = mTodoItems.size() - 1;
            mTodoItemsAdapter.notifyItemInserted(newPosition);
            mRecyclerView.scrollToPosition(mTodoItemsAdapter.getItemCount() - 1);
            editTextNewItem.setText("");
        }
    }

    private void setupListViewListener() {

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                TodoItem todoItem = getItem(position);
                EditItemFragment editItemFragment = EditItemFragment.newInstance(todoItem.getTitle(), position);
                editItemFragment.show(fragmentManager, "fragment_edit_item");
            }
        });

//        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
//                TodoItem todoItem = getItem(position);
//
//                todoItem.delete();
//                mTodoItems.remove(position);
//
//                mTodoItemsAdapter.notifyItemRemoved(position);
//                return true;
//            }
//        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TodoItem todoItem = getItem(position);

                todoItem.delete();
                mTodoItems.remove(position);

                mTodoItemsAdapter.notifyItemRemoved(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onFinishEditDialog(String newItemText, int itemPosition) {
        saveEdit(itemPosition, newItemText);
        Toast.makeText(this, "Edit saved", Toast.LENGTH_SHORT).show();
    }

    private TodoItem getItem(int position) {
        return mTodoItems.get(position);
    }

    private void saveEdit(int itemPosition, String newTitle) {
        TodoItem todoItem = getItem(itemPosition);
        todoItem.setTitle(newTitle);
        //firebaseSave(todoItem);
        mTodoItemsAdapter.notifyItemChanged(itemPosition);
    }

    @Override
    public void deleteItem(int position) {
//        TodoItem todoItem = getItem(position);
//        todoItem.delete();
//        mTodoItems.remove(position);
//        mTodoItemsAdapter.notifyItemRemoved(position);
    }

//    public void firebaseSave(TodoItem todoItem){
//        Map<String, Object> item = new HashMap<>();
//        item.put("title", todoItem.getTitle());
//
//        db.collection(currentUser.getEmail())
//                .add(item)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("TAG", "Error adding document", e);
//                    }
//                });
//    }

    // This is to get the data from firebase but i decided not to use it since all data are first obtained from the local database.
    public void firebaseRetrieve(){
        db.collection(currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}