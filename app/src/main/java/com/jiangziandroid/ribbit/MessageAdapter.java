package com.jiangziandroid.ribbit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JeremyYCJiang on 2015/4/23.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<ParseObject> mMessages;
    private Context mContext;

    public MessageAdapter(Context context, List<ParseObject> receivedMessages) {

        mContext = context;
        mMessages = receivedMessages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_item, viewGroup, false);
        return new MessageViewHolder(view);
    }

    //Bridge(Controller)
    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int i) {
        messageViewHolder.bindMessage(mMessages.get(i));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }


    //views(View) and data(Model) mapping code
    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mMessageIcon;
        public TextView mMessageSenderName;
        public TextView mMessageSendTime;

        public MessageViewHolder(View itemView) {
            super(itemView);
            mMessageIcon = (ImageView) itemView.findViewById(R.id.messageIcon);
            mMessageSenderName = (TextView) itemView.findViewById(R.id.messageSenderName);
            mMessageSendTime = (TextView) itemView.findViewById(R.id.messageSendTime);
            itemView.setOnClickListener(this);
        }

        public void bindMessage(ParseObject message) {
            if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)){
            mMessageIcon.setImageResource(R.drawable.ic_action_picture);
            }
            else if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_VIDEO)){
                mMessageIcon.setImageResource(R.drawable.ic_action_play_over_video);
            }
            mMessageSenderName.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
            mMessageSendTime.setText("Time to be added!");
        }

        //private String getStringTime(Date date) {
        //    SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy, k:m", Locale.US);
        //    return formatter.format(date);
        //}

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Media loading...", Toast.LENGTH_LONG).show();
            ParseObject message = mMessages.get(getLayoutPosition());
            String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
            ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
            Uri fileUri = Uri.parse(file.getUrl());
            if(messageType.equals(ParseConstants.TYPE_IMAGE)){
                //View the image
                Intent intent = new Intent(v.getContext(), ViewImageActivity.class);
                intent.setData(fileUri);
                v.getContext().startActivity(intent);
            }
            else if (messageType.equals(ParseConstants.TYPE_VIDEO)){
                //View the video
                Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
                intent.setDataAndType(fileUri, "video/*");
                v.getContext().startActivity(intent);
            }
            //delete message
            List<String> recipientIds = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
            if(recipientIds.size() == 1){
                //last recipient - delete the whole thing!
                message.deleteInBackground();
            }
            else {
                //remove the recipient and save
                //remove UserId locally
                recipientIds.remove(ParseUser.getCurrentUser().getObjectId());
                //remove UserId back-end
                ArrayList<String> idsTobeRemoved = new ArrayList<>();
                idsTobeRemoved.add(ParseUser.getCurrentUser().getObjectId());
                message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsTobeRemoved);
                message.saveInBackground();
            }
        }
    }

    public void refill(List<ParseObject> receivedMessages){
        mMessages.clear();
        mMessages.addAll(receivedMessages);
        notifyDataSetChanged();
    }

}
