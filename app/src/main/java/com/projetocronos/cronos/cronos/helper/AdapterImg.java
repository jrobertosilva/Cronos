package com.projetocronos.cronos.cronos.helper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jose- on 30/03/2018.
 */

public class AdapterImg extends PagerAdapter {
    private Context context;
    private ArrayList<byte[]> imgs;

    public AdapterImg(Context context, ArrayList<byte[]> imgs){
        this.context = context;
        this.imgs = imgs;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(lp);
        container.addView(ll);

        ImageView iv = new ImageView(context);
        iv.setImageResource(toInt(imgs.get(position)));
        ll.addView(iv);

        TextView tv = new TextView(context);
        tv.setText("Carro " + (position + 1));
        ll.addView(tv);
        Log.i("Script", "Build: Carro: " + (position + 1));
        return (ll);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view){
        Log.i("Script", "Destroy: Carro: " + (position + 1));
        container.removeView((View)view);
    }

    public static String bytesToString(byte[] b) {
        try {
            return new String(b, "UTF-8");
        }

        catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private int toInt(byte[] b1){
        String s1 = bytesToString(b1);
        int y = Integer.parseInt(s1);
        return y;
    }
}
