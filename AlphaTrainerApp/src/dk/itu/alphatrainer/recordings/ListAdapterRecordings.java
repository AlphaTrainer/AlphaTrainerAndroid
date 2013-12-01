package dk.itu.alphatrainer.recordings;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dk.itu.alphatrainer.R;
import dk.itu.alphatrainer.model.Recording;
import dk.itu.alphatrainer.ui.UiUtils;

public class ListAdapterRecordings extends ArrayAdapter<Recording> {

    Context context; 
    int layoutResourceId;    
    List<Recording> data = null;
    
    public ListAdapterRecordings(Context context, int layoutResourceId, List<Recording> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordingHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            UiUtils.changeFonts((ViewGroup) row);
            holder = new RecordingHolder();
            holder.txtId = (TextView) row.findViewById(R.id.txt_rec_id);
            holder.txtType = (TextView) row.findViewById(R.id.txt_type);
            row.setTag(holder);
        }
        else
        {
            holder = (RecordingHolder) row.getTag();
        }
        
        holder.txtId.setText(Integer.toString(data.get(position).getId()));
        holder.txtType.setText(data.get(position).getType());
        
        return row;
    }
    
    static class RecordingHolder
    {
        TextView txtId;
        TextView txtType;
    }
}
