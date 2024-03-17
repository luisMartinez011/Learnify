package com.example.aplicacion;

import static com.example.aplicacion.Common.Common.CHAT_DETAIL_REFERENCE;
import static com.example.aplicacion.Common.Common.CHAT_LIST_REFERENCE;
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
import com.example.aplicacion.ViewHolders.ChatPictureHolder;
import com.example.aplicacion.ViewHolders.ChatPictureReceiveHolder;
import com.example.aplicacion.ViewHolders.ChatTextHolder;
import com.example.aplicacion.ViewHolders.ChatTextReceiveHolder;
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

public class ChatActivity extends AppCompatActivity implements ILoadTimeFromFirebaseListener, IFirebaseLoadFailed {

    private static final int MY_CAMERA_REQUEST_CODE= 7171;
    private static final int MY_RESULT_LOAD_IMAGE=7172;

    TextView textView;
    Location location;
    double describeContents;
    List<Address> addresses;
    Geocoder geocoder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.img_camera)
    ImageView img_camera;

    @BindView(R.id.img_image)
    ImageView img_image;

    @BindView(R.id.edt_chat)
    AppCompatEditText edt_chat;

    @BindView(R.id.img_send)
    ImageView img_send;

    @BindView(R.id.recycler_chat)
    RecyclerView recycler_chat;

    @BindView(R.id.img_preview)
    ImageView img_preview;

    @BindView(R.id.img_avatar)
    ImageView img_avatar;

    @BindView(R.id.txt_name)
    TextView txt_name;

    FirebaseDatabase database;
    DatabaseReference chatRef,offsetRef;
    ILoadTimeFromFirebaseListener listener;
    IFirebaseLoadFailed errorListener;

    FirebaseRecyclerAdapter<ChatMessageModel, RecyclerView.ViewHolder> adapter;
    FirebaseRecyclerOptions<ChatMessageModel> options;

    Uri fileUri;

    StorageReference storageReference;


    LinearLayoutManager layoutManager;

    @OnClick(R.id.img_image)
    void onSelectImageClick()
    {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        SomeActivityResultLauncher.launch(intent);


    }




    ActivityResultLauncher<Intent> SomeActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>( ) {
                @Override
                public void onActivityResult(ActivityResult result ) {

                     if(result.getResultCode()==RESULT_OK)
                    {


                            Intent data = result.getData();
                            try {
                                final Uri imageUri = result.getData().getData();
                                InputStream inputStream = getContentResolver()
                                        .openInputStream(imageUri);
                                Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                                img_preview.setImageBitmap(selectedImage);
                                img_preview.setVisibility(View.VISIBLE);
                                fileUri = imageUri;

                            }catch(FileNotFoundException e)
                            {
                                e.getMessage();
                            }

                    }
                    else
                    {
                        //Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>( ) {
                @Override
                public void onActivityResult(ActivityResult result ) {
                    if (result.getResultCode()==Activity.RESULT_OK) {


                            Intent data = result.getData();
                            try {
                                Bitmap thumbnail = MediaStore.Images.Media
                                        .getBitmap(getContentResolver(),fileUri);
                                img_preview.setImageBitmap(thumbnail);
                                img_preview.setVisibility(View.VISIBLE);


                            }catch(FileNotFoundException e)
                            {
                                e.getMessage();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }





                    }

                    else
                    {
                        //Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
                    }
                }
            });




    @OnClick(R.id.img_camera)
    void onCaptureImageClick()
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From your Camera");
        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        someActivityResultLauncher.launch(intent);//min 6:30


    }



    @OnClick(R.id.img_send)
    void onSubmitChatClick()
    {


        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeInMs= System.currentTimeMillis()+offset;
                listener.onLoadOnlyTimeSuccess(estimatedServerTimeInMs);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorListener.onError(error.getMessage());
                Toast.makeText(ChatActivity.this, "no le estas picando", Toast.LENGTH_SHORT).show();

            }
        });
    }


    @OnClick(R.id.img_notes)
    void onSubmitChatClickk()
    {
        Intent i = new Intent(this, Chat_note.class);
        startActivity(i);
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
        setContentView(R.layout.activity_chat);
        textView = findViewById(R.id.textgps);
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        location = locationManager
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        geocoder = new Geocoder(this);
        initViews();
        loadChatContent();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
                        Address address = addresses.get(0);
                        textView.setText("" + address.getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    private void loadChatContent() {
        String receiverId = FirebaseAuth.getInstance()
                .getCurrentUser().getUid();
        adapter = new FirebaseRecyclerAdapter<ChatMessageModel, RecyclerView.ViewHolder>(options) {

            @Override
            public int getItemViewType(int position) {
                if(adapter.getItem(position).getSenderId().equals(receiverId))//If message is own
                {
                    return !adapter.getItem(position).isPicture()?0:1;
                }
                else
                {
                    return !adapter.getItem(position).isPicture()?2:3;
                }

            }

            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull ChatMessageModel model) {
                if(holder instanceof ChatTextHolder)
                {
                    ChatTextHolder chatTextHolder = (ChatTextHolder)holder;
                    chatTextHolder.txt_chat_message.setText(model.getContent());
                    chatTextHolder.txt_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(), Calendar.getInstance().getTimeInMillis(),0)
                            .toString());

                }
                else if (holder instanceof ChatTextReceiveHolder )
                {
                    ChatTextReceiveHolder chatTextHolder = (ChatTextReceiveHolder)holder;
                    chatTextHolder.txt_chat_message.setText(model.getContent());
                    chatTextHolder.txt_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(), Calendar.getInstance().getTimeInMillis(),0)
                            .toString());
                }
                else if(holder instanceof ChatPictureHolder)
                {
                    ChatPictureHolder chatPictureHolder = (ChatPictureHolder)holder;
                    chatPictureHolder.txt_chat_message.setText(model.getContent());
                    chatPictureHolder.txt_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(), Calendar.getInstance().getTimeInMillis(),0)
                            .toString());

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
                        Address address = addresses.get(0);
                        chatPictureHolder.txt_gps.setText("" + address.getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Glide.with(ChatActivity.this)
                            .load(model.getPictureLink())
                            .into(chatPictureHolder.img_preview);

                }
                else if(holder instanceof ChatPictureReceiveHolder)
                {
                    ChatPictureReceiveHolder chatPictureHolder = (ChatPictureReceiveHolder) holder;
                    chatPictureHolder.txt_chat_message.setText(model.getContent());
                    chatPictureHolder.txt_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(), Calendar.getInstance().getTimeInMillis(),0)
                            .toString());
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
                        Address address = addresses.get(0);
                        chatPictureHolder.txt_gps.setText("" + address.getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Glide.with(ChatActivity.this)
                            .load(model.getPictureLink())
                            .into(chatPictureHolder.img_preview);
                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                if(viewType==0 ) //Text message of user *own message*
                {
                    view= LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_message_text_own,parent,false);
                            return new ChatTextReceiveHolder(view);
                }
                else if(viewType==1)//Picture message for a friend
                {
                    view= LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_message_picture_own,parent,false);
                    return new ChatPictureReceiveHolder(view);

                }
                else if(viewType==2)//Text message for a friend
                {
                    view= LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_message_text_friend,parent,false);
                    return new ChatTextHolder(view);

                }
                else // Picture message of friend
                {
                    view= LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_message_picture_friend,parent,false);
                    return new ChatPictureHolder(view);
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
                if(lastVisiblePosition ==-1|| positionStart >= (friendlyMessageCount-1) && lastVisiblePosition == (positionStart-1))
                {
                    recycler_chat.scrollToPosition(positionStart);
                }


            }
        });

        recycler_chat.setAdapter(adapter);


    }

    private void initViews() {
        listener= this;
        errorListener=this;
        database= FirebaseDatabase.getInstance();
        chatRef= database.getReference("Chat"); //chat reference

        offsetRef= database.getReference(".info/serverTimeOffset");

        Query query = chatRef.child(generateChatRoomId(chatUser.getUid(), FirebaseAuth.getInstance()
                .getCurrentUser().getUid()

        )).child(CHAT_DETAIL_REFERENCE);

        options= new FirebaseRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();
        ButterKnife.bind(this);
        layoutManager= new LinearLayoutManager(this);
        recycler_chat.setLayoutManager(layoutManager);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color= generator.getColor( chatUser.getUid());
        TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();
        TextDrawable drawable = builder.build(chatUser.getNombre().substring(0,1),color);
        img_avatar.setImageDrawable(drawable);
        txt_name.setText(getName(chatUser));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            Intent i = new Intent(this, HomeActivity.class);//La flecha que esta arriba en los chats hace que los devuelva en home activity
            startActivity(i);
           finish();
        });





    }

    @Override
    public void onLoadOnlyTimeSuccess(long estimateTimeInMs) {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.setName(getName(users));//Aqui podria haber error 17:18 #5
        chatMessageModel.setContent(edt_chat.getText().toString());
        chatMessageModel.setTimeStamp(estimateTimeInMs);
        chatMessageModel.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());


        //Current, we just implement chat text
        if(fileUri==null) {//Sistema de img
            chatMessageModel.setPicture(false);
            submitChatToFirebase(chatMessageModel, chatMessageModel.isPicture(), estimateTimeInMs);
        }
        else
        {
            uploadPicture(fileUri,chatMessageModel,estimateTimeInMs);

        }




    }



    private void uploadPicture(Uri fileUri, ChatMessageModel chatMessageModel, long estimateTimeInMs) {
        AlertDialog dialog = new AlertDialog.Builder(ChatActivity.this)
                .setCancelable(false)
                .setMessage("Please wait")
                .create();
        dialog.show();

        String fileName= getFileName(getContentResolver(),fileUri);
        String path= new StringBuilder(chatUser.getUid())
                .append("/")
                .append(fileName)
                .toString();
        storageReference = FirebaseStorage.getInstance()
                .getReference()
                .child(path);
        UploadTask uploadTask = storageReference.putFile(fileUri);
        //Create Task
        Task<Uri> task = uploadTask.continueWithTask(task1 -> {
            if(!task1.isSuccessful()) {
                Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT).show();
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task12->
        {
            if(task12.isSuccessful())
            {
                String url= task12.getResult().toString();
                dialog.dismiss();
                chatMessageModel.setPicture(true);
                chatMessageModel.setPictureLink(url);

                submitChatToFirebase(chatMessageModel,chatMessageModel.isPicture(),estimateTimeInMs);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });//min 15


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
                        Toast.makeText(ChatActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void appendChat(ChatMessageModel chatMessageModel, boolean isPicture, long estimateTimeInMs) {

        Map<String,Object> update_data = new HashMap<>();
        update_data.put("lastUpdate",estimateTimeInMs);

        if(isPicture)
        {
            update_data.put("lastMessage","<Image>");
        }
        else {
            update_data.put("lastMessage", chatMessageModel.getContent());
        }
        //Update
        //Update on user list
        FirebaseDatabase.getInstance().getReference(CHAT_LIST_REFERENCE).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(chatUser.getUid()).updateChildren(update_data)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(unused -> {
                    //Submit success for ChatInfo
                    //Copy to Friend Chat List
                    FirebaseDatabase.getInstance().getReference(CHAT_LIST_REFERENCE)
                            .child(chatUser.getUid())//swap
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(update_data)//swap
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnSuccessListener(aVoid -> {

                                //Add on Chat Ref
                                chatRef.child(generateChatRoomId(chatUser.getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                        .child(CHAT_DETAIL_REFERENCE)
                                        .push().setValue(chatMessageModel).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                                    if(isPicture)
                                                    {
                                                        fileUri=null;
                                                        img_preview.setVisibility(View.GONE);

                                                    }
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

        FirebaseDatabase.getInstance().getReference(CHAT_LIST_REFERENCE).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(chatUser.getUid()).setValue(chatInfoModel)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(unused -> {
                    //Submit success for ChatInfo
                    //Copy to Friend Chat List
                    FirebaseDatabase.getInstance().getReference(CHAT_LIST_REFERENCE)
                            .child(chatUser.getUid())//swap
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(chatInfoModel)//swap
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnSuccessListener(aVoid -> {

                                //Add on Chat Ref
                                chatRef.child(generateChatRoomId(chatUser.getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                        .child(CHAT_DETAIL_REFERENCE)
                                        .push().setValue(chatMessageModel).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                                    if(isPicture)
                                                    {
                                                        fileUri=null;
                                                        img_preview.setVisibility(View.GONE);

                                                    }
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