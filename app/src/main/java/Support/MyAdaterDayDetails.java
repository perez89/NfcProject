package Support;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.perez.schedulebynfc.R;

import java.util.List;

/**
 * Created by User on 04/01/2017.
 */

public class MyAdaterDayDetails extends RecyclerView.Adapter<MyAdaterDayDetails.RecyclerItemViewHolder> {
    private List<EventData> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvIn, tvOut;

        public RecyclerItemViewHolder(View itemView) {
            super(itemView);
            tvIn = (TextView) itemView.findViewById(R.id.tvIn);
            tvOut = (TextView) itemView.findViewById(R.id.tvOut);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdaterDayDetails(List<EventData> items) {
        this.mDataset = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_day_details, parent, false);
        // set the view's size, margins, paddings and layout parameters

        RecyclerItemViewHolder vh = new RecyclerItemViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerItemViewHolder holder, int position) {

        EventData item = mDataset.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        int minute = item.getStartMinute();
        String minuteDisplay = "" + minute;

        //set in
        if (minute < 10)
            minuteDisplay = "0" + minuteDisplay;
        holder.tvIn.setText(item.getStartHour() + ":" + minuteDisplay);

        //set out
        if (item.isClosed()) {
            minute = item.getEndMinute();
            minuteDisplay = "" + minute;
            if (minute < 10)
                minuteDisplay = "0" + minuteDisplay;
            holder.tvOut.setText(item.getEndHour() + ":" + minuteDisplay);

            //set duration
           // holder.tvDuration.setText("" + LocalTime.getFormatTime(item.getDuration()));
        } else {
            holder.tvOut.setText("...");
            //holder.tvDuration.setText("...");
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void notifyData(List<EventData> myList) {
        Log.d("notifyData ", myList.size() + "");
        this.mDataset = myList;
        notifyDataSetChanged();
    }
}