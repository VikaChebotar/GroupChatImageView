package com.chebotar.groupchatimageview.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chebotar.R;
import com.chebotar.groupchatimageview.GroupChatImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vika on 23.05.16.
 */
public class ListExampleAdapter extends RecyclerView.Adapter<ListExampleAdapter.ViewHolder> {
    private List<ChatRoom> chatList;
    private Context context;

    public ListExampleAdapter(List<ChatRoom> chatList, Context context) {
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
    public ListExampleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_example_item, parent, false);
        ViewHolder vh = new ViewHolder(v);

        List<MyTarget> targetList = Arrays.asList(new MyTarget(vh.chatImage), new MyTarget(vh.chatImage), new MyTarget(vh.chatImage), new MyTarget(vh.chatImage));
        vh.chatImage.setTag(targetList);
        return vh;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.chatImage.clearBitmaps();
        List<MyTarget> targetList = (List<MyTarget>) holder.chatImage.getTag();
        for (MyTarget target : targetList) {
            Picasso.with(context).cancelRequest(target);
        }
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
        int imageSize = context.getResources().getDimensionPixelSize(R.dimen.image_size);
        List<MyTarget> targetList = (List<MyTarget>) holder.chatImage.getTag();
        chatImages = chatImages.subList(0, Math.min(chatImages.size(), GroupChatImageView.MAX_IMAGES_IN_VIEW));
        AtomicInteger atomicInteger = new AtomicInteger(chatImages.size());
        for (int i = 0; i < chatImages.size(); i++) {
            targetList.get(i).setWorkCounter(atomicInteger);
            Picasso.with(context).
                    load(chatImages.get(i)).
                    memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).
                    networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .resize(imageSize, imageSize)
                    .centerCrop()
                    .into(targetList.get(i));
        }
        for (int i = targetList.size(); i > chatImages.size(); i--) {
            Picasso.with(context).cancelRequest(targetList.get(i - 1));
        }
        holder.chatImage.setRedrawingAutomatically(false);
        holder.chatImage.setShapeMode(GroupChatImageView.ShapeMode.ROUNDED_RECTANGLE);
        holder.chatImage.setShowDivider(true);
        holder.chatImage.setCornerRadius(context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin));

        holder.chatName.setText(chatNameBuilder.substring(0, chatNameBuilder.lastIndexOf(",")));
        holder.chatResentMessage.setText(chatRoom.getLastMessage());
        holder.chatResentMessageTime.setText(chatRoom.getLastMessageTime());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    private class MyTarget implements Target {
        private final WeakReference<GroupChatImageView> chatImageRef;
        private AtomicInteger workCounter;

        public MyTarget(GroupChatImageView chatImageView) {
            chatImageRef = new WeakReference<GroupChatImageView>(chatImageView);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (chatImageRef != null) {
                chatImageRef.get().addBitmap(bitmap);
                int tasksLeft = workCounter.decrementAndGet();
                if (tasksLeft == 0) {
                    chatImageRef.get().invalidate();
                }
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("TAG", "failed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

        public void setWorkCounter(AtomicInteger workCounter) {
            this.workCounter = workCounter;
        }

    }
}