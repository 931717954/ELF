package com.example.universalrecyclerviewlibrary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {
    private Context context;//上下文
    private List<T> list;//数据源
    private LayoutInflater inflater;//布局器
    private int itemLayoutId;//布局id
    private boolean isScrolling;//是否在滚动
    private OnItemClickListener listener;//点击事件监听器
    private OnItemLongClickListener longClickListener;//长按监听器
    private RecyclerView recyclerView;

    public BaseRecyclerAdapter(Context context, List<T> list, int itemLayoutId) {
        this.context = context;
        this.list = list;
        this.itemLayoutId = itemLayoutId;
        inflater = LayoutInflater.from(context);
    }
    /**
     * 定义一个点击事件接口回调
     */
      public interface OnItemClickListener {
          void onItemClick(RecyclerView parent, View view, int position);
      }

              public interface OnItemLongClickListener {
          boolean onItemLongClick(RecyclerView parent, View view, int position);
      }
    public void add(T item){
        list.add(item);
        notifyItemInserted(getItemCount()-1);
    }
      /**
        * 插入一项
        *
        * @param item
        * @param position
        */
         public void insert(T item, int position) {
            list.add(position, item);
            notifyItemInserted(position);
         }
      /**
        * 删除一项
        *
        * @param position 删除位置
        */
              public void delete(int position) {
                 list.remove(position);
                 notifyItemRemoved(position);
             }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(itemLayoutId, viewGroup, false);
        return BaseRecyclerHolder.getRecyclerHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int i) {
        if (listener != null){
//                         holder.itemView.setBackgroundResource(R.drawable.recycler_bg);//设置背景
                     }
                 holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                                 if (listener != null && view != null && recyclerView != null) {
                                         int position = recyclerView.getChildAdapterPosition(view);
                                         listener.onItemClick(recyclerView, view, position);
                                     }
                             }
                });


                 holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View view) {
                if (longClickListener != null && view != null && recyclerView != null) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    longClickListener.onItemLongClick(recyclerView, view, position);
                    return true;
                    }
                return false;
                }
        });

                 convert(holder, list.get(i), i, isScrolling);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
        }
    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
        }
    /**
           * 填充RecyclerView适配器的方法，子类需要重写
           *
           * @param holder      ViewHolder
           * @param item        子项
           * @param position    位置
           * @param isScrolling 是否在滑动
           */
    public abstract void convert(BaseRecyclerHolder holder, T item, int position, boolean isScrolling);
}
