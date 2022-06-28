package com.example.chapter15.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chapter15.R;
import com.example.chapter15.entity.BookInfo;

import java.util.List;

@SuppressLint("SetTextI18n")
public class BookListAdapter extends BaseAdapter {
    private List<BookInfo> mBookList; // 声明一个书籍信息列表
    private Context mContext;

    public BookListAdapter(Context context, List<BookInfo> bookList) {
        mContext = context;
        mBookList = bookList;
    }

    @Override
    public int getCount() {
        return mBookList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mBookList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_book, null);
            holder.iv_format = convertView.findViewById(R.id.iv_format);
            holder.tv_title = convertView.findViewById(R.id.tv_title);
            holder.tv_author = convertView.findViewById(R.id.tv_author);
            holder.tv_page_number = convertView.findViewById(R.id.tv_page_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BookInfo book = mBookList.get(position);
        holder.iv_format.setImageResource(getBookImage(book.getFileName()));
        holder.tv_title.setText(book.getTitle());
        if (TextUtils.isEmpty(book.getAuthor())) {
            holder.tv_author.setVisibility(View.GONE);
        } else {
            holder.tv_author.setVisibility(View.VISIBLE);
            holder.tv_author.setText(book.getAuthor());
        }
        if (book.getPageCount() != 0) {
            holder.tv_page_number.setText(book.getPageCount() + "页");
        } else {
            holder.tv_page_number.setText("");
        }
        return convertView;
    }

    // 根据文件路径选取对应的书籍图标
    private int getBookImage(String path) {
        int icon_id = R.drawable.icon_pdf;
        if (path.endsWith(".pdf")) {
            icon_id = R.drawable.icon_pdf;
        } else if (path.endsWith(".epub")) {
            icon_id = R.drawable.icon_epub;
        } else if (path.endsWith(".djvu")) {
            icon_id = R.drawable.icon_djvu;
        }
        return icon_id;
    }

    public final class ViewHolder {
        public ImageView iv_format;
        public TextView tv_title;
        public TextView tv_author;
        public TextView tv_page_number;
    }

}
