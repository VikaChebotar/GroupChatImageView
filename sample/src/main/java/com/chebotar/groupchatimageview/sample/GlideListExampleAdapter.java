package com.chebotar.groupchatimageview.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chebotar.R;
import com.chebotar.groupchatimageview.GroupChatImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vika on 24.05.16.
 */
public class GlideListExampleAdapter extends RecyclerView.Adapter<GlideListExampleAdapter.ViewHolder> {
    private List<ChatRoom> chatList;
    private Context context;

    public GlideListExampleAdapter(List<ChatRoom> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        GroupChatImageView chatImage;
        TextView chatName;
        TextView chatResentMessage;
        TextView chatResentMessageTime;

        public ViewHolder(View v) {
            super(v);
            chatImage = (GroupChatImageView) v.findViewById(R.id.chatImage);
            chatName = (TextView) v.findViewById(R.id.chatName);
            chatResentMessage = (TextView) v.findViewById(R.id.chatResentMessage);
            chatResentMessageTime = (TextView) v.findViewById(R.id.chatResentMessageTime);
        }
    }

    @Override
    public GlideListExampleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_example_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.chatImage.clearBitmaps();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChatRoom chatRoom = chatList.get(position);
        StringBuilder chatNameBuilder = new StringBuilder();
        List<String> chatImages = new ArrayList<String>();
        for (User user : chatRoom.getParticipants()) {
            chatNameBuilder.append(user.getName()).append(", ");
            chatImages.add(user.getPhoto());
        }
        chatImages = chatImages.subList(0, Math.min(chatImages.size(), GroupChatImageView.MAX_IMAGES_IN_VIEW));

        holder.chatImage.setShowDivider(true);
        holder.chatImage.setCornerRadius(context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin));
        List<Bitmap> defaultBitmaps = new ArrayList<Bitmap>(chatImages.size());
        Bitmap defaultBitmap = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.noimage)).getBitmap();
        for (int i = 0; i < chatImages.size(); i++) {
            defaultBitmaps.add(defaultBitmap);
        }
        holder.chatImage.setBitmaps(defaultBitmaps);

        for (int i = 0; i < chatImages.size(); i++) {
            Glide.with(context)
                    .load(chatImages.get(i))
                    .asBitmap()
                    .into(new MyTarget(holder.chatImage, i));
        }

        holder.chatName.setText(chatNameBuilder.substring(0, chatNameBuilder.lastIndexOf(",")));
        holder.chatResentMessage.setText(chatRoom.getLastMessage());
        holder.chatResentMessageTime.setText(chatRoom.getLastMessageTime());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    private class MyTarget extends SimpleTarget<Bitmap> {
        private final WeakReference<GroupChatImageView> chatImageRef;
        private int position;

        public MyTarget(GroupChatImageView chatImageView, int position) {
            chatImageRef = new WeakReference<GroupChatImageView>(chatImageView);
            this.position = position;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            if (chatImageRef != null) {
                chatImageRef.get().replaceBitmap(position, resource);
            }
        }
    }
}
