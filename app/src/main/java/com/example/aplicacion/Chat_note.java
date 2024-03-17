package com.example.aplicacion;

import static com.example.aplicacion.Common.Common.CHAT_DETAIL_REFERENCE;
import static com.example.aplicacion.Common.Common.CHAT_LIST_REFERENCE;
import static com.example.aplicacion.Common.Common.NCHAT_DETAIL_REFERENCE;
import static com.example.aplicacion.Common.Common.NCHAT_LIST_REFERENCE;
import static com.example.aplicacion.Common.Common.chatUser;
import static com.example.aplicacion.Common.Common.generateChatRoomId;
import static com.example.aplicacion.Common.Common.getFileName;
import static com.example.aplicacion.Common.Common.getName;
import static com.example.aplicacion.Common.Common.users;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.aplicacion.Fragments.ChatFragment;
import com.example.aplicacion.Listener.IFirebaseLoadFailed;
import com.example.aplicacion.Listener.ILoadTimeFromFirebaseListener;
import com.example.aplicacion.Listener.NIFirebaseLoadFailed;
import com.example.aplicacion.Listener.NILoadTimeFromFirebaseListener;
import com.example.aplicacion.ViewHolders.ChatPictureHolder;
import com.example.aplicacion.ViewHolders.ChatPictureReceiveHolder;
import com.example.aplicacion.ViewHolders.ChatTextHolder;
import com.example.aplicacion.ViewHolders.ChatTextReceiveHolder;
import com.example.aplicacion.ViewHolders.NChatInfoHolder;
import com.example.aplicacion.ViewHolders.NChatTextHolder;
import com.example.aplicacion.ViewHolders.NChatTextReceiveHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Chat_note extends AppCompatActivity implements NILoadTimeFromFirebaseListener, NIFirebaseLoadFailed {

    FirebaseDatabase database;
    DatabaseReference chatRef,offsetRef;
    NILoadTimeFromFirebaseListener listenerN;
    NIFirebaseLoadFailed errorListenerN;
    LinearLayoutManager layoutManager;
    Uri fileUri;
    FirebaseRecyclerAdapter<ChatMessageModel, RecyclerView.ViewHolder> adapter;
    FirebaseRecyclerOptions<ChatMessageModel> options;

    @BindView(R.id.recycler_nchat)
    RecyclerView recycler_chat;
    @BindView(R.id.img_avatar)
    ImageView img_avatar;

    @BindView(R.id.txt_name)
    TextView txt_name;

    @BindView(R.id.ntoolbar)
    Toolbar toolbar;

    @BindView(R.id.barra_chat)
    AppCompatEditText edt_chat;

    @OnClick(R.id.img_flechita)
    void onSubmitChatClick()
    {


        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeInMs= System.currentTimeMillis()+offset;
                listenerN.onLooadOnlyTimeSuccess(estimatedServerTimeInMs);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorListenerN.onError(error.getMessage());
                Toast.makeText(Chat_note.this, "no le estas picando", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(adapter !=null)
        {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        if(adapter!=null)
        {
            adapter.stopListening();
        }
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
        {
            adapter.startListening();
        }

    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_note);


        initViews();
        loadChatContent();


    }

    private void initViews() {
        listenerN = this;
        errorListenerN = this;
        database = FirebaseDatabase.getInstance();
        chatRef = database.getReference("NChat"); //chat reference

        offsetRef = database.getReference(".info/serverTimeOffset");

        Query query = chatRef.child(generateChatRoomId(chatUser.getUid(), FirebaseAuth.getInstance()
                .getCurrentUser().getUid()

        )).child(NCHAT_DETAIL_REFERENCE);

        options = new FirebaseRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();
        ButterKnife.bind(this) ;
        layoutManager = new LinearLayoutManager(this);
        recycler_chat.setLayoutManager(layoutManager);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(chatUser.getUid());
        TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();
        TextDrawable drawable = builder.build(chatUser.getNombre().substring(0, 1), color);
        img_avatar.setImageDrawable(drawable);
        txt_name.setText(getName(chatUser));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            Intent i = new Intent(this, ChatActivity.class);//La flecha que esta arriba en los chats hace que los devuelva en home activity
            startActivity(i);
            finish();
        });


    }

    private void loadChatContent() {
        String receiverId = FirebaseAuth.getInstance()
                .getCurrentUser().getUid();
        adapter = new FirebaseRecyclerAdapter<ChatMessageModel, RecyclerView.ViewHolder>(options) {

            @Override
            public int getItemViewType(int position) {
                if (adapter.getItem(position).getSenderId().equals(receiverId))//If message is own
                {
                    return !adapter.getItem(position).isPicture() ? 0 : 1;
                } else {
                    return !adapter.getItem(position).isPicture() ? 2 : 3;
                }

            }

            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull ChatMessageModel model) {
                if (holder instanceof NChatTextHolder) {
                    NChatTextHolder chatTextHolder = (NChatTextHolder) holder;
                    chatTextHolder.ntxt_chat_message.setText(model.getContent());
                    chatTextHolder.ntxt_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(), Calendar.getInstance().getTimeInMillis(), 0)
                            .toString());

                } else if (holder instanceof NChatTextReceiveHolder) {
                    NChatTextReceiveHolder chatTextHolder = (NChatTextReceiveHolder) holder;
                    chatTextHolder.ntxt_chat_message.setText(model.getContent());
                    chatTextHolder.ntxt_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(), Calendar.getInstance().getTimeInMillis(), 0)
                            .toString());
                }


            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                if (viewType == 0) //Text message of user *own message*
                {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.nlayout_message_text_own, parent, false);
                    return new NChatTextReceiveHolder(view);
                } else //Text message for a friend
                {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.nlayout_message_text_friend, parent, false);
                    return new NChatTextHolder(view);

                }

            }
        };

        //Auto scroll when receive new message
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisiblePosition == -1 || positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1)) {
                    recycler_chat.scrollToPosition(positionStart);
                }


            }
        });

        recycler_chat.setAdapter(adapter);
    }



    @Override
    public void onLooadOnlyTimeSuccess(long estimateTimeInMs) {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.setName(getName(users));//Aqui podria haber error 17:18 #5
        chatMessageModel.setContent(edt_chat.getText().toString());
        chatMessageModel.setTimeStamp(estimateTimeInMs);
        chatMessageModel.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(fileUri==null) {//Sistema de img
            chatMessageModel.setPicture(false);
            submitChatToFirebase(chatMessageModel, chatMessageModel.isPicture(), estimateTimeInMs);
        }

    }

    private void submitChatToFirebase(ChatMessageModel chatMessageModel, boolean isPicture, long estimateTimeInMs) {

        chatRef.child(generateChatRoomId(chatUser.getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            appendChat(chatMessageModel,isPicture,estimateTimeInMs);
                        }
                        else
                        {
                            createChat(chatMessageModel,isPicture,estimateTimeInMs);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Chat_note.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void appendChat(ChatMessageModel chatMessageModel, boolean isPicture, long estimateTimeInMs) {

        Map<String,Object> update_data = new HashMap<>();
        update_data.put("lastUpdate",estimateTimeInMs);


        //Update
        //Update on user list
        FirebaseDatabase.getInstance().getReference(NCHAT_LIST_REFERENCE).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(chatUser.getUid()).updateChildren(update_data)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(unused -> {
                    //Submit success for ChatInfo
                    //Copy to Friend Chat List
                    FirebaseDatabase.getInstance().getReference(NCHAT_LIST_REFERENCE)
                            .child(chatUser.getUid())//swap
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(update_data)//swap
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnSuccessListener(aVoid -> {

                                //Add on Chat Ref
                                chatRef.child(generateChatRoomId(chatUser.getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                        .child(NCHAT_DETAIL_REFERENCE)
                                        .push().setValue(chatMessageModel).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Chat_note.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    edt_chat.setText("");
                                                    edt_chat.requestFocus();
                                                    if(adapter != null)
                                                    {
                                                        adapter.notifyDataSetChanged();
                                                    }

                                                    //Clear preview thumbnail

                                                }
                                            }
                                        });

                            });

                });


    }



    private void createChat(ChatMessageModel chatMessageModel, boolean isPicture, long estimateTimeInMs) {
        ChatInfoModel chatInfoModel = new ChatInfoModel();
        chatInfoModel.setCreateId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        chatInfoModel.setFriendName(getName(chatUser) );
        chatInfoModel.setFriendId(chatUser.getUid());
        chatInfoModel.setCreateName(getName(users));

        if(isPicture)
        {
            chatInfoModel.setLastMessage("<Image>");
        }
        else
        {
            chatInfoModel.setLastMessage(chatMessageModel.getContent());
        }


        chatInfoModel.setLastUpdate(estimateTimeInMs);
        chatInfoModel.setCreateDate(estimateTimeInMs);

        //Submit on Firebase
        //Add on User List

        FirebaseDatabase.getInstance().getReference(NCHAT_LIST_REFERENCE).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(chatUser.getUid()).setValue(chatInfoModel)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(unused -> {
                    //Submit success for ChatInfo
                    //Copy to Friend Chat List
                    FirebaseDatabase.getInstance().getReference(NCHAT_LIST_REFERENCE)
                            .child(chatUser.getUid())//swap
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(chatInfoModel)//swap
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnSuccessListener(aVoid -> {

                                //Add on Chat Ref
                                chatRef.child(generateChatRoomId(chatUser.getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                        .child(NCHAT_DETAIL_REFERENCE)
                                        .push().setValue(chatMessageModel).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Chat_note.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    edt_chat.setText("");
                                                    edt_chat.requestFocus();
                                                    if(adapter != null)
                                                    {
                                                        adapter.notifyDataSetChanged();

                                                    }

                                                    //Clear preview thumbnail

                                                }
                                            }
                                        });

                            });

                });



    }


    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}




