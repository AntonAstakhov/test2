package io.excitinglab.lifemanager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.sql.Types.NULL;

public class MyAdapterToday extends BaseAdapter {


    DatabaseHelper mDatabaseHelper;
//    CheckBox checkBox;


    Context context;
    ArrayList<Task> tasks;
    private static LayoutInflater inflater = null;

    public MyAdapterToday(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    Intent intent;
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, parent,false);

        mDatabaseHelper = DatabaseHelper.getInstance(context);

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, EditTaskActivity.class);
                int i = (int) tasks.get(position).getId();
                intent.putExtra("id", i);
                context.startActivity(intent);
            }
        });

        final TextView text1 = (TextView) vi.findViewById(R.id.tv_header);
        TextView text2 = (TextView) vi.findViewById(R.id.tv_list);
        TextView text3 = (TextView) vi.findViewById(R.id.tv_date);
        ImageView img1=(ImageView) vi.findViewById(R.id.img_date);
        ImageView img2=(ImageView) vi.findViewById(R.id.img_notif);
//        CheckBox ch=(CheckBox) vi.findViewById(R.id.checkBox);

        text1.setText(tasks.get(position).getName());

//        ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    //TODO set status to 1
//                    text1.setPaintFlags(text1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                }
//                else{
//                    //TODO set status to 0
//                    text1.setPaintFlags(0);
//                }
//            }
//        });



        if (tasks.get(position).getStatus() == 1) {
//            ch.setChecked(true);
            text1.setPaintFlags(text1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
//            ch.setChecked(false);
            text1.setPaintFlags(0);
        }




        Date nowDate = new Date (new java.util.Date().getTime());
        Date d = new Date (tasks.get(position).getDeadline());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
//        calendar.add(Calendar.DATE, -1);
        nowDate = calendar.getTime();
//        ImageView img3=(ImageView) vi.findViewById(R.id.img_attention);

        if (DateUtils.isToday(tasks.get(position).getDeadline())) {
            // do nothing
        }

        else if (d.before(nowDate)) {

//            img3.setVisibility(View.VISIBLE);
//            text1.setTextColor(Color.parseColor("#FF0000"));

//            text1.setTypeface(null, Typeface.BOLD);
//            text1.setTextColor(Color.parseColor("#FF0000"));
            img1.setColorFilter(ContextCompat.getColor(context,R.color.red_accent200));
            text3.setTextColor(Color.parseColor("#FF0000"));
        }
//        else {
////            img3.setVisibility(View.INVISIBLE);
////            text1.setTypeface(null, Typeface.NORMAL);
//        }

        if (tasks.get(position).getP_id() == 0) {
            text2.setText(R.string.Inbox);
        }
        else {
            text2.setText(mDatabaseHelper.getListByID(tasks.get(position).getP_id()).getName());
        }

        String dateString="";
//        String timeString="";
        if (tasks.get(position).getDeadline()!=NULL) {
            Date datetime=new Date(tasks.get(position).getDeadline());
//            SimpleDateFormat sdf1=new SimpleDateFormat("EEE, d MMM");
            SimpleDateFormat sdf1=new SimpleDateFormat("EEE, d MMM", Locale.getDefault());
            dateString=sdf1.format(datetime);
        }
        else {
            img1.setVisibility(View.INVISIBLE);
        }

        if (tasks.get(position).getReminder()==NULL || tasks.get(position).getReminder()==0) {
            img2.setVisibility(View.INVISIBLE);
        }
        else {
//            Date datetime=new Date(tasks.get(position).getReminder());
////            SimpleDateFormat sdf2=new SimpleDateFormat("EEE, d MMM hh:mm aaa");
//            SimpleDateFormat sdf2=new SimpleDateFormat("hh:mm a");
////            SimpleDateFormat sdf2=new SimpleDateFormat("yyyy.MM.dd HH:mm");
//            timeString=sdf2.format(datetime);
            img2.setVisibility(View.VISIBLE);
        }

        text3.setText(dateString);
//        text4.setText(timeString);

        return vi;
    }


//    private void toastMessage(String message){
//        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
//    }

}

