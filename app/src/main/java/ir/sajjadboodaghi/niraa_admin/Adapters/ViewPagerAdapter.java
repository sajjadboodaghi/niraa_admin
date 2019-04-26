package ir.sajjadboodaghi.niraa_admin.Adapters;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ir.sajjadboodaghi.niraa_admin.R;

/**
 * Created by Sajjad on 03/15/2018.
 */

public class ViewPagerAdapter extends PagerAdapter {
    public Context context;
    public String[] imageUrls;

    public ViewPagerAdapter(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(context)
                .load(imageUrls[position])
                .placeholder(ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_image, null))
                .error(ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_image, null))
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
