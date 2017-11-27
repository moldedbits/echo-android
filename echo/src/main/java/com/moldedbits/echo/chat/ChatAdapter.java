package com.moldedbits.echo.chat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moldedbits.echo.chat.core.model.EchoMessage;
import com.moldedbits.echo.chat.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

import moldedbits.com.echo.R;

class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<EchoMessage> mList = new ArrayList<>();

    private static final int MESSAGE_IN = 1;
    private static final int MESSAGE_OUT = 2;
    private OnItemClickListener onItemClickListener;
    private Activity activity;

    ChatAdapter(Activity activity, List<EchoMessage> list, String clientId) {
        this.activity = activity;
        if (list != null && mList != null) {
            mList.addAll(list);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == MESSAGE_OUT) {
            view = inflater.inflate(R.layout.item_chat_outgoing, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_chat_incoming, parent, false);
        }

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (!mList.get(position).isIncoming()) {
            return MESSAGE_IN;
        }
        return MESSAGE_OUT;
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0;
        }
    }

    public void add(EchoMessage message) {
        mList.add(message);
        this.notifyDataSetChanged();
    }

    void addAll(List<EchoMessage> messages) {
        if (messages != null) {
            mList.clear();
            mList.addAll(messages);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    void updateMessage(final EchoMessage message) {
        for (EchoMessage echoMessage : mList) {
            if (message.getMessageId().contentEquals(echoMessage.getMessageId())) {
                int index = mList.indexOf(echoMessage);
                mList.set(index, message);
                break;
            }
        }
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            ChatAdapterContract.ViewContract {

        private Drawable bubbleOutgoing;
        private Drawable bubbleIncoming;
        LinearLayout llChat;
        TextView messageTextView;
        TextView timeTextView;
        ImageView imageView;
        LinearLayout linearLayout;
        LinearLayout llDocContainer;
        RelativeLayout rlContainer;
        TextView imgName;
        TextView imgDate;
        TextView docName;
        TextView docDate;
        ImageView msgSentStatus;

        ChatAdapterPresenter mPresenter;

        MyViewHolder(View itemView) {
            super(itemView);
            llChat = (LinearLayout) itemView.findViewById(R.id.ll_chat);
            messageTextView = (TextView) itemView.findViewById(R.id.message);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            msgSentStatus = (ImageView) itemView.findViewById(R.id.iv_msg_send_status);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_image_container);
            llDocContainer = (LinearLayout) itemView.findViewById(R.id.ll_doc_container);
            rlContainer = (RelativeLayout) itemView.findViewById(R.id.rl_container);
            imgName = (TextView) itemView.findViewById(R.id.tv_img_name);
            imgDate = (TextView) itemView.findViewById(R.id.tv_img_date);
            docName = (TextView) itemView.findViewById(R.id.tv_doc_name);
            docDate = (TextView) itemView.findViewById(R.id.tv_doc_date);
            rlContainer.setOnClickListener(this);
            llDocContainer.setOnClickListener(this);

            mPresenter = new ChatAdapterPresenter(this);
        }

        @Override
        public void displayMessage(@Nullable String message) {
            if (TextUtils.isEmpty(message)) {
                messageTextView.setVisibility(View.GONE);
            } else {
                messageTextView.setVisibility(View.VISIBLE);
                llDocContainer.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);
                messageTextView.setText(message);
            }
        }

        @Override
        public void displayDocument(String name, String date, Uri uri, boolean showDocument) {
            if (showDocument) {
                llDocContainer.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                messageTextView.setVisibility(View.GONE);
                docName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_document_gray, 0, 0,
                                                                0);
                docName.setText(name);
                docName.setTag(uri);
                docDate.setText(date);
            } else {
                llDocContainer.setVisibility(View.GONE);
            }
        }

        @Override
        public void displayImage(Bitmap bitmap, EchoMessage message, boolean isImageVisible) {
            if (isImageVisible) {
                linearLayout.setVisibility(View.VISIBLE);
                llDocContainer.setVisibility(View.GONE);
                messageTextView.setVisibility(View.GONE);
                imageView.setImageBitmap(null);
                imageView.setImageBitmap(bitmap);
                imageView.setTag(message);
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void displayStatusIcons(int iconResource) {
            if (msgSentStatus != null) {
                msgSentStatus.setImageResource(iconResource);
            }
        }

        @Override
        public void displayTimestamp(@NonNull String timestamp) {
            timeTextView.setText(timestamp);
        }

        private void bind(final EchoMessage message) {
            rlContainer.setTag(message);
            mPresenter.bindData(message);
            if (message.isIncoming()) {
                bubbleIncoming = llChat.getBackground().mutate();
                bubbleIncoming.setAlpha(50);
                bubbleIncoming.setColorFilter(new PorterDuffColorFilter(
                        ColorUtils.fetchAccentColor(activity), PorterDuff.Mode.SRC_ATOP));
                llChat.setBackground(bubbleIncoming);
            } else {
                bubbleOutgoing = llChat.getBackground().mutate();
                bubbleOutgoing.setAlpha(50);
                bubbleOutgoing.setColorFilter(new PorterDuffColorFilter(
                        ColorUtils.fetchPrimaryColor(activity), PorterDuff.Mode.SRC_ATOP));
                llChat.setBackground(bubbleOutgoing);
            }
        }

        @Override
        public void onClick(View v) {
            // TODO: 06/06/17 check this later
            onItemClickListener.onItemClick((EchoMessage) v.getTag());
            //            int id = v.getId();
            //            if (id == R.id.ll_doc_container) {
            //
            //            }
            //            switch (v.getId()) {
            //                case R.id.iv_image: {
            //                    onItemClickListener.onItemClick((EchoMessage) v.getTag());
            //                    break;
            //                }
            //                case R.id.ll_doc_container: {
            //                    if (v.findViewById(R.id.tv_doc_name).getTag() != null) {
            //                        Uri url = (Uri) v.findViewById(R.id.tv_doc_name).getTag();
            //                        mActivity.startActivity(FileUtil.getOpenDocumentIntent(url));
            //                    }
            //                    break;
            //                }
            //            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(EchoMessage selectedImage);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }
}