package com.example.aplicacion.Common;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.example.aplicacion.Users;
import com.example.aplicacion.cal;
import com.firebase.ui.auth.data.model.User;

import java.util.Random;

public class Common
{

    public static final String CHAT_LIST_REFERENCE = "LChat";

    public static final String NCHAT_LIST_REFERENCE = "NListChat";


    public static final String  CHAT_DETAIL_REFERENCE = "Detail";
    public static final String  NCHAT_DETAIL_REFERENCE = "NDetail";

    public static  Users chatUser= new Users();

    public static cal calt= new cal();

    public static Users users= new Users();

    public static String generateChatRoomId(String a,String b)
    {
        if(a.compareTo(b)>0)
        {
            return new StringBuilder(a).append(b).toString();
        }
        else if(a.compareTo(b)<0)
        {
            return new StringBuilder(b).append(a).toString();
        }
        else {
            return new StringBuilder("Chat_Yourself_Error").append(new Random().nextInt()).toString();
        }
    }

    public static String getName(Users chatUser)
    {
        return new StringBuilder(chatUser.getNombre())
                .append(" ").append(chatUser.getApellido()).toString();
    }


    @SuppressLint("Range")
    public static String getFileName(ContentResolver contentResolver, Uri fileuri)
    {
        String result =null;
        if(fileuri.getScheme().equals("content"))
        {
            Cursor cursor = contentResolver.query(fileuri,null,null,null);
            try
            {
                if(cursor!= null && cursor.moveToFirst())
                {
                   result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }

            }finally {
                cursor.close();

            }
            {

            }
        }
        if(result==null)
        {
            result=fileuri.getPath();
            int cut = result.lastIndexOf("/");
            if(cut !=1)
            {
                result=result.substring(cut+1);
            }
        }
        return result;
    }
}
